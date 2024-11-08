package com.github.fmjsjx.libcommon.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextExtersionsTests {

    @Test
    fun testIsNumberic() {
        assertTrue("123".isNumberic)
        assertTrue("1234567890123".isNumberic)
        assertFalse("".isNumberic)
        assertFalse("abc".isNumberic)
        assertFalse("1a2b".isNumberic)
        assertFalse("+123".isNumberic)
        assertFalse("-123".isNumberic)
        assertFalse("3.14159".isNumberic)
    }

    @Test
    fun testUrlEncode() {
        assertEquals("%7E%21%40%23%24%25%5E%26*%28%29_-%2B%3D", "~!@#$%^&*()_-+=".urlEncode())
        assertEquals("%FE%FF%00%7E%00%21%00%40%00%23%00%24%00%25%00%5E%00%26*%FE%FF%00%28%00%29_-%FE%FF%00%2B%00%3D", "~!@#$%^&*()_-+=".urlEncode(Charsets.UTF_16))
    }

    @Test
    fun testUrlDecode() {
        assertEquals("~!@#$%^&*()_-+=", "%7E%21%40%23%24%25%5E%26*%28%29_-%2B%3D".urlDecode())
        assertEquals("~!@#$%^&*()_-+=", "%FE%FF%00%7E%00%21%00%40%00%23%00%24%00%25%00%5E%00%26*%FE%FF%00%28%00%29_-%FE%FF%00%2B%00%3D".urlDecode(Charsets.UTF_16))
    }

}