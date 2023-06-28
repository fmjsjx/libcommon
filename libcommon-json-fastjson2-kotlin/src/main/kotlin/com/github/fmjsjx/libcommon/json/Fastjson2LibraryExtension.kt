package com.github.fmjsjx.libcommon.json

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.TypeReference
import java.lang.reflect.Type

/**
 * The singleton [Fastjson2Library] instance
 *
 * @author MJ Fang
 * @since 3.4
 */
inline val fastjson2Library: Fastjson2Library get() = Fastjson2Library.getInstance()

/**
 * Extension to encode any object to JSON value as string.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun Any?.toFastjson2String(): String = fastjson2Library.dumpsToString(this)

/**
 * Extension to encode any object to JSON value as byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun Any?.toFastjson2Bytes(): ByteArray = fastjson2Library.dumpsToBytes(this)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> CharSequence.parseFastjson2(type: Class<T>): T = fastjson2Library.loads(toString(), type)

/**
 * Extension to decode data from string leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified T> CharSequence.parseFastjson2(): T = parseFastjson2(T::class.java)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> CharSequence.parseFastjson2(typeReference: TypeReference<T>): T =
    fastjson2Library.loads(toString(), typeReference)

/**
 * Extension to decode data from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> CharSequence.parseFastjson2(type: Type): T = fastjson2Library.loads(toString(), type)

/**
 * Extension to decode [JSONObject] from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun <S : CharSequence> S.parseJSONObject(): JSONObject = fastjson2Library.loads(toString())

/**
 * Extension to decode [JSONArray] from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun <S : CharSequence> S.parseJSONArray(): JSONArray = fastjson2Library.loadsArray(toString())

/**
 * Extension to decode List from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> CharSequence.parseFastjson2List(type: Type): List<T> =
    fastjson2Library.loadsList(toString(), type)

/**
 * Extension to decode List from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> CharSequence.parseFastjson2List(type: Class<T>): List<T> =
    fastjson2Library.loadsList(toString(), type)

/**
 * Extension to decode List from string.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified T> CharSequence.parseFastjson2List(): List<T> = parseFastjson2List(T::class.java)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> ByteArray.parseFastjson2(type: Class<T>): T = fastjson2Library.loads(this, type)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified T> ByteArray.parseFastjson2(): T = parseFastjson2(T::class.java)


/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> ByteArray.parseFastjson2(typeReference: TypeReference<T>): T =
    fastjson2Library.loads(toString(), typeReference)

/**
 * Extension to decode data from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
infix fun <T> ByteArray.parseFastjson2(type: Type): T = fastjson2Library.loads(toString(), type)

/**
 * Extension to decode [JSONObject] from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun ByteArray.parseJSONObject(): JSONObject = fastjson2Library.loads(toString())

/**
 * Extension to decode [JSONArray] from byte array.
 *
 * @author MJ Fang
 * @since 3.4
 */
fun ByteArray.parseJSONArray(): JSONArray = fastjson2Library.loadsArray(toString())

/**
 * Extension for [Fastjson2Library.listTypeReference] leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified T : Any> fastjson2ListTypeReference(): TypeReference<List<T>> =
    fastjson2Library.listTypeReference(T::class.java)

/**
 * Extension for [Fastjson2Library.mapTypeReference] leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified K : Any, reified V : Any> fastjson2MapTypeReference(): TypeReference<Map<K, V>> =
    fastjson2Library.mapTypeReference(K::class.java, V::class.java)

/**
 * Extension for [Fastjson2Library.collectionTypeReference] leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified T : Any, reified C : Collection<T>> fastjson2CollectionTypeReference(): TypeReference<C> =
    fastjson2Library.collectionTypeReference(T::class.java, C::class.java)

/**
 * Extension for [Fastjson2Library.mapTypeReference] leveraging reified type parameters.
 *
 * @author MJ Fang
 * @since 3.4
 */
inline fun <reified K : Any, reified V : Any, reified M : Map<K, V>> fastjson2SpecMapTypeReference(): TypeReference<M> =
    fastjson2Library.mapTypeReference(K::class.java, V::class.java, M::class.java)
