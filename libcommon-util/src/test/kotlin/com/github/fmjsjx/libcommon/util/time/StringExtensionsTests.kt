package com.github.fmjsjx.libcommon.util.time

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException

class StringExtensionsTests {

    @Test
    fun testToZoneIdWithValidIds() {
        // Test with UTC
        assertEquals(ZoneId.of("UTC"), "UTC".toZoneId())

        // Test with Etc/UTC
        assertEquals(ZoneId.of("Etc/UTC"), "Etc/UTC".toZoneId())

        // Test with Asia/Shanghai
        assertEquals(ZoneId.of("Asia/Shanghai"), "Asia/Shanghai".toZoneId())

        // Test with America/New_York
        assertEquals(ZoneId.of("America/New_York"), "America/New_York".toZoneId())

        // Test with offset-based zone ID
        assertEquals(ZoneId.of("+08:00"), "+08:00".toZoneId())
        assertEquals(ZoneId.of("-05:00"), "-05:00".toZoneId())
    }

    @Test
    fun testToZoneIdWithInvalidIds() {
        assertThrows<ZoneRulesException> {
            "Invalid/Zone".toZoneId()
        }

        assertThrows<Exception> {
            "".toZoneId()
        }
    }

    @Test
    fun testToZoneIdWithCharSequence() {
        // Test with StringBuilder
        val sb = StringBuilder("Asia/Tokyo")
        assertEquals(ZoneId.of("Asia/Tokyo"), sb.toZoneId())

        // Test with StringBuffer
        val buffer = StringBuffer("Europe/Paris")
        assertEquals(ZoneId.of("Europe/Paris"), buffer.toZoneId())
    }

    @Test
    fun testToLocalDateTimeWithFormatter() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTimeStr = "2024-01-15 10:30:45"
        
        val result = dateTimeStr.toLocalDateTime(formatter)
        
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), result)
    }

    @Test
    fun testToLocalDateTimeWithISOFormatter() {
        val dateTimeStr = "2024-01-15T10:30:45"
        
        val result = dateTimeStr.toLocalDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), result)
    }

    @Test
    fun testToLocalDateTimeWithCustomPattern() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dateTimeStr = "15/01/2024 10:30"
        
        val result = dateTimeStr.toLocalDateTime(formatter)
        
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), result)
    }

    @Test
    fun testToLocalDateTimeWithInvalidFormat() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val invalidStr = "invalid-date-time"
        
        assertThrows<DateTimeParseException> {
            invalidStr.toLocalDateTime(formatter)
        }
    }

    @Test
    fun testToLocalDateTimeWithCharSequence() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        
        // Test with StringBuilder
        val sb = StringBuilder("2024-01-15 10:30:45")
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), sb.toLocalDateTime(formatter))
        
        // Test with StringBuffer
        val buffer = StringBuffer("2024-01-15 10:30:45")
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 45), buffer.toLocalDateTime(formatter))
    }

    @Test
    fun testToDateTimeFormatterWithValidPattern() {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val formatter = pattern.toDateTimeFormatter()
        
        // Verify it can be used to format
        val dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45)
        assertEquals("2024-01-15 10:30:45", dateTime.format(formatter))
        
        // Verify it can parse back
        val parsed = LocalDateTime.parse("2024-01-15 10:30:45", formatter)
        assertEquals(dateTime, parsed)
    }

    @Test
    fun testToDateTimeFormatterWithVariousPatterns() {
        // ISO format
        val isoPattern = "yyyy-MM-dd'T'HH:mm:ss"
        val isoFormatter = isoPattern.toDateTimeFormatter()
        val dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45)
        assertEquals("2024-01-15T10:30:45", dateTime.format(isoFormatter))

        // European format
        val euroPattern = "dd/MM/yyyy HH:mm"
        val euroFormatter = euroPattern.toDateTimeFormatter()
        assertEquals("15/01/2024 10:30", dateTime.format(euroFormatter))

        // With milliseconds
        val milliPattern = "yyyy-MM-dd HH:mm:ss.SSS"
        val milliFormatter = milliPattern.toDateTimeFormatter()
        val dateTimeWithNano = LocalDateTime.of(2024, 1, 15, 10, 30, 45, 123000000)
        assertEquals("2024-01-15 10:30:45.123", dateTimeWithNano.format(milliFormatter))
    }

    @Test
    fun testToDateTimeFormatterWithInvalidPattern() {
        assertThrows<IllegalArgumentException> {
            "invalid-pattern-{{}}}".toDateTimeFormatter()
        }
    }

    @Test
    fun testToDateTimeFormatterWithCharSequence() {
        val pattern = "yyyy-MM-dd"
        
        // Test with StringBuilder
        val sb = StringBuilder(pattern)
        val formatter1 = sb.toDateTimeFormatter()
        val testDate = LocalDateTime.of(2024, 1, 15, 10, 30, 45)
        assertEquals("2024-01-15", testDate.format(formatter1))

        // Test with StringBuffer
        val buffer = StringBuffer(pattern)
        val formatter2 = buffer.toDateTimeFormatter()
        assertEquals("2024-01-15", testDate.format(formatter2))
    }

    @Test
    fun testRoundTripStringToLocalDateTime() {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val formatter = pattern.toDateTimeFormatter()
        
        val originalStr = "2024-01-15 10:30:45"
        val dateTime = originalStr.toLocalDateTime(formatter)
        val formattedStr = dateTime.format(formatter)
        
        assertEquals(originalStr, formattedStr)
    }
}
