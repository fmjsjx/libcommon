package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * An {@link IllegalJwtException} thrown when encountering a JWT that
 * missing the necessary {@code "alg"} parameter value in the JOSE header.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class MissingRequiredAlgException extends IllegalJwtException {

    /**
     * Constructs a new {@link MissingRequiredAlgException} with the
     * default detail message {@code Missing the necessary "alg"
     * parameter value in the header}.
     */
    public MissingRequiredAlgException() {
        this("Missing the necessary \"alg\" parameter value in the header");
    }

    /**
     * Constructs a new {@link MissingRequiredAlgException} with the
     * specified detail message.
     *
     * @param message the detail message
     */
    public MissingRequiredAlgException(String message) {
        super(message);
    }

}
