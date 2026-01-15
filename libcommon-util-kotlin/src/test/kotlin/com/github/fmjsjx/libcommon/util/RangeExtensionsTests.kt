package com.github.fmjsjx.libcommon.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.Random

@Suppress("EmptyRange")
class RangeExtensionsTests {

    @Test
    fun testIntRangeForEach() {
        val result = mutableListOf<Int>()
        (1..5).forEach { i ->
            result.add(i)
        }
        assertEquals(listOf(1, 2, 3, 4, 5), result)
        
        // 测试空范围
        val emptyResult = mutableListOf<Int>()
        (5..1).forEach { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())
        
        // 测试单个元素范围
        val singleResult = mutableListOf<Int>()
        (3..3).forEach { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3), singleResult)
    }

    @Test
    fun testLongRangeForEach() {
        val result = mutableListOf<Long>()
        (1L..5L).forEach { i ->
            result.add(i)
        }
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), result)
        
        // 测试空范围
        val emptyResult = mutableListOf<Long>()
        (5L..1L).forEach { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())
        
        // 测试单个元素范围
        val singleResult = mutableListOf<Long>()
        (3L..3L).forEach { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3L), singleResult)
    }

    @Test
    fun testCharRangeForEach() {
        val result = mutableListOf<Char>()
        ('a'..'e').forEach { c ->
            result.add(c)
        }
        assertEquals(listOf('a', 'b', 'c', 'd', 'e'), result)
        
        // 测试空范围
        val emptyResult = mutableListOf<Char>()
        ('e'..'a').forEach { c ->
            emptyResult.add(c)
        }
        assertTrue(emptyResult.isEmpty())
        
        // 测试单个元素范围
        val singleResult = mutableListOf<Char>()
        ('c'..'c').forEach { c ->
            singleResult.add(c)
        }
        assertEquals(listOf('c'), singleResult)
    }

    @Test
    fun testIntRangeForEachIndexed() {
        val indices = mutableListOf<Int>()
        val values = mutableListOf<Int>()
        (1..5).forEachIndexed { index, value ->
            indices.add(index)
            values.add(value)
        }
        assertEquals(listOf(0, 1, 2, 3, 4), indices)
        assertEquals(listOf(1, 2, 3, 4, 5), values)
        
        // 测试空范围
        val emptyIndices = mutableListOf<Int>()
        val emptyValues = mutableListOf<Int>()
        (5..1).forEachIndexed { index, value ->
            emptyIndices.add(index)
            emptyValues.add(value)
        }
        assertTrue(emptyIndices.isEmpty())
        assertTrue(emptyValues.isEmpty())
        
        // 测试单个元素范围
        val singleIndices = mutableListOf<Int>()
        val singleValues = mutableListOf<Int>()
        (3..3).forEachIndexed { index, value ->
            singleIndices.add(index)
            singleValues.add(value)
        }
        assertEquals(listOf(0), singleIndices)
        assertEquals(listOf(3), singleValues)
    }

    @Test
    fun testLongRangeForEachIndexed() {
        val indices = mutableListOf<Int>()
        val values = mutableListOf<Long>()
        (1L..5L).forEachIndexed { index, value ->
            indices.add(index)
            values.add(value)
        }
        assertEquals(listOf(0, 1, 2, 3, 4), indices)
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), values)
        
        // 测试空范围
        val emptyIndices = mutableListOf<Int>()
        val emptyValues = mutableListOf<Long>()
        (5L..1L).forEachIndexed { index, value ->
            emptyIndices.add(index)
            emptyValues.add(value)
        }
        assertTrue(emptyIndices.isEmpty())
        assertTrue(emptyValues.isEmpty())
        
        // 测试单个元素范围
        val singleIndices = mutableListOf<Int>()
        val singleValues = mutableListOf<Long>()
        (3L..3L).forEachIndexed { index, value ->
            singleIndices.add(index)
            singleValues.add(value)
        }
        assertEquals(listOf(0), singleIndices)
        assertEquals(listOf(3L), singleValues)
    }

    @Test
    fun testCharRangeForEachIndexed() {
        val indices = mutableListOf<Int>()
        val values = mutableListOf<Char>()
        ('a'..'e').forEachIndexed { index, value ->
            indices.add(index)
            values.add(value)
        }
        assertEquals(listOf(0, 1, 2, 3, 4), indices)
        assertEquals(listOf('a', 'b', 'c', 'd', 'e'), values)
        
        // 测试空范围
        val emptyIndices = mutableListOf<Int>()
        val emptyValues = mutableListOf<Char>()
        ('e'..'a').forEachIndexed { index, value ->
            emptyIndices.add(index)
            emptyValues.add(value)
        }
        assertTrue(emptyIndices.isEmpty())
        assertTrue(emptyValues.isEmpty())
        
        // 测试单个元素范围
        val singleIndices = mutableListOf<Int>()
        val singleValues = mutableListOf<Char>()
        ('c'..'c').forEachIndexed { index, value ->
            singleIndices.add(index)
            singleValues.add(value)
        }
        assertEquals(listOf(0), singleIndices)
        assertEquals(listOf('c'), singleValues)
    }

    @Test
    fun testCheckIndexOverflow() {
        // 正常情况
        assertEquals(0, checkIndexOverflow(0))
        assertEquals(1, checkIndexOverflow(1))
        assertEquals(Int.MAX_VALUE, checkIndexOverflow(Int.MAX_VALUE))
        
        // 溢出情况
        assertThrows(ArithmeticException::class.java) {
            checkIndexOverflow(-1)
        }
        assertThrows(ArithmeticException::class.java) {
            checkIndexOverflow(Int.MIN_VALUE)
        }
        assertThrows(ArithmeticException::class.java) {
            checkIndexOverflow(Int.MAX_VALUE + 1)
        }
    }

    @Test
    fun testIntRangeRandom() {
        // 测试基本功能
        val range = 1..10
        for (i in 1..100) {  // 运行多次以确保随机性
            val randomValue = range.random()
            assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
        }
        
        // 测试包含负数的范围
        val negativeRange = -5..5
        for (i in 1..100) {
            val randomValue = negativeRange.random()
            assertTrue(randomValue in negativeRange, "Random value $randomValue is not in range $negativeRange")
        }
        
        // 测试单个值的范围
        val singleRange = 5..5
        for (i in 1..10) {
            val randomValue = singleRange.random()
            assertEquals(5, randomValue, "Expected 5 but got $randomValue")
        }
        
        // 测试自定义随机数生成器
        val customRandom = Random(12345) // 固定种子确保可重复性
        val randomValue1 = range.random(customRandom)
        
        val customRandom2 = Random(12345) // 相同种子
        val randomValue2 = range.random(customRandom2)
        
        assertEquals(randomValue1, randomValue2, "Same seed should produce same result")
    }
}