package com.github.fmjsjx.libcommon.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents an operation that accepts a single input {@link ResultSet}
 * argument and returns no result.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #execute(ResultSet)}.
 */
@FunctionalInterface
public interface ResultExecution extends SQLExecution<ResultSet> {

    /**
     * Performs this operation on the given {@code ResultSet}.
     * 
     * @param rs the input {@code ResultSet}
     * @throws SQLException if a database access error occurs or this method is
     *                      called on a closed {@code ResultSet}
     */
    @Override
    void execute(ResultSet rs) throws SQLException;

}
