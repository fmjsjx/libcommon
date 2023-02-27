package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;

/**
 * The factory creates {@link TypeReference}s
 *
 * @author MJ Fang
 * @see DefaultTypeReferenceFactory
 * @see SimpleTypeReferenceFactory
 * @since 3.1
 */
@FunctionalInterface
public interface TypeReferenceFactory {

    /**
     * Creates a new {@link TypeReference} instance by the {@link ParameterizedType} given.
     *
     * @param type the {@link ParameterizedType}
     * @param <T>  the type of objects that the {@link TypeReference} object may be compared to
     * @return the created {@link TypeReference} instance
     */
    <T> TypeReference<T> create(ParameterizedType type);

}
