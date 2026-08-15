@file:Suppress("unused")

package com.github.fmjsjx.libcommon.r2dbc

import com.github.fmjsjx.libcommon.r2dbc.Sort.ASC
import com.github.fmjsjx.libcommon.r2dbc.Sort.DESC
import com.github.fmjsjx.libcommon.r2dbc.SqlBuilder.toSnakeCase
import org.springframework.data.relational.core.mapping.Column
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.isIn(values: List<Any>): SqlBuilder = `in`(values)

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.isIn(vararg values: Any): SqlBuilder = `in`(*values)

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.isIn(vararg values: Int): SqlBuilder = `in`(values)

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.isIn(vararg values: Long): SqlBuilder = `in`(values)

/**
 * Append `NOT IN` predicate into SQL.
 *
 * This method is equivalent to:
 * ```
 * not().`in`(values)
 * ```
 *
 * @param values a list contains values
 * @return this [SqlBuilder]
 * @since 3.14
 */
fun SqlBuilder.notIn(values: List<Any>): SqlBuilder = not().`in`(values)

/**
 * Append `NOT IN` predicate into SQL.
 *
 * This method is equivalent to:
 * ```
 * not().`in`(values)
 * ```
 *
 * @param values an array of values
 * @return this [SqlBuilder]
 * @since 3.14
 */
fun SqlBuilder.notIn(vararg values: Any): SqlBuilder = not().`in`(*values)

/**
 * Append `NOT IN` predicate into SQL.
 *
 * This method is equivalent to:
 * ```
 * not().`in`(values)
 * ```
 *
 * @param values an array of values
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.notIn(vararg values: Int): SqlBuilder = not().`in`(values)

/**
 * Append `NOT IN` predicate into SQL.
 *
 * This method is equivalent to:
 * ```
 * not().`in`(values)
 * ```
 *
 * @param values an array of values
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.notIn(vararg values: Long): SqlBuilder = not().`in`(values)

/**
 * Append columns in the select part of the SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @param columnsAliasPrefix the prefix for column aliases
 * @return this [SqlBuilder]
 * @since 3.13
 */
inline fun <reified E : Any> SqlBuilder.appendColumns(
    tableAlias: String? = null,
    columnsAliasPrefix: String? = null,
): SqlBuilder = appendColumns(E::class, tableAlias, columnsAliasPrefix)

/**
 * Append columns in the select part of the SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @param columnsAliasPrefix the prefix for column aliases
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.appendColumns(
    type: KClass<E>,
    tableAlias: String? = null,
    columnsAliasPrefix: String? = null,
): SqlBuilder = appendColumns(type.java, tableAlias, columnsAliasPrefix)

/**
 * Select columns.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @param columnsAliasPrefix the prefix for column aliases
 * @return this [SqlBuilder]
 * @since 3.13
 */
inline fun <reified E : Any> SqlBuilder.select(
    tableAlias: String? = null,
    columnsAliasPrefix: String? = null,
): SqlBuilder = select(E::class.java, tableAlias, columnsAliasPrefix)

/**
 * Select columns.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @param columnsAliasPrefix the prefix for column aliases
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.select(
    type: KClass<E>,
    tableAlias: String? = null,
    columnsAliasPrefix: String? = null,
): SqlBuilder = select(type.java, tableAlias, columnsAliasPrefix)

/**
 * Finish select part and append `FROM` into SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.from(tableAlias: String? = null): SqlBuilder = from(E::class, tableAlias)

/**
 * Finish select part and append `FROM` into SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.from(type: KClass<E>, tableAlias: String? = null): SqlBuilder =
    tableAlias?.let { from(type.java, it) } ?: from(type.java)

/**
 * Append {@code INNER JOIN} into SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.innerJoin(tableAlias: String? = null): SqlBuilder =
    innerJoin(E::class, tableAlias)

/**
 * Append {@code INNER JOIN} into SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.innerJoin(type: KClass<E>, tableAlias: String? = null): SqlBuilder =
    tableAlias?.let { innerJoin(type.java, tableAlias) } ?: innerJoin(type.java)

/**
 * Append {@code LEFT JOIN} into SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.leftJoin(tableAlias: String? = null): SqlBuilder =
    leftJoin(E::class, tableAlias)

/**
 * Append {@code LEFT JOIN} into SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.leftJoin(type: KClass<E>, tableAlias: String? = null): SqlBuilder =
    tableAlias?.let { leftJoin(type.java, tableAlias) } ?: leftJoin(type.java)

/**
 * Append {@code RIGHT JOIN} into SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.rightJoin(tableAlias: String? = null): SqlBuilder =
    rightJoin(E::class, tableAlias)

/**
 * Append {@code RIGHT JOIN} into SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @param tableAlias the alias of the table
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.rightJoin(type: KClass<E>, tableAlias: String? = null): SqlBuilder =
    tableAlias?.let { rightJoin(type.java, tableAlias) } ?: rightJoin(type.java)

/**
 * Finish select part and append `FROM` into SQL.
 *
 * @param types the array of entity kotlin classes from which to get the table names
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun SqlBuilder.from(vararg types: KClass<*>): SqlBuilder = from(*Array(types.size) { types[it].java })

/**
 * Append `INTO` clause into SQL with the table name be extracted from
 * the specified entity type.
 *
 * @param E the entity type
 * @return this [SqlBuilder]
 * @since 3.11
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.into(): SqlBuilder = into(E::class)

/**
 * Append `INTO` clause into SQL with the table name be extracted from
 * the specified entityType.
 *
 * @param entityType the entity kotlin class
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.into(entityType: KClass<*>): SqlBuilder = into(entityType.java)

/**
 * Append columns, extracted from the specified entity type given, into
 * the SQL
 *
 * @param E the entity type
 * @param generateId `true` if it should skip ID column
 * @return this [SqlBuilder]
 * @since 3.13
 */
