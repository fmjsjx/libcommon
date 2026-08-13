package com.github.fmjsjx.libcommon.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.Random

@Suppress("EmptyRange")
class RangeExtensionsTests {

    @Test
    fun testIntRangeEach() {
        val result = mutableListOf<Int>()
        (1..5).each { i ->
            result.add(i)
        }
        assertEquals(listOf(1, 2, 3, 4, 5), result)

        // 测试空范围
        val emptyResult = mutableListOf<Int>()
        (5..1).each { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())

        // 测试单个元素范围
        val singleResult = mutableListOf<Int>()
        (3..3).each { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3), singleResult)
    }

    @Test
    fun testLongRangeEach() {
        val result = mutableListOf<Long>()
        (1L..5L).each { i ->
            result.add(i)
        }
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), result)

        // 测试空范围
        val emptyResult = mutableListOf<Long>()
        (5L..1L).each { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())

        // 测试单个元素范围
        val singleResult = mutableListOf<Long>()
        (3L..3L).each { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3L), singleResult)
    }

    @Test
    fun testCharRangeEach() {
        val result = mutableListOf<Char>()
        ('a'..'e').each { c ->
            result.add(c)
        }
        assertEquals(listOf('a', 'b', 'c', 'd', 'e'), result)

        // 测试空范围
        val emptyResult = mutableListOf<Char>()
        ('e'..'a').each { c ->
            emptyResult.add(c)
        }
        assertTrue(emptyResult.isEmpty())

        // 测试单个元素范围
        val singleResult = mutableListOf<Char>()
        ('c'..'c').each { c ->
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
    fun testIntRangeOnEach() {
        val result = mutableListOf<Int>()
        val range = (1..5)
        val returnedRange = range.onEach { i ->
            result.add(i)
        }
        assertEquals(listOf(1, 2, 3, 4, 5), result)
        assertEquals(range, returnedRange) // 验证返回的是原始范围

        // 测试空范围
        val emptyResult = mutableListOf<Int>()
        val emptyRange = (5..1)
        val returnedEmptyRange = emptyRange.onEach { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())
        assertEquals(emptyRange, returnedEmptyRange)

        // 测试单个元素范围
        val singleResult = mutableListOf<Int>()
        val singleRange = (3..3)
        val returnedSingleRange = singleRange.onEach { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3), singleResult)
        assertEquals(singleRange, returnedSingleRange)
    }

    @Test
    fun testLongRangeOnEach() {
        val result = mutableListOf<Long>()
        val range = (1L..5L)
        val returnedRange = range.onEach { i ->
            result.add(i)
        }
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), result)
        assertEquals(range, returnedRange) // 验证返回的是原始范围

        // 测试空范围
        val emptyResult = mutableListOf<Long>()
        val emptyRange = (5L..1L)
        val returnedEmptyRange = emptyRange.onEach { i ->
            emptyResult.add(i)
        }
        assertTrue(emptyResult.isEmpty())
        assertEquals(emptyRange, returnedEmptyRange)

        // 测试单个元素范围
        val singleResult = mutableListOf<Long>()
        val singleRange = (3L..3L)
        val returnedSingleRange = singleRange.onEach { i ->
            singleResult.add(i)
        }
        assertEquals(listOf(3L), singleResult)
        assertEquals(singleRange, returnedSingleRange)
    }

    @Test
    fun testCharRangeOnEach() {
        val result = mutableListOf<Char>()
        val range = ('a'..'e')
        val returnedRange = range.onEach { c ->
            result.add(c)
        }
        assertEquals(listOf('a', 'b', 'c', 'd', 'e'), result)
        assertEquals(range, returnedRange) // 验证返回的是原始范围

        // 测试空范围
        val emptyResult = mutableListOf<Char>()
        val emptyRange = ('e'..'a')
        val returnedEmptyRange = emptyRange.onEach { c ->
            emptyResult.add(c)
        }
        assertTrue(emptyResult.isEmpty())
        assertEquals(emptyRange, returnedEmptyRange)

        // 测试单个元素范围
        val singleResult = mutableListOf<Char>()
        val singleRange = ('c'..'c')
        val returnedSingleRange = singleRange.onEach { c ->
            singleResult.add(c)
        }
        assertEquals(listOf('c'), singleResult)
        assertEquals(singleRange, returnedSingleRange)
    }

    @Test
    fun testRandomOneWithInvalidRange() {
        // 测试无效范围的情况 (min > max)
        val invalidRange = (5..4)
        assertThrows(IllegalArgumentException::class.java) {
            invalidRange.randomOne()
        }
    }

    @Test
    fun testRandomOneWithLargeRange() {
        // 测试大范围的情况
        val range = 1..1000000
        val randomValue = range.randomOne()
        assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
    }

    @Test
    fun testRandomOneWithNegativeRange() {
        // 测试完全负数范围
        val range = -10..-1
        val randomValue = range.randomOne()
        assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
    }

    @Test
    fun testRandomOneWithMaxIntRange() {
        // 测试最大整数值附近的范围
        val range = (Int.MAX_VALUE - 10)..Int.MAX_VALUE
        val randomValue = range.randomOne()
        assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
    }

    @Test
    fun testRandomOneWithMinIntRange() {
        // 测试最小整数值附近的范围
        val range = Int.MIN_VALUE..(Int.MIN_VALUE + 10)
        val randomValue = range.randomOne()
        assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
    }

    @Test
    fun testIntRangeRandom() {
        // 测试基本功能
        val range = 1..10
        for (i in 1..100) {  // 运行多次以确保随机性
            val randomValue = range.randomOne()
            assertTrue(randomValue in range, "Random value $randomValue is not in range $range")
        }

        // 测试包含负数的范围
        val negativeRange = -5..5
        for (i in 1..100) {
            val randomValue = negativeRange.randomOne()
            assertTrue(randomValue in negativeRange, "Random value $randomValue is not in range $negativeRange")
        }

        // 测试单个值的范围
        val singleRange = 5..5
        for (i in 1..10) {
            val randomValue = singleRange.randomOne()
            assertEquals(5, randomValue, "Expected 5 but got $randomValue")
        }

        // 测试自定义随机数生成器
        val customRandom = Random(12345) // 固定种子确保可重复性
        val randomValue1 = range.randomOne(customRandom)

        val customRandom2 = Random(12345) // 相同种子
        val randomValue2 = range.randomOne(customRandom2)

        assertEquals(randomValue1, randomValue2, "Same seed should produce same result")
    }
}