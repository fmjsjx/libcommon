package com.github.fmjsjx.libcommon.redis.locks

import com.github.fmjsjx.libcommon.redis.locks.AbstractRedisRemoteLock.DEFAULT_EACH_WAIT
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A coroutine-based remote lock based on Redis.
 *
 * @param K the type of the key
 * @param V the type of the value
 * @author MJ Fang
 * @see RedisRemoteLock
 * @see KeepAliveRedisCoroutineLock
 * @since 4.1
 */
interface RedisCoroutineLock<K : Any, V : Any> : RedisRemoteLock<K, V> {

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param keeperContext additional to [CoroutineScope.coroutineContext] context of the keep alive coroutine
     * @param action the action to execute when the lock is acquired
     * @param R the type of the action result
     * @return the [Pair] with if the lock is acquired and the result of the action
     */
    suspend fun <R> tryInLockAndAwait(
        keeperContext: CoroutineContext = EmptyCoroutineContext,
        action: suspend () -> R,
    ): Pair<Boolean, R?>

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param keeperContext additional to [CoroutineScope.coroutineContext] context of the keep alive coroutine
     * @param notAcquiredSupplier the supplier of the exception when the lock is not acquired
     * @param action the action to execute when the lock is acquired
     * @param T the type of the exception when the lock is not acquired
     * @param R the type of the action result
     * @return the result of the action
     * @throws T the exception when the lock is not acquired
     */
    suspend fun <R, T : Throwable> tryInLockAndAwait(
        keeperContext: CoroutineContext = EmptyCoroutineContext,
        notAcquiredSupplier: () -> T,
        action: suspend () -> R,
    ): R

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param maxWaitMillis the maximum wait time in milliseconds
     * @param eachWaitMillis the wait time between each try
     * @param keeperContext additional to [CoroutineScope.coroutineContext] context of the keep alive coroutine
     * @param action the action to execute when the lock is acquired
     * @param R the type of the action result
     * @return the [Pair] with if the lock is acquired and the result of the action
     */
    suspend fun <R> runInLockAndAwait(
        maxWaitMillis: Long = timeoutSeconds * 1000,
        eachWaitMillis: Long = DEFAULT_EACH_WAIT,
        keeperContext: CoroutineContext = EmptyCoroutineContext,
        action: suspend () -> R,
    ): Pair<Boolean, R?>

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param maxWaitMillis the maximum wait time in milliseconds
     * @param eachWaitMillis the wait time between each try
     * @param keeperContext additional to [CoroutineScope.coroutineContext] context of the keep alive coroutine
     * @param notAcquiredSupplier the supplier of the exception when the lock is not acquired
     * @param action the action to execute when the lock is acquired
     * @param T the type of the exception when the lock is not acquired
     * @param R the type of the action result
     * @return the result of the action
     * @throws T the exception when the lock is not acquired
     */
    suspend fun <R, T : Throwable> runInLockAndAwait(
        maxWaitMillis: Long = timeoutSeconds * 1000,
        eachWaitMillis: Long = DEFAULT_EACH_WAIT,
        keeperContext: CoroutineContext = EmptyCoroutineContext,
        notAcquiredSupplier: () -> T,
        action: suspend () -> R,
    ): R

}