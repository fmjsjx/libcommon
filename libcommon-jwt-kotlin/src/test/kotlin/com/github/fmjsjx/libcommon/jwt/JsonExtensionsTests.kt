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
        val result2 = mockk<JoseHeader>()
        every { JoseHeader.parse(any(), Fastjson2JsonRepresented.getFactory()) } returns result
        every { JoseHeader.parse(any(), JsoniterJsonRepresented.getFactory()) } returns result2
        assertEquals(result, "{}".encodeToByteArray().parseToJoseHeader())
        assertEquals(result2, "{}".encodeToByteArray().parseToJoseHeader(JsoniterJsonRepresented.getFactory()))
    }

    @Test
    fun testParseToJwtClaimsSet() {
        mockkStatic(JwtClaimsSet::class)
        val result = mockk<JwtClaimsSet>()
        val result2 = mockk<JwtClaimsSet>()
        every { JwtClaimsSet.parse(any(), Fastjson2JsonRepresented.getFactory()) } returns result
        every { JwtClaimsSet.parse(any(), JsoniterJsonRepresented.getFactory()) } returns result2
        assertEquals(result, "{}".encodeToByteArray().parseToJwtClaimsSet())
        assertEquals(result2, "{}".encodeToByteArray().parseToJwtClaimsSet(JsoniterJsonRepresented.getFactory()))
    }

}