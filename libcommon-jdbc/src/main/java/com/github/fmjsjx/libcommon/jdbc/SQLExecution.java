package com.github.fmjsjx.libcommon.jdbc;

import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #execute(Object)}.
 *
 * @param <T> the type of the input to the operation
 */
public interface SQLExecution<T> extends Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override
    default void accept(T t) {
        try {
            execute(t);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Performs this operation on the given argument.
     * 
     * @param t the input argument
     * @throws SQLException if a database access error occurs
     */
    void execute(T t) throws SQLException;

}