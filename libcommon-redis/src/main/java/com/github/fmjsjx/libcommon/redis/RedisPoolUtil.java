package com.github.fmjsjx.libcommon.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;

/**
 * Utility class for REDIS pool.
 */
public class RedisPoolUtil {

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then apply the
     * specified {@code action}. The acquired connection will be released back to
     * the {@code pool} automatically.
     * 
     * @param <K>    the key type of the connection
     * @param <V>    the value type of the connection
     * @param <R>    the return type
     * @param pool   a non-blocking pool to acquires REDIS connection
     * @param action the action
     * @return a {@code CompletableFuture<R>}
     */
    public static final <K, V, R> CompletableFuture<R> apply(AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, ? extends CompletionStage<R>> action) {
        return pool.acquire().thenCompose(autoRelease(pool, action));
    }

    /**
     * Converts the specified {@code action} to an {@code auto-release action} with
     * the specified {@link AsyncPool}.
     * 
     * @param <K>    the key type of the connection
     * @param <V>    the value type of the connection
     * @param <R>    the return type
     * @param pool   a non-blocking pool to acquires REDIS connection
     * @param action the action
     * @return an {@code auto-release action} with the specified {@link AsyncPool}
     */
    public static final <K, V, R> Function<? super StatefulRedisConnection<K, V>, ? extends CompletionStage<R>> autoRelease(
            AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, ? extends CompletionStage<R>> action) {
        return conn -> action.apply(conn).whenComplete((r, e) -> pool.release(conn));
    }

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then apply the
     * specified {@code action} asynchronously. The acquired connection will be
     * released back to the {@code pool} automatically.
     * 
     * @param <K>    the key type of the connection
     * @param <V>    the value type of the connection
     * @param <R>    the return type
     * @param pool   a non-blocking pool to acquires REDIS connection
     * @param action the action
     * @return a {@code CompletableFuture<R>}
     */
    public static final <K, V, R> CompletableFuture<R> applyAsync(AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, ? extends CompletionStage<R>> action) {
        return pool.acquire().thenComposeAsync(autoRelease(pool, action));
    }

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then apply the
     * specified {@code action} asynchronously. The acquired connection will be
     * released back to the {@code pool} automatically.
     * 
     * @param <K>      the key type of the connection
     * @param <V>      the value type of the connection
     * @param <R>      the return type
     * @param pool     a non-blocking pool to acquires REDIS connection
     * @param action   the action
     * @param executor the executor to use for asynchronous execution
     * @return a {@code CompletableFuture<R>}
     */
    public static final <K, V, R> CompletableFuture<R> applyAsync(AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, ? extends CompletionStage<R>> action, Executor executor) {
        return pool.acquire().thenComposeAsync(autoRelease(pool, action), executor);
    }

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then execute
     * the specified {@code action}. The acquired connection will be released back
     * to the {@code pool} automatically.
     * 
     * @param <K>    the key type of the connection
     * @param <V>    the value type of the connection
     * @param pool   a non-blocking pool to acquires REDIS connection
     * @param action the action
     * @return a {@code CompletableFuture<Void>}
     */
    public static final <K, V> CompletableFuture<Void> accept(AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, CompletionStage<Void>> action) {
        return pool.acquire().thenCompose(autoRelease(pool, action));
    }

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then execute
     * the specified {@code action} asynchronously. The acquired connection will be
     * released back to the {@code pool} automatically.
     * 
     * @param <K>    the key type of the connection
     * @param <V>    the value type of the connection
     * @param pool   a non-blocking pool to acquires REDIS connection
     * @param action the action
     * @return a {@code CompletableFuture<Void>}
     */
    public static final <K, V> CompletableFuture<Void> acceptAsync(
            AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, CompletionStage<Void>> action) {
        return pool.acquire().thenComposeAsync(autoRelease(pool, action));
    }

    /**
     * Acquire a connection from the specified {@link AsyncPool} and then execute
     * the specified {@code action} asynchronously. The acquired connection will be
     * released back to the {@code pool} automatically.
     * 
     * @param <K>      the key type of the connection
     * @param <V>      the value type of the connection
     * @param pool     a non-blocking pool to acquires REDIS connection
     * @param action   the action
     * @param executor the executor to use for asynchronous execution
     * @return a {@code CompletableFuture<Void>}
     */
    public static final <K, V> CompletableFuture<Void> acceptAsync(
            AsyncPool<? super StatefulRedisConnection<K, V>> pool,
            Function<? super StatefulRedisConnection<K, V>, CompletionStage<Void>> action, Executor executor) {
        return pool.acquire().thenComposeAsync(autoRelease(pool, action), executor);
    }

    private RedisPoolUtil() {
    }

}
