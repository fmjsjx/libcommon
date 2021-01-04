package com.github.fmjsjx.libcommon.redis;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.github.fmjsjx.libcommon.util.ArrayUtil;
import com.github.fmjsjx.libcommon.util.ExecutorUtil;

import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Utility class for REDIS (based on lettuce).
 */
public class RedisUtil {

    private static final long DEFAULT_EACH_WAIT = 200;

    /**
     * Converts the input values to a hash table (actually is
     * {@link LinkedHashMap}).
     * 
     * @param <K>    the key type
     * @param <V>    the value type
     * @param values the input values, size must be even
     * @return a hash table
     */
    @SuppressWarnings("unchecked")
    public static final <K, V> Map<K, V> toHash(List<Object> values) {
        if (values == null) {
            return null;
        }
        var size = values.size();
        if (size == 0) {
            return Collections.emptyMap();
        }
        if ((size & 1) == 1) {
            // is odd
            throw new IllegalArgumentException("the size of values must be even, actually was " + size);
        }
        var hash = new LinkedHashMap<>();
        var keys = size / 2;
        for (int i = 0; i < keys; i++) {
            var index = i << 1;
            var key = values.get(index);
            var value = values.get(index + 1);
            hash.put(key, value);
        }
        return (Map<K, V>) hash;
    }

    /**
     * Evaluates a LUA script on server side.
     * 
     * @param <K>    the key type
     * @param <V>    the value type
     * @param <R>    the return type
     * @param redis  the synchronous REDIS API
     * @param script the LUA script
     * @param keys   key names
     * @return the script result
     */
    @SuppressWarnings("unchecked")
    public static final <K, V, R> R eval(RedisCommands<K, V> redis, LuaScript<R> script, K... keys) {
        try {
            return redis.evalsha(script.sha1(), script.outputType(), keys);
        } catch (RedisNoScriptException e) {
            return redis.eval(script.script(), script.outputType(), keys);
        }
    }

    /**
     * Evaluates a LUA script on server side.
     * 
     * @param <K>    the key type
     * @param <V>    the value type
     * @param <R>    the return type
     * @param redis  the synchronous REDIS API
     * @param script the LUA script
     * @param keys   key names
     * @param values the values
     * @return the script result
     */
    @SuppressWarnings("unchecked")
    public static final <K, V, R> R eval(RedisCommands<K, V> redis, LuaScript<R> script, K[] keys, V... values) {
        try {
            return redis.evalsha(script.sha1(), script.outputType(), keys, values);
        } catch (RedisNoScriptException e) {
            return redis.eval(script.script(), script.outputType(), keys, values);
        }
    }

    /**
     * Evaluates a LUA script on server side.
     * 
     * @param <K>    the key type
     * @param <V>    the value type
     * @param <R>    the return type
     * @param redis  the asynchronous REDIS API
     * @param script the LUA script
     * @param keys   key names
     * @return the script result
     */
    @SuppressWarnings("unchecked")
    public static final <K, V, R> CompletionStage<R> eval(RedisAsyncCommands<K, V> redis, LuaScript<R> script,
            K... keys) {
        return redis.<R>evalsha(script.sha1(), script.outputType(), keys).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return redis.<R>eval(script.script(), script.outputType(), keys);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(Function.identity());
    }

