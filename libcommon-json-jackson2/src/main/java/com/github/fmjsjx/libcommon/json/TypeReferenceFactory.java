package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.util.function.Function;

/**
 * The factory creates {@link TypeReference}s
 *
 * @author MJ Fang
 * @see DefaultTypeReferenceFactory
 * @since 3.1
 */
@FunctionalInterface
public interface TypeReferenceFactory extends Function<ParameterizedType, TypeReference<?>> {

    /**
     * Returns the singleton instance of the simple implementation of {@link TypeReferenceFactory}.
     *
     * @return the singleton instance of the simple implementation of {@code TypeReferenceFactory}.
     */
    static TypeReferenceFactory simple() {
        return TypeReferenceImpl::new;
    }

    /**
     * Returns the global instance of the default implementation of {@link TypeReferenceFactory}.
     *
     * @return the global instance of the default implementation of {@code TypeReferenceFactory}.
     */
    static TypeReferenceFactory getDefault() {
        return DefaultTypeReferenceFactory.globalInstance();
    }

    /**
     * Creates a new {@link TypeReference} instance by the {@link ParameterizedType} given.
     *
     * @param type the {@link ParameterizedType}
     * @param <T>  the type of objects that the {@link TypeReference} object may be compared to
     * @return the created {@link TypeReference} instance
     */
    <T> TypeReference<T> create(ParameterizedType type);

    @Override
    default TypeReference<?> apply(ParameterizedType type) {
        return create(type);
    }
}
