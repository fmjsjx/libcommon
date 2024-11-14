package com.github.fmjsjx.libcommon.jwt;

import java.util.function.Function;

/**
 * The factory creates {@link JsonRepresented}s.
 *
 * @param <T> the real type of the {@link JsonRepresented}
 * @author MJ Fang
 * @since 3.10
 */
@FunctionalInterface
public interface JsonRepresentedFactory<T extends JsonRepresented> extends Function<byte[], T> {

    /**
     * Creates a new {@link JsonRepresented} instance by the specified
     * JSON byte array given.
     *
     * @param rawJson the raw JSON byte array
     * @return the created {@code JsonRepresented} instance
     */
    T create(byte[] rawJson);

    @Override
    default T apply(byte[] bytes) {
        return create(bytes);
    }

}
