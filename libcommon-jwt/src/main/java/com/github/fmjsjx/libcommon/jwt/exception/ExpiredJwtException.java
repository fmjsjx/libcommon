package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * An {@link IllegalJwtException} thrown when encountering an expired JWT.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class ExpiredJwtException extends IllegalJwtException {

    /**
     * Constructs a new {@link ExpiredJwtException} with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public ExpiredJwtException(String message) {
        super(message);
    }

}
