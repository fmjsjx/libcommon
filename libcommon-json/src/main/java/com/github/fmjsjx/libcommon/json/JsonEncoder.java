package com.github.fmjsjx.libcommon.json;

import java.io.OutputStream;

/**
 * An interface provides methods to encode JSON.
 * 
 * @since 1.1
 */
public interface JsonEncoder {

    /**
     * Encodes object to JSON value as byte array.
     * 
     * @param obj the object to be decoded
     * @return a {@code byte[]}
     * @throws JsonException if any JSON encode error occurs
     */
    byte[] dumpsToBytes(Object obj) throws JsonException;

    /**
     * Encodes object to JSON value as string.
     * 
     * @param obj the object to be decoded
     * @return a {@code String}
     * @throws JsonException if any JSON encode error occurs
     */
    String dumpsToString(Object obj) throws JsonException;

    /**
     * Encodes object to JSON value as string.
     * <p>
     * This method is equivalent to {@link #dumpsToString(Object)}.
     * 
     * @param obj the object to be decoded
     * @return a {@code String}
     * @throws JsonException if any JSON encode error occurs
     */
    default String dumps(Object obj) throws JsonException {
        return dumpsToString(obj);
    }

    /**
     * Encodes object to JSON value.
     * 
     * @param obj the object to be decoded
     * @param out the {@code OutputStream}
     * @throws JsonException if any JSON encode error occurs
     */
    void dumps(Object obj, OutputStream out) throws JsonException;

}