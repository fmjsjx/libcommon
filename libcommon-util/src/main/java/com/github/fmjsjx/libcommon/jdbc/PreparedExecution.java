package com.github.fmjsjx.libcommon.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents an operation that accepts a single {@link PreparedStatement} input
 * argument and returns no result.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #execute(PreparedStatement)}.
 */
@FunctionalInterface
public interface PreparedExecution extends SQLExecution<PreparedStatement> {

    /**
     * Performs this operation on the given {@code PreparedStatement}.
     * 
     * @param statement the input {@code PreparedStatement}
     * @throws SQLException if a database access error occurs or this method is
     *                      called on a closed {@code PreparedStatement}
     */
    @Override
    void execute(PreparedStatement statement) throws SQLException;

}
