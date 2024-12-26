package com.github.fmjsjx.libcommon.util.concurrent;

import java.util.function.Supplier;

class JdkEasyThreadLocal<V> implements EasyThreadLocal<V> {

    private final ThreadLocal<V> delegated;

    JdkEasyThreadLocal(Supplier<V> valueInitializer) {
        delegated = valueInitializer == null ? new ThreadLocal<>() : ThreadLocal.withInitial(valueInitializer);
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
    public String toString() {
        return "JdkEasyThreadLocal(delegated=" + delegated + ")";
    }

}
