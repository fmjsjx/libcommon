package com.github.fmjsjx.libcommon.util.time

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class NumberExtensionsTests {

    @Test
    fun testToInstantS() {
        // Test with Long
        val epochSeconds = 1705314045L // 2024-01-15 10:20:45 UTC
        val instant = epochSeconds.toInstantS()
        assertEquals(Instant.ofEpochSecond(epochSeconds), instant)
        assertEquals(epochSeconds, instant.epochSecond)

        // Test with Int
        val epochSecondsInt = 1705314045
        val instantFromInt = epochSecondsInt.toInstantS()
        assertEquals(Instant.ofEpochSecond(epochSecondsInt.toLong()), instantFromInt)

        // Test with zero
        val zeroInstant = 0L.toInstantS()
        assertEquals(Instant.ofEpochSecond(0), zeroInstant)

        // Test with Double
        val epochSecondsDouble = 1705314045.0
        val instantFromDouble = epochSecondsDouble.toInstantS()
        assertEquals(Instant.ofEpochSecond(epochSecondsDouble.toLong()), instantFromDouble)
    }

    @Test
    fun testToInstantM() {
        // Test with Long
        val epochMillis = 1705314045000L // 2024-01-15 10:20:45.000 UTC
        val instant = epochMillis.toInstantM()
        assertEquals(Instant.ofEpochMilli(epochMillis), instant)
        assertEquals(epochMillis, instant.toEpochMilli())

        // Test with Int
        val epochMillisInt = 1705314045
        val instantFromInt = epochMillisInt.toInstantM()
        assertEquals(Instant.ofEpochMilli(epochMillisInt.toLong()), instantFromInt)

        // Test with zero
        val zeroInstant = 0L.toInstantM()
        assertEquals(Instant.ofEpochMilli(0), zeroInstant)

        // Test with Double
        val epochMillisDouble = 1705314045000.0
        val instantFromDouble = epochMillisDouble.toInstantM()
        assertEquals(Instant.ofEpochMilli(epochMillisDouble.toLong()), instantFromDouble)
    }

    @Test
    fun testToLocalDateTimeS() {
        val epochSeconds = 1705314045L // 2024-01-15 10:20:45 UTC
        
        // Test with default zone (system default)
        val localDateTime1 = epochSeconds.toLocalDateTimeS()
        val expected1 = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
        assertEquals(expected1, localDateTime1)

        // Test with UTC
        val localDateTime2 = epochSeconds.toLocalDateTimeS(ETC_UTC)
        val expected2 = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ETC_UTC)
        assertEquals(expected2, localDateTime2)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 20, 45), localDateTime2)

        // Test with Asia/Shanghai
        val localDateTime3 = epochSeconds.toLocalDateTimeS(ASIA_SHANGHAI)
        val expected3 = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ASIA_SHANGHAI)
        assertEquals(expected3, localDateTime3)
        assertEquals(LocalDateTime.of(2024, 1, 15, 18, 20, 45), localDateTime3)

        // Test with Int
        val epochSecondsInt = 1705314045
        val localDateTimeFromInt = epochSecondsInt.toLocalDateTimeS(ETC_UTC)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 20, 45), localDateTimeFromInt)
    }

    @Test
    fun testToLocalDateTimeM() {
        val epochMillis = 1705314045000L // 2024-01-15 10:20:45.000 UTC
        
        // Test with default zone (system default)
        val localDateTime1 = epochMillis.toLocalDateTimeM()
        val expected1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
        assertEquals(expected1, localDateTime1)

        // Test with UTC
        val localDateTime2 = epochMillis.toLocalDateTimeM(ETC_UTC)
        val expected2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ETC_UTC)
        assertEquals(expected2, localDateTime2)

        // Test with Asia/Shanghai
        val localDateTime3 = epochMillis.toLocalDateTimeM(ASIA_SHANGHAI)
        val expected3 = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ASIA_SHANGHAI)
        assertEquals(expected3, localDateTime3)

        // Test with Int
        val epochMillisInt = 1705314045
        val localDateTimeFromInt = epochMillisInt.toLocalDateTimeM(ETC_UTC)
        val expectedFromInt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillisInt.toLong()), ETC_UTC)
        assertEquals(expectedFromInt, localDateTimeFromInt)
    }

    @Test
    fun testToLocalDateTimeU() {
        // Test with unix timestamp (seconds with decimal)
        val unixTime = 1705314045.0 // 2024-01-15 10:20:45.000 UTC
        
        // Test with default zone
        val localDateTime1 = unixTime.toLocalDateTimeU()
        val expectedMillis = (unixTime * 1000.0).toLong()
        val expected1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedMillis), ZoneId.systemDefault())
        assertEquals(expected1, localDateTime1)

        // Test with UTC
        val localDateTime2 = unixTime.toLocalDateTimeU(ETC_UTC)
        val expected2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedMillis), ETC_UTC)
        assertEquals(expected2, localDateTime2)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 20, 45), localDateTime2)

        // Test with fractional seconds
        val unixTimeWithFraction = 1705314045.123
        val localDateTime3 = unixTimeWithFraction.toLocalDateTimeU(ETC_UTC)
        val expectedMillis3 = (unixTimeWithFraction * 1000.0).toLong()
        val expected3 = LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedMillis3), ETC_UTC)
        assertEquals(expected3, localDateTime3)

        // Test with Long (should work too)
        val unixTimeLong = 1705314045L
        val localDateTime4 = unixTimeLong.toLocalDateTimeU(ETC_UTC)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 20, 45), localDateTime4)
    }

    @Test
    fun testToZonedDateTimeS() {
        val epochSeconds = 1705314045L // 2024-01-15 10:20:45 UTC
        
        // Test with default zone (system default)
        val zonedDateTime1 = epochSeconds.toZonedDateTimeS()
        val expected1 = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
        assertEquals(expected1, zonedDateTime1)

        // Test with UTC
        val zonedDateTime2 = epochSeconds.toZonedDateTimeS(ETC_UTC)
        val expected2 = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ETC_UTC)
        assertEquals(expected2, zonedDateTime2)
        assertEquals(ETC_UTC, zonedDateTime2.zone)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 20, 45), zonedDateTime2.toLocalDateTime())

        // Test with Asia/Shanghai
        val zonedDateTime3 = epochSeconds.toZonedDateTimeS(ASIA_SHANGHAI)
        val expected3 = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ASIA_SHANGHAI)
        assertEquals(expected3, zonedDateTime3)
        assertEquals(ASIA_SHANGHAI, zonedDateTime3.zone)

        // Test with Int
        val epochSecondsInt = 1705314045
        val zonedDateTimeFromInt = epochSecondsInt.toZonedDateTimeS(ETC_UTC)
        assertEquals(expected2, zonedDateTimeFromInt)
    }

    @Test
    fun testToZonedDateTimeM() {
        val epochMillis = 1705314045000L // 2024-01-15 10:20:45.000 UTC
        
        // Test with default zone (system default)
        val zonedDateTime1 = epochMillis.toZonedDateTimeM()
        val expected1 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
        assertEquals(expected1, zonedDateTime1)

        // Test with UTC
        val zonedDateTime2 = epochMillis.toZonedDateTimeM(ETC_UTC)
        val expected2 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ETC_UTC)
        assertEquals(expected2, zonedDateTime2)
        assertEquals(ETC_UTC, zonedDateTime2.zone)

        // Test with Asia/Shanghai
        val zonedDateTime3 = epochMillis.toZonedDateTimeM(ASIA_SHANGHAI)
        val expected3 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ASIA_SHANGHAI)
        assertEquals(expected3, zonedDateTime3)
        assertEquals(ASIA_SHANGHAI, zonedDateTime3.zone)

        // Test with Int
        val epochMillisInt = 1705314045
        val zonedDateTimeFromInt = epochMillisInt.toZonedDateTimeM(ETC_UTC)
        val expectedFromInt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillisInt.toLong()), ETC_UTC)
        assertEquals(expectedFromInt, zonedDateTimeFromInt)
    }

    @Test
    fun testRoundTripConversions() {
        // Test seconds round trip
        val originalSeconds = 1705314045L
        val instant1 = originalSeconds.toInstantS()
        assertEquals(originalSeconds, instant1.epochSecond)

        // Test milliseconds round trip
        val originalMillis = 1705314045000L
        val instant2 = originalMillis.toInstantM()
        assertEquals(originalMillis, instant2.toEpochMilli())

        // Test LocalDateTime round trip
        val localDateTime = originalSeconds.toLocalDateTimeS(ETC_UTC)
        val backToSeconds = localDateTime.toEpochSecond(ETC_UTC)
        assertEquals(originalSeconds, backToSeconds)

        // Test ZonedDateTime round trip
        val zonedDateTime = originalSeconds.toZonedDateTimeS(ETC_UTC)
        val backToSecondsFromZoned = zonedDateTime.toEpochSecond()
        assertEquals(originalSeconds, backToSecondsFromZoned)
    }
    @Test
    fun testDifferentNumberTypes() {
        // Test with BigInteger using extension method
        val bigIntegerVal = 1705314045L.toBigInteger()
        val instantFromBigInteger = bigIntegerVal.toInstantS()
        assertEquals(Instant.ofEpochSecond(1705314045L), instantFromBigInteger)

        // Test with BigDecimal using extension method
        val bigDecimalVal = 1705314045.0.toBigDecimal()
        val instantFromBigDecimal = bigDecimalVal.toInstantS()
        assertEquals(Instant.ofEpochSecond(1705314045L), instantFromBigDecimal)
    }

    @Test
    fun testEdgeCases() {
        // Test with zero
        assertEquals(Instant.ofEpochSecond(0), 0L.toInstantS())
        assertEquals(Instant.ofEpochMilli(0), 0L.toInstantM())
        
        // Test with negative values
        val negativeSeconds = -1000L
        val negativeInstant = negativeSeconds.toInstantS()
        assertEquals(Instant.ofEpochSecond(negativeSeconds), negativeInstant)

        // Test with very large values
        val largeSeconds = 2147483647L // Max int value
        val largeInstant = largeSeconds.toInstantS()
        assertEquals(Instant.ofEpochSecond(largeSeconds), largeInstant)
    }
}