package com.github.fmjsjx.libcommon.jwt.exception;

import java.security.Key;

/**
 * A {@link SecurityException} thrown when cannot find the {@link Key}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class NoSuchKeyException extends SecurityException {


    /**
     * Constructs a new {@link NoSuchKeyException} with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public NoSuchKeyException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link NoSuchKeyException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public NoSuchKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
