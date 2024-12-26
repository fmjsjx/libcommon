package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * A {@link SecurityException} thrown when encountering a key that is not
 * suitable for the required functionality, or when attempting to use a
 * Key in an incorrect or prohibited manner.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class InvalidKeyException extends SecurityException {


    /**
     * Constructs a new {@link InvalidKeyException} with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public InvalidKeyException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link InvalidKeyException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public InvalidKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
