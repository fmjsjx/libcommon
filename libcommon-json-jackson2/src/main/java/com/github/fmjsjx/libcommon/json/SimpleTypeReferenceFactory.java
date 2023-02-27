package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;

/**
 * The simple implementation of {@link TypeReferenceFactory}.
 *
 * @author MJ Fang
 * @see TypeReferenceFactory
 * @see DefaultTypeReferenceFactory
 * @since 3.1
 */
public class SimpleTypeReferenceFactory implements TypeReferenceFactory {

    private static final class InstanceHolder {
        private static final SimpleTypeReferenceFactory instance = new SimpleTypeReferenceFactory();
    }

    /**
     * Returns the singleton {@link SimpleTypeReferenceFactory} instance.
     *
     * @return the singleton {@code SimpleTypeReferenceFactory} instance
     */
    public static final SimpleTypeReferenceFactory instance() {
        return InstanceHolder.instance;
    }

    private SimpleTypeReferenceFactory() {
    }

    @Override
    public <T> TypeReference<T> create(ParameterizedType type) {
        return new TypeReferenceImpl<>(type);
    }

}
