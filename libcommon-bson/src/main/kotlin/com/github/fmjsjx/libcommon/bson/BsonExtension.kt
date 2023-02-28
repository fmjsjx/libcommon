package com.github.fmjsjx.libcommon.bson

import org.bson.*
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.jvm.Throws


/**
 * Extension to construct a new [BsonInt32] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T.toBsonInt32() = BsonInt32(toInt())

/**
 * Extension to construct a new [BsonInt32] instance or the singleton [BsonNull] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T?.toBsonInt32OrNull(): BsonValue = this?.toBsonInt32() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonInt64] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T.toBsonInt64() = BsonInt64(toLong())

/**
 * Extension to construct a new [BsonInt64] instance or the singleton [BsonNull] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T?.toBsonInt64OrNull(): BsonValue = this?.toBsonInt64() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonDouble] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T.toBsonDouble() = BsonDouble(toDouble())

/**
 * Extension to construct a new [BsonDouble] instance or the singleton [BsonNull] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T?.toBsonDoubleOrNull(): BsonValue = this?.toBsonDouble() ?: BsonNull.VALUE

/**
 * Extension to construct a new [Decimal128] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T.toDecimal128() =
    when (this) {
        is BigInteger -> Decimal128(BigDecimal(this))
        is BigDecimal -> Decimal128(this)
        is Long -> Decimal128(this)
        is Int -> Decimal128(this.toLong())
        is Byte -> Decimal128(this.toLong())
        is Short -> Decimal128(this.toLong())
        else -> Decimal128(BigDecimal(toString()))
    }

/**
 * Extension to construct a new [BsonDecimal128] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T.toBsonDecimal128() = BsonDecimal128(toDecimal128())

/**
 * Extension to construct a new [BsonDecimal128] instance or the singleton [BsonNull] instance with this [Number].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Number> T?.toBsonDecimal128OrNull(): BsonValue = this?.toBsonDecimal128() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonObjectId] instance with this [ObjectId].
 * @author MJ Fang
 * @since 3.1
 */
fun ObjectId.toBsonObjectId() = BsonObjectId(this)

/**
 * Extension to construct a new [BsonObjectId] instance or the singleton [BsonNull] instance with this [ObjectId].
 * @author MJ Fang
 * @since 3.1
 */
fun ObjectId?.toBsonObjectIdOrNull(): BsonValue = this?.toBsonObjectId() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonString] instance with this object.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Any> T.toBsonString() = BsonString(toString())

/**
 * Extension to construct a new [BsonString] instance or the singleton [BsonNull] instance with this object.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : Any> T?.toBsonStringOrNull(): BsonValue = this?.toBsonString() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonBinary] instance with this [UUID].
 * @author MJ Fang
 * @since 3.1
 */
fun UUID.toBsonBinary() = BsonBinary(this)

/**
 * Extension to construct a new [BsonBinary] instance or the singleton [BsonNull] instance with this [UUID].
 * @author MJ Fang
 * @since 3.1
 */
fun UUID?.toBsonBinaryOrNull(): BsonValue = this?.toBsonBinary() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonDateTime] instance with this [ZonedDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun ZonedDateTime.toBsonDateTime() = BsonDateTime(toInstant().toEpochMilli())

/**
 * Extension to construct a new [BsonDateTime] instance or the singleton [BsonNull] instance with this [ZonedDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun ZonedDateTime?.toBsonDateTimeOrNull(): BsonValue = this?.toBsonDateTime() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonDateTime] instance with this [OffsetDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun OffsetDateTime.toBsonDateTime() = BsonDateTime(toInstant().toEpochMilli())

/**
 * Extension to construct a new [BsonDateTime] instance or the singleton [BsonNull] instance with this [OffsetDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun OffsetDateTime?.toBsonDateTimeOrNull(): BsonValue = this?.toBsonDateTime() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonDateTime] instance with this [LocalDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun LocalDateTime.toBsonDateTime() = atZone(ZoneId.systemDefault()).toBsonDateTime()

/**
 * Extension to construct a new [BsonDateTime] instance or the singleton [BsonNull] instance with this [LocalDateTime].
 * @author MJ Fang
 * @since 3.1
 */
fun LocalDateTime?.toBsonDateTimeOrNull(): BsonValue = this?.toBsonDateTime() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonDateTime] instance with this legacy [Date].
 * @author MJ Fang
 * @since 3.1
 */
fun Date.toBsonDateTime() = BsonDateTime(time)

/**
 * Extension to construct a new [BsonDateTime] instance or the singleton [BsonNull] instance with this legacy [Date].
 * @author MJ Fang
 * @since 3.1
 */
