package com.github.fmjsjx.libcommon.redis

import kotlinx.coroutines.future.await
import java.util.concurrent.CompletionStage
import java.util.concurrent.atomic.AtomicInteger

/**
 * A distributed lock implemented by using REDIS.
 * @author MJ Fang
 * @since 3.1
 */
data class RedisDistributedLock<K, V>(
    /**
     * The REDIS key.
     */
    val key: K,
    /**
     * The REDIS value.
     */
    val value: V,
    /**
     * The `unlock` function.
     */
    val unlockMethod: (K, V) -> CompletionStage<Boolean>
) : AutoCloseable {

    private val state = AtomicInteger(1)

    /**
     * Unlock this lock.
     */
    suspend fun unlock() = unlockAsync().await()

    /**
     * Unlock this lock asynchronously.
     */
    fun unlockAsync() = state.runCatching {
        if (!compareAndSet(1, 0)) {
            throw IllegalStateException("RedisDistributedLock($key, $value) already been unlocked!")
        }
    }.map { unlockMethod(key, value) }.getOrThrow()

    override fun close() {
        unlockAsync()
    }

}
