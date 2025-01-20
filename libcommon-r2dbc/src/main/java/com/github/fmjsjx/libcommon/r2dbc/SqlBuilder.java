package com.github.fmjsjx.libcommon.r2dbc;

import io.r2dbc.spi.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A tool that provides assistance for building SQLs.
 *
 * @author MJ Fang
 * @since 3.11
 */
public class SqlBuilder {

    private static final Pattern QUESTION_MARK_PATTERN = Pattern.compile("\\?");

    private static final int NONE = 0;
    private static final int SUBQUERY = 1;
    private static final int GROUP = 2;
    private static final int WHERE_CLAUSE = 3;
    private static final int SET_CLAUSE = 4;

    private static final List<String> questionMarkGroups;

    static {
        var groups = new String[32];
        for (int i = 0; i < 32; i++) {
            var group = new String[i + 1];
            Arrays.fill(group, "?");
            groups[i] = String.join(", ", group);
        }
        questionMarkGroups = List.of(groups);
    }

    static final String questionMarks(int count) {
        if (count < 1) {
            return "";
        } else if (count <= 32) {
            return questionMarkGroups.get(count - 1);
        } else {
            var b = new StringBuilder().append("?");
            for (int i = 1; i < count; i++) {
                b.append(", ").append("?");
            }
            return b.toString();
        }
    }

    private final List<String> sqlParts;
    private final List<Object> values;
    private final SqlBuilder parent;
    private final int mode;

    private ParameterStyle parameterStyle = ParameterStyle.NONE;

    // select part
    private List<String> selectColumns;
    private boolean distinct;

    /**
     * Constructs a new {@link SqlBuilder} instance.
     */
    public SqlBuilder() {
        this(new ArrayList<>(), new ArrayList<>(), null, NONE);
    }

    SqlBuilder(SqlBuilder parent) {
        this(parent, SUBQUERY);
    }

    SqlBuilder(SqlBuilder parent, int mode) {
        this(new ArrayList<>(), new ArrayList<>(), parent, mode);
    }

    SqlBuilder(List<String> sqlParts, List<Object> values, SqlBuilder parent, int mode) {
        this.sqlParts = sqlParts;
        this.values = values;
        if (mode > 0) {
            if (parent == null) {
                var modeName = "";
                if (mode == SUBQUERY) {
                    modeName = "subquery";
                } else if (mode == GROUP) {
                    modeName = "group";
                } else if (mode == WHERE_CLAUSE) {
                    modeName = "where clause";
                } else if (mode == SET_CLAUSE) {
                    modeName = "set clause";
                }
                throw new IllegalArgumentException("parent must not be null in " + modeName + " mode");
            }
        }
        this.parent = parent;
        this.mode = mode;
    }

    /**
     * Sets the parameter style.
     *
     * @param parameterStyle the {@link ParameterStyle}
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder parameterStyle(ParameterStyle parameterStyle) {
        this.parameterStyle = Objects.requireNonNull(parameterStyle, "parameterStyle must not be null");
        return this;
    }

    /**
     * Returns the parameter style.
     *
     * @return the parameter style
     */
    public ParameterStyle getParameterStyle() {
        return parameterStyle;
    }

    /**
     * Returns an immutable list copy of the {@code sqlParts}.
     *
     * @return an immutable list copy of the {@code sqlParts}
     */
    public List<String> getSqlParts() {
        return List.copyOf(sqlParts);
    }

    /**
     * Returns an immutable list copy of the {@code values}.
     *
     * @return an immutable list copy of the {@code values}
     */
    public List<Object> getValues() {
        return List.copyOf(values);
    }

