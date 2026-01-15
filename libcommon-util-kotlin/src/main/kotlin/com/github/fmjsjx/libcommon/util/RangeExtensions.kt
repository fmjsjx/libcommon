package com.github.fmjsjx.libcommon.util

import java.util.Random

/**
 * Performs the given action on each [Int] element.
 */
inline fun IntRange.forEach(action: (Int) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Long] element.
 */
inline fun LongRange.forEach(action: (Long) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Char] element.
 */
inline fun CharRange.forEach(action: (Char) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Int] element, providing sequential
 * index with the element.
 */
inline fun IntRange.forEachIndexed(action: (Int, Int) -> Unit) {
    var index = 0
    for (i in this) {
        action(checkIndexOverflow(index++), i)
    }
}

/**
 * Performs the given action on each [Long] element, providing sequential
 * index with the element.
 */
inline fun LongRange.forEachIndexed(action: (Int, Long) -> Unit) {
    var index = 0
    for (i in this) {
        action(checkIndexOverflow(index++), i)
    }
}

/**
 * Performs the given action on each [Char] element, providing sequential
 * index with the element.
 */
inline fun CharRange.forEachIndexed(action: (Int, Char) -> Unit) {
    var index = 0
    for (i in this) {
        action(checkIndexOverflow(index++), i)
    }
}

/**
 * Check if the given index is overflow.
 *
 * @param index the index
 * @return the index if it is not overflow
 * @since 4.1
 */
@PublishedApi
internal fun checkIndexOverflow(index: Int): Int {
    if (index < 0) {
        throw ArithmeticException("Index overflow has happened.")
    }
    return index
}


/**
 * Returns a random Int in this range.
 */
fun IntRange.random(random: Random? = null): Int = RandomUtil.randomInRange(first, last, random)
