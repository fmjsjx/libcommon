package com.github.fmjsjx.libcommon.r2dbc;

import com.github.fmjsjx.libcommon.util.StringUtil;
import com.github.fmjsjx.libcommon.util.concurrent.EasyThreadLocal;
import io.r2dbc.spi.Parameters;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
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
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endSubquery() {
        if (mode != SUBQUERY) {
            throw new IllegalStateException("not in a subquery");
        }
        return parent.append(this).s(")");
    }

    /**
     * End the current subquery and returns the parent {@link SqlBuilder}.
     *
     * @param name the name of the subquery
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endSubquery(String name) {
        return endSubquery().s(name);
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

    /**
     * Sets {@code distinct}.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder distinct() {
        this.distinct = true;
        return this;
    }

    private List<String> ensureSelectColumns() {
        var selectColumns = this.selectColumns;
        if (selectColumns == null) {
            throw new IllegalStateException("not at select step");
        }
        return selectColumns;
    }

    /**
     * Append columns in the select part of the SQL.
     *
     * @param columns the columns SQL string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendColumns(String columns) {
        ensureSelectColumns().add(columns);
        return this;
    }

    /**
     * Append columns in the select part of the SQL.
     *
     * @param columns the array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendColumns(String... columns) {
        Collections.addAll(ensureSelectColumns(), columns);
        return this;
    }

    /**
     * Append columns in the select part of the SQL.
     *
     * @param columns the list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendColumns(List<String> columns) {
        ensureSelectColumns().addAll(columns);
        return this;
    }

    /**
     * Select columns.
     *
     * @param columns the columns SQL string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder select(String columns) {
        return select().appendColumns(columns);
    }

    /**
     * Functionally equals to: {@code SELECT *}.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectAll() {
        return select("*");
    }

    /**
     * Select columns.
     *
     * @param columns the array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder select(String... columns) {
        return select().appendColumns(columns);
    }

    /**
     * Select columns.
     *
     * @param columns the list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder select(List<String> columns) {
        return select().appendColumns(columns);
    }

    /**
     * Functionally equals to: {@code SELECT COUNT(*)}.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectCount() {
        return select("COUNT(*)");
    }

    /**
     * Select count with columns.
     *
     * @param columns the columns SQL string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectCount(String columns) {
        return select().appendColumns("COUNT(" + columns + ")");
    }

    /**
     * Select count with columns.
     *
     * @param columns the array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectCount(String... columns) {
        return select().selectCount(String.join(", ", columns));
    }

    /**
     * Select count with columns.
     *
     * @param columns the list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectCount(List<String> columns) {
        return select().selectCount(String.join(", ", columns));
    }

    /**
     * Select distinct columns.
     *
     * @param columns the columns SQL string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectDistinct(String columns) {
        return select().distinct().appendColumns(columns);
    }

    /**
     * Select distinct columns.
     *
     * @param columns the array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectDistinct(String... columns) {
        return select().distinct().appendColumns(columns);
    }

    /**
     * Select distinct columns.
     *
     * @param columns the list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder selectDistinct(List<String> columns) {
        return select().distinct().appendColumns(columns);
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from() {
        return finishSelect().s("FROM");
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @param table the table SQL string
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from(String table) {
        return finishSelect().s("FROM", table);
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @param tables the array of tables
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from(String... tables) {
        return finishSelect().s("FROM", String.join(", ", tables));
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @param tables the list contains tables
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from(List<String> tables) {
        return finishSelect().s("FROM", String.join(", ", tables));
    }

    /**
     * Get the table name from the entity class.
     *
     * @param entityClass the entity class
     * @return the table name
     */
    public static final String getTableName(Class<?> entityClass) {
        return TableNamesHolder.CACHE.computeIfAbsent(entityClass, TableNamesHolder::getTableName);
    }

    static final class TableNamesHolder {
        static final ConcurrentMap<Class<?>, String> CACHE = new ConcurrentHashMap<>();

        static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");

        static final String toLowercaseUnderline(String name) {
            var tmpName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            return UPPER_CASE.matcher(tmpName).replaceAll(matchResult -> "_" + matchResult.group().toLowerCase());
        }

        static final String getTableName(Class<?> entityClass) {
            var tableName = "";
            var schema = "";
            if (entityClass.isAnnotationPresent(Table.class)) {
                var table = entityClass.getAnnotation(Table.class);
                schema = table.schema();
                tableName = table.name();
                if (StringUtil.isEmpty(tableName)) {
                    tableName = table.value();
                }
            }
            if (StringUtil.isEmpty(tableName)) {
                tableName = toLowercaseUnderline(entityClass.getSimpleName());
            }
            return StringUtil.isEmpty(schema) ? tableName : schema + "." + tableName;
        }
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @param entityClass the entity class from which to get the table name
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from(Class<?> entityClass) {
        return from(getTableName(entityClass));
    }

    /**
     * Finish select part and append {@code FROM} into SQL.
     * <p>
     * <B>Note</B>: This method will trigger a {@link #finishSelect()}
     * call at first.
     *
     * @param entityClasses the array of entity classes from which to get
     *                      the table names
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder from(Class<?>... entityClasses) {
        var tableNames = new String[entityClasses.length];
        for (int i = 0; i < entityClasses.length; i++) {
            tableNames[i] = getTableName(entityClasses[i]);
        }
        return from(tableNames);
    }

    private SqlBuilder join(String type, String table) {
        return s(type, "JOIN", table);
    }

    private SqlBuilder join(String type, SqlBuilder subqueryBuilder, String tableAlias) {
        return s(type, "JOIN (").append(subqueryBuilder).s(")", tableAlias);
    }

    private SqlBuilder joinSubquery(String type) {
        return s(type, "JOIN").subquery();
    }

    /**
     * Append {@code INNER JOIN} into SQL.
     *
     * @param table the table
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder innerJoin(String table) {
        return join("INNER", table);
    }

    /**
     * Append {@code INNER JOIN} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} in subquery mode to
     *                        be joined
     * @param tableAlias      the alias of the subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder innerJoin(SqlBuilder subqueryBuilder, String tableAlias) {
        return join("INNER", subqueryBuilder, tableAlias);
    }

    /**
     * Append {@code INNER JOIN} into SQL and returns the subquery
     * {@link SqlBuilder}.
     *
     * @return the {@link SqlBuilder} in subquery mode to be joined
     */
    public SqlBuilder innerJoinSubquery() {
        return joinSubquery("INNER");
    }

    /**
     * Append {@code LEFT JOIN} into SQL.
     *
     * @param table the table
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder leftJoin(String table) {
        return join("LEFT", table);
    }

    /**
     * Append {@code LEFT JOIN} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} in subquery mode to
     *                        be joined
     * @param tableAlias      the alias of the subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder leftJoin(SqlBuilder subqueryBuilder, String tableAlias) {
        return join("LEFT", subqueryBuilder, tableAlias);
    }

    /**
     * Append {@code LEFT JOIN} into SQL and returns the subquery
     * {@link SqlBuilder}.
     *
     * @return the {@link SqlBuilder} in subquery mode to be joined
     */
    public SqlBuilder leftJoinSubquery() {
        return joinSubquery("LEFT");
    }

    /**
     * Append {@code RIGHT JOIN} into SQL.
     *
     * @param table the table
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder rightJoin(String table) {
        return join("RIGHT", table);
    }

    /**
     * Append {@code RIGHT JOIN} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} in subquery mode to
     *                        be joined
     * @param tableAlias      the alias of the subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder rightJoin(SqlBuilder subqueryBuilder, String tableAlias) {
        return join("RIGHT", subqueryBuilder, tableAlias);
    }

    /**
     * Append {@code RIGHT JOIN} into SQL and returns the subquery
     * {@link SqlBuilder}.
     *
     * @return the {@link SqlBuilder} in subquery mode to be joined
     */
    public SqlBuilder rightJoinSubquery() {
        return joinSubquery("RIGHT");
    }

    /**
     * Append {@code ON} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder on() {
        return s("ON");
    }

    /**
     * Append {@code ON} and search conditions into SQL.
     *
     * @param conditions the search conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder on(String conditions) {
        return s("ON", conditions);
    }

    /**
     * Append {@code USING} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder using() {
        return s("USING");
    }

    /**
     * Append {@code USING} clause into SQL with the specified columns.
     *
     * @param columns an array of columns that must exist in both tables
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder using(String... columns) {
        return s("USING", "(", String.join(", ", columns), ")");
    }

    /**
     * Append {@code USING} clause into SQL with the specified columns.
     *
     * @param columns a list of columns that must exist in both tables
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder using(List<String> columns) {
        return s("USING", "(", String.join(", ", columns), ")");
    }

    /**
     * Append {@code WHERE} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder where() {
        return s("WHERE");
    }

    /**
     * Append {@code WHERE} clause into SQL with the specified conditions.
     *
     * @param conditions the where conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder where(String conditions) {
        return s("WHERE", conditions);
    }

    /**
     * Append {@code WHERE} clause into SQL with the specified
     * conditions, joined by {@code AND}.
     *
     * @param conditions an array of the where conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder where(String... conditions) {
        return where(String.join(" AND ", conditions));
    }

    /**
     * Append {@code WHERE} clause into SQL and returns the
     * {@link SqlBuilder} in {@code WHERE CLAUSE} mode.
     *
     * @return the {@link SqlBuilder} in {@code WHERE CLAUSE} mode
     */
    public SqlBuilder whereClause() {
        return new SqlBuilder(this, WHERE_CLAUSE);
    }

    /**
     * End the current where clause and returns the parent
     * {@link SqlBuilder}.
     *
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endWhereClause() {
        return endClause("WHERE", WHERE_CLAUSE, "not in a where clause", "AND");
    }

    SqlBuilder endClause(String key, int mode, String errorMessage, String delimiter) {
        if (this.mode != mode) {
            throw new IllegalStateException(errorMessage);
        }
        var parent = this.parent;
        if (sqlParts.isEmpty()) {
            return parent;
        }
        parent.s(key);
        if (delimiter.equalsIgnoreCase(sqlParts.get(0))) {
            sqlParts.stream().skip(1).forEach(parent::s);
        } else {
            parent.s(sqlParts);
        }
        return parent.v(values);
    }

    /**
     * Append {@code AND} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder and() {
        return s("AND");
    }

    /**
     * Append {@code OR} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder or() {
        return s("OR");
    }

    /**
     * Append specified column string into SQL.
     * <p>
     * An alias for the method {@link #appendSql(String)}.
     * <p>
     * This method is equivalent to: <pre>{@code
     * appendSql(column);
     * }</pre>
     *
     * @param column the column
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder column(String column) {
        return appendSql(column);
    }

    /**
     * Append {@code ANY} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder any() {
        return s("ANY");
    }

    /**
     * Append {@code SOME} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder some() {
        return s("SOME");
    }

    /**
     * Append {@code ALL} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder all() {
        return s("ALL");
    }

    /**
     * Append {@code NOT} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder not() {
        return s("NOT");
    }

    /**
     * Append {@code EXISTS} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder exists() {
        return s("EXISTS");
    }

    /**
     * Append {@code IS NULL} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder isNull() {
        return s("IS NULL");
    }

    /**
     * Append {@code IS NOT NULL} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder isNotNull() {
        return s("IS NOT NULL");
    }

    /**
     * Append {@code =} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder eq(Object value) {
        return sv("= ?", value);
    }

    /**
     * Append {@code <>} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder ne(Object value) {
        return sv("<> ?", value);
    }

    /**
     * Append {@code >} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder gt(Object value) {
        return sv("> ?", value);
    }

    /**
     * Append {@code >=} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder ge(Object value) {
        return sv(">= ?", value);
    }

    /**
     * Append {@code <} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder lt(Object value) {
        return sv("< ?", value);
    }

    /**
     * Append {@code <=} comparison into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder le(Object value) {
        return sv("<= ?", value);
    }

    /**
     * Append {@code LIKE} predicate into SQL.
     *
     * @param value a scalar value, a parameter type for a {@code null}
     *              value or a {@code null} value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder like(Object value) {
        return sv("LIKE ?", value);
    }

    /**
     * Quick invocation to append "any" {@code LIKE} predicate into SQL.
     * <p>
     * This method is equivalent to: <pre>{@code
     * like("%" + value + "%");
     * }</pre>
     *
     * @param value a string value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder anyLike(Object value) {
        return like("%" + value + "%");
    }

    /**
     * Quick invocation to append "begin" {@code LIKE} predicate into SQL.
     * <p>
     * This method is equivalent to: <pre>{@code
     * like(value + "%");
     * }</pre>
     *
     * @param value a string value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder beginLike(Object value) {
        return like(value + "%");
    }

    /**
     * Quick invocation to append "end" {@code LIKE} predicate into SQL.
     * <p>
     * This method is equivalent to: <pre>{@code
     * like("%" + value);
     * }</pre>
     *
     * @param value a string value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder endLike(Object value) {
        return like("%" + value);
    }

    /**
     * Quick invocation to append "any" {@code LIKE} predicate into SQL
     * by using {@code CONCAT} function.
     * <p>
     * This method is equivalent to: <pre>{@code
     * appendSqlValues("LIKE CONCAT('%', ?, '%')", value);
     * }</pre>
     *
     * @param value a string value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder likeConcat(Object value) {
        return sv("LIKE CONCAT('%', ?, '%')", value);
    }

    /**
     * Append {@code IN} predicate into SQL.
     *
     * @param values an array of values
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder in(Object... values) {
        return s("IN", "(", questionMarks(values.length), ")").v(values);
    }

    /**
     * Append {@code IN} predicate into SQL.
     *
     * @param values a list contains values
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder in(List<Object> values) {
        return s("IN", "(", questionMarks(values.size()), ")").v(values);
    }

    /**
     * Append {@code IN} predicate into SQL.
     *
     * @param subqueryBuilder a {@link SqlBuilder} in subquery mode
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder in(SqlBuilder subqueryBuilder) {
        return s("IN", "(").append(subqueryBuilder).s(")");
    }

    /**
     * Append {@code IN} predicate into SQL and returns the
     * {@link SqlBuilder} builds the subquery.
     *
     * @return the {@link SqlBuilder} builds the subquery
     */
    public SqlBuilder inSubquery() {
        return s("IN").subquery();
    }

    /**
     * Append {@code BETWEEN} predicate into SQL.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder between(Object value1, Object value2) {
        return sv("BETWEEN ? AND ?", value1, value2);
    }

    /**
     * Returns a new {@link SqlBuilder} in group mode.
     *
     * @return a new {@link SqlBuilder} in group mode
     */
    public SqlBuilder beginGroup() {
        return new SqlBuilder(s("("), GROUP);
    }

    /**
     * End the current group and returns the parent {@link SqlBuilder}.
     *
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endGroup() {
        if (mode != GROUP) {
            throw new IllegalStateException("not in group");
        }
        return parent.append(this).s(")");
    }

    /**
     * End all groups and returns the top level {@link SqlBuilder}.
     *
     * @return the top level {@link SqlBuilder}
     */
    public SqlBuilder endAllGroups() {
        var current = this;
        for (; current.mode == GROUP; current = current.parent) {
            current.parent.append(current).s(")");
        }
        return current;
    }

    /**
     * Append {@code AS} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder as() {
        return s("AS");
    }

    /**
     * Append {@code WITH} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder with() {
        return s("WITH");
    }

    /**
     * Append {@code GROUP BY} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder groupBy() {
        return s("GROUP BY");
    }

    /**
     * Append {@code GROUP BY} clause into SQL with the specified columns.
     *
     * @param columns the columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder groupBy(String columns) {
        return groupBy().s(columns);
    }

    /**
     * Append {@code GROUP BY} clause into SQL with the specified columns.
     *
     * @param columns an array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder groupBy(String... columns) {
        return groupBy().s(String.join(", ", columns));
    }

    /**
     * Append {@code GROUP BY} clause into SQL with the specified columns.
     *
     * @param columns a list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder groupBy(List<String> columns) {
        return groupBy().s(String.join(", ", columns));
    }

    /**
     * Append {@code HAVING} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder having() {
        return s("HAVING");
    }

    /**
     * Append {@code HAVING} clause into SQL with the specified conditions.
     *
     * @param conditions the conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder having(String conditions) {
        return s("HAVING", conditions);
    }

    /**
     * Append {@code HAVING} clause into SQL with the specified conditions.
     *
     * @param conditions an array of conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder having(String... conditions) {
        return having(String.join(" AND ", conditions));
    }

    /**
     * Append {@code HAVING} clause into SQL with the specified conditions.
     *
     * @param conditions a list contains conditions
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder having(List<String> conditions) {
        return having(String.join(" AND ", conditions));
    }

    /**
     * Append {@code ORDER BY} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder orderBy() {
        return s("ORDER BY");
    }

    /**
     * Append {@code ORDER BY} clause into SQL with the specified orders.
     *
     * @param orders the orders
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder orderBy(String orders) {
        return orderBy().s(orders);
    }

    /**
     * Append {@code ORDER BY} clause into SQL with the specified orders.
     *
     * @param orders an array of orders
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder orderBy(String... orders) {
        return orderBy().s(String.join(", ", orders));
    }

    /**
     * Append {@code ORDER BY} clause into SQL with the specified orders.
     *
     * @param orders a list contains orders
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder orderBy(List<String> orders) {
        return orderBy().s(String.join(", ", orders));
    }

    /**
     * Append {@code OFFSET} clause into SQL with the specified offset.
     *
     * @param offset the offset
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder offset(long offset) {
        return sv("OFFSET ?", offset);
    }

    /**
     * Append {@code LIMIT} clause into SQL with the specified limit.
     *
     * @param limit the limit
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder limit(int limit) {
        return sv("LIMIT ?", limit);
    }

    /**
     * Append {@code LIMIT} clause into SQL with the specified offset and
     * specified limit.
     *
     * @param offset the offset
     * @param limit  the limit
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder limit(long offset, int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be greater than or equal to 0");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        if (offset == 0) {
            return limit(limit);
        }
        return sv("LIMIT ?, ?", offset, limit);
    }

    /**
     * Append {@code UNION} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder union() {
        return s("UNION");
    }

    /**
     * Append {@code UNION} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} stores subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder union(SqlBuilder subqueryBuilder) {
        return union().s("(").append(subqueryBuilder).s(")");
    }

    /**
     * Append {@code UNION} clause into SQL and returns the
     * {@link SqlBuilder} in subquery mode.
     *
     * @return a {@link SqlBuilder} in subquery mode
     */
    public SqlBuilder unionSubquery() {
        return union().subquery();
    }

    /**
     * Append {@code UNION DISTINCT} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder unionDistinct() {
        return s("UNION", "DISTINCT");
    }

    /**
     * Append {@code UNION DISTINCT} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} stores subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder unionDistinct(SqlBuilder subqueryBuilder) {
        return unionDistinct().s("(").append(subqueryBuilder).s(")");
    }

    /**
     * Append {@code UNION DISTINCT} clause into SQL and returns the
     * {@link SqlBuilder} in subquery mode.
     *
     * @return a {@link SqlBuilder} in subquery mode
     */
    public SqlBuilder unionDistinctSubquery() {
        return unionDistinct().subquery();
    }

    /**
     * Append {@code UNION ALL} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder unionAll() {
        return s("UNION", "ALL");
    }

    /**
     * Append {@code UNION ALL} into SQL.
     *
     * @param subqueryBuilder the {@link SqlBuilder} stores subquery
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder unionAll(SqlBuilder subqueryBuilder) {
        return unionAll().s("(").append(subqueryBuilder).s(")");
    }

    /**
     * Append {@code UNION ALL} clause into SQL and returns the
     * {@link SqlBuilder} in subquery mode.
     *
     * @return a {@link SqlBuilder} in subquery mode
     */
    public SqlBuilder unionAllSubquery() {
        return unionAll().subquery();
    }

    /**
     * Append {@code INSERT} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder insert() {
        return s("INSERT");
    }

    /**
     * Append {@code INSERT} into SQL with the specified options.
     *
     * @param options an array of options
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder insert(String... options) {
        return insert().s(options);
    }

    /**
     * Append {@code IGNORE} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder ignore() {
        return s("IGNORE");
    }

    /**
     * Append {@code INTO} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder into() {
        return s("INTO");
    }

    /**
     * Append {@code INTO} clause into SQL with the specified tableName.
     *
     * @param tableName the name of the table
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder into(String tableName) {
        return into().s(tableName);
    }

    /**
     * Append {@code INTO} clause into SQL with the table name be
     * extracted from the specified entityClass.
     *
     * @param entityClass the entity class
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder into(Class<?> entityClass) {
        return into(getTableName(entityClass));
    }

    /**
     * Append {@code PARTITION} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder partition() {
        return s("PARTITION");
    }

    /**
     * Append {@code PARTITION} clause into SQL with the specified
     * partitionName.
     *
     * @param partitionName the partition name
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder partition(String partitionName) {
        return partition().s("(", partitionName, ")");
    }

    /**
     * Append {@code PARTITION} clause into SQL with the specified
     * partitionNames.
     *
     * @param partitionNames an array of partition names
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder partition(String... partitionNames) {
        return partition().s("(", String.join(", ", partitionNames), ")");
    }

    /**
     * Append {@code PARTITION} clause into SQL with the specified
     * partitionNames.
     *
     * @param partitionNames a list contains partition names
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder partition(List<String> partitionNames) {
        return partition().s("(", String.join(", ", partitionNames), ")");
    }

    /**
     * Append columns into SQL.
     *
     * @param columns the columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder columns(String columns) {
        return s("(", columns, ")");
    }

    /**
     * Append columns into SQL.
     *
     * @param columns an array of columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder columns(String... columns) {
        return columns(String.join(", ", columns));
    }

    /**
     * Append columns into SQL.
     *
     * @param columns a list contains columns
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder columns(List<String> columns) {
        return columns(String.join(", ", columns));
    }

    /**
     * Append columns extracted from the specified entityClass into SQL.
     *
     * @param entityClass the entity class
     * @param <E>         the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder columns(Class<E> entityClass) {
        var info = getRequiredPersistentEntityInfo(entityClass);
        var columns = info.getColumns().stream().map(PersistentColumnInfo::getColumnName).collect(Collectors.joining(", "));
        return columns(columns);
    }

    static <E> PersistentEntityInfo<E> getRequiredPersistentEntityInfo(Class<E> entityClass) {
        return getPersistentEntityInfo(entityClass)
                .orElseThrow(() -> new IllegalStateException("Cannot extract persistent columns for class " + entityClass));
    }

    static final class PersistentEntityInfoCacheHolder {
        static final Object EMPTY = new Object();
        static final ConcurrentMap<Class<?>, Object> cache = new ConcurrentHashMap<>();
        static final EasyThreadLocal<Set<Object>> threadLocalSet = EasyThreadLocal.create(ConcurrentHashMap::newKeySet);
    }

    @SuppressWarnings("unchecked")
    static final <E> Optional<PersistentEntityInfo<E>> getPersistentEntityInfo(Class<E> entityClass) {
        var info = PersistentEntityInfoCacheHolder.cache.get(entityClass);
        if (info == null) {
            info = createPersistentEntityInfo(entityClass);
            PersistentEntityInfoCacheHolder.cache.put(entityClass, info);
        }
        if (info == PersistentEntityInfoCacheHolder.EMPTY) {
            return Optional.empty();
        }
        return Optional.of((PersistentEntityInfo<E>) info);
    }

    static final <E> Object createPersistentEntityInfo(Class<E> entityClass) {
        var creatingSet = PersistentEntityInfoCacheHolder.threadLocalSet.get();
        if (!creatingSet.add(entityClass)) {
            throw new IllegalArgumentException("a circular reference occurs on entity class " + entityClass);
        }
        try {
            var columnBuilders = new ArrayList<PersistentColumnInfo.Builder>();
            var parentClass = entityClass.getSuperclass();
            if (!parentClass.equals(Object.class)) {
                getPersistentEntityInfo(parentClass).ifPresent(info ->
                        info.getColumns().stream().map(PersistentColumnInfo::toBuilder).forEach(columnBuilders::add));
            }
            var fields = entityClass.getDeclaredFields();
            for (var field : fields) {
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
                    continue;
                }
                if (field.isAnnotationPresent(Id.class) ||
                        field.isAnnotationPresent(Transient.class) ||
                        field.isAnnotationPresent(ReadOnlyProperty.class)) {
                    continue;
                }
                if (tryAppendEmbeddedColumns(entityClass, field, columnBuilders)) {
                    continue;
                }
                var columnName = field.getName();
                if (field.isAnnotationPresent(Column.class)) {
                    var columnAnnotation = field.getAnnotation(Column.class);
                    columnName = columnAnnotation.value();
                }
                var valueGetter = getValueGetter(entityClass, field);
                if (valueGetter == null) {
                    continue;
                }
                var insertOnly = field.isAnnotationPresent(InsertOnlyProperty.class);
                var builder = PersistentColumnInfo.builder()
                        .columnName(columnName)
                        .valueGetter(valueGetter)
                        .insertOnly(insertOnly);
                columnBuilders.add(builder);
            }
            if (columnBuilders.isEmpty()) {
                return PersistentEntityInfoCacheHolder.EMPTY;
            }
            return new PersistentEntityInfo<>(entityClass, columnBuilders);
        } finally {
            creatingSet.remove(entityClass);
        }
    }

    static final <E> Function<E, Object> getValueGetter(Class<E> entityClass, Field field) {
        var valueGetter = tryUseGetter(entityClass, field);
        if (valueGetter == null) {
            // the field hasn't a getter method, try using the field itself
            if (!field.trySetAccessible()) {
                // can't access this field, just skip it
                return null;
            }
            valueGetter = (e) -> {
                try {
                    return field.get(e);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            };
        }
        return valueGetter;
    }

    static final <E> boolean tryAppendEmbeddedColumns(Class<E> entityClass, Field field,
                                                      List<PersistentColumnInfo.Builder> columnBuilders) {
        String prefix = null;
        if (field.isAnnotationPresent(Embedded.class)) {
            prefix = field.getAnnotation(Embedded.class).prefix();
        } else if (field.isAnnotationPresent(Embedded.Empty.class)) {
            prefix = field.getAnnotation(Embedded.Empty.class).prefix();
        } else if (field.isAnnotationPresent(Embedded.Nullable.class)) {
            prefix = field.getAnnotation(Embedded.Nullable.class).prefix();
        }
        if (prefix != null) {
            appendEmbeddedColumns(entityClass, field, columnBuilders, prefix);
            return true;
        }
        return false;
    }

    static <E> void appendEmbeddedColumns(Class<E> entityClass, Field field,
                                          List<PersistentColumnInfo.Builder> columnBuilders, String prefix) {
        var embeddedValueGetter = getValueGetter(entityClass, field);
        if (embeddedValueGetter != null) {
            getPersistentEntityInfo(field.getType()).ifPresent(info -> {
                for (var column : info.getColumns()) {
                    var columnName = prefix + column.getColumnName();
                    @SuppressWarnings("unchecked") var getter = (Function<Object, Object>) column.getValueGetter();
                    Function<E, Object> valueGetter = entity -> {
                        var embedded = embeddedValueGetter.apply(entity);
                        if (embedded == null) {
                            return null;
                        }
                        return getter.apply(embedded);
                    };
                    columnBuilders.add(column.toBuilder().columnName(columnName).valueGetter(valueGetter));
                }
            });
        }
    }

    static <E> Function<E, Object> tryUseGetter(Class<E> entityClass, Field field) {
        var getterName = toGetterName(field.getName(), field.getType().equals(boolean.class));
        try {
            var method = entityClass.getMethod(getterName);
            if (!method.getReturnType().equals(field.getType())) {
                return null;
            }
            return entity -> {
                try {
                    return method.invoke(entity);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    static final String toGetterName(String fieldName, boolean isBoolean) {
        var prefix = isBoolean ? "is" : "get";
        return prefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    /**
     * Append {@code VALUE} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder value() {
        return s("VALUE");
    }

    /**
     * Append {@code VALUE} into SQL with the values extracted from the
     * specified entity given.
     *
     * @param entity the entity object
     * @param <E>    the entity type
     * @return this {@link SqlBuilder}
     */
    @SuppressWarnings("unchecked")
    public <E> SqlBuilder value(E entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        return value((Class<E>) entity.getClass(), entity);
    }

    /**
     * Append {@code VALUE} into SQL with the values extracted from the
     * specified entity given.
     *
     * @param entityClass the entity class
     * @param entity      the entity object
     * @param <E>         the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder value(Class<E> entityClass, E entity) {
        var info = getRequiredPersistentEntityInfo(entityClass);
        return value().s("(", questionMarks(info.getColumns().size()), ")").addValues(info, entity);
    }

    <E> SqlBuilder addValues(PersistentEntityInfo<E> info, E entity) {
        for (var column : info.getColumns()) {
            v(column.getValue(entity));
        }
        return this;
    }

    /**
     * Append {@code VALUES} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder values() {
        return s("VALUES");
    }

    /**
     * Append {@code VALUES} into SQL with the values extracted from the
     * specified entity given.
     *
     * @param entity the entity object
     * @param <E>    the entity type
     * @return this {@link SqlBuilder}
     */
    @SuppressWarnings("unchecked")
    public <E> SqlBuilder values(E entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        Class<E> entityClass = (Class<E>) entity.getClass();
        return values(entityClass, List.of(entity), false);
    }

    /**
     * Append {@code VALUES} into SQL with the values extracted from the
     * specified entityList given.
     *
     * @param entityList a list contains entity objects
     * @param <E>        the entity type
     * @return this {@link SqlBuilder}
     */
    @SuppressWarnings("unchecked")
    public <E> SqlBuilder values(List<E> entityList) {
        if (entityList.isEmpty()) {
            throw new IllegalArgumentException("entityList must not be empty");
        }
        Class<E> entityClass = (Class<E>) entityList.get(0).getClass();
        return values(entityClass, entityList, false);
    }

    /**
     * Append {@code VALUES} into SQL with the values extracted from the
     * specified entityClass and the specified entityList given.
     *
     * @param entityClass the entity class
     * @param entityList  a list contains entity objects
     * @param <E>         the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder values(Class<E> entityClass, List<E> entityList) {
        return values(entityClass, entityList, true);
    }

    <E> SqlBuilder values(Class<E> entityClass, List<E> entityList, boolean checkEmpty) {
        if (checkEmpty && entityList.isEmpty()) {
            throw new IllegalArgumentException("entityList must not be empty");
        }
        var info = getRequiredPersistentEntityInfo(entityClass);
        values();
        var questionMarks = questionMarks(info.getColumns().size());
        var index = 0;
        for (var entity : entityList) {
            if (index++ != 0) {
                s(",");
            }
            s("(", questionMarks, ")").addValues(info, entity);
        }
        return this;
    }

    /**
     * Append {@code DELETE} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder delete() {
        return s("DELETE");
    }

    /**
     * Append {@code DELETE} into SQL with the specified options.
     *
     * @param options an array of options
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder delete(String... options) {
        return delete().s(options);
    }

    /**
     * Append {@code DELETE} clause into SQL from the specified table.
     *
     * @param table the table name
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder deleteFrom(String table) {
        return delete().s("FROM", table);
    }


    /**
     * Append {@code DELETE} clause into SQL from the table extracted
     * from the specified entityClass given.
     *
     * @param entityClass the entity class
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder deleteFrom(Class<?> entityClass) {
        return deleteFrom(getTableName(entityClass));
    }

    /**
     * Append {@code UPDATE} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder update() {
        return s("UPDATE");
    }

    /**
     * Append {@code UPDATE} into SQL with the specified options.
     *
     * @param options an array of options
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder update(String... options) {
        return update().s(options);
    }

    /**
     * Append {@code UPDATE} clause into SQL for the table.
     *
     * @param table the table name
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder update(String table) {
        return update().s(table);
    }

    /**
     * Append {@code UPDATE} clause into SQL for the table extracted
     * from the specified entityClass given.
     *
     * @param entityClass the entity class
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder update(Class<?> entityClass) {
        return update(getTableName(entityClass));
    }

    /**
     * Append {@code SET} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder set() {
        return s("SET");
    }

    /**
     * Returns a new {@link SqlBuilder} with the set clause mode.
     *
     * @return a new {@link SqlBuilder} with the set clause mode
     */
    public SqlBuilder setClause() {
        return new SqlBuilder(this, SET_CLAUSE);
    }

    /**
     * End the current set clause and returns the parent
     * {@link SqlBuilder}.
     *
     * @return the parent {@link SqlBuilder}
     */
    public SqlBuilder endSetClause() {
        return endClause("SET", SET_CLAUSE, "not in a set clause", ",");
    }

    /**
     * Append {@code ,} into SQL.
     *
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder comma() {
        return s(",");
    }

    /**
     * Append assignment into SQL.
     *
     * @param assignment the assignment
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendAssignment(String assignment) {
        return comma().s(assignment);
    }

    /**
     * Append assignment into SQL with the specified column and the
     * specified value given.
     *
     * @param column the column
     * @param value  the value
     * @return this {@link SqlBuilder}
     */
    public SqlBuilder appendAssignment(String column, Object value) {
        return comma().column(column).sv("= ?", value);
    }

    /**
     * Append assignments, extracted from the specified entity given, into
     * SQL.
     *
     * @param entity the entity object
     * @param <E>    the entity type
     * @return this {@link SqlBuilder}
     */
    @SuppressWarnings("unchecked")
    public <E> SqlBuilder appendAssignments(E entity) {
        return appendAssignments((Class<E>) entity.getClass(), entity);
    }

    /**
     * Append assignments, extracted from the specified entityClass and
     * the specified entity given, into SQL.
     *
     * @param entityClass the entity class
     * @param entity      the entity object
     * @param <E>         the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder appendAssignments(Class<E> entityClass, E entity) {
        var info = getRequiredPersistentEntityInfo(entityClass);
        for (var column : info.getColumns()) {
            if (!column.isInsertOnly()) {
                var value = column.getValue(entity);
                if (value != null) {
                    appendAssignment(column.getColumnName(), value);
                }
            }
        }
        return this;
    }

    /**
     * Append set clause into SQL with the assignments extracted from
     * the specified entity given.
     *
     * @param entity the entity object
     * @param <E>    the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder set(E entity) {
        return setClause().appendAssignments(entity).endSetClause();
    }

    /**
     * Append set clause into SQL with the assignments extracted from
     * the specified entityClass and the specified entity given.
     *
     * @param entityClass the entity class
     * @param entity      the entity object
     * @param <E>         the entity type
     * @return this {@link SqlBuilder}
     */
    public <E> SqlBuilder set(Class<E> entityClass, E entity) {
        return setClause().appendAssignments(entityClass, entity).endSetClause();
    }

}
