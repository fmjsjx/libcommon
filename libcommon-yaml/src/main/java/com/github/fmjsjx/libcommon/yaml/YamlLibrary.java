package com.github.fmjsjx.libcommon.yaml;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import com.github.fmjsjx.libcommon.json.JsonLibrary;

/**
 * A YAML encode/decode library.
 *
 * @param <O> the type of dynamic object
 */
public interface YamlLibrary<O> extends JsonLibrary<O> {

    /**
     * Returns the default implementation of {@link YamlLibrary}.
     * 
     * @return the default implementation of {@code YamlLibrary}
     * @see Jackson2YamlLibrary
     */
    static Jackson2YamlLibrary defaultLibrary() {
        return Jackson2YamlLibrary.getInstance();
    }

    /**
     * Decodes dynamic YAML object from byte array.
     * 
     * @param <T> the type of dynamic YAML object
     * @param src the source byte array
     * @return the dynamic YAML object
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    <T extends O> T loads(byte[] src) throws YamlException;

    /**
     * Decodes dynamic YAML object from string.
     * 
     * @param <T> the type of dynamic YAML object
     * @param src the source string
     * @return the dynamic YAML object
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    default <T extends O> T loads(String src) throws YamlException {
        return loads(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the class of the type
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    <T> T loads(byte[] src, Class<T> type) throws YamlException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the class of the type
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    default <T> T loads(String src, Class<T> type) throws YamlException {
        return loads(src.getBytes(StandardCharsets.UTF_8), type);
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    <T> T loads(byte[] src, Type type) throws YamlException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    default <T> T loads(String src, Type type) throws YamlException {
        return loads(src.getBytes(StandardCharsets.UTF_8), type);
    }

    /**
     * Encodes object to YAML value as byte array.
     * 
     * @param obj the object to be decoded
     * @return a {@code byte[]}
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    byte[] dumpsToBytes(Object obj) throws YamlException;

    /**
     * Encodes object to YAML value as string.
     * 
     * @param obj the object to be decoded
     * @return a {@code String}
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    String dumpsToString(Object obj) throws YamlException;

    /**
     * Encodes object to YAML value as string.
     * <p>
     * This method is equivalent to {@link #dumpsToString(Object)}.
     * 
     * @param obj the object to be decoded
     * @return a {@code String}
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    default String dumps(Object obj) throws YamlException {
        return dumpsToString(obj);
    }

    /**
     * Encodes object to YAML value.
     * 
     * @param obj the object to be decoded
     * @param out the {@code OutputStream}
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    void dumps(Object obj, OutputStream out) throws YamlException;

}