    /**
     * Append the specified sql part string into the {@code sqlParts}.
     *
     * @param sqlPart the sql part string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendSql(String sqlPart) {
        sqlParts.add(Objects.requireNonNull(sqlPart, "sqlPart must not be null"));
        return this;
    }

    /**
     * Append the specified sql part strings into the {@code sqlParts}.
     *
     * @param sqlParts the array of sql part strings
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendSql(String... sqlParts) {
        for (var sqlPart : sqlParts) {
            appendSql(sqlPart);
        }
        return this;
    }

    /**
     * Append the specified sql part strings into the {@code sqlParts}.
     *
     * @param sqlParts the list contains sql part strings
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendSql(List<String> sqlParts) {
        for (var sqlPart : sqlParts) {
            appendSql(sqlPart);
        }
        return this;
    }

    /**
     * An alias for the method {@link #appendSql(String)}.
     *
     * @param sqlPart the sql part string
     * @return this {@link SqlBuilder}
     * @see #appendSql(String)
     */
    public SqlBuilder s(String sqlPart) {
        return appendSql(sqlPart);
    }

    /**
     * An alias for the method {@link #appendSql(String...)}.
     *
     * @param sqlParts the array of sql part strings
     * @return this {@link SqlBuilder}
     * @see #appendSql(String...)
     */
    public SqlBuilder s(String... sqlParts) {
        return appendSql(sqlParts);
    }

    /**
     * An alias for the method {@link #appendSql(List)}.
     *
     * @param sqlParts the list contains sql part strings
     * @return this {@link SqlBuilder}
     * @see #appendSql(List)
     */
    public SqlBuilder s(List<String> sqlParts) {
        return appendSql(sqlParts);
    }

    /**
     * Add a value as a parameter identified by its {@code index}.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder addValue(Object value) {
        if (value == null) {
            values.add(Parameters.in(Object.class));
        } else if (value instanceof Class<?> type) {
            values.add(Parameters.in(type));
        } else {
            values.add(value);
        }
        return this;
    }

    /**
     * Add values as parameters identified by their {@code index}.
     *
     * @param values an array of values
     * @return this {@link SqlBuilder}
     * @see #addValue(Object)
     */
    public SqlBuilder addValues(Object... values) {
        for (var value : values) {
            addValue(value);
        }
        return this;
    }

    /**
     * Add values as parameters identified by their {@code index}.
     *
     * @param values a list contains values
     * @return this {@link SqlBuilder}
     * @see #addValue(Object)
     */
    public SqlBuilder addValues(List<Object> values) {
        for (var value : values) {
            addValue(value);
        }
        return this;
    }

    /**
     * An alias for the method {@link #addValue(Object)}.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     * @see #addValue(Object)
     */
    public SqlBuilder v(Object value) {
        return addValue(value);
    }

    /**
     * An alias for the method {@link #addValues(Object...)}.
     *
     * @param values an array of values
     * @return this {@link SqlBuilder}
     * @see #addValue(Object)
     * @see #addValues(Object...)
     */
    public SqlBuilder v(Object... values) {
        return addValues(values);
    }

    /**
     * An alias for the method {@link #addValues(List)}.
     *
     * @param values a list contains values
     * @return this {@link SqlBuilder}
     * @see #addValue(Object)
     * @see #addValues(List)
     */
    public SqlBuilder v(List<Object> values) {
        return addValues(values);
    }

    /**
     * Append the specified sql part string into the {@code sqlParts}
     * and then add values as parameters identified by their {@code index}.
     * <p>
     * This method is equivalent to: <pre>{@code
     * appendSql(sqlPart).addValues(values);
     * }</pre>
     *
     * @param sqlPart the sql part string
     * @param values  an array of values
     * @return this {@link SqlBuilder}
     * @see #appendSql(String)
     * @see #addValues(List)
     */
    public SqlBuilder appendSqlValues(String sqlPart, Object... values) {
        return appendSql(sqlPart).addValues(values);
    }