inline fun <reified E : Any> SqlBuilder.columns(generateId: Boolean = true): SqlBuilder = columns(E::class, generateId)

/**
 * Append columns, extracted from the specified entityType given, into
 * the SQL.
 *
 * @param E the entity type
 * @param entityType the entity kotlin class
 * @param generateId `true` if it should skip ID column
 * @return this [SqlBuilder]
 * @since 3.13
 */
fun <E : Any> SqlBuilder.columns(
    entityType: KClass<E>,
    generateId: Boolean = true,
): SqlBuilder = columns(entityType.java, generateId)

/**
 * Append `UPDATE` clause into SQL for the table extracted from the
 * specified entity type given.
 *
 * @param E the entity type
 * @return this [SqlBuilder]
 * @since 3.11
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.update(): SqlBuilder = update(E::class)

/**
 * Append `UPDATE` clause into SQL for the table extracted from the
 * specified entityType given.
 *
 * @param entityType the entity kotlin class
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.update(entityType: KClass<*>): SqlBuilder = update(entityType.java)

private object FieldColumnMappingsHolder {
    val mappings: ConcurrentMap<KProperty1<*, *>, String> = ConcurrentHashMap()
}

/**
 * Convert field to column name string.
 * > Since version 3.14, the default column name change to be the snake case of the field name.
 * @return the column name
 * @since 3.12
 */
fun <T, V> KProperty1<T, V>.toColumn(): String = FieldColumnMappingsHolder.mappings.getOrPut(this) {
    javaField?.getAnnotation(Column::class.java)?.value ?: toSnakeCase(name)
}

/**
 * Convert field to [Order].
 *
 * @param sort the [Sort]
 * @return an [Order] with the specified `sort` given
 * @since 3.12
 */
infix fun <T, V> KProperty1<T, V>.toOrder(sort: Sort? = null): Order = Order(toColumn(), sort)

/**
 * Convert field to an ASC [Order].
 *
 * @return an ASC [Order]
 * @since 3.12
 */
fun <T, V> KProperty1<T, V>.asc(): Order = toOrder(ASC)

/**
 * Convert field to a DESC [Order].
 *
 * @return a DESC [Order]
 * @since 3.12
 */
fun <T, V> KProperty1<T, V>.desc(): Order = toOrder(DESC)

/**
 * Select columns.
 *
 * @param columns the columns
 * @return this [SqlBuilder]
 * @since 3.12
 */
fun SqlBuilder.select(vararg columns: KProperty1<*, *>): SqlBuilder = select(columns.map { it.toColumn() })

/**
 * Select distinct columns.
 *
 * @param columns the columns
 * @return this [SqlBuilder]
 * @since 3.15
 */
fun SqlBuilder.selectDistinct(vararg columns: KProperty1<*, *>): SqlBuilder =
    selectDistinct(columns.map { it.toColumn() })

/**
 * Append `WHERE` and the specified `column` into SQL.
 *
 * @param column the column
 * @return this [SqlBuilder]
 * @since 3.12
 */
fun SqlBuilder.where(column: KProperty1<*, *>): SqlBuilder = where().s(column.toColumn())

/**
 * Append `AND` and the specified `column` into SQL.
 *
 * @param column the column
 * @return this [SqlBuilder]
 * @since 3.12
 */
fun SqlBuilder.and(column: KProperty1<*, *>): SqlBuilder = and(column.toColumn())

/**
 * Append `OR` and the specified `column` into SQL.
 *
 * @param column the column
 * @return this [SqlBuilder]
 * @since 3.15
 */
fun SqlBuilder.or(column: KProperty1<*, *>): SqlBuilder = or(column.toColumn())

