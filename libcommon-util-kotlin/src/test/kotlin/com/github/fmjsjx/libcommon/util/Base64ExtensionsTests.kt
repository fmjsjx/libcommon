package com.github.fmjsjx.libcommon.util

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Base64ExtensionsTests {

    @Test
    fun testEncodeToBase64String() {
        val byteArray = "这行1".encodeToByteArray()
        assertEquals("6L+Z6KGMMQ==", byteArray.encodeToBase64String())
        assertEquals("6L+Z6KGMMQ==", byteArray.encodeToBase64String(false))
        assertEquals("6L+Z6KGMMQ==", byteArray.encodeToBase64String(false, false))
        assertEquals("6L+Z6KGMMQ", byteArray.encodeToBase64String(withoutPadding = true))
        assertEquals("6L+Z6KGMMQ", byteArray.encodeToBase64String(false, true))
        assertEquals("6L-Z6KGMMQ==", byteArray.encodeToBase64String(true))
        assertEquals("6L-Z6KGMMQ==", byteArray.encodeToBase64String(true, false))
        assertEquals("6L-Z6KGMMQ", byteArray.encodeToBase64String(true, true))
    }

    @Test
    fun testEncodeToBase64() {
        val byteArray = "这行1".encodeToByteArray()
        assertArrayEquals("6L+Z6KGMMQ==".encodeToByteArray(), byteArray.encodeToBase64())
        assertArrayEquals("6L+Z6KGMMQ==".encodeToByteArray(), byteArray.encodeToBase64(false))
        assertArrayEquals("6L+Z6KGMMQ==".encodeToByteArray(), byteArray.encodeToBase64(false, false))
        assertArrayEquals("6L+Z6KGMMQ".encodeToByteArray(), byteArray.encodeToBase64(withoutPadding = true))
        assertArrayEquals("6L+Z6KGMMQ".encodeToByteArray(), byteArray.encodeToBase64(false, true))
        assertArrayEquals("6L-Z6KGMMQ==".encodeToByteArray(), byteArray.encodeToBase64(true))
        assertArrayEquals("6L-Z6KGMMQ==".encodeToByteArray(), byteArray.encodeToBase64(true, false))
        assertArrayEquals("6L-Z6KGMMQ".encodeToByteArray(), byteArray.encodeToBase64(true, true))
    }

    @Test
    fun testDecodeBase64() {
        assertArrayEquals("这行1".encodeToByteArray(), "6L+Z6KGMMQ==".decodeBase64())
        assertArrayEquals("这行1".encodeToByteArray(), "6L+Z6KGMMQ==".decodeBase64(false))
        assertArrayEquals("这行1".encodeToByteArray(), "6L-Z6KGMMQ==".decodeBase64(true))
        assertArrayEquals("这行1".encodeToByteArray(), "6L+Z6KGMMQ==".encodeToByteArray().decodeBase64())
        assertArrayEquals("这行1".encodeToByteArray(), "6L+Z6KGMMQ==".encodeToByteArray().decodeBase64(false))
        assertArrayEquals("这行1".encodeToByteArray(), "6L-Z6KGMMQ==".encodeToByteArray().decodeBase64(true))
    }

}
