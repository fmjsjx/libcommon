package com.github.fmjsjx.libcommon.jwt.exception;

/**
 * A {@link SecurityException} thrown when encountering a digital
 * signature or mac that failed to pass verification.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class InvalidSignatureException extends SecurityException {


    /**
     * Constructs a new {@link InvalidSignatureException} with the
     * specified detail message.
     *
     * @param message the detail message
     */
    public InvalidSignatureException(String message) {
        super(message);
    }

}
