package com.github.fmjsjx.libcommon.r2dbc

import com.github.fmjsjx.libcommon.r2dbc.Sort.ASC
import com.github.fmjsjx.libcommon.r2dbc.Sort.DESC
import org.springframework.data.relational.core.mapping.Column
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

/**
 * An alias for the method [SqlBuilder.in].
 *
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.isIn(values: List<Any>): SqlBuilder = `in`(values)

/**
 * An alias for the method [SqlBuilder.in].
 *
 * @return this [SqlBuilder]
 * @since 3.11
 */
fun SqlBuilder.isIn(vararg values: Any): SqlBuilder = `in`(*values)

/**
 * Append columns in the select part of the SQL.
 *
 * @param E the entity type from which to get the table name
 * @param tableAlias the alias of the table
 * @param columnsAliasPrefix the prefix for column aliases
 * @return this [SqlBuilder]
 * @since 3.13
 */
inline fun <reified E : Any>SqlBuilder.appendColumns(
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
 *
 * @return the column name
 * @since 3.12
 */
fun <T, V> KProperty1<T, V>.toColumn(): String =
    FieldColumnMappingsHolder.mappings.getOrPut(this) { javaField?.getAnnotation(Column::class.java)?.value ?: name }

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
fun SqlBuilder.select(vararg columns: KProperty1<*, *>): SqlBuilder =
    select(*columns.map { it.toColumn() }.toTypedArray())

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
fun SqlBuilder.and(column: KProperty1<*, *>): SqlBuilder = and().s(column.toColumn())