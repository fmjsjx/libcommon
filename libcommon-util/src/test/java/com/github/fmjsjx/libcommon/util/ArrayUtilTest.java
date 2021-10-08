package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class ArrayUtilTest {

    @Test
    public void testForEachUnless_Lint() {
        try {
            var array = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUnless(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUnless((index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testForEachUnless_Llong() {
        try {
            var array = new long[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUnless(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUnless((index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testForEachUnless() {
        try {
            var array = new String[] { "a", "b", "c", "d", "e", "f", "g" };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUnless(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUnless((index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index < 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testForEachUntil_Lint() {
        try {
            var array = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUntil(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index >= 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUntil((index, element) -> {
                assertEquals(array[index], element);
                return index >= 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testForEachUntil_Llong() {
        try {
            var array = new long[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUntil(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index >= 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUntil((index, element) -> {
                assertEquals(array[index], element);
                return index >= 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testForEachUntil() {
        try {
            var array = new String[] { "a", "b", "c", "d", "e", "f", "g" };
            var count = new AtomicInteger();

            count.set(0);
            var n = ArrayUtil.forEachUntil(array, (index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index >= 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUntil((index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index >= 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

}
