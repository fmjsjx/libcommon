package com.github.fmjsjx.libcommon.redis.locks;

import com.github.fmjsjx.libcommon.redis.LuaScript;
import com.github.fmjsjx.libcommon.redis.LuaScripts;
import com.github.fmjsjx.libcommon.redis.RedisUtil;
import com.github.fmjsjx.libcommon.redis.core.RedisConnectionAdapter;
import com.github.fmjsjx.libcommon.util.ArrayUtil;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * The implementation of {@link RedisRemoteLock} with keep alive feature.
 * The difference between this class and {@link DefaultRedisRemoteLock}
 * is that it will keep the lock alive in the backend.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 * @author MJ Fang
 * @see DefaultRedisRemoteLock
 * @see RedisRemoteLock
 * @since 4.1
 */
public class KeepAliveRedisRemoteLock<K, V> extends DefaultRedisRemoteLock<K, V> {

    /**
     * Script for action: Set expire if value equals.
     */
    protected static final LuaScript<Boolean> EXPIRE_IF_VALUE_EQUALS = LuaScript.forBoolean(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end");

    /**
     * The global keep alive scheduled executor instance holder.
     */
    protected static final class GlobalKeepAliveExecutorInstanceHolder {

        /**
         * The global keep alive scheduled executor instance.
         */
        protected static final ScheduledExecutorService INSTANCE = Executors
                .newSingleThreadScheduledExecutor(new DefaultThreadFactory("locks-keeper", true));

        private GlobalKeepAliveExecutorInstanceHolder() {
        }

    }

    /**
     * The keep alive executor.
     */
    protected final ScheduledExecutorService keepAliveExecutor;

    private K[] keys;
    private V timeoutInValueType;

    /**
     * Constructs a new {@link KeepAliveRedisRemoteLock} with the global
     * keep alive scheduled executor and the specified parameters.
     *
     * @param connection     the connection to Redis
     * @param key            the key
     * @param value          the value
     * @param timeoutSeconds the timeout seconds
     */
    public KeepAliveRedisRemoteLock(RedisConnectionAdapter<K, V> connection, K key, V value, long timeoutSeconds) {
        this(connection, key, value, timeoutSeconds, GlobalKeepAliveExecutorInstanceHolder.INSTANCE);
    }

    /**
     * Constructs a new {@link KeepAliveRedisRemoteLock} with the specified
     * parameters.
     *
     * @param connection        the connection to Redis
     * @param key               the key
     * @param value             the value
     * @param timeoutSeconds    the timeout seconds
     * @param keepAliveExecutor the keep alive scheduled executor
     */
    public KeepAliveRedisRemoteLock(RedisConnectionAdapter<K, V> connection, K key, V value, long timeoutSeconds,
                                    ScheduledExecutorService keepAliveExecutor) {
        super(connection, key, value, timeoutSeconds);
        this.keepAliveExecutor = Objects.requireNonNull(keepAliveExecutor, "keepAliveExecutor must not be null");
    }

    @SuppressWarnings("unchecked")
    protected K[] getKeys() {
        var keys = this.keys;
        if (keys == null) {
            this.keys = keys = ArrayUtil.self(getKey());
        }
        return keys;
    }

    /**
     * Gets the timeout in value type.
     *
     * @return the timeout in value type
     */
    @SuppressWarnings("unchecked")
    protected V getTimeoutInValueType() {
        var timeoutInValueType = this.timeoutInValueType;
        if (timeoutInValueType == null) {
            var timeoutString = Long.toString(getTimeoutSeconds());
            var timeout = isValueIsByteArray() ? timeoutString.getBytes() : timeoutString;
            this.timeoutInValueType = timeoutInValueType = (V) timeout;
        }
        return timeoutInValueType;
    }

    /**
     * Gets the keep alive interval in millisecond.
     *
     * @return the keep alive interval in millisecond
     */
    protected long getKeepAliveIntervalMillis() {
        return getTimeoutSeconds() * 1000 * 3 / 5;
    }

    /**
     * Schedules the keep alive task.
     *
     * @param inLock the flag to indicate whether the lock is in use
     * @return the scheduled future
     */
    protected ScheduledFuture<?> scheduleKeepAlive(AtomicBoolean inLock) {
        var intervalMillis = getKeepAliveIntervalMillis();
        return keepAliveExecutor.scheduleAtFixedRate(keepAliveTask(inLock), intervalMillis, intervalMillis,
                TimeUnit.MICROSECONDS);
    }

    /**
     * Creates the keep alive task.
     *
     * @param inLock the flag to indicate whether the lock is in use
     * @return the keep alive task
     */
    protected Runnable keepAliveTask(AtomicBoolean inLock) {
        return () -> {
            if (inLock.get()) {
                keepAliveAsync();
            }
        };
    }

    /**
     * Keep the key alive asynchronously.
     *
     * @return a {@code CompletionStage<Boolean>}
     */
    @SuppressWarnings("unchecked")
    protected CompletionStage<Boolean> keepAliveAsync() {
        return RedisUtil.eval(getConnection().async(), EXPIRE_IF_VALUE_EQUALS, getKeys(), getValue(), getTimeoutInValueType());
    }

    @Override
    protected <R> Optional<R> runAndUnlock(Supplier<R> action) {
        var inLock = new AtomicBoolean(true);
        var future = scheduleKeepAlive(inLock);
        try {
            return Optional.ofNullable(action.get());
        } finally {
            inLock.set(false);
            future.cancel(false);
            unlockAsync();
        }
    }

    @Override
    protected <R> CompletionStage<Optional<R>> runAndUnlockAsync(Supplier<CompletionStage<R>> asyncAction) {
        var inLock = new AtomicBoolean(true);
        var future = scheduleKeepAlive(inLock);
        return asyncAction.get()
                .whenComplete((r, e) -> {
                    inLock.set(false);
                    future.cancel(false);
                    unlockAsync();
                })
                .thenApply(Optional::ofNullable);
    }

    @Override
    protected <R> CompletionStage<Optional<R>> runAndUnlockAsync(Supplier<CompletionStage<R>> asyncAction,
                                                                 Executor executor) {
        var inLock = new AtomicBoolean(true);
        var future = scheduleKeepAlive(inLock);
        return asyncAction.get()
                .whenCompleteAsync((v, e) -> {
                    inLock.set(false);
                    future.cancel(false);
                    unlockAsync();
                }, executor)
                .thenApply(Optional::ofNullable);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CompletionStage<Boolean> unlockAsync() {
        return RedisUtil.eval(getConnection().async(), LuaScripts.DEL_IF_VALUE_EQUALS, getKeys(), getValue());
    }

}