    /**
     * Append the specified sql part string into the {@code sqlParts}
     * and then add values as parameters identified by their {@code index}.
     * <p>
     * This method is equivalent to: <pre>{@code
     * appendSql(sqlPart).addValues(values);
     * }</pre>
     *
     * @param sqlPart the sql part string
     * @param values  a list contains values
     * @return this {@link SqlBuilder}
     * @see #appendSql(String)
     * @see #addValues(List)
     */
    public SqlBuilder appendSqlValues(String sqlPart, List<Object> values) {
        return appendSql(sqlPart).addValues(values);
    }

    /**
     * An alias for the method {@link #appendSqlValues(String, Object...)}.
     *
     * @param sqlPart the sql part string
     * @param values  an array of values
     * @return this {@link SqlBuilder}
     * @see #appendSql(String)
     * @see #addValues(List)
     * @see #appendSqlValues(String, Object...)
     */
    public SqlBuilder sv(String sqlPart, Object... values) {
        return appendSqlValues(sqlPart, values);
    }

    /**
     * An alias for the method {@link #appendSqlValues(String, List)}.
     *
     * @param sqlPart the sql part string
     * @param values  a list contains values
     * @return this {@link SqlBuilder}
     * @see #appendSql(String)
     * @see #addValues(List)
     * @see #appendSqlValues(String, List)
     */
    public SqlBuilder sv(String sqlPart, List<Object> values) {
        return appendSqlValues(sqlPart, values);
    }

    /**
     * Append another {@link SqlBuilder} into this.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()} call at
     * first.
     *
     * @param other the another {@code SqlBuilder}
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder append(SqlBuilder other) {
        return finishSelect().s(other.sqlParts).v(other.values);
    }

    /**
     * Finish {@code SELECT} part of the SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder finishSelect() {
        var selectColumns = this.selectColumns;
        this.selectColumns = null;
        if (selectColumns != null) {
            if (distinct) {
                s("DISTINCT");
            }
            s(String.join(", ", selectColumns));
        }
        return this;
    }

    /**
     * Build and returns the SQL string.
     *
     * @return the SQL string
     */
    public String buildSql() {
        var parameterStyle = this.parameterStyle;
        if (parameterStyle.isPrefixed()) {
            return buildSql(parameterStyle.getPrefix(), parameterStyle.getBaseIndex());
        }
        return buildSql0();
    }

    private String buildSql0() {
        return String.join(" ", sqlParts);
    }

    /**
     * Build and returns the SQL string uses index parameters that are
     * prefixed with the specified {@code parameterPrefix} given.
     *
     * @param parameterPrefix the prefix of the parameter
     * @param baseIndex       the base index
     * @return the SQL string
     */
    public String buildSql(String parameterPrefix, int baseIndex) {
        var sql = buildSql0();
        var valueSize = values.size();
        if (valueSize == 0) {
            return sql;
        }
        var matcher = QUESTION_MARK_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return sql;
        }
        var lastStart = 0;
        var length = sql.length();
        var sb = new StringBuilder(length + valueSize);
        var index = baseIndex;
        do {
            sb.append(sql, lastStart, matcher.start())
                    .append(parameterPrefix).append(index++);
            lastStart = matcher.end();
        } while (matcher.find());
        if (lastStart < length) {
            sb.append(sql, lastStart, length);
        }
        return sb.toString();
    }

    /**
     * Creates and returns a new {@link SqlBuilder} in subquery mode.
     *
     * @return a new {@link SqlBuilder} in subquery mode
     */
    public SqlBuilder subquery() {
        s("(");
        return new SqlBuilder(this);
    }

    /**
     * End the current subquery and returns the parent {@link SqlBuilder}.
     *
     * @param name the name of the subquery
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endSubquery(String name) {
        if (mode != SUBQUERY) {
            throw new IllegalStateException("not in a subquery");
        }
        return parent.append(this).s(")", name);
    }

    /**
     * Append {@code SELECT} and initialize the select part of the SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder select() {
        if (selectColumns == null) {
            selectColumns = new ArrayList<>();
            s("SELECT");
        }
        return this;
    }

}
