package com.github.fmjsjx.libcommon.util.pool;

import java.util.Optional;

/**
 * An interface provides a pool caching objects.
 *
 * @param <E> the type of the object to be cached
 * 
 * @since 1.1
 * @see BlockingCachedPool
 * @see ConcurrentCachedPool
 * @see AutoGenerationCachedPool
 */
public interface CachedPool<E> {

    /**
     * Returns the number of the cached objects in this pool.
     * 
     * @return the number of the cached objects in this pool
     */
    int size();

    /**
     * Returns the maximum number of the cached objects in this pool.
     * 
     * @return the maximum number of the cached objects in this pool
     */
    int limit();

    /**
     * Try to take a cached object from this pool.
     * 
     * @return an {@code Optional} object
     */
    Optional<E> tryTake();

    /**
     * Try to back a object into this pool.
     * 
     * @param e the object to be cached
     * @return {@code true} if the object was cached to this pool, {@code false}
     *         otherwise
     */
    boolean tryBack(E e);

    /**
     * Try to release(remove) the specified object from this pool.
     * 
     * @param e the object to be released(removed)
     * @return {@code true} if the object was released(removed) from this pool,
     *         {@code false} otherwise
     */
    boolean tryRelease(E e);

}
