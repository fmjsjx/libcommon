package com.github.fmjsjx.libcommon.util.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * The [ZoneId] of ID `Etc/UTC`.
 *
 * @since 4.0
 */
val ETC_UTC: ZoneId = "Etc/UTC".toZoneId()

/**
 * The [ZoneId] of ID `Asia/Shanghai`.
 *
 * @since 4.0
 */
val ASIA_SHANGHAI: ZoneId = "Asia/Shanghai".toZoneId()

/**
 * The [ZoneId] of ID `Asia/Hong_Kong`.
 *
 * @since 4.0
 */
val ASIA_HONG_KONG: ZoneId = "Asia/Hong_Kong".toZoneId()

/**
 * The [ZoneId] of ID `Asia/Taipei`.
 *
 * @since 4.0
 */
val ASIA_TAIPEI: ZoneId = "Asia/Taipei".toZoneId()

/**
 * The [ZoneId] of ID `America/New_York`.
 *
 * @since 4.0
 */
val AMERICA_NEW_YORK: ZoneId = "America/New_York".toZoneId()

/**
 * The [LocalDateTime] of EPOCH.
 *
 * @since 4.2
 */
val LOCAL_DATE_TIME_EPOCH: LocalDateTime = LocalDate.EPOCH.atStartOfDay()