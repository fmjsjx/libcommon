package com.github.fmjsjx.libcommon.bson

import com.github.fmjsjx.libcommon.util.DateTimeUtil
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.bson.*
import org.bson.types.Decimal128
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.math.BigInteger
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@ExtendWith(MockKExtension::class)
class BsonValueUtilTests {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testEncodeNumber() {
        assertEquals(BsonNull.VALUE, BsonValueUtil.encode(null as Number?))
        assertEquals(BsonInt32(1), BsonValueUtil.encode(1))
        assertEquals(BsonInt32(123), BsonValueUtil.encode(123.toByte()))
        assertEquals(BsonInt32(12345), BsonValueUtil.encode(12345.toShort()))
        assertEquals(BsonInt32(123456), BsonValueUtil.encode(AtomicInteger(123456)))
        assertEquals(BsonInt64(1234567), BsonValueUtil.encode(1234567L))
        assertEquals(BsonInt64(12345678), BsonValueUtil.encode(AtomicLong(12345678L)))
        assertEquals(BsonDouble(1234.5678), BsonValueUtil.encode(1234.5678))
        assertEquals(BsonDouble(12.3f.toDouble()), BsonValueUtil.encode(12.3f))
        assertEquals(
            BsonDecimal128(Decimal128(BigDecimal("12345678.87654321"))),
            BsonValueUtil.encode(Decimal128(BigDecimal("12345678.87654321")))
        )
        assertEquals(
            BsonDecimal128(Decimal128(BigDecimal("1234567890.0987654321"))),
            BsonValueUtil.encode(BigDecimal("1234567890.0987654321"))
        )
        assertEquals(
            BsonDecimal128(Decimal128(BigDecimal(BigInteger("1234567890987654321")))),
            BsonValueUtil.encode(BigInteger("1234567890987654321"))
        )
        assertEquals(
            BsonDecimal128(Decimal128(BigDecimal("12345678987654321"))),
            BsonValueUtil.encode(TestBigNumber(BigInteger("12345678987654321")))
        )
    }

    class TestBigNumber(
        private val v: BigInteger,
    ) : BigInteger(v.toString()) {
        override fun toByte() = v.toByte()

        @Suppress("OVERRIDE_DEPRECATION")
        override fun toChar() = v.toInt().toChar()

        override fun toShort() = v.toShort()

    }

