package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * An {@link IllegalJwtException} thrown when encountering a JWT that
 * missing the necessary {@code "kid"} parameter value in the JOSE header.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class MissingRequiredKidException extends IllegalJwtException {

    /**
     * Constructs a new {@link MissingRequiredKidException} with the
     * default detail message {@code Missing the necessary "kid"
     * parameter value in the header}.
     */
    public MissingRequiredKidException() {
        this("Missing the necessary \"kid\" parameter value in the header");
    }

    /**
     * Constructs a new {@link MissingRequiredKidException} with the
     * specified detail message.
     *
     * @param message the detail message
     */
    public MissingRequiredKidException(String message) {
        super(message);
    }

}
