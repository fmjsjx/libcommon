package com.github.fmjsjx.libcommon.redis.locks

import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.ByteArrayCodec
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicInteger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeepAliveRedisCoroutineLockTests {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeepAliveRedisCoroutineLockTests::class.java)

        private const val DEFAULT_REDIS_URI = "redis://localhost:6379/9"
        private const val REDIS_URI_ENV_KEY = "TEST_REDIS_URI"
        private val CONNECT_TIMEOUT: Duration = Duration.ofSeconds(2)
    }

    private lateinit var redisClient: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>
    private lateinit var adapter: RedisConnectionAdapter<String, String>
    private lateinit var byteConnection: StatefulRedisConnection<ByteArray, ByteArray>
    private lateinit var byteAdapter: RedisConnectionAdapter<ByteArray, ByteArray>
    private lateinit var scheduler: ScheduledExecutorService
    private var redisAvailable: Boolean = false

    @BeforeAll
    fun setup() {
        var redisUriString = System.getenv(REDIS_URI_ENV_KEY)
        if (redisUriString.isNullOrBlank()) {
            redisUriString = DEFAULT_REDIS_URI
        }

        try {
            val redisUri = RedisURI.create(redisUriString)
            redisUri.timeout = CONNECT_TIMEOUT

            redisClient = RedisClient.create(redisUri)
            connection = redisClient.connect()

            // Test connection
            val pingResult = connection.sync().ping()
            redisAvailable = "PONG".equals(pingResult, ignoreCase = true)

            if (redisAvailable) {
                adapter = RedisConnectionAdapter.ofDirect(connection)
                byteConnection = redisClient.connect(ByteArrayCodec.INSTANCE)
                byteAdapter = RedisConnectionAdapter.ofDirect(byteConnection)
                scheduler = Executors.newScheduledThreadPool(2)
                logger.info("Redis is available at: {}", redisUriString)
            } else {
                logger.warn("Redis ping failed, tests will be skipped")
            }
        } catch (e: Exception) {
            logger.warn("Redis is not available: {}, tests will be skipped", e.message)
            redisAvailable = false
        }
    }

    @AfterAll
    fun cleanup() {
        if (::scheduler.isInitialized) {
            try {
                scheduler.shutdown()
            } catch (e: Exception) {
                // Ignore
            }
        }
        if (::byteConnection.isInitialized) {
            try {
                byteConnection.close()
            } catch (e: Exception) {
                // Ignore
            }
        }
        if (::connection.isInitialized) {
            try {
                connection.close()
            } catch (e: Exception) {
                // Ignore
            }
        }
        if (::redisClient.isInitialized) {
            try {
                redisClient.shutdown()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    @BeforeEach
    fun checkRedisAvailable() {
        Assumptions.assumeTrue(redisAvailable, "Redis is not available")
    }

    // ========== String Type Tests ==========

    @Test
    fun testGetKey() {
        val key = "test:lock:${UUID.randomUUID()}"
        val value = UUID.randomUUID().toString()
        val timeout = 10L

        val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

        assertEquals(key, lock.key)
    }

    @Test
    fun testGetValue() {
        val key = "test:lock:${UUID.randomUUID()}"
        val value = UUID.randomUUID().toString()
        val timeout = 10L

        val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

        assertEquals(value, lock.value)
    }

    @Test
    fun testGetTimeoutSeconds() {
        val key = "test:lock:${UUID.randomUUID()}"
        val value = UUID.randomUUID().toString()
        val timeout = 15L

        val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

        assertEquals(timeout, lock.timeoutSeconds)
    }

    @Test
    fun testTryInLockAndAwait_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.tryInLockAndAwait {
                actionExecuted = true
                "success"
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            assertEquals("success", result)

            // Verify lock is released
            val keyValue = connection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testTryInLockAndAwait_AlreadyLocked() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value1 = UUID.randomUUID().toString()
            val value2 = UUID.randomUUID().toString()
            val timeout = 10L

            // Lock with another value
            connection.sync().set(key, value2)

            try {
                val lock = KeepAliveRedisCoroutineLock(adapter, key, value1, timeout, scheduler)

                var actionExecuted = false
                val (acquired, result) = lock.tryInLockAndAwait {
                    actionExecuted = true
                    "success"
                }

                assertFalse(actionExecuted)
                assertFalse(acquired)
                assertNull(result)
            } finally {
                connection.sync().del(key)
            }
        }
    }

    @Test
    fun testTryInLockAndAwait_KeepAliveRefreshesLock() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 2L // 2 seconds timeout

            val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.tryInLockAndAwait {
                actionExecuted = true
                // Sleep longer than timeout, keep-alive should refresh the lock
                delay(3000)
                // Check if lock still exists
                connection.sync().ttl(key)
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            // TTL should be positive, meaning the lock was kept alive
            assertTrue(result!! > 0)

            // Verify lock is released after action
            val keyValue = connection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testTryInLockAndAwait_WithExceptionSupplier_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val result = lock.tryInLockAndAwait(
                notAcquiredSupplier = { IllegalStateException("Lock not acquired") }
            ) {
                actionExecuted = true
                "success"
            }

            assertTrue(actionExecuted)
            assertEquals("success", result)

            // Verify lock is released
            val keyValue = connection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testTryInLockAndAwait_WithExceptionSupplier_Throws() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value1 = UUID.randomUUID().toString()
            val value2 = UUID.randomUUID().toString()
            val timeout = 10L

            // Lock with another value
            connection.sync().set(key, value2)

            try {
                val lock = KeepAliveRedisCoroutineLock(adapter, key, value1, timeout, scheduler)

                var actionExecuted = false
                assertThrows(IllegalStateException::class.java) {
                    runBlocking {
                        lock.tryInLockAndAwait(
                            notAcquiredSupplier = { IllegalStateException("Lock not acquired") }
                        ) {
                            actionExecuted = true
                        }
                    }
                }

                assertFalse(actionExecuted)
            } finally {
                connection.sync().del(key)
            }
        }
    }

    @Test
    fun testRunInLockAndAwait_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.runInLockAndAwait(1000) {
                actionExecuted = true
                "success"
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            assertEquals("success", result)

            // Verify lock is released
            val keyValue = connection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testRunInLockAndAwait_WithRetry() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 10L

            // Lock with another value first
            connection.sync().setex(key, 1, "other")

            try {
                val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

                var actionExecuted = false
                val (acquired, result) = lock.runInLockAndAwait(2000, 100) {
                    actionExecuted = true
                    "success"
                }

                assertTrue(actionExecuted)
                assertTrue(acquired)
                assertEquals("success", result)
            } finally {
                connection.sync().del(key)
            }
        }
    }

    @Test
    fun testRunInLockAndAwait_WithExceptionSupplier_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value = UUID.randomUUID().toString()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val result = lock.runInLockAndAwait(
                maxWaitMillis = 1000,
                notAcquiredSupplier = { IllegalStateException("Lock not acquired") }
            ) {
                actionExecuted = true
                "success"
            }

            assertTrue(actionExecuted)
            assertEquals("success", result)

            // Verify lock is released
            val keyValue = connection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testConcurrentLocking() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}"
            val value1 = "thread1-${UUID.randomUUID()}"
            val value2 = "thread2-${UUID.randomUUID()}"
            val timeout = 10L

            val lock1 = KeepAliveRedisCoroutineLock(adapter, key, value1, timeout, scheduler)
            val lock2 = KeepAliveRedisCoroutineLock(adapter, key, value2, timeout, scheduler)

            val counter = AtomicInteger(0)
            val startSemaphore = Semaphore(2, 2) // 2个许可，初始全部已获取
            val errors = mutableListOf<Exception>()

            val task: suspend (KeepAliveRedisCoroutineLock<String, String>) -> Unit = { lock ->
                try {
                    startSemaphore.acquire() // 获取信号量，等待父协程释放
                    lock.runInLockAndAwait(1000, 50) {
                        counter.incrementAndGet()
                        delay(100)
                        counter.get()
                    }
                } catch (e: Exception) {
                    synchronized(errors) {
                        errors.add(e)
                    }
                }
            }

            val job1 = launch(Dispatchers.IO) { task(lock1) }
            val job2 = launch(Dispatchers.IO) { task(lock2) }

            // 等待两个协程都启动后，同时释放信号量让它们开始执行
            delay(100) // 确保两个协程都在等待
            startSemaphore.release() // 释放第一个许可
            startSemaphore.release() // 释放第二个许可

            // 等待两个任务完成
            withTimeout(10000) {
                job1.join()
                job2.join()
            }

            assertTrue(errors.isEmpty(), "Expected no exceptions but got: $errors")
            assertEquals(2, counter.get())

            // Clean up
            connection.sync().del(key)
        }
    }

    @Test
    fun testConstructorWithDefaultExecutor() {
        val key = "test:lock:${UUID.randomUUID()}"
        val value = UUID.randomUUID().toString()
        val timeout = 10L

        // Test constructor without scheduler parameter
        val lock = KeepAliveRedisCoroutineLock(adapter, key, value, timeout)

        assertEquals(key, lock.key)
        assertEquals(value, lock.value)
        assertEquals(timeout, lock.timeoutSeconds)
    }

    // ========== Byte Array Tests ==========

    @Test
    fun testByteArray_GetKey() {
        val key = "test:lock:${UUID.randomUUID()}".toByteArray()
        val value = UUID.randomUUID().toString().toByteArray()
        val timeout = 10L

        val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

        assertArrayEquals(key, lock.key)
    }

    @Test
    fun testByteArray_GetValue() {
        val key = "test:lock:${UUID.randomUUID()}".toByteArray()
        val value = UUID.randomUUID().toString().toByteArray()
        val timeout = 10L

        val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

        assertArrayEquals(value, lock.value)
    }

    @Test
    fun testByteArray_GetTimeoutSeconds() {
        val key = "test:lock:${UUID.randomUUID()}".toByteArray()
        val value = UUID.randomUUID().toString().toByteArray()
        val timeout = 15L

        val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

        assertEquals(timeout, lock.timeoutSeconds)
    }

    @Test
    fun testByteArray_TryInLockAndAwait_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value = UUID.randomUUID().toString().toByteArray()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.tryInLockAndAwait {
                actionExecuted = true
                "success".toByteArray()
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            assertArrayEquals("success".toByteArray(), result)

            // Verify lock is released
            val keyValue = byteConnection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testByteArray_TryInLockAndAwait_AlreadyLocked() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value1 = UUID.randomUUID().toString().toByteArray()
            val value2 = UUID.randomUUID().toString().toByteArray()
            val timeout = 10L

            // Lock with another value
            byteConnection.sync().set(key, value2)

            try {
                val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value1, timeout, scheduler)

                var actionExecuted = false
                val (acquired, result) = lock.tryInLockAndAwait {
                    actionExecuted = true
                    "success".toByteArray()
                }

                assertFalse(actionExecuted)
                assertFalse(acquired)
                assertNull(result)
            } finally {
                byteConnection.sync().del(key)
            }
        }
    }

    @Test
    fun testByteArray_TryInLockAndAwait_KeepAliveRefreshesLock() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value = UUID.randomUUID().toString().toByteArray()
            val timeout = 2L // 2 seconds timeout

            val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.tryInLockAndAwait {
                actionExecuted = true
                // Sleep longer than timeout, keep-alive should refresh the lock
                delay(3000)
                // Check if lock still exists
                byteConnection.sync().ttl(key)
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            // TTL should be positive, meaning the lock was kept alive
            assertTrue(result!! > 0)

            // Verify lock is released after action
            val keyValue = byteConnection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testByteArray_RunInLockAndAwait_Success() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value = UUID.randomUUID().toString().toByteArray()
            val timeout = 10L

            val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

            var actionExecuted = false
            val (acquired, result) = lock.runInLockAndAwait(1000) {
                actionExecuted = true
                "success".toByteArray()
            }

            assertTrue(actionExecuted)
            assertTrue(acquired)
            assertArrayEquals("success".toByteArray(), result)

            // Verify lock is released
            val keyValue = byteConnection.sync().get(key)
            assertNull(keyValue)
        }
    }

    @Test
    fun testByteArray_RunInLockAndAwait_WithRetry() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value = UUID.randomUUID().toString().toByteArray()
            val timeout = 10L

            // Lock with another value first
            byteConnection.sync().setex(key, 1, "other".toByteArray())

            try {
                val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout, scheduler)

                var actionExecuted = false
                val (acquired, result) = lock.runInLockAndAwait(2000, 100) {
                    actionExecuted = true
                    "success".toByteArray()
                }

                assertTrue(actionExecuted)
                assertTrue(acquired)
                assertArrayEquals("success".toByteArray(), result)
            } finally {
                byteConnection.sync().del(key)
            }
        }
    }

    @Test
    fun testByteArray_ConcurrentLocking() {
        runBlocking {
            val key = "test:lock:${UUID.randomUUID()}".toByteArray()
            val value1 = "thread1-${UUID.randomUUID()}".toByteArray()
            val value2 = "thread2-${UUID.randomUUID()}".toByteArray()
            val timeout = 10L

            val lock1 = KeepAliveRedisCoroutineLock(byteAdapter, key, value1, timeout, scheduler)
            val lock2 = KeepAliveRedisCoroutineLock(byteAdapter, key, value2, timeout, scheduler)

            val counter = AtomicInteger(0)
            val startSemaphore = Semaphore(2, 2) // 2个许可，初始全部已获取
            val errors = mutableListOf<Exception>()

            val task: suspend (KeepAliveRedisCoroutineLock<ByteArray, ByteArray>) -> Unit = { lock ->
                try {
                    startSemaphore.acquire() // 获取信号量，等待父协程释放
                    lock.runInLockAndAwait(1000, 50) {
                        counter.incrementAndGet()
                        delay(100)
                        counter.get().toString().toByteArray()
                    }
                } catch (e: Exception) {
                    synchronized(errors) {
                        errors.add(e)
                    }
                }
            }

            val job1 = launch(Dispatchers.IO) { task(lock1) }
            val job2 = launch(Dispatchers.IO) { task(lock2) }

            // 等待两个协程都启动后，同时释放信号量让它们开始执行
            delay(100) // 确保两个协程都在等待
            startSemaphore.release() // 释放第一个许可
            startSemaphore.release() // 释放第二个许可

            // 等待两个任务完成
            withTimeout(10000) {
                job1.join()
                job2.join()
            }

            assertTrue(errors.isEmpty(), "Expected no exceptions but got: $errors")
            assertEquals(2, counter.get())

            // Clean up
            byteConnection.sync().del(key)
        }
    }

    @Test
    fun testByteArray_ConstructorWithDefaultExecutor() {
        val key = "test:lock:${UUID.randomUUID()}".toByteArray()
        val value = UUID.randomUUID().toString().toByteArray()
        val timeout = 10L

        // Test constructor without scheduler parameter
        val lock = KeepAliveRedisCoroutineLock(byteAdapter, key, value, timeout)

        assertArrayEquals(key, lock.key)
        assertArrayEquals(value, lock.value)
        assertEquals(timeout, lock.timeoutSeconds)
    }

}