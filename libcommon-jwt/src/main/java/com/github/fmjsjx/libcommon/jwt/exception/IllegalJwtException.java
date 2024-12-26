package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * Exception indicating that a JWT was not correctly constructed and
 * should be rejected.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class IllegalJwtException extends JwtException {

    /**
     * Constructs a new {@link IllegalJwtException} with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public IllegalJwtException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link IllegalJwtException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public IllegalJwtException(String message, Throwable cause) {
        super(message, cause);
    }

}
