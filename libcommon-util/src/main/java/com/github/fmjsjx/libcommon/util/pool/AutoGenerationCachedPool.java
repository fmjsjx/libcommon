package com.github.fmjsjx.libcommon.util.pool;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A Wrapper of {@link CachedPool} that provides the {@code auto-generation}
 * feature.
 *
 * @param <E> the type of the objects to be cached
 */
public class AutoGenerationCachedPool<E> implements CachedPool<E> {

    private final CachedPool<E> pool;
    private final Supplier<E> generator;

    /**
     * Creates a new {@link AutoGenerationCachedPool} with the specified pool and
     * generator.
     * 
     * @param pool      the pool
     * @param generator the generator
     */
    public AutoGenerationCachedPool(CachedPool<E> pool, Supplier<E> generator) {
        this.pool = Objects.requireNonNull(pool, "pool must not be null");
        this.generator = Objects.requireNonNull(generator, "generator must not be null");
    }

    @Override
    public int size() {
        return pool.size();
    }

    @Override
    public int limit() {
        return pool.limit();
    }

    @Override
    public Optional<E> tryTake() {
        return pool.tryTake();
    }

    @Override
    public boolean tryBack(E e) {
        return pool.tryBack(e);
    }

    @Override
    public boolean tryRelease(E e) {
        return pool.tryRelease(e);
    }

    /**
     * Take a cached object from this pool. If no object present in this pool then
     * this method will auto generate a new object.
     * 
     * @return the object
     */
    public E take() {
        return tryTake().orElseGet(generator);
    }
    
    @Override
    public void clear() {
        pool.clear();
    }

    @Override
    public String toString() {
        return "AutoGenerationCachedPool(pool=" + pool + ")";
    }

}
