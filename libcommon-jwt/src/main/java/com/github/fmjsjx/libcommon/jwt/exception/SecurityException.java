package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * A {@code JwtException} attributed to a problem with security-related elements, such as
 * cryptographic keys, algorithms, or the underlying Java JCA API.\
 *
 * @author MJ Fang
 * @since 3.10
 */
public class SecurityException extends JwtException {

    /**
     * Constructs a new {@link SecurityException} with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public SecurityException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link SecurityException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

}
