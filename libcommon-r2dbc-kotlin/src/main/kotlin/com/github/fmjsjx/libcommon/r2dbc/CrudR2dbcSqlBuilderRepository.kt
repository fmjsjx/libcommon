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
     * Deletes the specified entity by [id].
     *
     * @param entityType the type of the entity
     * @param id the ID of the entity
     * @return the number of deleted rows
     */
    fun deleteOneById(entityType: KClass<E>, id: ID): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .deleteFrom(entityType.java)
            .where().filterById(entityType.java, id)
            .let { r2dbcEntityOperations.executeUpdate(it) }

    /**
     * Deletes the specified entities by [ids].
     *
     * @param entityType the type of the entity
     * @param ids the IDs of the entities
     * @return the number of deleted rows
     */
    fun deleteManyByIds(entityType: KClass<E>, ids: List<ID>): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .deleteFrom(entityType.java)
            .where().filterByIds(entityType.java, ids)
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

    /**
     * Finds all entities.
     *
     * @param entityType the type of the entity
     * @return the found entities
     */
    fun findAll(entityType: KClass<E>): Flux<E> =
        SqlBuilder().parameterStyle(parameterStyle)
            .selectAll()
            .from(entityType.java)
            .let { r2dbcEntityOperations.select(entityType, it) }

    /**
     * Counts all entities.
     *
     * @param entityType the type of the entity
     * @return the count of entities
     */
    fun countAll(entityType: KClass<E>): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .selectCount()
            .from(entityType.java)
            .let { r2dbcEntityOperations.selectLong(it) }

    /**
     * Counts the entities by the specified [id].
     *
     * @param entityType the type of the entity
     * @param id the ID of the entity
     * @return the count of entities
     */
    fun countById(entityType: KClass<E>, id: ID): Mono<Long> =
        SqlBuilder().parameterStyle(parameterStyle)
            .selectCount()
            .from(entityType.java)
            .where().filterById(entityType.java, id)
            .let(r2dbcEntityOperations::selectLong)

    /**
     * Checks if an entity with the given [id] exists.
     *
     * @param entityType the type of the entity
     * @param id the ID of the entity
     */
    fun existsById(entityType: KClass<E>, id: ID): Mono<Boolean> = countById(entityType, id).map { it > 0 }

}

/**
 * Deletes the specified entity by [id].
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.deleteOneById(id: ID): Mono<Long> =
    deleteOneById(E::class, id)

/**
 * Deletes the specified entities by [ids].
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.deleteManyByIds(ids: List<ID>): Mono<Long> =
    deleteManyByIds(E::class, ids)


/**
 * Finds the entity by the specified [id].
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.findOneById(id: ID): Mono<E> =
    findOneById(E::class, id)

/**
 * Finds all entities by the specified [ids].
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.findAllByIds(ids: List<ID>): Flux<E> =
    findAllByIds(E::class, ids)

/**
 * Finds all entities
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.findAll(): Flux<E> =
    findAll(E::class)

/**
 * Counts all entities.
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.countAll(): Mono<Long> =
    countAll(E::class)

/**
 * Counts the entities by the specified [id].
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.countById(id: ID): Mono<Long> =
    countById(E::class, id)


/**
 * Checks if an entity with the given [id] exists.
 *
 * @since 4.2
 */
inline fun <reified E : Any, ID : Serializable, O : CrudR2dbcSqlBuilderRepository<E, ID>> O.existsById(id: ID): Mono<Boolean> =
    existsById(E::class, id)
