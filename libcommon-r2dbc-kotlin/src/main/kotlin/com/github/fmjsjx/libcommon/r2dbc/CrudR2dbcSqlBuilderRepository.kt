package com.github.fmjsjx.libcommon.r2dbc

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * A [CrudR2dbcSqlBuilderRepository] is a R2DBC CRUD repository that uses [SqlBuilder] to build SQL.
 *
 * @since 4.2
 * @author MJ Fang
 */
interface CrudR2dbcSqlBuilderRepository<E : Any, ID : Serializable> {

    /**
     * The [R2dbcEntityOperations] used by this repository.
     */
    val r2dbcEntityOperations: R2dbcEntityOperations

    /**
     * The [ParameterStyle] used by this repository.
     *
     * The default value is [ParameterStyle.NONE].
     */
    val parameterStyle: ParameterStyle get() = ParameterStyle.NONE

    /**
     * Inserts the specified [entity].
     *
     * @param entity the entity to insert
     * @return the inserted entity
     */
    fun insertOne(entity: E): Mono<E> = r2dbcEntityOperations.insert(entity)

    /**
     * Deletes the specified [entity].
     *
     * @param entity the entity to delete
     * @return the number of deleted rows
     */
    fun deleteOne(entity: E): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .deleteFrom(entity::class.java)
            .where().filterById(entity)
            .let { r2dbcEntityOperations.executeUpdate(it) }

    /**
     * Updates the specified [entity].
     *
     * @param entity the entity to update
     * @return the number of updated rows
     */
    fun updateOne(entity: E): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .update(entity::class.java)
            .set(entity)
            .where().filterById(entity)
            .let { r2dbcEntityOperations.executeUpdate(it) }

    /**
     * Finds the entity by the specified [id].
     *
     * @param entityType the type of the entity
     * @param id the ID of the entity
     * @return the found entity or empty [Mono]
     */
    fun findOneById(entityType: KClass<E>, id: ID): Mono<E> =
        SqlBuilder().parameterStyle(parameterStyle)
            .selectAll()
            .from(entityType.java)
            .where().filterById(entityType.java, id)
            .let { r2dbcEntityOperations.selectOne(entityType, it) }

    /**
     * Finds all entities by the specified [ids].
     *
     * @param entityType the type of the entity
     * @param ids the IDs of the entities
     * @return the found entities
     */
    fun findAllByIds(entityType: KClass<E>, ids: List<ID>): Flux<E> =
        SqlBuilder().parameterStyle(parameterStyle)
            .selectAll()
            .from(entityType.java)
            .where().filterByIds(entityType.java, ids)
            .let { r2dbcEntityOperations.select(entityType, it) }

}

/**
 * Finds the entity by the specified [id].
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.findOneById(id: ID): Mono<E> =
    findOneById(E::class, id)

/**
 * Finds all entities by the specified [ids].
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.findAllByIds(ids: List<ID>): Flux<E> =
    findAllByIds(E::class, ids)