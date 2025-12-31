package com.github.fmjsjx.libcommon.redis.locks;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * A remote lock based on Redis.
 *
 * @author MJ Fang
 * @see DefaultRedisRemoteLock
 * @since 4.1
 */
public interface RedisRemoteLock<K, V> {

    /**
     * Returns the key of this lock.
     *
     * @return the key of this lock
     */
    K getKey();

    /**
     * Returns the value of this lock.
     *
     * @return the value of this lock
     */
    V getValue();

    /**
     * Returns the timeout in seconds of this lock.
     *
     * @return the timeout in seconds of this lock
     */
    long getTimeoutSeconds();

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param action the action to execute when the lock is acquired
     * @return the result of the action, or {@code null} if the lock is not acquired
     */
    <R> Optional<R> tryInLock(Supplier<R> action);

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param maxWaitMillis the maximum wait time in milliseconds
     * @param action        the action to execute when the lock is acquired
     * @param <R>           the type of the action result
     * @return the result of the action, or {@code null} if the lock is not acquired
     * @throws InterruptedException if the current thread is interrupted while waiting for
     */
    <R> Optional<R> runInLock(long maxWaitMillis, Supplier<R> action) throws InterruptedException;

    /**
     * Try to acquire the lock. Runs the given action when the lock is
     * acquired. And finally release the lock.
     *
     * @param maxWaitMillis  the maximum wait time in milliseconds
     * @param eachWaitMillis the wait time between each attempt
     * @param action         the action to execute when the lock is acquired
     * @param <R>            the type of the action result
     * @return the result of the action, or {@code null} if the lock is not acquired
     * @throws InterruptedException if the current thread is interrupted while waiting for
     */
    <R> Optional<R> runInLock(long maxWaitMillis, long eachWaitMillis, Supplier<R> action) throws InterruptedException;

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param asyncAction the action to execute when the lock is acquired
     * @param <R>         the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     * done, or the lock is not acquired
     */
    <R> CompletionStage<Optional<R>> tryInLockAsync(Supplier<CompletionStage<R>> asyncAction);

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param asyncAction the action to execute when the lock is acquired
     * @param executor    the executor to run the action
     * @param <R>         the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     * done, or the lock is not acquired
     */
    <R> CompletionStage<Optional<R>> tryInLockAsync(Supplier<CompletionStage<R>> asyncAction, Executor executor);

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param maxWaitMillis the maximum wait time in milliseconds
     * @param asyncAction   the action to execute when the lock is acquired
     * @param <R>           the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     * done, or the lock is not acquired
     */
    <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, Supplier<CompletionStage<R>> asyncAction);

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param maxWaitMillis  the maximum wait time in milliseconds
     * @param eachWaitMillis the wait time between each attempt
     * @param asyncAction    the action to execute when the lock is acquired
     * @param <R>            the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     * done, or the lock is not acquired
     */
    <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, long eachWaitMillis,
                                                    Supplier<CompletionStage<R>> asyncAction);

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param maxWaitMillis the maximum wait time in milliseconds
     * @param asyncAction   the action to execute when the lock is acquired
     * @param executor      the executor to run the action
     * @param <R>           the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     * done, or the lock is not acquired
     */
    <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, Supplier<CompletionStage<R>> asyncAction,
                                                    Executor executor);

    /**
     * Try to acquire the lock asynchronously. Runs the given action when
     * the lock is acquired. And finally release the lock.
     *
     * @param maxWaitMillis  the maximum wait time in milliseconds
     * @param eachWaitMillis the wait time between each attempt
     * @param asyncAction    the action to execute when the lock is acquired
     * @param executor       the executor to run the action
     * @param <R>            the type of the action result
     * @return a {@code CompletionStage} that complete when the action is
     */
    <R> CompletionStage<Optional<R>> runInLockAsync(long maxWaitMillis, long eachWaitMillis,
                                                    Supplier<CompletionStage<R>> asyncAction, Executor executor);

}
