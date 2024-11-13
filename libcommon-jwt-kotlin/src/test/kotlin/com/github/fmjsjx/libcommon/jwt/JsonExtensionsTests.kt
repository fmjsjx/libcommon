package com.github.fmjsjx.libcommon.jwt

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonExtensionsTests {

    @AfterEach
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun testParseToJoseHeader() {
        mockkStatic(JoseHeader::class)
        val result = mockk<JoseHeader>()
        every { JoseHeader.parse(any()) } returns result
        assertEquals(result, "{}".encodeToByteArray().parseToJoseHeader())
    }

    @Test
    fun testParseToJwtClaimsSet() {
        mockkStatic(JwtClaimsSet::class)
        val result = mockk<JwtClaimsSet>()
        every { JwtClaimsSet.parse(any()) } returns result
        assertEquals(result, "{}".encodeToByteArray().parseToJwtClaimsSet())
    }

}