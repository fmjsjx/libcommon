package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * An {@link IllegalJwtException} thrown when encountering a JWT that
 * missing the necessary {@code "kid"} parameter value in the JOSE header.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class MissingRequiredKidException extends IllegalJwtException {

    private static final class InstanceHolder {
        private static final MissingRequiredKidException INSTANCE = new MissingRequiredKidException();
    }

    /**
     * Returns the singleton {@link MissingRequiredKidException} instance.
     *
     * @return the singleton {@code MissingRequiredKidException} instance
     */
    public static MissingRequiredKidException getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private MissingRequiredKidException() {
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
