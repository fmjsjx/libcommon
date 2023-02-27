package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link TypeReferenceFactory}.
 *
 * @author MJ Fang
 * @see TypeReferenceFactory
 * @see SimpleTypeReferenceFactory
 * @since 3.1
 */
public class DefaultTypeReferenceFactory implements TypeReferenceFactory {

    private static final class GlobalInstanceHolder {
        private static final DefaultTypeReferenceFactory instance = new DefaultTypeReferenceFactory();
    }

    /**
     * Returns the global {@link DefaultTypeReferenceFactory} instance.
     *
     * @return the global {@code DefaultTypeReferenceFactory} instance
     */
    public static final DefaultTypeReferenceFactory globalInstance() {
        return GlobalInstanceHolder.instance;
    }

    private final ConcurrentMap<ParameterizedType, TypeReference<?>> typeReferences = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeReference<T> create(ParameterizedType type) {
        return (TypeReference<T>) typeReferences.computeIfAbsent(type, TypeReferenceImpl::new);
    }

}