/**
 * Append the assignment into SQL with the specified `column` and the
 * specified `value` given.
 *
 * @param column the column
 * @param value the value
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.assign(column: KProperty1<*, *>, value: Any?): SqlBuilder = assign(column.toColumn(), value)

/**
 * Append the specified `column` into SQL.
 *
 * @param column the column
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.column(column: KProperty1<*, *>): SqlBuilder = column(column.toColumn())

/**
 * Returns a new SqlBuilder in group mode and append the specified
 * `column` into it.
 *
 * @param column the column
 * @return this [SqlBuilder]
 * @since 3.17
 */
fun SqlBuilder.beginGroup(column: KProperty1<*, *>): SqlBuilder = beginGroup(column.toColumn())

/**
 * Append the next assignment into SQL with the specified `column` and
 * the specified `value` given.
 *
 * @param column the column
 * @param value the value
 * @return this [SqlBuilder]
 * @since 4.2
 */
fun SqlBuilder.appendAssignment(column: KProperty1<*, *>, value: Any?): SqlBuilder =
    appendAssignment(column.toColumn(), value)

/**
 * Append `GROUP BY` clause into SQL with the specified `columns` given.
 *
 * @param columns the columns
 * @return this [SqlBuilder]
 * @since 4.3
 */
fun SqlBuilder.groupBy(vararg columns: KProperty1<*, *>): SqlBuilder = groupBy(columns.map { it.toColumn() })

/**
 * Append a subquery into SQL.
 *
 * @param name the name of the subquery
 * @param block the subquery block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.subquery(name: String, block: SqlBuilder.() -> Unit): SqlBuilder =
    subquery().apply { block() }.endSubquery(name)

/**
 * Append `IN` clause into SQL with the specified subquery.
 *
 * @param subqueryBlock the subquery block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.isIn(subqueryBlock: SqlBuilder.() -> Unit): SqlBuilder =
    inSubquery().apply { subqueryBlock() }.endSubquery()

/**
 * Append `INNER JOIN` clause into SQL with the specified subquery.
 *
 * @param name the name of the subquery
 * @param subqueryBlock the subquery block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.innerJoin(name: String, subqueryBlock: SqlBuilder.() -> Unit): SqlBuilder =
    innerJoinSubquery().apply { subqueryBlock() }.endSubquery(name)

/**
 * Append `LEFT JOIN` clause into SQL with the specified subquery.
 *
 * @param name the name of the subquery
 * @param subqueryBlock the subquery block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.leftJoin(name: String, subqueryBlock: SqlBuilder.() -> Unit): SqlBuilder =
    leftJoinSubquery().apply { subqueryBlock() }.endSubquery(name)

/**
 * Append `RIGHT JOIN` clause into SQL with the specified subquery.
 *
 * @param name the name of the subquery
 * @param subqueryBlock the subquery block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.rightJoin(name: String, subqueryBlock: SqlBuilder.() -> Unit): SqlBuilder =
    rightJoinSubquery().apply { subqueryBlock() }.endSubquery(name)

/**
 * Append `USING` clause into SQL with the specified columns.
 *
 * @param columns the columns
 * @return this [SqlBuilder]
 * @since 4.3
 */
fun SqlBuilder.using(vararg columns: KProperty1<*, *>): SqlBuilder = using(columns.map { it.toColumn() })

/**
 * Append columns into SQL with the specified columns.
 *
 * @param columns the columns
 * @return this [SqlBuilder]
 * @since 4.3
 */
fun SqlBuilder.columns(vararg columns: KProperty1<*, *>): SqlBuilder = columns(columns.map { it.toColumn() })

/**
 * Append select clause into SQL with the specified block.
 *
 * > **Note**: This method will trigger a [finishSelect] call after
 * ending the `block`
 *
 * @param block the select block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.select(block: SqlBuilder.() -> Unit): SqlBuilder =
    select().apply { block() }.finishSelect()

/**
 * Append set clause into SQL with the specified block.
 *
 * @param block the set block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.setClause(block: SqlBuilder.() -> Unit): SqlBuilder =
    setClause().apply { block() }.endSetClause()

/**
 * Append where clause into SQL with the specified block.
 *
 * @param block the where block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.where(block: SqlBuilder.() -> Unit): SqlBuilder =
    whereClause().apply { block() }.endWhereClause()

/**
 * Append group by clause into SQL with the specified block.
 *
 * @param block the group block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.group(block: SqlBuilder.() -> Unit): SqlBuilder =
    beginGroup().apply { block() }.endGroup()

/**
 * Append having clause into SQL with the specified block.
 *
 * @param block the having block
 * @return this [SqlBuilder]
 * @since 4.3
 */
inline fun SqlBuilder.having(block: SqlBuilder.() -> Unit): SqlBuilder =
    havingClause().apply { block() }.endHavingClause()