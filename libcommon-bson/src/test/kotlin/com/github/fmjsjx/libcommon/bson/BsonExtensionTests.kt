package com.github.fmjsjx.libcommon.bson

import io.mockk.junit5.MockKExtension
import io.mockk.unmockkAll
import org.bson.BsonDouble
import org.bson.BsonInt32
import org.bson.BsonInt64
import org.bson.BsonNull
import org.bson.types.Decimal128
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

@ExtendWith(MockKExtension::class)
class BsonExtensionTests {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testToBsonInt32() {
        assertEquals(BsonInt32(123), 123.toBsonInt32())
        assertNotEquals(BsonInt32(123), 456.toBsonInt32())
    }

    @Test
    fun testToBsonInt32OrNull() {
        assertEquals(BsonInt32(123), 123.toBsonInt32OrNull())
        val v: Int? = null
        assertEquals(BsonNull.VALUE, v.toBsonInt32OrNull())
    }

    @Test
    fun testToBsonInt64() {
        assertEquals(BsonInt64(123), 123.toBsonInt64())
        assertNotEquals(BsonInt64(123), 456.toBsonInt64())
    }

    @Test
    fun testToBsonInt64OrNull() {
        assertEquals(BsonInt64(123), 123.toBsonInt64OrNull())
        val v: Long? = null
        assertEquals(BsonNull.VALUE, v.toBsonInt64OrNull())
    }

    @Test
    fun testToBsonDouble() {
        assertEquals(BsonDouble(1.2), 1.2.toBsonDouble())
        assertNotEquals(BsonDouble(1.2), 1.23.toBsonDouble())
    }

    @Test
    fun testToBsonDoubleOrNull() {
        assertEquals(BsonDouble(1.2), 1.2.toBsonDoubleOrNull())
        val v: Double? = null
        assertEquals(BsonNull.VALUE, v.toBsonDoubleOrNull())
    }

    @Test
    fun testToDecimal128() {
        assertEquals(Decimal128(BigDecimal(BigInteger("1234567890123"))), BigInteger("1234567890123").toDecimal128())
        assertEquals(Decimal128(BigDecimal("1234567890.123456")), BigDecimal("1234567890.123456").toDecimal128())
        assertEquals(Decimal128(1234567890123L), 1234567890123L.toDecimal128())
        assertEquals(Decimal128(123), 123.toDecimal128())
        assertEquals(Decimal128(123), 123.toByte().toDecimal128())
        assertEquals(Decimal128(123), 123.toShort().toDecimal128())
        assertEquals(Decimal128(BigDecimal("123.456").setScale(3)), 123.456.toDecimal128())
        assertEquals(Decimal128(BigDecimal("123.456")), 123.456f.toDecimal128())
        assertEquals(Decimal128(123456), AtomicInteger(123456).toDecimal128())
    }

}