    /**
     * Evaluates a LUA script on server side.
     * 
     * @param <K>    the key type
     * @param <V>    the value type
     * @param <R>    the return type
     * @param redis  the asynchronous REDIS API
     * @param script the LUA script
     * @param keys   key names
     * @param values the values
     * @return the script result
     */
    @SuppressWarnings("unchecked")
    public static final <K, V, R> CompletionStage<R> eval(RedisAsyncCommands<K, V> redis, LuaScript<R> script, K[] keys,
            V... values) {
        return redis.<R>evalsha(script.sha1(), script.outputType(), keys, values).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return redis.<R>eval(script.script(), script.outputType(), keys, values);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(Function.identity());
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>     the key type
     * @param <V>     the value type
     * @param redis   the synchronous REDIS API
     * @param key     the key name
     * @param value   the value
     * @param timeout the timeout in seconds
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> boolean tryLock(RedisCommands<K, V> redis, K key, V value, long timeout) {
        var ok = redis.set(key, value, new SetArgs().nx().ex(timeout));
        return ok != null;
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>     the key type
     * @param <V>     the value type
     * @param redis   the synchronous REDIS API
     * @param key     the key name
     * @param value   the value
     * @param timeout the timeout in seconds
     * @param maxWait the maximum milliseconds to wait for the lock
     * @return {@code true} if the operation is success, {@code false} otherwise
     * @throws InterruptedException if any thread has interrupted the current thread
     */
    public static final <K, V> boolean tryLock(RedisCommands<K, V> redis, K key, V value, long timeout, long maxWait)
            throws InterruptedException {
        return tryLock(redis, key, value, timeout, maxWait, DEFAULT_EACH_WAIT);
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>      the key type
     * @param <V>      the value type
     * @param redis    the synchronous REDIS API
     * @param key      the key name
     * @param value    the value
     * @param timeout  the timeout in seconds
     * @param maxWait  the maximum milliseconds to wait for the lock
     * @param eachWait the milliseconds to wait for each loop
     * @return {@code true} if the operation is success, {@code false} otherwise
     * @throws InterruptedException if any thread has interrupted the current thread
     */
    public static final <K, V> boolean tryLock(RedisCommands<K, V> redis, K key, V value, long timeout, long maxWait,
            long eachWait) throws InterruptedException {
        var now = System.currentTimeMillis();
        var r = tryLock(redis, key, value, timeout);
        if (!r) {
            var end = now + maxWait;
            for (;;) {
                now = System.currentTimeMillis();
                var millis = Math.min(end - now, eachWait);
                if (millis <= 0) {
                    break;
                }
                Thread.sleep(eachWait);
                r = tryLock(redis, key, value, timeout);
                if (r) {
                    break;
                }
            }
        }
        return r;
    }

    /**
     * Releases the lock.
     * 
     * @param <K>   the key type
     * @param <V>   the value type
     * @param redis the synchronous REDIS API
     * @param key   the key name
     * @param value the value
     * @return {@code true} if the lock not timeout and be released successfully,
     *         {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    public static final <K, V> boolean unlock(RedisCommands<K, V> redis, K key, V value) {
        K[] keys = ArrayUtil.self(key);
        return eval(redis, LuaScripts.DEL_IF_VALUE_EQUALS, keys, value);
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>     the key type
     * @param <V>     the value type
     * @param redis   the asynchronous REDIS API
     * @param key     the key name
     * @param value   the value
     * @param timeout the timeout in seconds
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> CompletionStage<Boolean> tryLock(RedisAsyncCommands<K, V> redis, K key, V value,
            long timeout) {
        return redis.set(key, value, new SetArgs().nx().ex(timeout)).thenApply(Objects::nonNull);
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>     the key type
     * @param <V>     the value type
     * @param redis   the asynchronous REDIS API
     * @param key     the key name
     * @param value   the value
     * @param timeout the timeout in seconds
     * @param maxWait the maximum milliseconds to wait for the lock
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> CompletionStage<Boolean> tryLock(RedisAsyncCommands<K, V> redis, K key, V value,
            long timeout, long maxWait) {
        return tryLock(redis, key, value, timeout, maxWait, DEFAULT_EACH_WAIT);
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>      the key type
     * @param <V>      the value type
     * @param redis    the asynchronous REDIS API
     * @param key      the key name
     * @param value    the value
     * @param timeout  the timeout in seconds
     * @param maxWait  the maximum milliseconds to wait for the lock
     * @param eachWait the milliseconds to wait for each loop
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> CompletionStage<Boolean> tryLock(RedisAsyncCommands<K, V> redis, K key, V value,
            long timeout, long maxWait, long eachWait) {
        return tryLock(redis, key, value, timeout).thenCompose(success -> {
            if (success) {
                return CompletableFuture.completedStage(success);
            }
            if (maxWait <= 0) {
                return CompletableFuture.completedStage(Boolean.FALSE);
            }
            var delayMillis = Math.min(eachWait, maxWait);
            var delayedExecutor = CompletableFuture.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS);
            var remainingWait = maxWait - eachWait;
            return CompletableFuture.supplyAsync(() -> {
                return tryLock(redis, key, value, timeout, remainingWait, eachWait);
            }, delayedExecutor).thenCompose(Function.identity());
        });
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>      the key type
     * @param <V>      the value type
     * @param redis    the asynchronous REDIS API
     * @param key      the key name
     * @param value    the value
     * @param timeout  the timeout in seconds
     * @param maxWait  the maximum milliseconds to wait for the lock
     * @param executor the base executor
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> CompletionStage<Boolean> tryLock(RedisAsyncCommands<K, V> redis, K key, V value,
            long timeout, long maxWait, Executor executor) {
        return tryLock(redis, key, value, timeout, maxWait, DEFAULT_EACH_WAIT, executor);
    }

    /**
     * Try to lock the specified key with the specified value.
     * 
     * @param <K>      the key type
     * @param <V>      the value type
     * @param redis    the asynchronous REDIS API
     * @param key      the key name
     * @param value    the value
     * @param timeout  the timeout in seconds
     * @param maxWait  the maximum milliseconds to wait for the lock
     * @param eachWait the milliseconds to wait for each loop
     * @param executor the base executor
     * @return {@code true} if the operation is success, {@code false} otherwise
     */
    public static final <K, V> CompletionStage<Boolean> tryLock(RedisAsyncCommands<K, V> redis, K key, V value,
            long timeout, long maxWait, long eachWait, Executor executor) {
        return tryLock(redis, key, value, timeout).thenComposeAsync(success -> {
            if (success) {
                return CompletableFuture.completedStage(success);
            }
            if (maxWait <= 0) {
                return CompletableFuture.completedStage(Boolean.FALSE);
            }
            var delayMillis = Math.min(eachWait, maxWait);
            var delayedExecutor = delayedExecutor(delayMillis, executor);
            var remainingWait = maxWait - eachWait;
            return CompletableFuture.supplyAsync(() -> {
                return tryLock(redis, key, value, timeout, remainingWait, eachWait, executor);
            }, delayedExecutor).thenCompose(Function.identity());
        }, executor);
    }

    private static final Executor delayedExecutor(long delayMillis, Executor executor) {
        if (executor instanceof ScheduledExecutorService) {
            return ExecutorUtil.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS,
                    (ScheduledExecutorService) executor);
        }
        return CompletableFuture.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS, executor);
    }

    /**
     * Releases the lock.
     * 
     * @param <K>   the key type
     * @param <V>   the value type
     * @param redis the asynchronous REDIS API
     * @param key   the key name
     * @param value the value
     * @return {@code true} if the lock not timeout and be released successfully,
     *         {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    public static final <K, V> CompletionStage<Boolean> unlock(RedisAsyncCommands<K, V> redis, K key, V value) {
        K[] keys = ArrayUtil.self(key);
        return eval(redis, LuaScripts.DEL_IF_VALUE_EQUALS, keys, value);
    }

    private RedisUtil() {
    }

}
