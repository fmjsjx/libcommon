package com.github.fmjsjx.libcommon.redis

import io.lettuce.core.RedisNoScriptException
import io.lettuce.core.SetArgs
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.async.RedisScriptingAsyncCommands
import io.lettuce.core.api.async.RedisStringAsyncCommands
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletionStage
import kotlin.math.min

/**
 * Try to lock the specified key with the specified value.
 *
 * @author MJ Fang
 * @since 3.15
 */
suspend fun <K, V, C : RedisStringAsyncCommands<K, V>> C.tryLock(
    key: K,
    value: V,
    timeout: Long,
    maxWait: Long,
    eachWait: Long = 200
): Boolean {
    val setArgs = SetArgs().nx().ex(timeout)
    val deadline = System.currentTimeMillis() + maxWait
    while (set(key, value, setArgs).await() == null) {
        val now = System.currentTimeMillis()
        if (now >= deadline) {
            return false
        }
        delay(min(eachWait, deadline - now))
    }
    return true
}

/**
 * Extension for [RedisUtil.unlock].
 *
 * @author MJ Fang
 * @since 3.15
 */
fun <K, V, C : RedisScriptingAsyncCommands<K, V>> C.unlockAsync(key: K, value: V): CompletionStage<Boolean> =
    RedisUtil.unlock(this, key, value)

/**
 * Extension to release the lock.
 *
 * @author MJ Fang
 * @since 3.15
 */
suspend fun <K, V, C : RedisScriptingAsyncCommands<K, V>> C.unlock(key: K, value: V): Boolean =
    unlockAsync(key, value).await()

/**
 * Extension to try to create a [RedisDistributedLock] by the params given.
 *
 * @author MJ Fang
 * @since 3.15
 */
suspend inline fun <K, V, C : RedisAsyncCommands<K, V>> C.tryDistributedLock(
    key: K,
    timeout: Long = 5,
    maxWait: Long = 10_000,
    eachWait: Long = 200,
    valueSupplier: () -> V,
): RedisDistributedLock<K, V>? {
    val value = valueSupplier.invoke()
    if (tryLock(key, value, timeout, maxWait, eachWait)) {
        return RedisDistributedLock(key, value, this::unlockAsync)
    }
    return null
}

/**
 * Extension to try to create a [RedisDistributedLock] by the params given.
 *
 * @author MJ Fang
 * @since 3.15
 */
suspend inline fun <K, V, C : RedisClusterAsyncCommands<K, V>> C.tryDistributedLock(
    key: K,
    timeout: Long = 5,
    maxWait: Long = 10_000,
    eachWait: Long = 200,
    valueSupplier: () -> V,
): RedisDistributedLock<K, V>? {
    val value = valueSupplier.invoke()
    if (tryLock(key, value, timeout, maxWait, eachWait)) {
        return RedisDistributedLock(key, value, this::unlockAsync)
    }
    return null
}

/**
 * Extension for [RedisUtil.eval].
 *
 * @author MJ Fang
 * @since 3.15
 */
fun <K, V, C : RedisScriptingAsyncCommands<K, V>, R> C.evalAsync(
    script: LuaScript<R>,
    vararg keys: K,
): CompletionStage<R> =
    RedisUtil.eval(this, script, *keys)

/**
 * Extension for [RedisUtil.eval].
 *
 * @author MJ Fang
 * @since 3.15
 */
fun <K, V, C : RedisScriptingAsyncCommands<K, V>, R> C.evalAsync(
    script: LuaScript<R>,
    keys: Array<K>,
    vararg values: V,
): CompletionStage<R> =
    RedisUtil.eval(this, script, keys, *values)

/**
 * Evaluates a LUA script on server side.
 *
 * @param script the LUA script
 * @param keys the key names
 * @return the script result.
 * @author MJ Fang
 * @since 3.15
 */
suspend fun <K, V, C : RedisScriptingAsyncCommands<K, V>, R> C.eval(
    script: LuaScript<R>,
    vararg keys: K,
): R = try {
    evalsha<R>(script.sha1(), script.outputType(), *keys).await()
} catch (_: RedisNoScriptException) {
    eval<R>(script.script(), script.outputType(), *keys).await()
}

/**
 * Evaluates a LUA script on server side.
 *
 * @param script the LUA script
 * @param keys the key names
 * @param values the values
 * @return the script result.
 * @author MJ Fang
 * @since 3.15
 */
suspend fun <K, V, C : RedisScriptingAsyncCommands<K, V>, R> C.eval(
    script: LuaScript<R>,
    keys: Array<K>,
    vararg values: V,
): R = try {
    evalsha<R>(script.sha1(), script.outputType(), keys, *values).await()
} catch (_: RedisNoScriptException) {
    eval<R>(script.script(), script.outputType(), keys, *values).await()
}