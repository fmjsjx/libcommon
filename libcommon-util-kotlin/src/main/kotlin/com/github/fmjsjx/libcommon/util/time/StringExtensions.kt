package com.github.fmjsjx.libcommon.util.time

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Obtains an instance of [ZoneId] from an ID ensuring that the ID is
 * valid and available for use.
 *
 * @param this the time-zone ID
 * @return the zone ID
 *
 * @author MJ Fang
 * @since 4.0
 */
fun <T : CharSequence> T.toZoneId(): ZoneId = ZoneId.of(toString())

/**
 * Obtains an instance of [LocalDateTime] from a text string using the
 * specified [DateTimeFormatter].
 *
 * @param this the text to parse
 * @param formatter the formatter to use
 * @return the parsed local date-time
 * @author MJ Fang
 * @since 4.0
 */
infix fun <T : CharSequence> T.toLocalDateTime(formatter: DateTimeFormatter): LocalDateTime =
    LocalDateTime.parse(toString(), formatter)

/**
 * Obtains an instance of [DateTimeFormatter] from a pattern.
 *
 * @param this the pattern
 * @return the formatter
 * @author MJ Fang
 * @since 4.0
 */
fun <T : CharSequence> T.toDateTimeFormatter(): DateTimeFormatter = DateTimeFormatter.ofPattern(toString())