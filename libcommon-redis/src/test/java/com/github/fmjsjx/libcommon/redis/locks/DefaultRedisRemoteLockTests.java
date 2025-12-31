package com.github.fmjsjx.libcommon.redis.locks;

import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultRedisRemoteLockTests {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedisRemoteLockTests.class);

    private static final String DEFAULT_REDIS_URI = "redis://localhost:6379/9";
    private static final String REDIS_URI_ENV_KEY = "TEST_REDIS_URI";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisConnectionAdapter<String, String> adapter;
    private StatefulRedisConnection<byte[], byte[]> byteConnection;
    private RedisConnectionAdapter<byte[], byte[]> byteAdapter;
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
                byteConnection = redisClient.connect(ByteArrayCodec.INSTANCE);
                byteAdapter = RedisConnectionAdapter.ofDirect(byteConnection);
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

    @Test
    void testGetKey() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

        assertEquals(key, lock.getKey());
    }

    @Test
    void testGetValue() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

        assertEquals(value, lock.getValue());
    }

    @Test
    void testGetTimeoutSeconds() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 15;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

        assertEquals(timeout, lock.getTimeoutSeconds());
    }

    @Test
    void testTryInLock_Success() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

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

        // Lock with first value
        connection.sync().set(key, value2);

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value1, timeout);

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
    void testRunInLock_Success() throws InterruptedException {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

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
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

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
    void testRunInLock_Timeout() throws InterruptedException {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value
        connection.sync().set(key, "other");

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<String> result = lock.runInLock(500, 100, () -> {
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
    void testTryInLockAsync_Success() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

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
    void testTryInLockAsync_AlreadyLocked() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value1 = UUID.randomUUID().toString();
        String value2 = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value
        connection.sync().set(key, value2);

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value1, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.tryInLockAsync(() -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("async-success");
            });

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            connection.sync().del(key);
        }
    }

    @Test
    void testTryInLockAsync_WithExecutor() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;
        var executor = Executors.newSingleThreadExecutor();

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.tryInLockAsync(() -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("executor-success");
            }, executor);

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertEquals("executor-success", result.get());

            // Verify lock is released
            String keyValue = connection.sync().get(key);
            assertNull(keyValue);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testRunInLockAsync_Success() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

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
    void testRunInLockAsync_WithRetry() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value first
        connection.sync().setex(key, 1, "other");

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.runInLockAsync(2000, 100, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("retry-success");
            });

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertEquals("retry-success", result.get());
        } finally {
            connection.sync().del(key);
        }
    }

    @Test
    void testRunInLockAsync_Timeout() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        // Lock with another value
        connection.sync().set(key, "other");

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.runInLockAsync(500, 100, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("should-not-execute");
            });

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            connection.sync().del(key);
        }
    }

    @Test
    void testRunInLockAsync_WithExecutor() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;
        var executor = Executors.newSingleThreadExecutor();

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.runInLockAsync(1000, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("executor-wait-success");
            }, executor);

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertEquals("executor-wait-success", result.get());

            // Verify lock is released
            String keyValue = connection.sync().get(key);
            assertNull(keyValue);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testRunInLockAsync_WithRetryAndExecutor() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;
        var executor = Executors.newSingleThreadExecutor();

        // Lock with another value first
        connection.sync().setex(key, 1, "other");

        try {
            DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<String>> resultStage = lock.runInLockAsync(2000, 100, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("executor-retry-success");
            }, executor);

            Optional<String> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertEquals("executor-retry-success", result.get());
        } finally {
            connection.sync().del(key);
            executor.shutdown();
        }
    }

    @Test
    void testConcurrentLocking() throws Exception {
        String key = "test:lock:" + UUID.randomUUID();
        String value1 = "thread1-" + UUID.randomUUID();
        String value2 = "thread2-" + UUID.randomUUID();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock1 = new DefaultRedisRemoteLock<>(adapter, key, value1, timeout);
        DefaultRedisRemoteLock<String, String> lock2 = new DefaultRedisRemoteLock<>(adapter, key, value2, timeout);

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
    void testLockWithNullReturnValue() {
        String key = "test:lock:" + UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        long timeout = 10;

        DefaultRedisRemoteLock<String, String> lock = new DefaultRedisRemoteLock<>(adapter, key, value, timeout);

        Optional<String> result = lock.tryInLock(() -> null);

        assertTrue(result.isEmpty());

        // Verify lock is released
        String keyValue = connection.sync().get(key);
        assertNull(keyValue);
    }

    // ========== Byte Array Tests ==========

    @Test
    void testByteArray_GetKey() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

        assertArrayEquals(key, lock.getKey());
    }

    @Test
    void testByteArray_GetValue() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

        assertArrayEquals(value, lock.getValue());
    }

    @Test
    void testByteArray_GetTimeoutSeconds() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 15;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

        assertEquals(timeout, lock.getTimeoutSeconds());
    }

    @Test
    void testByteArray_TryInLock_Success() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

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
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value1, timeout);

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
    void testByteArray_RunInLock_Success() throws InterruptedException {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

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
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

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
    void testByteArray_RunInLock_Timeout() throws InterruptedException {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value
        byteConnection.sync().set(key, "other".getBytes());

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Optional<byte[]> result = lock.runInLock(500, 100, () -> {
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
    void testByteArray_TryInLockAsync_Success() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

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
    void testByteArray_TryInLockAsync_AlreadyLocked() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value1 = UUID.randomUUID().toString().getBytes();
        byte[] value2 = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value
        byteConnection.sync().set(key, value2);

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value1, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<byte[]>> resultStage = lock.tryInLockAsync(() -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("async-success".getBytes());
            });

            Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            byteConnection.sync().del(key);
        }
    }

    @Test
    void testByteArray_RunInLockAsync_Success() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

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
    void testByteArray_RunInLockAsync_WithRetry() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value first
        byteConnection.sync().setex(key, 1, "other".getBytes());

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<byte[]>> resultStage = lock.runInLockAsync(2000, 100, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("retry-success".getBytes());
            });

            Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertArrayEquals("retry-success".getBytes(), result.get());
        } finally {
            byteConnection.sync().del(key);
        }
    }

    @Test
    void testByteArray_RunInLockAsync_Timeout() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        // Lock with another value
        byteConnection.sync().set(key, "other".getBytes());

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<byte[]>> resultStage = lock.runInLockAsync(500, 100, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("should-not-execute".getBytes());
            });

            Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertFalse(actionExecuted.get());
            assertNull(result);
        } finally {
            byteConnection.sync().del(key);
        }
    }

    @Test
    void testByteArray_TryInLockAsync_WithExecutor() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;
        var executor = Executors.newSingleThreadExecutor();

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<byte[]>> resultStage = lock.tryInLockAsync(() -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("executor-success".getBytes());
            }, executor);

            Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertArrayEquals("executor-success".getBytes(), result.get());

            // Verify lock is released
            byte[] keyValue = byteConnection.sync().get(key);
            assertNull(keyValue);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testByteArray_RunInLockAsync_WithExecutor() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;
        var executor = Executors.newSingleThreadExecutor();

        try {
            DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            CompletionStage<Optional<byte[]>> resultStage = lock.runInLockAsync(1000, () -> {
                actionExecuted.set(true);
                return CompletableFuture.completedStage("executor-wait-success".getBytes());
            }, executor);

            Optional<byte[]> result = resultStage.toCompletableFuture().get(5, TimeUnit.SECONDS);

            assertTrue(actionExecuted.get());
            assertTrue(result.isPresent());
            assertArrayEquals("executor-wait-success".getBytes(), result.get());

            // Verify lock is released
            byte[] keyValue = byteConnection.sync().get(key);
            assertNull(keyValue);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testByteArray_ConcurrentLocking() throws Exception {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value1 = ("thread1-" + UUID.randomUUID()).getBytes();
        byte[] value2 = ("thread2-" + UUID.randomUUID()).getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock1 = new DefaultRedisRemoteLock<>(byteAdapter, key, value1, timeout);
        DefaultRedisRemoteLock<byte[], byte[]> lock2 = new DefaultRedisRemoteLock<>(byteAdapter, key, value2, timeout);

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
    void testByteArray_LockWithNullReturnValue() {
        byte[] key = ("test:lock:" + UUID.randomUUID()).getBytes();
        byte[] value = UUID.randomUUID().toString().getBytes();
        long timeout = 10;

        DefaultRedisRemoteLock<byte[], byte[]> lock = new DefaultRedisRemoteLock<>(byteAdapter, key, value, timeout);

        Optional<byte[]> result = lock.tryInLock(() -> null);

        assertTrue(result.isEmpty());

        // Verify lock is released
        byte[] keyValue = byteConnection.sync().get(key);
        assertNull(keyValue);
    }

}
