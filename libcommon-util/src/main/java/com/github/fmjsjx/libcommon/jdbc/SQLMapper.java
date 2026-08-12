package com.github.fmjsjx.libcommon.jdbc;

import java.sql.SQLException;
import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #map(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface SQLMapper<T, R> extends Function<T, R> {

    @Override
    default R apply(T t) throws SQLRuntimeException {
        try {
            return map(t);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Applies this function to the given argument.
     * 
     * @param t the function argument
     * @return the function result
     * @throws SQLException if a database access error occurs
     */
    R map(T t) throws SQLException;

}
