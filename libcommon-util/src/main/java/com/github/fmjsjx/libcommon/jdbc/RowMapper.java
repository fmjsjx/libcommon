package com.github.fmjsjx.libcommon.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a function that accepts one {@link ResultSet} argument and
 * produces a result.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #map(ResultSet)}.
 *
 * @param <E> the type of the result of the function
 */
@FunctionalInterface
public interface RowMapper<E> extends SQLMapper<ResultSet, E> {

    /**
     * Applies this function to the given {@code ResultSet}
     * 
     * @param rs the input {@code ResultSet}
     * @return the function result
     * @throws SQLException if a database access error occurs or this method is
     *                      called on a closed {@code ResultSet}
     */
    @Override
    E map(ResultSet rs) throws SQLException;

}
