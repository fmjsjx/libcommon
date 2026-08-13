package com.github.fmjsjx.libcommon.util

import java.util.Random

/**
 * Performs the given action on each [Int] element.
 *
 * @since 4.1
 */
inline fun IntRange.each(action: (Int) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Long] element.
 *
 * @since 4.1
 */
inline fun LongRange.each(action: (Long) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Char] element.
 *
 * @since 4.1
 */
inline fun CharRange.each(action: (Char) -> Unit) {
    for (i in this) {
        action(i)
    }
}

/**
 * Performs the given action on each [Int] element and returns the
 * [IntRange] itself afterwards.
 *
 * @since 4.1
 */
inline fun IntRange.onEach(action: (Int) -> Unit): IntRange = apply { each(action) }

/**
 * Performs the given action on each [Long] element and returns the
 * [LongRange] itself afterwards.
 *
 * @since 4.1
 */

inline fun LongRange.onEach(action: (Long) -> Unit): LongRange = apply { each(action) }

/**
 * Performs the given action on each [Char] element and returns the
 * [CharRange] itself afterwards.
 *
 * @since 4.1
 */
inline fun CharRange.onEach(action: (Char) -> Unit): CharRange = apply { each(action) }

/**
 * Performs the given action on each [Int] element, providing sequential
 * index with the element.
 *
 * @since 4.1
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
 *
 * @since 4.1
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
 *
 * @since 4.1
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
 * Returns one random Int in this range.
 *
 * @since 4.1
 */
fun IntRange.randomOne(random: Random? = null): Int = RandomUtil.randomInRange(first, last, random)
