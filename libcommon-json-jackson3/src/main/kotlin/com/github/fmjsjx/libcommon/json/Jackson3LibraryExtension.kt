package com.github.fmjsjx.libcommon.json

import tools.jackson.core.type.TypeReference
import tools.jackson.databind.JavaType
import tools.jackson.databind.JsonNode
import java.lang.reflect.Type
import kotlin.reflect.KClass


/**
 * The singleton [Jackson3Library] instance.
 *
 * @author MJ Fang
 * @since 3.17
 */
inline val jackson3Library: Jackson3Library get() = Jackson3Library.getInstance()

/**
 * Extension to encode any object to JSON value as string.
 *
 * @author MJ Fang
 * @since 3.17
 */
fun Any?.toJackson3String(): String = jackson3Library.dumpsToString(this)

/**
 * Extension to encode any object to JSON value as byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
fun Any?.toJackson3Bytes(): String = jackson3Library.dumpsToString(this)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3(type: KClass<T>): T = parseJackson3(type.java)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3(type: Class<T>): T = jackson3Library.loads(toString(), type)

/**
 * Extension to decode data from string leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.17
 */
inline fun <reified T : Any> CharSequence.parseJackson3(): T = parseJackson3(T::class)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3(typeReference: TypeReference<T>): T =
    jackson3Library.loads(toString(), typeReference)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3(javaType: JavaType): T =
    jackson3Library.loads(toString(), javaType)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3(type: Type): T = jackson3Library.loads(toString(), type)

/**
 * Extension to decode dynamic JSON object from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
fun <T : JsonNode> CharSequence.parseJackson3Node(): T = jackson3Library.loads(toString())

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3(type: Class<T>): T = jackson3Library.loads(this, type)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3(type: KClass<T>): T = parseJackson3(type.java)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
inline fun <reified T : Any> ByteArray.parseJackson3(): T = parseJackson3(T::class)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3(typeReference: TypeReference<T>): T = 
    jackson3Library.loads(this, typeReference)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3(javaType: JavaType): T = jackson3Library.loads(this, javaType)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3(type: Type): T = jackson3Library.loads(this, type)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
fun <T : JsonNode> ByteArray.parseJackson3Node(): T = jackson3Library.loads(this)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3OrNull(type: KClass<T>): T? = parseJackson3OrNull(type.java)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3OrNull(type: Class<T>): T? = jackson3Library.loads(toString(), type)

/**
 * Extension to decode data from string leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.17
 */
inline fun <reified T : Any> CharSequence.parseJackson3OrNull(): T? = parseJackson3OrNull(T::class)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3OrNull(typeReference: TypeReference<T>): T? =
    jackson3Library.loads(toString(), typeReference)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3OrNull(javaType: JavaType): T? =
    jackson3Library.loads(toString(), javaType)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> CharSequence.parseJackson3OrNull(type: Type): T? = jackson3Library.loads(toString(), type)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3OrNull(type: Class<T>): T? = jackson3Library.loads(this, type)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3OrNull(type: KClass<T>): T? = parseJackson3OrNull(type.java)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
inline fun <reified T : Any> ByteArray.parseJackson3OrNull(): T? = parseJackson3OrNull(T::class)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3OrNull(typeReference: TypeReference<T>): T? = 
    jackson3Library.loads(this, typeReference)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3OrNull(javaType: JavaType): T? = jackson3Library.loads(this, javaType)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.17
 */
infix fun <T : Any> ByteArray.parseJackson3OrNull(type: Type): T? = jackson3Library.loads(this, type)

/**
 * Extension for [Jackson3Library.listTypeReference] leveraging reified type parameters.
 */
inline fun <reified T : Any> listTypeReference(): TypeReference<List<T>> =
    jackson3Library.listTypeReference(T::class.java)

/**
 * Extension for [Jackson3Library.mapTypeReference] leveraging reified type parameters.
 */
inline fun <reified K : Any, reified V : Any> mapTypeReference(): TypeReference<Map<K, V>> =
    jackson3Library.mapTypeReference(K::class.java, V::class.java)

/**
 * Extension for [Jackson3Library.collectionTypeReference] leveraging reified type parameters.
 */
inline fun <reified T : Any, reified C : Collection<T>> collectionTypeReference(): TypeReference<C> =
    jackson3Library.collectionTypeReference(T::class.java, C::class.java)

/**
 * Extension for [Jackson3Library.mapTypeReference] leveraging reified type parameters.
 */
inline fun <reified K : Any, reified V : Any, reified M : Map<K, V>> anyMapTypeReference(): TypeReference<M> =
    jackson3Library.mapTypeReference(K::class.java, V::class.java, M::class.java)