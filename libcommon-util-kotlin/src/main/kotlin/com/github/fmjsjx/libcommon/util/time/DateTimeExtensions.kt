package com.github.fmjsjx.libcommon.util.time

import com.github.fmjsjx.libcommon.util.DateTimeUtil
import java.util.Date
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Converts this date-time to the number of seconds from the epoch of
 * 1970-01-01T00:00:00Z.
 *
 * @param zone the time-zone
 * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDateTime.toEpochSecond(zone: ZoneId = ZoneId.systemDefault()): Long =
    DateTimeUtil.toEpochSecond(this, zone)

/**
 * Converts this date-time to the number of milliseconds from the epoch
 * of 1970-01-01T00:00:00Z.
 *
 * @param zone the time-zone
 * @return the number of milliseconds since the epoch of 1970-01-01T00:00:00Z
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDateTime.toEpochMilli(zone: ZoneId = ZoneId.systemDefault()): Long =
    DateTimeUtil.toEpochMilli(this, zone)

/**
 * Converts this date-time to the number of milliseconds from the epoch
 * of 1970-01-01T00:00:00Z.
 *
 * @param offset the zone offset
 * @return the number of milliseconds since the epoch of 1970-01-01T00:00:00Z
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDateTime.toEpochMilli(offset: ZoneOffset): Long =
    DateTimeUtil.toEpochMilli(this, offset)

/**
 * Returns `true` if this date and the specified date is in same week
 * (Monday start), `false` otherwise.
 *
 * @param this  the first date
 * @param other the second date
 * @return `true` if the specified to dates is in same week (Monday
 *         start), `false` otherwise
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDate.isSameWeek(other: LocalDate): Boolean =
    if (isEqual(other)) {
        true
    } else {
        DateTimeUtil.isSameWeek(this, other)
    }

/**
 * Converts this date-time to an [Instant].
 *
 * @param zone the time-zone to use for the conversion
 * @return an [Instant] representing the same instant
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDateTime.toInstant(zone: ZoneId = ZoneId.systemDefault()): Instant =
    atZone(zone).toInstant()

/**
 * Converts this [Instant] to a [LocalDateTime].
 *
 * @param zone the time-zone to use for the conversion
 * @return a [LocalDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Instant.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    LocalDateTime.ofInstant(this, zone)

/**
 * Converts this [Instant] to a [ZonedDateTime].
 *
 * @param zone the time-zone to use for the conversion
 * @return a [ZonedDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Instant.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    ZonedDateTime.ofInstant(this, zone)

/**
 * Converts this [Instant] to a [OffsetDateTime].
 *
 * @param zone the time-zone to use for the conversion
 * @return a [OffsetDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Instant.toOffsetDateTime(zone: ZoneId = ZoneId.systemDefault()): OffsetDateTime =
    OffsetDateTime.ofInstant(this, zone)

/**
 * Converts this [Instant] to a legacy [Date].
 *
 * @return a [Date]
 * @author MJ Fang
 * @since 4.0
 */
fun Instant.toLegacyDate(): Date = Date.from(this)

/**
 * Converts this date-time to a legacy [Date].
 *
 * @param zone the time-zone to use for the conversion
 * @return a [Date]
 * @author MJ Fang
 * @since 4.0
 */
infix fun LocalDateTime.toLegacyDate(zone: ZoneId = ZoneId.systemDefault()): Date =
    toInstant(zone).toLegacyDate()

/**
 * Converts this date-time to a legacy [Date].
 *
 * @return a [Date]
 * @author MJ Fang
 * @since 4.0
 */
fun ZonedDateTime.toLegacyDate(): Date = toInstant().toLegacyDate()

/**
 * Converts this date-time to a legacy [Date].
 *
 * @return a [Date]
 * @author MJ Fang
 * @since 4.0
 */
fun OffsetDateTime.toLegacyDate(): Date = toInstant().toLegacyDate()

/**
 * Converts this legacy [Date] to a [LocalDateTime],
 *
 * @param zone the time-zone to use for the conversion
 * @return a [LocalDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Date.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    DateTimeUtil.local(this, zone)

/**
 * Converts this legacy [Date] to a [ZonedDateTime],
 *
 * @param zone the time-zone to use for the conversion
 * @return a [ZonedDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Date.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    DateTimeUtil.zoned(this, zone)

/**
 * Converts this legacy [Date] to a [OffsetDateTime],
 *
 * @param zone the time-zone to use for the conversion
 * @return a [OffsetDateTime]
 * @author MJ Fang
 * @since 4.0
 */
infix fun Date.toOffsetDateTime(zone: ZoneId = ZoneId.systemDefault()): OffsetDateTime =
    DateTimeUtil.offset(this, zone)