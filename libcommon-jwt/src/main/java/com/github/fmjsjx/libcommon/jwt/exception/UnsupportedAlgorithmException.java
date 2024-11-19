package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * Exception indicating that the cryptographic algorithm is still
 * unsupported now.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class UnsupportedAlgorithmException extends SecurityException {

    /**
     * Constructs a new {@link UnsupportedAlgorithmException} with the
     * specified detail message.
     *
     * @param message the detail message
     */
    public UnsupportedAlgorithmException(String message) {
        super(message);
    }

}
