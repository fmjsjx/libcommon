package com.github.fmjsjx.libcommon.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for {@code JDBC}.
 */
public class JdbcUtil {

    /**
     * Execute a select/query SQL, setting parameters by a
     * {@link PreparedExecution}, reading the {@link ResultSet} with a
     * {@link ResultExecution}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param execution    the {@link ResultExecution} to handle all rows of results
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final void select(Connection conn, String sql, PreparedExecution paramsSetter,
            ResultExecution execution) throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            try (var rs = statement.executeQuery()) {
                execution.execute(rs);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute a select/query SQL, setting parameters by a
     * {@link PreparedExecution}, mapping each row to a list via a
     * {@link RowMapper}.
     * 
     * @param <T>          the result type
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param rowMapper    a {@link RowMapper} to map one object per row
     * @return the result List, containing mapped objects
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final <T> List<T> selectMany(Connection conn, String sql, PreparedExecution paramsSetter,
            RowMapper<T> rowMapper) throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            try (var rs = statement.executeQuery()) {
                var list = new ArrayList<T>();
                for (; rs.next();) {
                    list.add(rowMapper.map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute a select/query SQL, setting parameters by a
     * {@link PreparedExecution}, mapping first row to the result via a
     * {@link RowMapper}.
     * 
     * @param <T>          the result type
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param rowMapper    a {@link RowMapper} to map one object per row
     * @return an {@code Optional<T>}
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final <T> Optional<T> selectOne(Connection conn, String sql, PreparedExecution paramsSetter,
            RowMapper<T> rowMapper) throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rowMapper.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private JdbcUtil() {
    }

}
