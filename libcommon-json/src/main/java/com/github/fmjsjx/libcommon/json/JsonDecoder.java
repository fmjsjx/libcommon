package com.github.fmjsjx.libcommon.json;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * An interface provides methods to decode JSON.
 * 
 * @param <JSON> the type of dynamic JSON object
 * @since 1.1
 */
public interface JsonDecoder<JSON> {

    /**
     * Decodes dynamic JSON object from byte array.
     * 
     * @param <T> the type of dynamic JSON object
     * @param src the source byte array
     * @return the dynamic JSON object
     * @throws JsonException if any JSON decode error occurs
     */
    <T extends JSON> T loads(byte[] src) throws JsonException;

    /**
     * Decodes dynamic JSON object from string.
     * 
     * @param <T> the type of dynamic JSON object
     * @param src the source string
     * @return the dynamic JSON object
     * @throws JsonException if any JSON decode error occurs
     */
    default <T extends JSON> T loads(String src) throws JsonException {
        return loads(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the class of the type
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    <T> T loads(byte[] src, Class<T> type) throws JsonException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the class of the type
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    default <T> T loads(String src, Class<T> type) throws JsonException {
        return loads(src.getBytes(StandardCharsets.UTF_8), type);
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    <T> T loads(byte[] src, Type type) throws JsonException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    default <T> T loads(String src, Type type) throws JsonException {
        return loads(src.getBytes(StandardCharsets.UTF_8), type);
    }
}
