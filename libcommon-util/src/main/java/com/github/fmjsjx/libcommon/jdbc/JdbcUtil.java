package com.github.fmjsjx.libcommon.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

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

    /**
     * Execute a select/query SQL, setting parameters by a
     * {@link PreparedExecution}, mapping first column on first row to the result as
     * {@code int}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @return an {@code OptionalInt}
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final OptionalInt selectInt(Connection conn, String sql, PreparedExecution paramsSetter)
            throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return OptionalInt.of(rs.getInt(1));
                }
                return OptionalInt.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute a select/query SQL, setting parameters by a
     * {@link PreparedExecution}, mapping first column on first row to the result as
     * {@code long}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @return an {@code OptionalInt}
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final OptionalLong selectLong(Connection conn, String sql, PreparedExecution paramsSetter)
            throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return OptionalLong.of(rs.getLong(1));
                }
                return OptionalLong.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute an update SQL, setting parameters by a {@link PreparedExecution}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final int update(Connection conn, String sql, PreparedExecution paramsSetter)
            throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql)) {
            paramsSetter.execute(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute an update SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keyHolder}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keyHolder    an {@link IntConsumer} to holding the first generated key
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final int update(Connection conn, String sql, PreparedExecution paramsSetter, IntConsumer keyHolder)
            throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            paramsSetter.execute(statement);
            var rows = statement.executeUpdate();
            if (rows > 0) {
                try (var rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        keyHolder.accept(rs.getInt(1));
                    }
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute an update SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keyHolder}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keyHolder    a {@link LongConsumer} to holding the first generated key
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final int update(Connection conn, String sql, PreparedExecution paramsSetter, LongConsumer keyHolder)
            throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            paramsSetter.execute(statement);
            var rows = statement.executeUpdate();
            if (rows > 0) {
                try (var rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        keyHolder.accept(rs.getLong(1));
                    }
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute an update SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keysHolder}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keysHolder    a {@link ResultExecution} to holding all generated keys
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     */
    public static final int update(Connection conn, String sql, PreparedExecution paramsSetter,
            ResultExecution keysHolder) throws SQLRuntimeException {
        try (var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            paramsSetter.execute(statement);
            var rows = statement.executeUpdate();
            if (rows > 0) {
                try (var rs = statement.getGeneratedKeys()) {
                    keysHolder.execute(rs);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    /**
     * Execute an insert SQL, setting parameters by a {@link PreparedExecution}.
     * <p>
     * This method is equivalent to
     * {@link #update(Connection, String, PreparedExecution)}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     * 
     * @see #update(Connection, String, PreparedExecution)
     */
    public static final int insert(Connection conn, String sql, PreparedExecution paramsSetter)
            throws SQLRuntimeException {
        return update(conn, sql, paramsSetter);
    }

    /**
     * Execute an insert SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keyHolder}.
     * <p>
     * This method is equivalent to
     * {@link #update(Connection, String, PreparedExecution, IntConsumer)}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keyHolder    a {@link IntConsumer} to holding the first generated key
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     * 
     * @see #update(Connection, String, PreparedExecution, IntConsumer)
     */
    public static final int insert(Connection conn, String sql, PreparedExecution paramsSetter, IntConsumer keyHolder)
            throws SQLRuntimeException {
        return update(conn, sql, paramsSetter, keyHolder);
    }

    /**
     * Execute an insert SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keyHolder}.
     * 
     * <p>
     * This method is equivalent to
     * {@link #update(Connection, String, PreparedExecution, LongConsumer)}
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keyHolder    a {@link LongConsumer} to holding the first generated key
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     * 
     * @see #update(Connection, String, PreparedExecution, LongConsumer)
     */
    public static final int insert(Connection conn, String sql, PreparedExecution paramsSetter, LongConsumer keyHolder)
            throws SQLRuntimeException {
        return update(conn, sql, paramsSetter, keyHolder);
    }

    /**
     * Execute an insert SQL, setting parameters by a {@link PreparedExecution}. The
     * first generated key will be put into the given {@code keysHolder}.
     * 
     * <p>
     * This method is equivalent to
     * {@link #update(Connection, String, PreparedExecution, ResultExecution)}
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @param keysHolder    a {@link ResultExecution} to holding all generated keys
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     * 
     * @see #update(Connection, String, PreparedExecution, ResultExecution)
     */
    public static final int insert(Connection conn, String sql, PreparedExecution paramsSetter,
            ResultExecution keysHolder) throws SQLRuntimeException {
        return update(conn, sql, paramsSetter, keysHolder);
    }

    /**
     * Execute an delete SQL, setting parameters by a {@link PreparedExecution}.
     * <p>
     * This method is equivalent to
     * {@link #update(Connection, String, PreparedExecution)}.
     * 
     * @param conn         the SQL connection
     * @param sql          the SQL to execute
     * @param paramsSetter the {@link PreparedExecution} to set parameters
     * @return the number of rows affected
     * @throws SQLRuntimeException if a database access error occurs
     * 
     * @see #update(Connection, String, PreparedExecution)
     */
    public static final int delete(Connection conn, String sql, PreparedExecution paramsSetter)
            throws SQLRuntimeException {
        return update(conn, sql, paramsSetter);
    }

    private JdbcUtil() {
    }

}
