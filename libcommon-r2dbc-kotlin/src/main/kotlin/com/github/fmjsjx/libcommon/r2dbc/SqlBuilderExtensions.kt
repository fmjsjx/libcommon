package com.github.fmjsjx.libcommon.r2dbc

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id as EntityId
import org.springframework.data.annotation.Transient as EntityTransient
import org.springframework.data.annotation.ReadOnlyProperty as EntityReadOnlyProperty
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 */
fun SqlBuilder.isIn(values: List<Any>): SqlBuilder = `in`(values)

/**
 * An alias for the method [SqlBuilder. in].
 *
 * @return this [SqlBuilder]
 */
fun SqlBuilder.isIn(vararg values: Any): SqlBuilder = `in`(*values)

/**
 * Finish select part and append {@code FROM} into SQL.
 *
 * @param E the entity type from which to get the table name
 * @return this [SqlBuilder]
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.from(): SqlBuilder = from(E::class)

/**
 * Finish select part and append {@code FROM} into SQL.
 *
 * @param type the entity kotlin class from which to get the table name
 * @return this [SqlBuilder]
 */
fun <E : Any> SqlBuilder.from(type: KClass<E>): SqlBuilder = from(getTableName(type))

private fun getTableName(type: KClass<*>): String =
    SqlBuilder.TableNamesHolder.CACHE.computeIfAbsent(type.java) {
        var tableName = ""
        var tableSchema = ""
        type.findAnnotation<Table>()?.apply {
            tableSchema = schema
            tableName = name
            if (tableName.isEmpty()) {
                tableName = value
            }
        }
        if (tableName.isEmpty()) {
            tableName = type.simpleName?.let { "${it[0].lowercase()}${it.substring(1)}" }?.let {
                it.replace(Regex("[A-Z]")) { "_${it.value.lowercase()}" }
            } ?: ""
        }
        if (tableSchema.isEmpty()) {
            tableName
        } else {
            "$tableSchema.$tableName"
        }
    }

/**
 * Append `INTO` clause into SQL with the table name be extracted from
 * the specified entity type.
 *
 * @param E the entity type
 * @return this [SqlBuilder]
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.into(): SqlBuilder = into(E::class)

/**
 * Append `INTO` clause into SQL with the table name be extracted from
 * the specified entityType.
 *
 * @param entityType the entity kotlin class
 * @return this [SqlBuilder]
 */
fun SqlBuilder.into(entityType: KClass<*>): SqlBuilder = into(getTableName(entityType))

/**
 * Append columns, extracted from the specified entity type given, into
 * the SQL
 *
 * @param E the entity type
 * @return this [SqlBuilder]
 */
inline fun <reified E : Any> SqlBuilder.columns(): SqlBuilder = columns(E::class)

/**
 * Append columns, extracted from the specified entityType given, into
 * the SQL.
 *
 * @param E the entity type
 * @param entityType the entity kotlin class
 * @return this [SqlBuilder]
 */
fun <E : Any> SqlBuilder.columns(entityType: KClass<E>): SqlBuilder =
    getPersistentEntityInfo(entityType)?.let { info ->
        info.columns.joinToString(", ") { it.columnName }.let { columns(it) }
    } ?: throw IllegalStateException("Cannot extract persistent columns for type $entityType")

@Suppress("UNCHECKED_CAST")
private fun <E : Any> getPersistentEntityInfo(entityType: KClass<E>): PersistentEntityInfo<E>? {
    var info = SqlBuilder.PersistentEntityInfoCacheHolder.cache[entityType.java]
    if (info == null) {
        info = createPersistentEntityInfo(entityType)
        SqlBuilder.PersistentEntityInfoCacheHolder.cache[entityType.java] = info
    }
    if (info == SqlBuilder.PersistentEntityInfoCacheHolder.EMPTY) {
        return null
    }
    return info as PersistentEntityInfo<E>
}

private fun <E : Any> createPersistentEntityInfo(entityType: KClass<E>): Any {
    val creatingSet = SqlBuilder.PersistentEntityInfoCacheHolder.threadLocalSet.get()
    if (!creatingSet.add(entityType)) {
        throw IllegalStateException("a circular reference occurs on entity type $entityType")
    }
    try {
        var columnBuilders = ArrayList<PersistentColumnInfo.Builder>()
        for (prop in entityType.memberProperties) {
            val field = prop.javaField ?: continue
            val annotations = field.annotations
            if (prop.visibility != KVisibility.PUBLIC || annotations.any(::isSkipAnnotation)) {
                continue
            }
            val embeddedPrefix = annotations.firstNotNullOfOrNull {
                when (it) {
                    is Embedded -> it.prefix
                    is Embedded.Empty -> it.prefix
                    is Embedded.Nullable -> it.prefix
                    else -> null
                }
            }
            if (embeddedPrefix != null) {
                appendEmbeddedColumns(prop, columnBuilders, embeddedPrefix)
                continue
            }
            val column = annotations.firstNotNullOfOrNull { it as? Column }
            var columnName = column?.value ?: prop.name
            val valueGetter = getValueGetter(prop)
            val insertOnly = annotations.firstOrNull { it is InsertOnlyProperty } != null
            PersistentColumnInfo.builder().columnName(columnName).valueGetter(valueGetter).insertOnly(insertOnly)
                .let(columnBuilders::add)
        }
        if (columnBuilders.isEmpty()) {
            return SqlBuilder.PersistentEntityInfoCacheHolder.EMPTY
        }
        return PersistentEntityInfo(entityType.java, columnBuilders)
    } finally {
        creatingSet.remove(entityType)
    }
}

private fun isSkipAnnotation(annotation: Annotation): Boolean =
    annotation is Transient || annotation is EntityId || annotation is EntityTransient || annotation is EntityReadOnlyProperty

private fun appendEmbeddedColumns(
    prop: KProperty1<*, *>,
    columnBuilders: MutableList<PersistentColumnInfo.Builder>,
    prefix: String,
) {
    val type = prop.returnType.classifier as? KClass<*> ?: return
    val embeddedValueGetter = getValueGetter(prop)
    val info = getPersistentEntityInfo(type) ?: return
    info.columns.forEach { column ->
        val columnName = "$prefix${column.columnName}"
        val getter = column.valueGetter as Function<Any, *>
        val valueGetter: (Any) -> Any? = { entity ->
            embeddedValueGetter(entity)?.let { getter.apply(it) }
        }
        columnBuilders.add(column.toBuilder().columnName(columnName).valueGetter(valueGetter))
    }
}

private fun getValueGetter(prop: KProperty<*>): (Any) -> Any? = prop.getter::call

/**
 * Append `UPDATE` clause into SQL for the table extracted from the
 * specified entity type given.
 *
 * @param E the entity type
 * @return this [SqlBuilder]
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified E : Any> SqlBuilder.update(): SqlBuilder = update(E::class)

/**
 * Append `UPDATE` clause into SQL for the table extracted from the
 * specified entityType given.
 *
 * @param entityType the entity kotlin class
 * @return this [SqlBuilder]
 */
fun SqlBuilder.update(entityType: KClass<*>): SqlBuilder = update(getTableName(entityType))


