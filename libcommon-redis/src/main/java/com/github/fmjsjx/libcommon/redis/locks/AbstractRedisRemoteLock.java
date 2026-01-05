package com.github.fmjsjx.libcommon.redis.locks;

import com.github.fmjsjx.libcommon.redis.RedisUtil;
import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter;
import com.github.fmjsjx.libcommon.util.ExecutorUtil;
import io.lettuce.core.SetArgs;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract implementation of {@link RedisRemoteLock}.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 * @author MJ Fang
 * @see DefaultRedisRemoteLock
 * @see RedisRemoteLock
 * @since 4.1
 */
abstract class AbstractRedisRemoteLock<K, V> implements RedisRemoteLock<K, V> {

    /**
     * The default value of the milliseconds to wait for each loop.
     */
    protected static final long DEFAULT_EACH_WAIT = 200;

    /**
     * The minimum value of the milliseconds to wait for each loop.
     */
    protected static final long MIN_EACH_WAIT = 5;

    private final RedisConnectionAdapter<K, V> connection;
    private final K key;
    private final V value;
    private final boolean valueIsByteArray;
    private final long timeoutSeconds;
    private SetArgs setArgs;

    /**
     * Constructs a new {@link AbstractRedisRemoteLock} with the specified
     * parameters.
     *
     * @param connection     the connection to Redis
     * @param key            the key
     * @param value          the value
     * @param timeoutSeconds the timeout seconds
     */
    protected AbstractRedisRemoteLock(RedisConnectionAdapter<K, V> connection, K key, V value, long timeoutSeconds) {
        this.connection = Objects.requireNonNull(connection, "connection must not be null");
        this.key = Objects.requireNonNull(key, "key must not be null");
        this.value = Objects.requireNonNull(value, "value must not be null");
        this.valueIsByteArray = value instanceof byte[];
        this.timeoutSeconds = Math.max(1, timeoutSeconds);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    @Override
    public <R> Optional<R> runInLock(long maxWaitMillis, Supplier<R> action) throws InterruptedException {
        return runInLock(maxWaitMillis, DEFAULT_EACH_WAIT, action);
    }

    @Override
    public <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, Supplier<CompletionStage<R>> asyncAction) {
        return runInLockAsync(maxWaitMillis, DEFAULT_EACH_WAIT, asyncAction);
    }

