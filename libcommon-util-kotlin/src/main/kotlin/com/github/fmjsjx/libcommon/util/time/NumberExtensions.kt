package com.github.fmjsjx.libcommon.util.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Obtains an instance of [Instant] from this number of seconds from the
 * epoch of 1970-01-01T00:00:00Z.
 *
 * @return the instant
 * @author MJ Fang
 * @since 4.0
 */
fun <T : Number> T.toInstantS(): Instant = Instant.ofEpochSecond(toLong())

/**
 * Obtains an instance of [Instant] from this number of milliseconds from
 * the epoch of 1970-01-01T00:00:00.000Z.
 *
 * @return the instant
 * @author MJ Fang
 * @since 4.0
 */
fun <T : Number> T.toInstantM(): Instant = Instant.ofEpochMilli(toLong())

/**
 * Obtains an instance of [Instant] from this number of unix time.
 *
 * @return the instant
 * @author MJ Fang
 * @since 4.0
 */
fun <T : Number> T.toInstantU(): Instant = (toDouble() * 1000.0).toInstantM()

/**
 * Obtains an instance of [LocalDateTime] from this number of seconds
 * from the epoch of 1970-01-01T00:00:00Z.
 *
 * @param zone the time-zone
 * @return the local date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toLocalDateTimeS(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    toInstantS().toLocalDateTime(zone)

/**
 * Obtains an instance of [LocalDateTime] from this number of
 * milliseconds from the epoch of 1970-01-01T00:00:00.000Z.
 *
 * @param zone the time-zone
 * @return the local date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toLocalDateTimeM(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    toInstantM().toLocalDateTime(zone)

/**
 * Obtains an instance of [LocalDateTime] from this number of unix time.
 *
 * @param zone the time-zone
 * @return the local date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toLocalDateTimeU(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    toInstantU().toLocalDateTime(zone)

/**
 * Obtains an instance of [ZonedDateTime] from this number of seconds
 * from the epoch of 1970-01-01T00:00:00Z.
 *
 * @param zone the time-zone
 * @return the zoned date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toZonedDateTimeS(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    toInstantS().toZonedDateTime(zone)

/**
 * Obtains an instance of [ZonedDateTime] from this number of
 * milliseconds from the epoch of 1970-01-01T00:00:00.000Z.
 *
 * @param zone the time-zone
 * @return the zoned date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toZonedDateTimeM(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    toInstantM().toZonedDateTime(zone)

/**
 * Obtains an instance of [ZonedDateTime] from this number of unix time.
 *
 * @param zone the time-zone
 * @return the zoned date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toZonedDateTimeU(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    toInstantU().toZonedDateTime(zone)

/**
 * Obtains an instance of [OffsetDateTime] from this number of seconds
 * from the epoch of 1970-01-01T00:00:00Z.
 *
 * @param zone the time-zone
 * @return the offset date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toOffsetDateTimeS(zone: ZoneId = ZoneId.systemDefault()): OffsetDateTime =
    toInstantS().toOffsetDateTime(zone)

/**
 * Obtains an instance of [OffsetDateTime] from this number of
 * milliseconds from the epoch of 1970-01-01T00:00:00.000Z.
 *
 * @param zone the time-zone
 * @return the offset date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toOffsetDateTimeM(zone: ZoneId = ZoneId.systemDefault()): OffsetDateTime =
    toInstantM().toOffsetDateTime(zone)

/**
 * Obtains an instance of [OffsetDateTime] from this number of unix time.
 *
 * @param zone the time-zone
 * @return the offset date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : Number> T.toOffsetDateTimeU(zone: ZoneId = ZoneId.systemDefault()): OffsetDateTime =
    toInstantU().toOffsetDateTime(zone)