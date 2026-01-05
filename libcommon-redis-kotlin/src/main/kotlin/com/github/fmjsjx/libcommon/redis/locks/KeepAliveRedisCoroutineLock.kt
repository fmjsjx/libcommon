package com.github.fmjsjx.libcommon.redis.locks

import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

/**
 * The implementation of [RedisCoroutineLock] with keep alive feature.
 *
 * @param K the type of the key
 * @param V the type of the value
 * @author MJ Fang
 * @see RedisCoroutineLock
 * @see RedisRemoteLock
 * @see KeepAliveRedisRemoteLock
 * @since 4.1
 */
class KeepAliveRedisCoroutineLock<K : Any, V : Any>(
    connection: RedisConnectionAdapter<K, V>,
    key: K,
    value: V,
    timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
    keepAliveExecutor: ScheduledExecutorService = GlobalKeepAliveExecutorInstanceHolder.INSTANCE,
) : KeepAliveRedisRemoteLock<K, V>(
    connection,
    key,
    value,
    timeoutSeconds,
    keepAliveExecutor,
), RedisCoroutineLock<K, V> {

    companion object {
        /**
         * The default timeout seconds of the lock.
         */
        const val DEFAULT_TIMEOUT_SECONDS = 5L
    }

    private val async: RedisClusterAsyncCommands<K, V> get() = connection.async()

    private suspend fun remoteValue(): V? = async.get(key).await()

    private suspend fun tryLockAndAwait(): Boolean {
        return async.set(key, value, setArgs).await()?.let {
            async.get(key).await().let { isValueEqual(it) }
        } ?: false
    }

    private suspend fun tryLockAndAwait(maxWaitMillis: Long, eachWaitMillis: Long): Boolean {
        var now = System.currentTimeMillis()
        var r = tryLockAndAwait()
        if (!r) {
            val deadline = now + maxWaitMillis
            while (!r) {
                now = System.currentTimeMillis()
                if (deadline <= now) {
                    break
                }
                val nextWaitMillis = min(eachWaitMillis, deadline - now)
                if (nextWaitMillis > 0) {
                    delay(nextWaitMillis)
                }
                r = tryLockAndAwait()
            }
        }
        return r
    }

    override suspend fun <R> tryInLockAndAwait(
        keeperContext: CoroutineContext,
        action: suspend () -> R,
    ): Pair<Boolean, R?> =
        if (tryLockAndAwait()) {
            action.invokeAndKeepAlive(keeperContext).let { true to it }
        } else {
            false to null
        }

    private suspend fun <R> (suspend () -> R).invokeAndKeepAlive(
        keeperContext: CoroutineContext,
    ): R = coroutineScope {
        val inLock = AtomicBoolean(true)
        val keeper = launch(keeperContext) {
            val intervalMillis = keepAliveIntervalMillis
            do {
                delay(intervalMillis)
                keepAliveAsync()
            } while (inLock.get() && isActive)
        }
        async {
            try {
                invoke()
            } finally {
                inLock.set(false)
                keeper.cancel()
                unlockAsync()
            }
        }.await()
    }

    override suspend fun <R, T : Throwable> tryInLockAndAwait(
        keeperContext: CoroutineContext,
        notAcquiredSupplier: () -> T,
        action: suspend () -> R
    ): R {
        if (!tryLockAndAwait()) {
            throw notAcquiredSupplier()
        }
        return action.invokeAndKeepAlive(keeperContext)
    }

    override suspend fun <R> runInLockAndAwait(
        maxWaitMillis: Long,
        eachWaitMillis: Long,
        keeperContext: CoroutineContext,
        action: suspend () -> R,
    ): Pair<Boolean, R?> =
        if (tryLockAndAwait(maxWaitMillis, eachWaitMillis)) {
            action.invokeAndKeepAlive(keeperContext).let { true to it }
        } else {
            false to null
        }

    override suspend fun <R, T : Throwable> runInLockAndAwait(
        maxWaitMillis: Long,
        eachWaitMillis: Long,
        keeperContext: CoroutineContext,
        notAcquiredSupplier: () -> T,
        action: suspend () -> R
    ): R {
        if (!tryLock(maxWaitMillis, eachWaitMillis)) {
            throw notAcquiredSupplier()
        }
        return action.invokeAndKeepAlive(keeperContext)
    }

}