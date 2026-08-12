package com.github.fmjsjx.libcommon.bson

import com.github.fmjsjx.libcommon.util.DateTimeUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.bson.*
import org.bson.types.Decimal128
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.math.BigInteger
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID
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
        BsonValueUtil.encode(null as Number?) shouldBe BsonNull.VALUE
        BsonValueUtil.encode(1) shouldBe BsonInt32(1)

        BsonValueUtil.encode(123.toByte()) shouldBe BsonInt32(123)
        BsonValueUtil.encode(12345.toShort()) shouldBe BsonInt32(12345)
        BsonValueUtil.encode(AtomicInteger(123456)) shouldBe BsonInt32(123456)
        BsonValueUtil.encode(1234567L) shouldBe BsonInt64(1234567)
        BsonValueUtil.encode(AtomicLong(12345678L)) shouldBe BsonInt64(12345678)
        BsonValueUtil.encode(1234.5678) shouldBe BsonDouble(1234.5678)
        BsonValueUtil.encode(12.3f) shouldBe BsonDouble(12.3f.toDouble())
        BsonValueUtil.encode(Decimal128(BigDecimal("12345678.87654321"))) shouldBe BsonDecimal128(
            Decimal128(
                BigDecimal(
                    "12345678.87654321"
                )
            )
        )
        BsonValueUtil.encode(BigDecimal("1234567890.0987654321")) shouldBe BsonDecimal128(Decimal128(BigDecimal("1234567890.0987654321")))
        BsonValueUtil.encode(BigInteger("1234567890987654321")) shouldBe BsonDecimal128(
            Decimal128(
                BigDecimal(
                    BigInteger(
                        "1234567890987654321"
                    )
                )
            )
        )
        BsonValueUtil.encode(TestBigNumber(BigInteger("12345678987654321"))) shouldBe BsonDecimal128(
            Decimal128(
                BigDecimal("12345678987654321")
            )
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
        BsonValueUtil.encode(null as Any?) shouldBe BsonNull.VALUE
        BsonValueUtil.encode(BsonNull.VALUE) shouldBe BsonNull.VALUE
        BsonInt32(123).apply { BsonValueUtil.encode(this) shouldBe this }
        BsonValueUtil.encode(123) shouldBe BsonInt32(123)
        BsonValueUtil.encode("abcdefg hijklmn") shouldBe BsonString("abcdefg hijklmn")
        BsonValueUtil.encode("abcdefg hijklmn".toCharArray()) shouldBe BsonString("abcdefg hijklmn")
        BsonValueUtil.encode(true) shouldBe BsonBoolean.TRUE
        BsonValueUtil.encode(false) shouldBe BsonBoolean.FALSE
        BsonValueUtil.encode(Date(1234567890123)) shouldBe BsonDateTime(1234567890123)
        BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123)) shouldBe BsonDateTime(1234567890123)
        BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123).atZone(ZoneId.systemDefault())) shouldBe
                BsonDateTime(1234567890123)
        BsonValueUtil.encode(DateTimeUtil.ofEpochMilli(1234567890123).atOffset(ZoneOffset.ofHours(8))) shouldBe
                BsonDateTime(1234567890123)
        DateUnit.entries.forEach {
            BsonValueUtil.encode(it) shouldBe it.toBsonString()
        }
        MetaDataKeyword.entries.forEach {
            BsonValueUtil.encode(it) shouldBe it.toBsonString()
        }
        BsonType.entries.forEach {
            BsonValueUtil.encode(it) shouldBe BsonInt32(it.value)
        }
        BsonValueUtil.encode(DayOfWeek.SUNDAY) shouldBe BsonString("sun")
        BsonValueUtil.encode(DayOfWeek.MONDAY) shouldBe BsonString("mon")
        BsonValueUtil.encode(DayOfWeek.TUESDAY) shouldBe BsonString("tue")
        BsonValueUtil.encode(DayOfWeek.WEDNESDAY) shouldBe BsonString("wed")
        BsonValueUtil.encode(DayOfWeek.THURSDAY) shouldBe BsonString("thu")
        BsonValueUtil.encode(DayOfWeek.FRIDAY) shouldBe BsonString("fri")
        BsonValueUtil.encode(DayOfWeek.SATURDAY) shouldBe BsonString("sat")
        "abcdefg".encodeToByteArray().run {
            BsonValueUtil.encode(this) shouldBe BsonBinary(this)
        }
        UUID.randomUUID().run {
            BsonValueUtil.encode(this) shouldBe BsonBinary(this)
        }
        BsonValueUtil.encode(shortArrayOf(1, 2, 3)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonInt32(1), BsonInt32(2), BsonInt32(3))
        }
        BsonValueUtil.encode(intArrayOf(1, 2, 3)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonInt32(1), BsonInt32(2), BsonInt32(3))
        }
        BsonValueUtil.encode(longArrayOf(1, 2, 3)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonInt64(1), BsonInt64(2), BsonInt64(3))
        }
        BsonValueUtil.encode(doubleArrayOf(1.1, 2.2)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonDouble(1.1), BsonDouble(2.2))
        }
        BsonValueUtil.encode(floatArrayOf(1.1f, 2.2f)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonDouble(1.1f.toDouble()), BsonDouble(2.2f.toDouble()))
        }
        BsonValueUtil.encode(booleanArrayOf(true, false, true)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonBoolean.TRUE, BsonBoolean.FALSE, BsonBoolean.TRUE)
        }
        BsonValueUtil.encode(listOf(1, "b", true)).run {
            shouldBeInstanceOf<BsonArray>()
            values shouldBe listOf(BsonInt32(1), BsonString("b"), BsonBoolean.TRUE)
        }
        BsonValueUtil.encode(
            linkedMapOf(
                "a" to 1,
                "b" to true,
                "c" to "hello",
                "d" to 2.2,
            )
        ) shouldBe BsonDocument("a", BsonInt32(1)).append("b", BsonBoolean.TRUE).append("c", BsonString("hello"))
            .append("d", BsonDouble(2.2))
        BsonValueUtil.encode(ZoneId.of("Asia/Shanghai")) shouldBe BsonString("Asia/Shanghai")
        BsonValueUtil.encode(ZoneOffset.ofHours(8)) shouldBe BsonString("+08:00")
        BsonValueUtil.encode(ZoneOffset.ofHours(-8)) shouldBe BsonString("-08:00")
    }

    @Test
    fun testEncodeList() {
        mockkStatic(BsonValueUtil::class)
        every { BsonValueUtil.encode(any<Any>()) } returns mockk()

        BsonValueUtil.encodeList(1, "2", 3.0).run {
            shouldBeInstanceOf<BsonArray>()
            size shouldBe 3
            verify(exactly = 3) { BsonValueUtil.encode(any<Any>()) }
        }
    }

}