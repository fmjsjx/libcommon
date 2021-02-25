package com.github.fmjsjx.libcommon.util.pool;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The implementation of {@link CachedPool} using {@link ConcurrentLinkedDeque}
 * to cache objects.
 * 
 * @param <E> the type of the objects to be cached
 * @since 1.1
 */
public class ConcurrentCachedPool<E> extends AbstractDequeCachedPool<E, ConcurrentLinkedDeque<E>> {

    protected final AtomicInteger estimatedSize = new AtomicInteger();

    /**
     * Creates a new {@link ConcurrentCachedPool} with the specified limit.
     * 
     * @param limit the limit of this pool
     */
    public ConcurrentCachedPool(int limit) {
        super(new ConcurrentLinkedDeque<>(), limit);
    }

    /**
     * Returns the estimated number of the cached objects in this pool.
     * 
     * @return the estimated number of the cached objects in this pool
     */
    public int estimatedSize() {
        return estimatedSize.get();
    }

    /**
     * Returns the number of the cached objects in this pool.
     * <p>
     * This method is equivalent to {@link #estimatedSize()}.
     * 
     * @return the number of the cached objects in this pool
     */
    @Override
    public int size() {
        return estimatedSize();
    }

    /**
     * Traverse the objects in the pool and count them.
     * 
     * <p>
     * Beware that, returned result may be inaccurate. Thus, this method is
     * typically not very useful in concurrent applications.
     * 
     * @return the number of cached objects in this pool
     */
    public int countPool() {
        return deque.size();
    }

    @Override
    public Optional<E> tryTake() {
        var o = super.tryTake();
        if (o.isPresent()) {
            estimatedSize.decrementAndGet();
        }
        return o;
    }

    @Override
    public boolean tryBack(E e) {
        var estimatedSize = this.estimatedSize;
        if (estimatedSize.get() < limit) {
            super.tryBack(e);
            estimatedSize.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public boolean tryRelease(E e) {
        if (super.tryRelease(e)) {
            estimatedSize.decrementAndGet();
            return true;
        }
        return false;
    }

}
