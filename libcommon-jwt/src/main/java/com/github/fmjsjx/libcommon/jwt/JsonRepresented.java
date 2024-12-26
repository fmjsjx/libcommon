package com.github.fmjsjx.libcommon.jwt;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * The interface provides functionality of a JSON object that represented
 * by a JOSE header or a JWT Claims Set.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JsonRepresented {

    /**
     * Returns the specified JSON value by the name given.
     *
     * @param name the name
     * @param type the value type
     * @param <T>  the value type
     * @return the value or {@code null} if not present
     */
    <T> T get(String name, Class<? extends T> type);

    /**
     * Returns the specified JSON value by the name given.
     *
     * @param name the name
     * @param type the value type
     * @param <T>  the value type
     * @return the value or {@code null} if not present
     */
    <T> T get(String name, Type type);

    /**
     * Returns the specified JSON value string by the name given.
     *
     * @param name the name
     * @return the value string or {@code null} if not present
     */
    String getString(String name);

    /**
     * Returns the specified JSON value number as {@code int} type
     * by the name given.
     *
     * @param name the name
     * @return an {@link OptionalInt}
     */
    OptionalInt getInt(String name);

    /**
     * Returns the specified JSON value number as {@code long} type
     * by the name given.
     *
     * @param name the name
     * @return an {@link OptionalLong}
     */
    OptionalLong getLong(String name);

    /**
     * Returns the specified JSON value number as {@code double} type
     * by the name given.
     *
     * @param name the name
     * @return an {@link OptionalDouble}
     */
    OptionalDouble getDouble(String name);

    /**
     * Returns the specified JSON value number as {@link Boolean} type
     * by the name given.
     *
     * @param name the name
     * @return the Boolean value or {@code null} if not present
     */
    Boolean getBoolean(String name);

    /**
     * Returns the specified JSON list value by the name given.
     *
     * @param name        the name
     * @param elementType the type of the element in the list
     * @param <T>         the type of the element in the list
     * @return the list value or {@code null} if not present
     */
    <T> List<T> getList(String name, Class<T> elementType);

}
