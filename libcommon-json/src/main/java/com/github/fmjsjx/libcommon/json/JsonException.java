package com.github.fmjsjx.libcommon.json;

import java.io.Serial;

/**
 * A runtime exception threw by a JSON encoder/decoder.
 */
public class JsonException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4234828639293246052L;

    /**
     * Constructs a new {@link JsonException}.
     */
    public JsonException() {
        super();
    }

    /**
     * Constructs a new {@link JsonException} with the specified detail message.
     * 
     * @param message the detail message
     */
    public JsonException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link JsonException} with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link JsonException} with the specified cause.
     * 
     * @param cause the cause
     */
    public JsonException(Throwable cause) {
        super(cause);
    }

}
