package com.github.fmjsjx.libcommon.util.time

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.*
import java.util.Date

class DateTimeExtensionsTests {

    @Test
    fun testToEpochSecond() {
        val now = LocalDateTime.now()
        assertEquals(now.atZone(ZoneId.systemDefault()).toEpochSecond(), now.toEpochSecond())
        assertEquals(now.atZone(ETC_UTC).toEpochSecond(), now.toEpochSecond(ETC_UTC))
    }

    @Test
    fun testToEpochMilli() {
        val now = LocalDateTime.now()
        assertEquals(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), now.toEpochMilli())
        assertEquals(now.atZone(ETC_UTC).toInstant().toEpochMilli(), now.toEpochMilli(ETC_UTC))
        val offset = ZoneOffset.UTC
        assertEquals(now.atOffset(offset).toInstant().toEpochMilli(), now.toEpochMilli(offset))
    }

    @Test
    fun testIsSameWeek() {
        // Test same date
        val date1 = LocalDate.of(2024, 1, 15) // Monday
        assertTrue(date1 isSameWeek date1)

        // Test dates in same week
        val date2 = LocalDate.of(2024, 1, 16) // Tuesday
        val date3 = LocalDate.of(2024, 1, 21) // Sunday
        assertTrue(date1 isSameWeek date2)
        assertTrue(date1 isSameWeek date3)
        assertTrue(date2 isSameWeek date3)

        // Test dates in different weeks
        val date4 = LocalDate.of(2024, 1, 22) // Monday of next week
        assertFalse(date1 isSameWeek date4)
        assertFalse(date3 isSameWeek date4)

        // Test edge case: Sunday and Monday in different weeks
        val sunday = LocalDate.of(2024, 1, 14) // Sunday
        val monday = LocalDate.of(2024, 1, 15) // Monday
        assertFalse(sunday isSameWeek monday)
    }

    @Test
    fun testLocalDateTimeToInstant() {
        val localDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45)

        // Test with system default zone
        val expectedInstant1 = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        assertEquals(expectedInstant1, localDateTime.toInstant())

        // Test with specific zone
        val expectedInstant2 = localDateTime.atZone(ETC_UTC).toInstant()
        assertEquals(expectedInstant2, localDateTime.toInstant(ETC_UTC))

        // Test with different zone
        val expectedInstant3 = localDateTime.atZone(ASIA_SHANGHAI).toInstant()
        assertEquals(expectedInstant3, localDateTime.toInstant(ASIA_SHANGHAI))
    }

    @Test
    fun testInstantToLocalDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC

        val localDateTime1 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(localDateTime1, instant.toLocalDateTime())

        val localDateTime2 = LocalDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(localDateTime2, instant.toLocalDateTime(ETC_UTC))
    }

    @Test
    fun testInstantToZonedDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC

        val zonedDateTime1 = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(zonedDateTime1, instant.toZonedDateTime())

        val zonedDateTime2 = ZonedDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(zonedDateTime2, instant.toZonedDateTime(ETC_UTC))
    }

    @Test
    fun testInstantToOffsetDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC

        val offsetDateTime1 = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(offsetDateTime1, instant.toOffsetDateTime())

        val offsetDateTime2 = OffsetDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(offsetDateTime2, instant.toOffsetDateTime(ETC_UTC))
    }

    @Test
    fun testInstantToLegacyDate() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC
        val date = instant.toLegacyDate()

        assertEquals(instant.toEpochMilli(), date.time)
        assertEquals(Date.from(instant), date)
    }

    @Test
    fun testLocalDateTimeToLegacyDate() {
        val localDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45)

        // Test with system default zone
        val expectedDate1 = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        assertEquals(expectedDate1, localDateTime.toLegacyDate())

        // Test with specific zone
        val expectedDate2 = Date.from(localDateTime.atZone(ETC_UTC).toInstant())
        assertEquals(expectedDate2, localDateTime.toLegacyDate(ETC_UTC))

        // Test with different zone
        val expectedDate3 = Date.from(localDateTime.atZone(ASIA_SHANGHAI).toInstant())
        assertEquals(expectedDate3, localDateTime.toLegacyDate(ASIA_SHANGHAI))
    }

    @Test
    fun testZonedDateTimeToLegacyDate() {
        val zonedDateTime = ZonedDateTime.of(2024, 1, 15, 10, 30, 45, 0, ETC_UTC)
        val date = zonedDateTime.toLegacyDate()

        assertEquals(zonedDateTime.toInstant().toEpochMilli(), date.time)
        assertEquals(Date.from(zonedDateTime.toInstant()), date)
    }

    @Test
    fun testOffsetDateTimeToLegacyDate() {
        val offsetDateTime = OffsetDateTime.of(2024, 1, 15, 10, 30, 45, 0, ZoneOffset.UTC)
        val date = offsetDateTime.toLegacyDate()

        assertEquals(offsetDateTime.toInstant().toEpochMilli(), date.time)
        assertEquals(Date.from(offsetDateTime.toInstant()), date)
    }

    @Test
    fun testDateToLocalDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC
        val date = Date.from(instant)

        // Test with system default zone
        val expectedLocalDateTime1 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(expectedLocalDateTime1, date.toLocalDateTime())

        // Test with specific zone
        val expectedLocalDateTime2 = LocalDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(expectedLocalDateTime2, date.toLocalDateTime(ETC_UTC))

        // Test with different zone
        val expectedLocalDateTime3 = LocalDateTime.ofInstant(instant, ASIA_SHANGHAI)
        assertEquals(expectedLocalDateTime3, date.toLocalDateTime(ASIA_SHANGHAI))
    }

    @Test
    fun testDateToZonedDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC
        val date = Date.from(instant)

        // Test with system default zone
        val expectedZonedDateTime1 = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(expectedZonedDateTime1, date.toZonedDateTime())

        // Test with specific zone
        val expectedZonedDateTime2 = ZonedDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(expectedZonedDateTime2, date.toZonedDateTime(ETC_UTC))

        // Test with different zone
        val expectedZonedDateTime3 = ZonedDateTime.ofInstant(instant, ASIA_SHANGHAI)
        assertEquals(expectedZonedDateTime3, date.toZonedDateTime(ASIA_SHANGHAI))
    }

    @Test
    fun testDateToOffsetDateTime() {
        val instant = Instant.ofEpochMilli(1705310445000L) // 2024-01-15 10:30:45 UTC
        val date = Date.from(instant)

        // Test with system default zone
        val expectedOffsetDateTime1 = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())
        assertEquals(expectedOffsetDateTime1, date.toOffsetDateTime())

        // Test with specific zone
        val expectedOffsetDateTime2 = OffsetDateTime.ofInstant(instant, ETC_UTC)
        assertEquals(expectedOffsetDateTime2, date.toOffsetDateTime(ETC_UTC))

        // Test with different zone
        val expectedOffsetDateTime3 = OffsetDateTime.ofInstant(instant, ASIA_SHANGHAI)
        assertEquals(expectedOffsetDateTime3, date.toOffsetDateTime(ASIA_SHANGHAI))
    }

    @Test
    fun testRoundTripConversions() {
        // Test LocalDateTime -> Date -> LocalDateTime
        val originalLocalDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45)
        val zone = ETC_UTC
        val date = originalLocalDateTime.toLegacyDate(zone)
        val convertedBack = date.toLocalDateTime(zone)
        assertEquals(originalLocalDateTime, convertedBack)

        // Test Date -> Instant -> Date
        val originalDate = Date(1705310445000L)
        val instant = originalDate.toInstant()
        val convertedDate = instant.toLegacyDate()
        assertEquals(originalDate, convertedDate)

        // Test ZonedDateTime -> Date -> ZonedDateTime
        val originalZonedDateTime = ZonedDateTime.of(2024, 1, 15, 10, 30, 45, 0, ETC_UTC)
        val dateFromZoned = originalZonedDateTime.toLegacyDate()
        val convertedBackZoned = dateFromZoned.toZonedDateTime(ETC_UTC)
        assertEquals(originalZonedDateTime.toInstant(), convertedBackZoned.toInstant())
    }

}