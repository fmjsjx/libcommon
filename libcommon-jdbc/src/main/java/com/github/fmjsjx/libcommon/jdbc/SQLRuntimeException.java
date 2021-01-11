package com.github.fmjsjx.libcommon.jdbc;

import java.sql.SQLException;

/**
 * A runtime exception holding {@link SQLException}s.
 */
public class SQLRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 3175714205735380275L;

    /**
     * Constructs a new {@link SQLRuntimeException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * 
     * @param cause the cause
     */
    public SQLRuntimeException(SQLException cause) {
        super(cause);
    }

    /**
     * Constructs a new {@link SQLRuntimeException} with the specified detail
     * message and cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public SQLRuntimeException(String message, SQLException cause) {
        super(message, cause);
    }

}
