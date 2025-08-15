package com.github.fmjsjx.libcommon.redis

import io.lettuce.core.RedisFuture
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.CompletableFuture

@ExtendWith(MockKExtension::class)
class RedisExtensionTests {

    private val redisAsyncCommands: RedisAsyncCommands<String, String> = mockk()
    private val redisClusterAsyncCommands: RedisClusterAsyncCommands<String, String> = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testTryLock() {
        val ok = mockk<RedisFuture<String>>()
        every { ok.toCompletableFuture() } returns CompletableFuture.completedFuture("OK")
        val nil = mockk<RedisFuture<String>>()
        every { nil.toCompletableFuture() } returns CompletableFuture.completedFuture(null)
        every { redisAsyncCommands.set(any(), any(), any()) } returns ok
        every { redisAsyncCommands.set("nil", any(), any()) } returns nil
        every { redisClusterAsyncCommands.set(any(), any(), any()) } returns ok
        every { redisClusterAsyncCommands.set("nil", any(), any()) } returns nil

        runBlocking {
            assertTrue(redisAsyncCommands.tryLock("key", "value", 5, 10_000))
            assertFalse(redisAsyncCommands.tryLock("nil", "value", 5, -1))
            assertTrue(redisClusterAsyncCommands.tryLock("key", "value", 5, 10_000))
            assertFalse(redisClusterAsyncCommands.tryLock("nil", "value", 5, -1))
        }
    }

}