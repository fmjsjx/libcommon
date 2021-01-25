package com.github.fmjsjx.libcommon.yaml;

import com.github.fmjsjx.libcommon.json.JsonException;

/**
 * A runtime exception threw by a YAML encoder/decoder.
 */
public class YamlException extends JsonException {

    private static final long serialVersionUID = 3898138564693760208L;

    /**
     * Constructs a new {@link YamlException}.
     */
    public YamlException() {
        super();
    }

    /**
     * Constructs a new {@link YamlException} with the specified detail message.
     * 
     * @param message the detail message
     */
    public YamlException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link YamlException} with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public YamlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link YamlException} with the specified cause.
     * 
     * @param cause the cause
     */
    public YamlException(Throwable cause) {
        super(cause);
    }

}
