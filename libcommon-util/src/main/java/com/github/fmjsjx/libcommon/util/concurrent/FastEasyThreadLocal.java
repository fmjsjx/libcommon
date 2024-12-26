package com.github.fmjsjx.libcommon.util.concurrent;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.function.Supplier;

class FastEasyThreadLocal<V> implements EasyThreadLocal<V> {

    private final FastThreadLocal<V> delegated;

    FastEasyThreadLocal(Supplier<V> valueInitializer) {
        delegated = valueInitializer == null ? new FastThreadLocal<>() : new FastThreadLocal<>() {
            @Override
            protected V initialValue() {
                return valueInitializer.get();
            }
        };
    }

    @Override
    public V get() {
        return delegated.get();
    }

    @Override
    public void set(V value) {
        delegated.set(value);
    }

    @Override
    public void remove() {
        delegated.remove();
    }

    @Override
    public V getIfExists() {
        return delegated.getIfExists();
    }

    @Override
    public String toString() {
        return "FastEasyThreadLocal(delegated=" + delegated + ")";
    }

}
