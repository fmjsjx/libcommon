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
                return index == 6;
            });
            assertEquals(6, n);

            count.set(0);
            n = ArrayUtil.forEachUntil((index, element) -> {
                assertEquals(count.getAndIncrement(), index);
                assertEquals(array[index], element);
                return index == 6;
            }, array);
            assertEquals(6, n);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testContains_IntArray_Int() {
        try {
            var array = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

            assertTrue(ArrayUtil.contains(array, 0));
            assertTrue(ArrayUtil.contains(array, 5));
            assertTrue(ArrayUtil.contains(array, 9));
            assertFalse(ArrayUtil.contains(array, -1));
            assertFalse(ArrayUtil.contains(array, 10));

            assertFalse(ArrayUtil.contains(new int[0], 0));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testContains_IntArray_Int_Int_Int() {
        try {
            var array = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

            assertTrue(ArrayUtil.contains(array, 2, 5, 3));
            assertFalse(ArrayUtil.contains(array, 2, 5, 6));
            assertTrue(ArrayUtil.contains(array, 0, array.length, 9));
            assertTrue(ArrayUtil.contains(array, 5, array.length, 5));
            assertFalse(ArrayUtil.contains(array, 0, 5, 5));
            assertFalse(ArrayUtil.contains(array, 3, 3, 3));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testContains_LongArray_Long() {
        try {
            var array = new long[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

            assertTrue(ArrayUtil.contains(array, 0L));
            assertTrue(ArrayUtil.contains(array, 5L));
            assertTrue(ArrayUtil.contains(array, 9L));
            assertFalse(ArrayUtil.contains(array, -1L));
            assertFalse(ArrayUtil.contains(array, 10L));

            assertFalse(ArrayUtil.contains(new long[0], 0L));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testContains_LongArray_Int_Int_Long() {
        try {
            var array = new long[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

            assertTrue(ArrayUtil.contains(array, 2, 5, 3));
            assertFalse(ArrayUtil.contains(array, 2, 5, 6));
            assertTrue(ArrayUtil.contains(array, 0, array.length, 9));
            assertTrue(ArrayUtil.contains(array, 5, array.length, 5));
            assertFalse(ArrayUtil.contains(array, 0, 5, 5));
            assertFalse(ArrayUtil.contains(array, 3, 3, 3));
        } catch (Exception e) {
            fail(e);
        }
    }

}
