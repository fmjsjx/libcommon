package com.github.fmjsjx.libcommon.util.pool;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * The implementation of {@link CachedPool} using {@link LinkedBlockingDeque} to
 * cache objects.
 * 
 * @param <E> the type of the objects to be cached
 * @since 1.1
 */
public class BlockingCachedPool<E> extends AbstractDequeCachedPool<E, LinkedBlockingDeque<E>> {

    /**
     * Creates a new {@link BlockingCachedPool} with the specified limit.
     * 
     * @param limit the limit of this pool
     */
    public BlockingCachedPool(int limit) {
        super(new LinkedBlockingDeque<>(limit), limit);
    }

}