fun Date?.toBsonDateTimeOrNull(): BsonValue = this?.toBsonDateTime() ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonArray] instance with this [Iterable].
 * @author MJ Fang
 * @since 3.1
 */
fun <E : BsonValue, T : Iterable<E>> T.toBsonArray() = BsonArray(
    if (this is List<*>) {
        @Suppress("UNCHECKED_CAST")
        this as List<E>
    } else {
        toList()
    }
)

/**
 * Extension to construct a new [BsonArray] instance with this [Iterable].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <E : Any?, T : Iterable<E>> T.toBsonArray(transform: (E) -> BsonValue) =
    mapTo(BsonArray(if (this is Collection<*>) size else 10), transform)

/**
 * Extension to construct a new [BsonArray] instance or the singleton [BsonNull] instance with this [Iterable].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <E : Any?, T : Iterable<E>> T?.toBsonArrayOrNull(transform: (E) -> BsonValue): BsonValue =
    this?.toBsonArray(transform) ?: BsonNull.VALUE

/**
 * Extension to construct a new [BsonArray] instance with this [Array].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <E : Any?> Array<E>.toBsonArray(transform: (E) -> BsonValue) = mapTo(BsonArray(size), transform)

/**
 * Extension to construct a new [BsonArray] instance or the singleton [BsonNull] instance with this [Array].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <E : Any?> Array<E>?.toBsonArrayOrNull(transform: (E) -> BsonValue): BsonValue =
    this?.toBsonArray(transform) ?: BsonNull.VALUE

/**
 * Extension to convert to [Int] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
@Throws(IllegalStateException::class)
fun <T : BsonValue> T.intValue() =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).intValue()
        BsonType.STRING -> (this as BsonString).value.toInt()
        else -> throw IllegalStateException("Value expected to be of type NUMBER is of unexpected type $bsonType")
    }

/**
 * Extension to convert to [Int] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.intValueOrNull(): Int? =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).intValue()
        BsonType.STRING -> (this as BsonString).value.toIntOrNull()
        else -> null
    }

/**
 * Extension to convert to [Long] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
@Throws(IllegalStateException::class)
fun <T : BsonValue> T.longValue() =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).longValue()
        BsonType.STRING -> (this as BsonString).value.toLong()
        else -> throw IllegalStateException("Value expected to be of type NUMBER is of unexpected type $bsonType")
    }

/**
 * Extension to convert to [Long] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.longValueOrNull(): Long? =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).longValue()
        BsonType.STRING -> (this as BsonString).value.toLongOrNull()
        else -> null
    }

/**
 * Extension to convert to [Double] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
@Throws(IllegalStateException::class)
fun <T : BsonValue> T.doubleValue() =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).doubleValue()
        BsonType.STRING -> (this as BsonString).value.toDouble()
        else -> throw IllegalStateException("Value expected to be of type NUMBER is of unexpected type $bsonType")
    }

/**
 * Extension to convert to [Double] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.doubleValueOrNull(): Double? =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 -> (this as BsonNumber).doubleValue()
        BsonType.STRING -> (this as BsonString).value.toDoubleOrNull()
        else -> null
    }

/**
 * Extension to convert to [BigInteger] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
@Throws(IllegalStateException::class)
fun <T : BsonValue> T.bigIntegerValue(): BigInteger =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 ->
            (this as BsonNumber).decimal128Value().bigDecimalValue().toBigInteger()

        BsonType.STRING -> (this as BsonString).value.toBigInteger()
        else -> throw IllegalStateException("Value expected to be of type NUMBER is of unexpected type $bsonType")
    }

/**
 * Extension to convert to [BigInteger] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.bigIntegerValueOrNull(): BigInteger? =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 ->
            (this as BsonNumber).decimal128Value().bigDecimalValue().toBigInteger()

        BsonType.STRING -> (this as BsonString).value.toBigIntegerOrNull()
        else -> null
    }

/**
 * Extension to convert to [BigDecimal] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
@Throws(IllegalStateException::class)
fun <T : BsonValue> T.bigDecimalValue(): BigDecimal =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 ->
            (this as BsonNumber).decimal128Value().bigDecimalValue()

        BsonType.STRING -> (this as BsonString).value.toBigDecimal()
        else -> throw IllegalStateException("Value expected to be of type NUMBER is of unexpected type $bsonType")
    }

/**
 * Extension to convert to [BigDecimal] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.bigDecimalValueOrNull(): BigDecimal? =
    when (bsonType) {
        BsonType.DOUBLE, BsonType.INT32, BsonType.INT64, BsonType.DECIMAL128 ->
            (this as BsonNumber).decimal128Value().bigDecimalValue()

        BsonType.STRING -> (this as BsonString).value.toBigDecimalOrNull()
        else -> null
    }

/**
 * Extension to convert to [String] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.stringValue(): String = stringValueOrNull()
    ?: throw IllegalStateException("Value expected to be of type STRING is of unexpected type $bsonType")

/**
 * Extension to convert to [String] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.stringValueOrNull(): String? =
    when (bsonType) {
        BsonType.STRING -> (this as BsonString).value
        BsonType.DOUBLE -> (this as BsonDouble).value.toString()
        BsonType.INT32 -> (this as BsonInt32).value.toString()
        BsonType.INT64 -> (this as BsonInt64).value.toString()
        BsonType.DECIMAL128 -> (this as BsonDecimal128).value.toString()
        BsonType.OBJECT_ID -> (this as BsonObjectId).value.toString()
        else -> null
    }

/**
 * Extension to convert to [ZonedDateTime] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.zonedDateTimeValue(zoneId: ZoneId = ZoneId.systemDefault()) = zonedDateTimeValueOrNull(zoneId)
    ?: throw IllegalStateException("Value expected to be of type DATE_TIME is of unexpected type $bsonType")

/**
 * Extension to convert to [ZonedDateTime] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.zonedDateTimeValueOrNull(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime? =
    ZonedDateTime.ofInstant(
        when (bsonType) {
            BsonType.DATE_TIME -> Instant.ofEpochMilli((this as BsonDateTime).value)
            BsonType.TIMESTAMP -> Instant.ofEpochSecond((this as BsonTimestamp).time.toLong())
            else -> null
        }, zoneId
    )

/**
 * Extension to convert to [OffsetDateTime] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.offsetDateTimeValue(zoneId: ZoneId = ZoneId.systemDefault()) = offsetDateTimeValueOrNull(zoneId)
    ?: throw IllegalStateException("Value expected to be of type DATE_TIME is of unexpected type $bsonType")

/**
 * Extension to convert to [OffsetDateTime] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.offsetDateTimeValueOrNull(zoneId: ZoneId = ZoneId.systemDefault()): OffsetDateTime? =
    OffsetDateTime.ofInstant(
        when (bsonType) {
            BsonType.DATE_TIME -> Instant.ofEpochMilli((this as BsonDateTime).value)
            BsonType.TIMESTAMP -> Instant.ofEpochSecond((this as BsonTimestamp).time.toLong())
            else -> null
        }, zoneId
    )

/**
 * Extension to convert to [LocalDateTime] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.localDateTimeValueOrNull(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime? =
    zonedDateTimeValueOrNull(zoneId)?.toLocalDateTime()

/**
 * Extension to convert to [LocalDateTime] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
fun <T : BsonValue> T.localDateTimeValue(zoneId: ZoneId = ZoneId.systemDefault()) = localDateTimeValueOrNull(zoneId)
    ?: throw IllegalStateException("Value expected to be of type DATE_TIME is of unexpected type $bsonType")

/**
 * Extension to convert to [List] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, E : Any> T.listValueOrNull(transform: (BsonValue) -> E): List<E>? =
    when (bsonType) {
        BsonType.ARRAY -> (this as BsonArray).values.map(transform)
        else -> null
    }

/**
 * Extension to convert to [List] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, E : Any> T.listValue(transform: (BsonValue) -> E) = listValueOrNull(transform)
    ?: throw IllegalStateException("Value expected to be of type ARRAY is of unexpected type $bsonType")


/**
 * Extension to convert to [MutableList] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, E : Any> T.mutableListValueOrNull(transform: (BsonValue) -> E): MutableList<E>? =
    when (bsonType) {
        BsonType.ARRAY -> (this as BsonArray).values.mapTo(ArrayList(size), transform)
        else -> null
    }

/**
 * Extension to convert to [MutableList] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, E : Any> T.mutableListValue(transform: (BsonValue) -> E) = mutableListValueOrNull(transform)
    ?: throw IllegalStateException("Value expected to be of type ARRAY is of unexpected type $bsonType")

/**
 * Extension to convert to [Array] value from this [BsonValue] or `null`.
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, reified E : Any> T.arrayValueOrNull(transform: (BsonValue) -> E): Array<E>? =
    when (bsonType) {
        BsonType.ARRAY -> (this as BsonArray).values.map(transform).toTypedArray()
        else -> null
    }

/**
 * Extension to convert to [Array] value from this [BsonValue].
 * @author MJ Fang
 * @since 3.1
 */
inline fun <T : BsonValue, reified E : Any> T.arrayValue(transform: (BsonValue) -> E) = arrayValueOrNull(transform)
    ?: throw IllegalStateException("Value expected to be of type ARRAY is of unexpected type $bsonType")

