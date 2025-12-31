package com.github.fmjsjx.libcommon.redis.locks;

import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Default implementation of {@link RedisRemoteLock}.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 * @author MJ Fang
 * @see RedisRemoteLock
 * @see KeepAliveRedisRemoteLock
 * @since 4.1
 */
public class DefaultRedisRemoteLock<K, V> extends AbstractRedisRemoteLock<K, V> {

    /**
     * Constructs a new {@link DefaultRedisRemoteLock} with the specified
     * parameters.
     *
     * @param connection     the connection to Redis
     * @param key            the key
     * @param value          the value
     * @param timeoutSeconds the timeout seconds
     */
    public DefaultRedisRemoteLock(RedisConnectionAdapter<K, V> connection, K key, V value, long timeoutSeconds) {
        super(connection, key, value, timeoutSeconds);
    }

    @SuppressWarnings("OptionalAssignedToNull")
    @Override
    public <R> Optional<R> tryInLock(Supplier<R> action) {
        return tryLock() ? runAndUnlock(action) : null;
    }

    @SuppressWarnings("OptionalAssignedToNull")
    @Override
    public <R> Optional<R> runInLock(long maxWaitMillis, long eachWaitMillis, Supplier<R> action) throws InterruptedException {
        return tryLock(maxWaitMillis, eachWaitMillis) ? runAndUnlock(action) : null;
    }

    @Override
    public <R> CompletionStage<Optional<R>> tryInLockAsync(Supplier<CompletionStage<R>> asyncAction) {
        return tryLockAsync().thenCompose(success ->
                success ? runAndUnlockAsync(asyncAction) : CompletableFuture.completedStage(null));
    }

    @Override
    public <R> CompletionStage<Optional<R>> tryInLockAsync(Supplier<CompletionStage<R>> asyncAction,
                                                           Executor executor) {
        return tryLockAsync(executor).thenComposeAsync(success ->
                        success ? runAndUnlockAsync(asyncAction, executor)
                                : CompletableFuture.completedStage(null),
                executor);
    }

    @Override
    public <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, long eachWaitMillis,
                                                           Supplier<CompletionStage<R>> asyncAction) {
        return tryLockAsync(maxWaitMillis, eachWaitMillis).thenCompose(success ->
                success ? runAndUnlockAsync(asyncAction) : CompletableFuture.completedStage(null));
    }

    @Override
    public <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, long eachWaitMillis,
                                                           Supplier<CompletionStage<R>> asyncAction,
                                                           Executor executor) {
        return tryLockAsync(maxWaitMillis, eachWaitMillis, executor).thenComposeAsync(success ->
                        success ? runAndUnlockAsync(asyncAction, executor) : CompletableFuture.completedStage(null),
                executor);
    }

}
