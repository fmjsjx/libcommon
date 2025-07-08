package com.github.fmjsjx.libcommon.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.lang.reflect.Type

class Fastjson2LibraryExtensionTests {

    @Test
    fun testParseFastjson2_TypeReference() {
        val src1 = """[{"id":1234567890,"name":"test"}]""".encodeToByteArray()
        val list1 = src1.parseFastjson2(fastjson2ListTypeReference<TestObject>())
        assertEquals(1, list1.size)
        list1.first().also {
            assertEquals(1234567890, it.id)
            assertEquals("test", it.name)
        }
        val src2 = """{"k1":1,"k2":2,"k3":3}""".encodeToByteArray()
        val map2 = src2.parseFastjson2(fastjson2MapTypeReference<String, Int>())
        assertEquals(3, map2.size)
        assertEquals(1, map2["k1"])
        assertEquals(2, map2["k2"])
        assertEquals(3, map2["k3"])
    }

    data class TestObject(
        var id: Long? = null,
        var name: String? = null,
    )

    @Test
    fun testParseFastjson2_Type() {
        val src = """{"id":1234567890,"name":"test"}""".encodeToByteArray()
        val type: Type = TestObject::class.java
        val obj = src.parseFastjson2<TestObject>(type)
        assertNotNull(obj)
        assertEquals(1234567890, obj.id)
        assertEquals("test", obj.name)
    }

    @Test
    fun testParseJSONObject() {
        val src = """{"id":1234567890,"name":"test"}""".encodeToByteArray()
        val obj = src.parseJSONObject()
        assertNotNull(obj)
        assertEquals(2, obj.size)
        assertEquals(1234567890, obj.getLong("id"))
        assertEquals("test", obj.getString("name"))
    }

    @Test
    fun testParseJSONArray() {
        val src1 = """[1,2,3]""".encodeToByteArray()
        val arr1 = src1.parseJSONArray()
        assertNotNull(arr1)
        assertEquals(3, arr1.size)
        assertEquals(1, arr1.getInteger(0))
        assertEquals(2, arr1.getInteger(1))
        assertEquals(3, arr1.getInteger(2))
        val src2 = """[{"id":1234567890,"name":"test"}]""".encodeToByteArray()
        val arr2 = src2.parseJSONArray()
        assertNotNull(arr2)
        assertEquals(1, arr2.size)
        arr2.getJSONObject(0).also {
            assertEquals(1234567890, it.getLong("id"))
            assertEquals("test", it.getString("name"))
        }
    }

}