package com.github.fmjsjx.libcommon.redis.locks;

import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KeepAliveRedisRemoteLockTests {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveRedisRemoteLockTests.class);

    private static final String DEFAULT_REDIS_URI = "redis://localhost:6379/9";
    private static final String REDIS_URI_ENV_KEY = "TEST_REDIS_URI";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisConnectionAdapter<String, String> adapter;
    private StatefulRedisConnection<byte[], byte[]> byteConnection;
    private RedisConnectionAdapter<byte[], byte[]> byteAdapter;
    private ScheduledExecutorService scheduler;
    private boolean redisAvailable;

    @BeforeAll
    void setup() {
        String redisUriString = System.getenv(REDIS_URI_ENV_KEY);
        if (redisUriString == null || redisUriString.isBlank()) {
            redisUriString = DEFAULT_REDIS_URI;
        }

        try {
            RedisURI redisUri = RedisURI.create(redisUriString);
            redisUri.setTimeout(CONNECT_TIMEOUT);

            redisClient = RedisClient.create(redisUri);
            connection = redisClient.connect();

            // Test connection
            String pingResult = connection.sync().ping();
            redisAvailable = "PONG".equalsIgnoreCase(pingResult);

            if (redisAvailable) {
                adapter = RedisConnectionAdapter.ofDirect(connection);
                byteConnection = redisClient.connect(io.lettuce.core.codec.ByteArrayCodec.INSTANCE);
                byteAdapter = RedisConnectionAdapter.ofDirect(byteConnection);
                scheduler = Executors.newScheduledThreadPool(2);
                logger.info("Redis is available at: {}", redisUriString);
            } else {
                logger.warn("Redis ping failed, tests will be skipped");
            }
        } catch (Exception e) {
            logger.warn("Redis is not available: {}, tests will be skipped", e.getMessage());
            redisAvailable = false;
        }
    }

    @AfterAll
    void cleanup() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (Exception e) {
                // Ignore
            }
        }
        if (byteConnection != null) {
            try {
                byteConnection.close();
            } catch (Exception e) {
                // Ignore
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                // Ignore
            }
        }
        if (redisClient != null) {
            try {
                redisClient.shutdown();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @BeforeEach
    void checkRedisAvailable() {
        assumeTrue(redisAvailable, "Redis is not available");
    }

    // ========== String Type Tests ==========

    @Test
    void testGetKey() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        assertEquals(key, lock.getKey());
    }

    @Test
    void testGetValue() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        assertEquals(value, lock.getValue());
    }

    @Test
    void testGetTimeoutSeconds() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 15;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        assertEquals(timeout, lock.getTimeoutSeconds());
    }

    @Test
    void testTryInLock_Success() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<String> result = lock.tryInLock(() -> {
            actionExecuted.set(true);
            return "success";
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertEquals("success", result.get());

        // Verify lock is released
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testTryInLock_AlreadyLocked() {
        String key = "test:lock:" + UUID.randomUUID();
        String value1 = UUID.randomUUID().toString();
        String value2 = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value
        connection.sync().set(key, value2);

        try {
            KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value1, timeout, scheduler);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<String> result = lock.tryInLock(() -> {
                actionExecuted.set(true);
                return "success";
            });

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            connection.sync().del(key);
        }
    }

    @Test
    void testTryInLock_KeepAliveRefreshesLock() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 2; // 2 seconds timeout

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<Long> result = lock.tryInLock(() -> {
            actionExecuted.set(true);
            try {
                // Sleep longer than timeout, keep-alive should refresh the lock
                Thread.sleep(3000);
                // Check if lock still exists
                return connection.sync().ttl(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        // TTL should be positive, meaning the lock was kept alive
        assertTrue(result.get() > 0);

        // Verify lock is released after action
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testRunInLock_Success() throws InterruptedException {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<String> result = lock.runInLock(1000, () -> {
            actionExecuted.set(true);
            return "success";
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertEquals("success", result.get());

        // Verify lock is released
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testRunInLock_WithRetry() throws InterruptedException {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value first
        connection.sync().setex(key, 1, "other");

        try {
            KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<String> result = lock.runInLock(2000, 100, () -> {
                actionExecuted.set(true);
                return "success";
            });

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertEquals("success", result.get());
        } finally {
            connection.sync().del(key);
        }
    }

    @Test
    void testTryInLockAsync_Success() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<String>> resultStage = lock.tryInLockAsync(() -> {
            actionExecuted.set(true);
            return CompletableFuture.completedStage("async-success");
        });

        Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertEquals("async-success", result.get());

        // Verify lock is released
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testTryInLockAsync_KeepAliveRefreshesLock() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 2; // 2 seconds timeout

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<Long>> resultStage = lock.tryInLockAsync(() -> {
            actionExecuted.set(true);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // Sleep longer than timeout, keep-alive should refresh the lock
                    Thread.sleep(3000);
                    // Check if lock still exists
                    return connection.sync().ttl(key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });

        Optional<Long> result = resultStage.toCompletableFuture().get(10, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        // TTL should be positive, meaning the lock was kept alive
        assertTrue(result.get() > 0);

        // Verify lock is released after action
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testRunInLockAsync_Success() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<String>> resultStage = lock.runInLockAsync(1000, () -> {
            actionExecuted.set(true);
            return CompletableFuture.completedStage("async-wait-success");
        });

        Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertEquals("async-wait-success", result.get());

        // Verify lock is released
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testConcurrentLocking() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value1 = "thread1-" + UUID.randomUUID();
        String value2 = "thread2-" + UUID.randomUUID();
        long timeout = 10;

        KeepAliveRedisRemoteLock<String, String> lock1 = new KeepAliveRedisRemoteLock<>(adapter, key, value1, timeout, scheduler);
        KeepAliveRedisRemoteLock<String, String> lock2 = new KeepAliveRedisRemoteLock<>(adapter, key, value2, timeout, scheduler);

        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(2);
        CountDownLatch endLatch = new CountDownLatch(2);
        AtomicReference<Exception> exception = new AtomicReference<>();

        Runnable task = () -> {
            try {
                startLatch.countDown();
                startLatch.await();
                var lock = Thread.currentThread().getName().contains("1") ? lock1 : lock2;
                lock.runInLock(1000, 50, () -> {
                    counter.incrementAndGet();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return counter.get();
                });
            } catch (Exception e) {
                exception.set(e);
            } finally {
                endLatch.countDown();
            }
        };

        Thread thread1 = new Thread(task, "test-thread-1");
        Thread thread2 = new Thread(task, "test-thread-2");

        thread1.start();
        thread2.start();

        assertTrue(endLatch.await(10, TimeUnit.SECONDS));
        assertNull(exception.get());
        assertEquals(2, counter.get());

        // Verify lock is released
        connection.sync().del(key);
    }

    @Test
    void testConstructorWithDefaultExecutor() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        // Test constructor without scheduler parameter
        KeepAliveRedisRemoteLock<String, String> lock = new KeepAliveRedisRemoteLock<>(adapter, key, value, timeout);

        assertEquals(key, lock.getKey());
        assertEquals(value, lock.getValue());
        assertEquals(timeout, lock.getTimeoutSeconds());
    }

    // ========== Byte Array Tests ==========

    @Test
    void testByteArray_GetKey() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        assertArrayEquals(key, lock.getKey());
    }

    @Test
    void testByteArray_GetValue() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        assertArrayEquals(value, lock.getValue());
    }

    @Test
    void testByteArray_GetTimeoutSeconds() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 15;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        assertEquals(timeout, lock.getTimeoutSeconds());
    }

    @Test
    void testByteArray_TryInLock_Success() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<byte[]> result = lock.tryInLock(() -> {
            actionExecuted.set(true);
            return "success".getBytes();
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertArrayEquals("success".getBytes(), result.get());

        // Verify lock is released
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_TryInLock_AlreadyLocked() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value1 = UUID.randomUUID().toString().getBytes();
        byte[] value2 = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value
        byteConnection.sync().set(key, value2);

        try {
            KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value1, timeout, scheduler);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<byte[]> result = lock.tryInLock(() -> {
                actionExecuted.set(true);
                return "success".getBytes();
            });

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            byteConnection.sync().del(key);
        }
    }

    @Test
    void testByteArray_TryInLock_KeepAliveRefreshesLock() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 2; // 2 seconds timeout

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<Long> result = lock.tryInLock(() -> {
            actionExecuted.set(true);
            try {
                // Sleep longer than timeout, keep-alive should refresh the lock
                Thread.sleep(3000);
                // Check if lock still exists
                return byteConnection.sync().ttl(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        // TTL should be positive, meaning the lock was kept alive
        assertTrue(result.get() > 0);

        // Verify lock is released after action
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_RunInLock_Success() throws InterruptedException {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Optional<byte[]> result = lock.runInLock(1000, () -> {
            actionExecuted.set(true);
            return "success".getBytes();
        });

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertArrayEquals("success".getBytes(), result.get());

        // Verify lock is released
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_RunInLock_WithRetry() throws InterruptedException {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value first
        byteConnection.sync().setex(key, 1, "other".getBytes());

        try {
            KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<byte[]> result = lock.runInLock(2000, 100, () -> {
                actionExecuted.set(true);
                return "success".getBytes();
            });

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertArrayEquals("success".getBytes(), result.get());
        } finally {
            byteConnection.sync().del(key);
        }
    }

    @Test
    void testByteArray_TryInLockAsync_Success() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<byte[]>> resultStage = lock.tryInLockAsync(() -> {
            actionExecuted.set(true);
            return CompletableFuture.completedStage("async-success".getBytes());
        });

        Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertArrayEquals("async-success".getBytes(), result.get());

        // Verify lock is released
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_TryInLockAsync_KeepAliveRefreshesLock() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 2; // 2 seconds timeout

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<Long>> resultStage = lock.tryInLockAsync(() -> {
            actionExecuted.set(true);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // Sleep longer than timeout, keep-alive should refresh the lock
                    Thread.sleep(3000);
                    // Check if lock still exists
                    return byteConnection.sync().ttl(key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });

        Optional<Long> result = resultStage.toCompletableFuture().get(10, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        // TTL should be positive, meaning the lock was kept alive
        assertTrue(result.get() > 0);

        // Verify lock is released after action
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_RunInLockAsync_Success() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout, scheduler);

        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        CompletionStage<Optional<byte[]>> resultStage = lock.runInLockAsync(1000, () -> {
            actionExecuted.set(true);
            return CompletableFuture.completedStage("async-wait-success".getBytes());
        });

        Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

        assertTrue(actionExecuted.get());
        assertTrue(result.isPresent());
        assertArrayEquals("async-wait-success".getBytes(), result.get());

        // Verify lock is released
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

    @Test
    void testByteArray_ConcurrentLocking() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value1 = ("thread1-" + UUID.randomUUID()).getBytes();
        byte[] value2 = ("thread2-" + UUID.randomUUID()).getBytes();
        long timeout = 10;

        KeepAliveRedisRemoteLock<byte[], byte[]> lock1 = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value1, timeout, scheduler);
        KeepAliveRedisRemoteLock<byte[], byte[]> lock2 = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value2, timeout, scheduler);

        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch startLatch = new CountDownLatch(2);
        CountDownLatch endLatch = new CountDownLatch(2);
        AtomicReference<Exception> exception = new AtomicReference<>();

        Runnable task = () -> {
            try {
                startLatch.countDown();
                startLatch.await();
                var lock = Thread.currentThread().getName().contains("1") ? lock1 : lock2;
                lock.runInLock(1000, 50, () -> {
                    counter.incrementAndGet();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return Integer.toString(counter.get()).getBytes();
                });
            } catch (Exception e) {
                exception.set(e);
            } finally {
                endLatch.countDown();
            }
        };

        Thread thread1 = new Thread(task, "test-thread-1");
        Thread thread2 = new Thread(task, "test-thread-2");

        thread1.start();
        thread2.start();

        assertTrue(endLatch.await(10, TimeUnit.SECONDS));
        assertNull(exception.get());
        assertEquals(2, counter.get());

        // Verify lock is released
        byteConnection.sync().del(key);
    }

    @Test
    void testByteArray_ConstructorWithDefaultExecutor() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Test constructor without scheduler parameter
        KeepAliveRedisRemoteLock<byte[], byte[]> lock = new KeepAliveRedisRemoteLock<>(byteAdapter, key, value, timeout);

        assertArrayEquals(key, lock.getKey());
        assertArrayEquals(value, lock.getValue());
        assertEquals(timeout, lock.getTimeoutSeconds());
    }

}