    @Test
    fun testEncode() {
        assertEquals(BsonNull.VALUE, BsonValueUtil.encode(null as Any?))
        assertEquals(BsonNull.VALUE, BsonValueUtil.encode(BsonNull.VALUE))
        BsonInt32(123).apply { assertEquals(this, BsonValueUtil.encode(this)) }
        assertEquals(BsonInt32(123), BsonValueUtil.encode(123))
        assertEquals(BsonString("abcdefg hijklmn"), BsonValueUtil.encode("abcdefg hijklmn"))
        assertEquals(BsonString("abcdefg hijklmn"), BsonValueUtil.encode("abcdefg hijklmn".toCharArray()))
        assertEquals(BsonBoolean.TRUE, BsonValueUtil.encode(true))
        assertEquals(BsonBoolean.FALSE, BsonValueUtil.encode(false))
        assertEquals(BsonDateTime(1234567890123), BsonValueUtil.encode(Date(1234567890123)))
        assertEquals(BsonDateTime(1234567890123), BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123)))
        assertEquals(
            BsonDateTime(1234567890123),
            BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123).atZone(ZoneId.systemDefault()))
        )
        assertEquals(
            BsonDateTime(1234567890123),
            BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123).atOffset(ZoneOffset.ofHours(8)))
        )
        DateUnit.entries.forEach {
            assertEquals(it.toBsonString(), BsonValueUtil.encode(it))
        }
        MetaDataKeyword.entries.forEach {
            assertEquals(it.toBsonString(), BsonValueUtil.encode(it))
        }
        BsonType.entries.forEach {
            assertEquals(BsonInt32(it.value), BsonValueUtil.encode(it))
        }
        assertEquals(BsonString("sun"), BsonValueUtil.encode(DayOfWeek.SUNDAY))
        assertEquals(BsonString("mon"), BsonValueUtil.encode(DayOfWeek.MONDAY))
        assertEquals(BsonString("tue"), BsonValueUtil.encode(DayOfWeek.TUESDAY))
        assertEquals(BsonString("wed"), BsonValueUtil.encode(DayOfWeek.WEDNESDAY))
        assertEquals(BsonString("thu"), BsonValueUtil.encode(DayOfWeek.THURSDAY))
        assertEquals(BsonString("fri"), BsonValueUtil.encode(DayOfWeek.FRIDAY))
        assertEquals(BsonString("sat"), BsonValueUtil.encode(DayOfWeek.SATURDAY))
        "abcdefg".encodeToByteArray().run {
            assertEquals(BsonBinary(this), BsonValueUtil.encode(this))
        }
        UUID.randomUUID().run {
            assertEquals(BsonBinary(this), BsonValueUtil.encode(this))
        }
        BsonValueUtil.encode(shortArrayOf(1, 2, 3)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(arrayOf(BsonInt32(1), BsonInt32(2), BsonInt32(3)), (this as BsonArray).toArray())
        }
        BsonValueUtil.encode(intArrayOf(1, 2, 3)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(arrayOf(BsonInt32(1), BsonInt32(2), BsonInt32(3)), (this as BsonArray).toArray())
        }
        BsonValueUtil.encode(longArrayOf(1, 2, 3)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(arrayOf(BsonInt64(1), BsonInt64(2), BsonInt64(3)), (this as BsonArray).toArray())
        }
        BsonValueUtil.encode(doubleArrayOf(1.1, 2.2)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(arrayOf(BsonDouble(1.1), BsonDouble(2.2)), (this as BsonArray).toArray())
        }
        BsonValueUtil.encode(floatArrayOf(1.1f, 2.2f)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(
                arrayOf(BsonDouble(1.1f.toDouble()), BsonDouble(2.2f.toDouble())),
                (this as BsonArray).toArray()
            )
        }
        BsonValueUtil.encode(booleanArrayOf(true, false, true)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(
                arrayOf(BsonBoolean.TRUE, BsonBoolean.FALSE, BsonBoolean.TRUE),
                (this as BsonArray).toArray()
            )
        }
        BsonValueUtil.encode(listOf(1, "b", true)).run {
            assertTrue(this is BsonArray)
            assertArrayEquals(
                arrayOf(BsonInt32(1), BsonString("b"), BsonBoolean.TRUE),
                (this as BsonArray).toArray()
            )
        }
        assertEquals(
            BsonDocument("a", BsonInt32(1)).append("b", BsonBoolean.TRUE).append("c", BsonString("hello"))
                .append("d", BsonDouble(2.2)),
            BsonValueUtil.encode(
                linkedMapOf(
                    "a" to 1,
                    "b" to true,
                    "c" to "hello",
                    "d" to 2.2,
                )
            )
        )
        assertEquals(BsonString("Asia/Shanghai"), BsonValueUtil.encode(ZoneId.of("Asia/Shanghai")))
        assertEquals(BsonString("+08:00"), BsonValueUtil.encode(ZoneOffset.ofHours(8)))
        assertEquals(BsonString("-08:00"), BsonValueUtil.encode(ZoneOffset.ofHours(-8)))
    }

    @Test
    fun testEncodeList() {
        mockkStatic(BsonValueUtil::class)
        every { BsonValueUtil.encode(any<Any>()) } returns mockk()

        BsonValueUtil.encodeList(1, "2", 3.0).run {
            assertTrue(this is BsonArray)
            assertEquals(3, (this as BsonArray).size)
            verify(exactly = 3) { BsonValueUtil.encode(any<Any>()) }
        }
    }

}