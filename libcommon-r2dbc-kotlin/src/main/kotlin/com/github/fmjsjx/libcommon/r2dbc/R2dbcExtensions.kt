package com.github.fmjsjx.libcommon.r2dbc

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * Execute SQL.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @return a new [GenericExecuteSpec]
 * @since 3.11
 */
@Suppress("SqlSourceToSinkFlow")
fun DatabaseClient.execute(sqlBuilder: SqlBuilder): GenericExecuteSpec =
    sql(sqlBuilder.buildSql()).bindValues(sqlBuilder)

/**
 * Bind values from [SqlBuilder].
 *
 * @param sqlBuilder the [SqlBuilder]
 * @return a new [GenericExecuteSpec]
 * @since 3.11
 */
fun GenericExecuteSpec.bindValues(sqlBuilder: SqlBuilder): GenericExecuteSpec =
    sqlBuilder.values.takeUnless { it.isEmpty() }?.let(::bindValues) ?: this

/**
 * Execute SQL.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @return a new [GenericExecuteSpec]
 * @since 3.12
 */
fun R2dbcEntityOperations.execute(sqlBuilder: SqlBuilder): GenericExecuteSpec =
    databaseClient.execute(sqlBuilder)

/**
 * Execute SQL for select and returns one long result.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @return a Mono emitting one long result
 * @since 3.11
 */
fun R2dbcEntityOperations.selectLong(sqlBuilder: SqlBuilder): Mono<Long> =
    execute(sqlBuilder).filter { it.fetchSize(1) }.mapValue(Long::class.java).one()

/**
 * Execute SQL for select and returns all matching results.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @param T the entity type
 * @return a Flux emitting all results
 * @since 3.11
 */
inline fun <reified T : Any> R2dbcEntityOperations.select(sqlBuilder: SqlBuilder): Flux<T> =
    select(T::class, sqlBuilder)

/**
 * Execute SQL for select and returns all matching results.
 *
 * @param entityClass the entity kotlin class
 * @param sqlBuilder the [SqlBuilder]
 * @return a Flux emitting all results
 * @since 3.11
 */
fun <T : Any> R2dbcEntityOperations.select(entityClass: KClass<T>, sqlBuilder: SqlBuilder): Flux<T> =
    execute(sqlBuilder).map { row, metadata -> converter.read(entityClass.java, row, metadata) }.all()

/**
 * Execute SQL for select and returns exactly zero or one result.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @param T the entity type
 * @return a Mono emitting one result, or [Mono.empty] if no match found.
 *         Completes with `IncorrectResultSizeDataAccessException` if more
 *         than one match found
 * @since 3.11
 */
inline fun <reified T : Any> R2dbcEntityOperations.selectOne(sqlBuilder: SqlBuilder): Mono<T> =
    selectOne(T::class, sqlBuilder)

/**
 * Execute SQL for select and returns exactly zero or one result.
 *
 * @param entityClass the entity kotlin class
 * @param sqlBuilder the [SqlBuilder]
 * @return a Mono emitting one result, or [Mono.empty] if no match found.
 *         Completes with `IncorrectResultSizeDataAccessException` if more
 *         than one match found
 * @since 3.11
 */
fun <T : Any> R2dbcEntityOperations.selectOne(entityClass: KClass<T>, sqlBuilder: SqlBuilder): Mono<T> =
    execute(sqlBuilder).filter { it.fetchSize(2) }
        .map { row, metadata -> converter.read(entityClass.java, row, metadata) }.one()

/**
 * Execute SQL for select and returns the first or no result.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @param T the entity type
 * @return a Mono emitting first result, or [Mono.empty] if no match found
 * @since 3.11
 */
inline fun <reified T : Any> R2dbcEntityOperations.selectFirst(sqlBuilder: SqlBuilder): Mono<T> =
    selectFirst(T::class, sqlBuilder)

/**
 * Execute SQL for select and returns the first or no result.
 *
 * @param entityClass the entity kotlin class
 * @param sqlBuilder the [SqlBuilder]
 * @return a Mono emitting first result, or [Mono.empty] if no match found
 * @since 3.11
 */
fun <T : Any> R2dbcEntityOperations.selectFirst(entityClass: KClass<T>, sqlBuilder: SqlBuilder): Mono<T> =
    execute(sqlBuilder).filter { it.fetchSize(1) }
        .map { row, metadata -> converter.read(entityClass.java, row, metadata) }.first()

/**
 * Execute SQL and returns the number of updated rows.
 *
 * @param sqlBuilder the [SqlBuilder]
 * @return a Mono emitting the number of updated rows
 * @since 3.12
 */
fun R2dbcEntityOperations.executeUpdate(sqlBuilder: SqlBuilder): Mono<Long> = execute(sqlBuilder).fetch().rowsUpdated()