    @Override
    public <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, Supplier<CompletionStage<R>> asyncAction, Executor executor) {
        return runInLockAsync(maxWaitMillis, DEFAULT_EACH_WAIT, asyncAction, executor);
    }

    /**
     * Gets the connection to Redis.
     *
     * @return a {@link RedisConnectionAdapter}
     */
    protected RedisConnectionAdapter<K, V> getConnection() {
        return connection;
    }

    /**
     * Returns whether the value of this lock is a byte array.
     *
     * @return {@code true} if the value of this lock is a byte array,
     * {@code false} otherwise
     */
    protected boolean isValueIsByteArray() {
        return valueIsByteArray;
    }

    /**
     * Gets the {@link SetArgs} for the {@code SET} command.
     *
     * @return a {@link SetArgs}
     */
    protected SetArgs getSetArgs() {
        var setArgs = this.setArgs;
        if (setArgs == null) {
            this.setArgs = setArgs = new SetArgs().nx().ex(timeoutSeconds);
        }
        return setArgs;
    }

    /**
     * Checks whether the specified value is equal to the value of this
     * lock.
     *
     * @param v the value to check
     * @return {@code true} if the specified value is equal to the value
     * of this lock, {@code false} otherwise
     */
    protected boolean isValueEqual(V v) {
        if (v == null) {
            return false;
        }
        if (isValueIsByteArray()) {
            return Arrays.equals((byte[]) value, (byte[]) v);
        }
        return value.equals(v);
    }

    /**
     * Tries to lock this lock.
     *
     * @return {@code true} if the lock is successfully locked, {@code false}
     * otherwise
     */
    protected boolean tryLock() {
        var ok = getConnection().sync().set(key, value, getSetArgs());
        if (ok != null) {
            return isValueEqual(getConnection().sync().get(key));
        }
        return false;
    }

    /**
     * Tries to lock this lock.
     *
     * @param maxWaitMillis  the maximum milliseconds to wait
     * @param eachWaitMillis the milliseconds to wait for each loop
     * @return {@code true} if the lock is successfully locked,
     * {@code false} otherwise
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting
     */
    @SuppressWarnings("BusyWait")
    protected boolean tryLock(long maxWaitMillis, long eachWaitMillis) throws InterruptedException {
        var now = System.currentTimeMillis();
        var r = tryLock();
        if (!r) {
            var deadline = now + maxWaitMillis;
            var eachWait = Math.max(MIN_EACH_WAIT, eachWaitMillis);
            for (; ; ) {
                now = System.currentTimeMillis();
                if (deadline <= now) {
                    break;
                }
                var nextWaitMillis = Math.min(eachWait, deadline - now);
                if (nextWaitMillis > 0) {
                    Thread.sleep(nextWaitMillis);
                }
                r = tryLock();
                if (r) {
                    break;
                }
            }
        }
        return r;
    }

    /**
     * Runs the specified action in this lock and unlock this lock.
     *
     * @param <R>    the type of the result
     * @param action the action to run
     * @return the result of the action
     */
    protected <R> Optional<R> runAndUnlock(Supplier<R> action) {
        try {
            return Optional.ofNullable(action.get());
        } finally {
            unlockAsync();
        }
    }

    /**
     * Tries to lock this lock asynchronously.
     *
     * @return a {@code CompletionStage<Boolean>}
     */
    protected CompletionStage<Boolean> tryLockAsync() {
        return getConnection().async().set(key, value, getSetArgs()).thenCompose(ok -> {
            if (ok != null) {
                return getConnection().async().get(key).thenApply(this::isValueEqual);
            }
            return CompletableFuture.completedStage(false);
        });
    }

    /**
     * Tries to lock this lock asynchronously.
     *
     * @param executor the executor to use
     * @return a {@code CompletionStage<Boolean>}
     */
    protected CompletionStage<Boolean> tryLockAsync(Executor executor) {
        return getConnection().async().set(key, value, getSetArgs()).thenComposeAsync(ok -> {
            if (ok != null) {
                return getConnection().async().get(key).thenApply(this::isValueEqual);
            }
            return CompletableFuture.completedStage(false);
        }, executor);
    }

    /**
     * Tries to lock this lock asynchronously.
     *
     * @param maxWaitMillis  the maximum milliseconds to wait
     * @param eachWaitMillis the milliseconds to wait for each loop
     * @return a {@code CompletionStage<Boolean>}
     */
    protected CompletionStage<Boolean> tryLockAsync(long maxWaitMillis, long eachWaitMillis) {
        return new TryLockAsyncAction(System.currentTimeMillis() + maxWaitMillis, eachWaitMillis, null).doNext();
    }

    /**
     * Tries to lock this lock asynchronously.
     *
     * @param maxWaitMillis  the maximum milliseconds to wait
     * @param eachWaitMillis the milliseconds to wait for each loop
     * @param executor       the executor to use
     * @return a {@code CompletionStage<Boolean>}
     */
    protected CompletionStage<Boolean> tryLockAsync(long maxWaitMillis, long eachWaitMillis, Executor executor) {
        return new TryLockAsyncAction(System.currentTimeMillis() + maxWaitMillis, eachWaitMillis, executor).doNext();
    }

    /**
     * The action class to try to lock this lock asynchronously.
     */
    protected class TryLockAsyncAction {

        /**
         * The deadline.
         */
        protected final long deadline;
        /**
         * The milliseconds to wait for each loop.
         */
        protected final long eachWaitMillis;
        /**
         * The executor to use, can be {@code null}.
         */
        protected final Executor executor;

        /**
         * Constructs a new {@code TryLockAsyncAction} instance.
         *
         * @param deadline       the deadline
         * @param eachWaitMillis the milliseconds to wait for each loop
         * @param executor       the executor to use, can be {@code null}
         */
        protected TryLockAsyncAction(long deadline, long eachWaitMillis, Executor executor) {
            this.deadline = deadline;
            this.eachWaitMillis = Math.max(MIN_EACH_WAIT, eachWaitMillis);
            this.executor = executor;
        }

        /**
         * Checks whether the deadline is expired.
         *
         * @param now the current time
         * @return {@code true} if the deadline is expired, {@code false} otherwise
         */
        protected boolean isExpired(long now) {
            return deadline <= now;
        }

        /**
         * Calculates the milliseconds to wait for the next loop.
         *
         * @param now the current time
         * @return the milliseconds to wait for the next loop
         */
        protected long nextWaitMillis(long now) {
            return Math.min(eachWaitMillis, deadline - now);
        }

        /**
         * Does the next try to lock this lock asynchronously.
         *
         * @return a {@code CompletionStage<Boolean>}
         */
        protected CompletionStage<Boolean> doNext() {
            var executor = this.executor;
            if (executor == null) {
                return tryLockAsync().thenCompose(this::onTryLockResult);
            } else {
                return tryLockAsync(executor).thenComposeAsync(this::onTryLockResult, executor);
            }
        }

        /**
         * Called when the try to lock this lock asynchronously is completed.
         *
         * @param success {@code true} if the try to lock this lock is successful,
         *                {@code false} otherwise
         * @return a {@code CompletionStage<Boolean>}
         */
        protected CompletionStage<Boolean> onTryLockResult(Boolean success) {
            if (success) {
                return CompletableFuture.completedStage(Boolean.TRUE);
            }
            var now = System.currentTimeMillis();
            if (isExpired(now)) {
                return CompletableFuture.completedStage(Boolean.FALSE);
            }
            var delayMillis = nextWaitMillis(now);
            var executor = this.executor;
            var delayedExecutor = switch (executor) {
                case ScheduledExecutorService scheduledExecutor ->
                        ExecutorUtil.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS, scheduledExecutor);
                case null, default -> CompletableFuture.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS);
            };
            return CompletableFuture.supplyAsync(this::doNext, delayedExecutor).thenCompose(Function.identity());
        }

    }

    /**
     * Unlocks this lock asynchronously.
     *
     * @return a {@code CompletionStage<Boolean>}
     */
    protected CompletionStage<Boolean> unlockAsync() {
        return RedisUtil.unlock(getConnection().async(), key, value);
    }

    /**
     * Runs the given action and unlock this lock asynchronously.
     *
     * @param asyncAction the action to run
     * @param <R>         the type of the action result
     * @return a {@code CompletionStage<Optional<R>>}
     */
    protected <R> CompletionStage<Optional<R>> runAndUnlockAsync(Supplier<CompletionStage<R>> asyncAction) {
        return asyncAction.get()
                .whenComplete((r, e) -> unlockAsync())
                .thenApply(Optional::ofNullable);
    }

    /**
     * Runs the given action and unlock this lock asynchronously.
     *
     * @param asyncAction the action to run
     * @param executor    the executor to use
     * @param <R>         the type of the action result
     * @return a {@code CompletionStage<Optional<R>>}
     */
    protected <R> CompletionStage<Optional<R>> runAndUnlockAsync(Supplier<CompletionStage<R>> asyncAction,
                                                                 Executor executor) {
        return asyncAction.get()
                .whenCompleteAsync((v, e) -> unlockAsync(), executor)
                .thenApply(Optional::ofNullable);
    }

}
