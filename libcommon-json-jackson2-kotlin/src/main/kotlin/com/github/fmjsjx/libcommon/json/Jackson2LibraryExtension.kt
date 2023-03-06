package com.github.fmjsjx.libcommon.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import java.lang.reflect.Type

/**
 * The singleton [Jackson2Library] instance.
 * @author MJ Fang
 * @since 3.1
 */
inline val jackson2Library: Jackson2Library get() = Jackson2Library.getInstance()!!

/**
 * Extension to encode any object to JSON value as string.
 * @author MJ Fang
 * @since 3.1
 */
fun Any?.toJsonString(): String = jackson2Library.dumpsToString(this)

/**
 * Extension to encode any object to JSON value as byte array.
 * @author MJ Fang
 * @since 3.1
 */
fun Any?.toJsonBytes(): ByteArray = jackson2Library.dumpsToBytes(this)

/**
 * Extension to decode data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJackson2(type: Class<T>): T = jackson2Library.loads(toString(), type)

/**
 * Extension to decode data from string leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T> CharSequence.parseJackson2(): T = jackson2Library.loads(toString(), T::class.java)

/**
 * Extension to decode data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJackson2(typeReference: TypeReference<T>): T =
    jackson2Library.loads(toString().toByteArray(), typeReference)

/**
 * Extension to decode data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJackson2(javaType: JavaType): T = jackson2Library.loads(toString(), javaType)

/**
 * Extension to decode data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJackson2(type: Type): T = jackson2Library.loads(toString(), type)

/**
 * Extension to decode dynamic JSON object from string.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : JsonNode> CharSequence.parseJsonNode(): T = jackson2Library.loads(toString())

/**
 * Extension to decode data from byte array leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T> ByteArray.parseJackson2(): T = jackson2Library.loads(this, T::class.java)

/**
 * Extension to decode data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJackson2(type: Class<T>): T = jackson2Library.loads(this, type)

/**
 * Extension to decode data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJackson2(typeReference: TypeReference<T>): T = jackson2Library.loads(this, typeReference)

/**
 * Extension to decode data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJackson2(javaType: JavaType): T = jackson2Library.loads(this, javaType)

/**
 * Extension to decode data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJackson2(type: Type): T = jackson2Library.loads(this, type)

/**
 * Extension to decode dynamic JSON object from byte array.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : JsonNode> ByteArray.parseJsonNode(): T = jackson2Library.loads(this)

/**
 * Extension for [Jackson2Library.listTypeReference] leveraging reified type parameters.
 */
inline fun <reified T : Any> listTypeReference(): TypeReference<List<T>> =
    jackson2Library.listTypeReference(T::class.java)

/**
 * Extension for [Jackson2Library.mapTypeReference] leveraging reified type parameters.
 */
inline fun <reified K : Any, reified V : Any> mapTypeReference(): TypeReference<Map<K, V>> =
    jackson2Library.mapTypeReference(K::class.java, V::class.java)

/**
 * Extension for [Jackson2Library.collectionTypeReference] leveraging reified type parameters.
 */
inline fun <reified T : Any, reified C : Collection<T>> collectionTypeReference(): TypeReference<C> =
    jackson2Library.collectionTypeReference(T::class.java, C::class.java)

/**
 * Extension for [Jackson2Library.mapTypeReference] leveraging reified type parameters.
 */
inline fun <reified K : Any, reified V : Any, reified M : Map<K, V>> anyMapTypeReference(): TypeReference<M> =
    jackson2Library.mapTypeReference(K::class.java, V::class.java, M::class.java)
