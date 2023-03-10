package com.github.fmjsjx.libcommon.json

import com.jsoniter.ValueType
import com.jsoniter.spi.TypeLiteral
import java.lang.reflect.Type
import java.util.*

/**
 * Type alias for [com.jsoniter.any.Any].
 * @author MJ Fang
 * @since 3.1
 */
typealias JsonAny = com.jsoniter.any.Any

/**
 * The singleton [JsoniterLibrary] instance.
 * @author MJ Fang
 * @since 3.1
 */
inline val jsoniterLibrary: JsoniterLibrary get() = JsoniterLibrary.getInstance()!!

/**
 * Extension to encode any object to JSON value as string.
 * @author MJ Fang
 * @since 3.1
 */
fun Any?.toJsoniterString(): String = jsoniterLibrary.dumpsToString(this)

/**
 * Extension to encode any object to JSON value as byte array.
 * @author MJ Fang
 * @since 3.1
 */
fun Any?.toJsoniterBytes(): ByteArray = jsoniterLibrary.dumpsToBytes(this)

/**
 * Extension to decodes data from string leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T> CharSequence.parseJsoniter(): T = parseJsoniter(T::class.java)

/**
 * Extension to decodes data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJsoniter(type: Class<T>): T = jsoniterLibrary.loads(toString(), type)

/**
 * Extension to decodes data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJsoniter(typeLiteral: TypeLiteral<T>): T =
    jsoniterLibrary.loads(toString(), typeLiteral)

/**
 * Extension to decodes data from string.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> CharSequence.parseJsoniter(type: Type): T = jsoniterLibrary.loads(toString(), type)

/**
 * Extension to decodes dynamic JSON object from string.
 * @author MJ Fang
 * @since 3.1
 */
fun CharSequence.parseJsoniter(): JsonAny = jsoniterLibrary.loads(toString())

/**
 * Extension to decodes data from byte array leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T> ByteArray.parseJsoniter(): T = parseJsoniter(T::class.java)

/**
 * Extension to decodes data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJsoniter(type: Class<T>): T = jsoniterLibrary.loads(this, type)

/**
 * Extension to decodes data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJsoniter(typeLiteral: TypeLiteral<T>): T = jsoniterLibrary.loads(this, typeLiteral)

/**
 * Extension to decodes data from byte array.
 * @author MJ Fang
 * @since 3.1
 */
infix fun <T> ByteArray.parseJsoniter(type: Type): T = jsoniterLibrary.loads(this, type)

/**
 * Extension to decodes dynamic JSON object from byte array.
 * @author MJ Fang
 * @since 3.1
 */
fun ByteArray.parseJsoniter(): JsonAny = jsoniterLibrary.loads(this)

/**
 * Extension for [JsoniterLibrary.listTypeLiteral] leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T : Any> listTypeLiteral(): TypeLiteral<List<T>> =
    jsoniterLibrary.listTypeLiteral(T::class.java)

/**
 * Extension for [JsoniterLibrary.mapTypeLiteral] leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified K : Any, reified V : Any> mapTypeLiteral(): TypeLiteral<Map<K, V>> =
    jsoniterLibrary.mapTypeLiteral(K::class.java, V::class.java)

/**
 * Extension for [JsoniterLibrary.collectionTypeLiteral] leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified T : Any, reified C : Collection<T>> collectionTypeLiteral(): TypeLiteral<C> =
    jsoniterLibrary.collectionTypeLiteral(T::class.java, C::class.java)

/**
 * Extension for [JsoniterLibrary.mapTypeLiteral] leveraging reified type parameters.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <reified K : Any, reified V : Any, reified M : Map<K, V>> anyMapTypeLiteral(): TypeLiteral<M> =
    jsoniterLibrary.mapTypeLiteral(K::class.java, V::class.java, M::class.java)

/**
 * Enum set contains `null` [ValueType]s.
 * @author MJ Fang
 * @since 3.1
 */
val nullValueTypes: EnumSet<ValueType> = EnumSet.of(ValueType.INVALID, ValueType.NULL)

/**
 * Extension to returns the [JsonAny] by the specified key given unless the value is in `null` [ValueType]s.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.getOrNull(key: String): JsonAny? = get(key).takeUnless { it.valueType() in nullValueTypes }

/**
 * Extension to returns the [JsonAny] by the specified keys given unless the value is in `null` [ValueType]s.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.getOrNull(vararg keys: Any): JsonAny? = get(*keys).takeUnless { it.valueType() in nullValueTypes }

/**
 * Extension to returns the string value by the specified key given unless the value is in `null` [ValueType]s.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.toStringOrNull(key: String): String? = getOrNull(key)?.toString()

/**
 * Extension to returns the string value by the specified keys given unless the value is in `null` [ValueType]s.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.toStringOrNull(vararg keys: Any): String? = getOrNull(*keys)?.toString()

/**
 * Extension to returns the string value by the specified key given unless the value `is null or empty`.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.toNotEmptyStringOrNull(key: String): String? = toStringOrNull(key).takeUnless(String?::isNullOrEmpty)

/**
 * Extension to returns the string value by the specified keys given unless the value `is null or empty`.
 * @author MJ Fang
 * @since 3.1
 */
fun JsonAny.toNotEmptyStringOrNull(vararg keys: Any): String? = toStringOrNull(keys).takeUnless(String?::isNullOrEmpty)
