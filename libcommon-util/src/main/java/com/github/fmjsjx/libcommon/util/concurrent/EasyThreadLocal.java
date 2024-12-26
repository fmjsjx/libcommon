package com.github.fmjsjx.libcommon.util.concurrent;

import java.util.function.Supplier;

/**
 * Interface for thread local variable.
 *
 * @param <V> the value type
 * @author MJ Fang
 * @since 3.10
 */
public interface EasyThreadLocal<V> {

    /**
     * Creates and returns a new {@link EasyThreadLocal} instance with
     * the specified value initializer.
     *
     * @param valueInitializer the supplier initialize values
     * @param <V>              the type of the value
     * @return an {@code EasyThreadLocal} instance
     */
    static <V> EasyThreadLocal<V> create(Supplier<V> valueInitializer) {
        return ThreadLocalUtil.isFastThreadLocalAvailable()
                ? new FastEasyThreadLocal<>(valueInitializer)
                : new JdkEasyThreadLocal<>(valueInitializer);
    }

    /**
     * Returns the current value for the current thread.
     *
     * @return the current value for the current thread
     */
    V get();

    /**
     * Sets the value for the current thread.
     *
     * @param value the value to be set
     */
    void set(V value);

    /**
     * Sets the value to uninitialized for the specified thread local map.
     */
    void remove();

    /**
     * Returns the current value for the current thread if it exists,
     * {@code null} otherwise.
     * <p>
     * Only {@link FastEasyThreadLocal} support this method.
     *
     * @return the current value for the current thread if it exists,
     * {@code null} otherwise
     * @throws UnsupportedOperationException if the implementation not support this method
     */
    default V getIfExists() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getIfExists on " + getClass().getSimpleName());
    }

}
