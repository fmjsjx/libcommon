package com.github.fmjsjx.libcommon.redis.core

import com.github.fmjsjx.libcommon.redis.locks.KeepAliveRedisCoroutineLock
import com.github.fmjsjx.libcommon.redis.locks.KeepAliveRedisCoroutineLock.Companion.DEFAULT_TIMEOUT_SECONDS
import com.github.fmjsjx.libcommon.redis.locks.RedisCoroutineLock
import java.util.concurrent.ScheduledExecutorService

/**
 * Extension function for [RedisConnectionAdapter] to create [RedisCoroutineLock]s.
 *
 * @param key the key
 * @param value the value
 * @param timeoutSeconds the timeout seconds
 * @param keepAliveExecutor the keep alive scheduled executor
 * @param K the type of the key
 * @param V the type of the value
 * @return a new [RedisCoroutineLock] instance
 * @author MJ Fang
 * @since 4.1
 */
fun <K : Any, V : Any> RedisConnectionAdapter<K, V>.newCoroutineLock(
    key: K,
    value: V,
    timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
    keepAliveExecutor: ScheduledExecutorService? = null,
): RedisCoroutineLock<K, V> =
    keepAliveExecutor?.let {
        KeepAliveRedisCoroutineLock(this, key, value, timeoutSeconds, it)
    } ?: KeepAliveRedisCoroutineLock(this, key, value, timeoutSeconds)