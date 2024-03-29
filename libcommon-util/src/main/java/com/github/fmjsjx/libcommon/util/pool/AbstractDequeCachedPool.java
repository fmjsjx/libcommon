package com.github.fmjsjx.libcommon.util.pool;

import java.util.Deque;
import java.util.Optional;

/**
 * The abstract implementation of {@link CachedPool} using {@link Deque}s to
 * cache objects.
 *
 * @param <E> the type of the objects to be cached
 * @param <Q> the type of the {@link Deque}
 * 
 * @since 1.1
 * @see BlockingCachedPool
 * @see ConcurrentCachedPool
 */
public abstract class AbstractDequeCachedPool<E, Q extends Deque<E>> implements CachedPool<E> {

    /**
     * The deque.
     */
    protected final Q deque;
    /**
     * The limit size.
     */
    protected final int limit;

    /**
     * Constructs new instance with specified {@code deque} and the specified {@code limit} given.
     *
     * @param deque the deque
     * @param limit the limit size
     */
    protected AbstractDequeCachedPool(Q deque, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be a positive integer");
        }
        this.deque = deque;
        this.limit = limit;
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public int limit() {
        return limit;
    }

    @Override
    public Optional<E> tryTake() {
        return Optional.ofNullable(deque.pollLast());
    }

    @Override
    public boolean tryBack(E e) {
        return deque.offerLast(e);
    }

    @Override
    public boolean tryRelease(E e) {
        return deque.removeFirstOccurrence(e);
    }

    @Override
    public void clear() {
        deque.clear();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(size=" + size() + ", limit=" + limit() + ")";
    }

}
