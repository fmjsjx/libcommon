package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * Base class for JWT-related runtime exceptions.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class JwtException extends RuntimeException {

    /**
     * Constructs a new JWT exception with {@code null} as its
     * detail message.
     */
    public JwtException() {
        super();
    }

    /**
     * Constructs a new JWT exception with the specified detail message.
     *
     * @param message the detail message
     */
    public JwtException(String message) {
        super(message);
    }

    /**
     * Constructs a new JWT exception with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JWT exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause
     */
    public JwtException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message
     * @param cause              the cause
     * @param enableSuppression  whether suppression is enabled or not
     * @param writableStackTrace whether the stack trace should be writable or not
     */
    protected JwtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
