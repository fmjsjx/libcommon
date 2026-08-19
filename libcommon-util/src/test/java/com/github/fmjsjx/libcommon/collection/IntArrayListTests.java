package com.github.fmjsjx.libcommon.collection;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class IntArrayListTests {

    private static int[] valuesOf(IntArrayList list) {
        try {
            var field = IntArrayList.class.getDeclaredField("values");
            field.setAccessible(true);
            return (int[]) field.get(list);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int sizeOf(IntArrayList list) {
        try {
            var field = IntArrayList.class.getDeclaredField("size");
            field.setAccessible(true);
            return field.getInt(list);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertValues(IntArrayList list, int... expected) {
        assertEquals(expected.length, sizeOf(list));
        assertArrayEquals(expected, Arrays.copyOf(valuesOf(list), sizeOf(list)));
    }

    @Test
    public void testConstructors() {
        var list = new IntArrayList();
        assertValues(list);

        list = new IntArrayList(8);
        assertEquals(8, valuesOf(list).length);

        list = new IntArrayList(0);
        assertValues(list);

        assertThrows(IllegalArgumentException.class, () -> new IntArrayList(-1));

        list = new IntArrayList(1, 2, 3);
        assertValues(list, 1, 2, 3);

        list = new IntArrayList(new int[0]);
        assertValues(list);

        list = new IntArrayList(new ArrayList<>());
        assertValues(list);

        list = new IntArrayList(List.of(1, 2, 3));
        assertValues(list, 1, 2, 3);

        var other = new IntArrayList(1, 2, 3);
        list = new IntArrayList(other);
        assertValues(list, 1, 2, 3);
        assertNotSame(valuesOf(other), valuesOf(list));
        assertEquals(other, list);
    }

    @Test
    public void testContains() {
        var list = new IntArrayList(1, 2, 3);
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertFalse(list.contains(0));
        assertFalse(list.contains(4));
        assertFalse(new IntArrayList().contains(1));

        var boundaries = new IntArrayList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        assertTrue(boundaries.contains(Integer.MIN_VALUE));
        assertTrue(boundaries.contains(Integer.MAX_VALUE));
        assertFalse(boundaries.contains(1));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testContainsObject() {
        var list = new IntArrayList(1, 2, 3);

        // Integer
        assertTrue(list.contains(Integer.valueOf(1)));
        assertTrue(list.contains(Integer.valueOf(2)));
        assertTrue(list.contains(Integer.valueOf(3)));
        assertFalse(list.contains(Integer.valueOf(9)));

        // Long within int range
        assertTrue(list.contains(2L));
        assertFalse(list.contains(9L));
        // Long out of int range
        assertFalse(list.contains(Integer.MAX_VALUE + 1L));
        assertFalse(list.contains(Integer.MIN_VALUE - 1L));

        // Short
        assertTrue(list.contains(Short.valueOf((short) 3)));
        assertFalse(list.contains(Short.valueOf((short) 9)));

        // Byte
        assertTrue(list.contains(Byte.valueOf((byte) 1)));
        assertFalse(list.contains(Byte.valueOf((byte) 9)));

        // BigInteger
        assertTrue(list.contains(BigInteger.TWO));
        assertFalse(list.contains(BigInteger.valueOf(9)));
        assertThrows(ArithmeticException.class,
                () -> list.contains(BigInteger.valueOf(Integer.MAX_VALUE + 1L)));
        assertThrows(ArithmeticException.class,
                () -> list.contains(BigInteger.valueOf(Integer.MIN_VALUE - 1L)));

        // other types
        assertFalse(list.contains("2"));
        assertFalse(list.contains(2.0));
    }

    @Test
    public void testAdd() {
        var list = new IntArrayList();
        for (var i = 0; i < 3; i++) {
            assertTrue(list.add(i));
        }
        assertValues(list, 0, 1, 2);
    }

    @Test
    public void testAddIndexed() {
        var list = new IntArrayList(1, 3);
        list.add(1, 2);
        assertValues(list, 1, 2, 3);
        list.add(0, 0);
        assertValues(list, 0, 1, 2, 3);
        list.add(4, 4);
        assertValues(list, 0, 1, 2, 3, 4);
    }

    @Test
    public void testAddInteger() {
        var list = new IntArrayList();
        assertTrue(list.add(Integer.valueOf(1)));
        list.add(0, Integer.valueOf(0));
        list.add(2, Integer.valueOf(2));
        assertValues(list, 0, 1, 2);
    }

    @Test
    public void testValueAt() {
        var list = new IntArrayList(10, 20, 30);
        assertEquals(10, list.valueAt(0));
        assertEquals(20, list.valueAt(1));
        assertEquals(30, list.valueAt(2));
    }

    @Test
    public void testGet() {
        var list = new IntArrayList(1, 2, 3);
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
    }

    @Test
    public void testSet() {
        var list = new IntArrayList(1, 2, 3);
        assertEquals(2, list.set(1, 9));
        assertValues(list, 1, 9, 3);
    }

    @Test
    public void testSetInteger() {
        var list = new IntArrayList(1, 2, 3);
        assertEquals(Integer.valueOf(2), list.set(1, Integer.valueOf(9)));
        assertValues(list, 1, 9, 3);
    }

    @Test
    public void testIndexOf() {
        var list = new IntArrayList(1, 2, 3, 2, 1);
        assertEquals(0, list.indexOf(1));
        assertEquals(1, list.indexOf(2));
        assertEquals(2, list.indexOf(3));
        assertEquals(-1, list.indexOf(9));
        assertEquals(-1, new IntArrayList().indexOf(1));
    }

    @Test
    public void testLastIndexOf() {
        var list = new IntArrayList(1, 2, 3, 2, 1);
        assertEquals(4, list.lastIndexOf(1));
        assertEquals(3, list.lastIndexOf(2));
        assertEquals(2, list.lastIndexOf(3));
        assertEquals(-1, list.lastIndexOf(9));
        assertEquals(-1, new IntArrayList().lastIndexOf(1));
    }

    @Test
    public void testRemoveAt() {
        var list = new IntArrayList(1, 2, 3, 4, 5);
        assertEquals(3, list.removeAt(2));
        assertValues(list, 1, 2, 4, 5);
        assertEquals(1, list.removeAt(0));
        assertEquals(4, list.removeAt(1));
        assertValues(list, 2, 5);
    }

    @Test
    public void testRemoveFirst() {
        assertThrows(NoSuchElementException.class, () -> new IntArrayList().removeFirst());

        var list = new IntArrayList(1, 2, 3);
        assertEquals(Integer.valueOf(1), list.removeFirst());
        assertValues(list, 2, 3);
        assertEquals(Integer.valueOf(2), list.removeFirst());
        assertEquals(Integer.valueOf(3), list.removeFirst());
        assertValues(list);
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    public void testRemoveLast() {
        assertThrows(NoSuchElementException.class, () -> new IntArrayList().removeLast());

        var list = new IntArrayList(1, 2, 3);
        assertEquals(Integer.valueOf(3), list.removeLast());
        assertValues(list, 1, 2);
        assertEquals(Integer.valueOf(2), list.removeLast());
        assertEquals(Integer.valueOf(1), list.removeLast());
        assertValues(list);
        assertThrows(NoSuchElementException.class, list::removeLast);
    }

    @Test
    public void testRemoveFirstValue() {
        var list = new IntArrayList(1, 2, 3, 2);
        assertTrue(list.removeFirst(2));
        assertValues(list, 1, 3, 2);
        assertTrue(list.removeFirst(2));
        assertValues(list, 1, 3);
        assertFalse(list.removeFirst(2));
        assertValues(list, 1, 3);
        assertFalse(list.removeFirst(9));
        assertValues(list, 1, 3);
    }

    @Test
    public void testRemoveAllValue() {
        var list = new IntArrayList(1, 2, 2, 2, 3, 2);
        assertTrue(list.removeAllValue(2));
        assertValues(list, 1, 3);

        var allRemoved = new IntArrayList(2, 2, 2);
        assertTrue(allRemoved.removeAllValue(2));
        assertValues(allRemoved);

        var unchanged = new IntArrayList(1, 3);
        assertFalse(unchanged.removeAllValue(2));
        assertValues(unchanged, 1, 3);

        assertFalse(new IntArrayList().removeAllValue(1));

        list = new IntArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertTrue(list.removeAllValue(3));
        assertValues(list, 0, 1, 2, 4, 5, 6, 7, 8, 9, 0, 1, 2, 4, 5, 6, 7, 8, 9);
        assertTrue(list.removeAllValue(9));
        assertValues(list, 0, 1, 2, 4, 5, 6, 7, 8, 0, 1, 2, 4, 5, 6, 7, 8);
        assertTrue(list.removeAllValue(0));
        assertValues(list, 1, 2, 4, 5, 6, 7, 8, 1, 2, 4, 5, 6, 7, 8);
    }

    @Test
    public void testRemoveAllVarargs() {
        var list = new IntArrayList(1, 2, 3, 4, 5);
        assertTrue(list.removeAll(2, 4));
        assertValues(list, 1, 3, 5);
        assertFalse(list.removeAll(9));
        assertEquals(3, sizeOf(list));
        assertFalse(new IntArrayList().removeAll(1, 2));
    }

    @Test
    public void testIntStream() {
        var list = new IntArrayList(1, 2, 3, 4);
        assertEquals(4, list.intStream().count());
        assertEquals(10, list.intStream().sum());
        assertArrayEquals(new int[]{2, 4}, list.intStream().filter(v -> v % 2 == 0).toArray());
        assertEquals(0, new IntArrayList().intStream().count());
    }

    @Test
    public void testToIntArray() {
        var list = new IntArrayList(1, 2, 3);
        var array = list.toIntArray();
        assertArrayEquals(new int[]{1, 2, 3}, array);
        // returned array must be a copy of the internal array
        array[0] = 9;
        assertValues(list, 1, 2, 3);

        assertArrayEquals(new int[0], new IntArrayList().toIntArray());
    }

    @SuppressWarnings("UseBulkOperation")
    @Test
    public void testForEachIntConsumer() {
        var list = new IntArrayList(1, 2, 3);
        var sum = new int[1];
        list.forEach((IntConsumer) v -> sum[0] += v);
        assertEquals(6, sum[0]);

        var values = new ArrayList<Integer>();
        list.forEach((IntConsumer) values::add);
        assertEquals(List.of(1, 2, 3), values);

        new IntArrayList().forEach((IntConsumer) v -> fail("should not be called"));
    }

    @Test
    public void testSizeAndIsEmpty() {
        var list = new IntArrayList();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add(1);
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    public void testClear() {
        var list = new IntArrayList(1, 2, 3);
        list.clear();
        assertValues(list);

        assertTrue(list.add(1));
        assertValues(list, 1);
    }

    @Test
    public void testEnsureCapacity() {
        var list = new IntArrayList();
        list.ensureCapacity(100);
        assertValues(list);
        assertTrue(valuesOf(list).length >= 100);
        var expected = new int[50];
        for (var i = 0; i < 50; i++) {
            expected[i] = i;
            list.add(i);
        }
        assertValues(list, expected);
    }

    @Test
    public void testGrow() {
        var list = new IntArrayList();
        var expected = new int[100];
        for (var i = 0; i < 100; i++) {
            expected[i] = i;
            list.add(i);
        }
        assertValues(list, expected);
        // the backing array must have been grown beyond the default capacity of 10
        assertTrue(valuesOf(list).length >= 100);
    }

    @Test
    public void testClone() {
        var list = new IntArrayList(1, 2, 3);
        var clone = (IntArrayList) list.clone();
        assertNotSame(list, clone);
        assertEquals(list, clone);
        assertEquals(list.hashCode(), clone.hashCode());

        // the clone must be independent of the original list
        assertNotSame(valuesOf(list), valuesOf(clone));
        clone.add(4);
        clone.set(0, 9);
        assertValues(list, 1, 2, 3);
        assertValues(clone, 9, 2, 3, 4);
    }

    @Test
    public void testIndexOutOfBounds() {
        var list = new IntArrayList(1, 2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> list.valueAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.valueAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, 9));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 9));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(4, 9));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 9));
    }

    @Test
    public void testIterator() {
        var list = new IntArrayList(1, 2, 3);
        var it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        it.remove();
        assertValues(list, 1, 3);
        assertEquals(3, it.next());
        assertFalse(it.hasNext());

        // fail-fast
        var it2 = list.iterator();
        assertEquals(1, it2.next());
        list.add(4);
        assertThrows(ConcurrentModificationException.class, it2::next);
    }

    @Test
    public void testListIterator() {
        var list = new IntArrayList(1, 2, 3);
        var it = list.listIterator();
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(0, it.nextIndex());
        assertEquals(-1, it.previousIndex());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previous());
        it.set(9);
        assertValues(list, 1, 9, 3);
        assertEquals(9, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());

        // fail-fast
        var it2 = list.listIterator();
        assertEquals(1, it2.next());
        list.removeAt(0);
        assertThrows(ConcurrentModificationException.class, it2::next);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testRemoveObject() {
        var list = new IntArrayList(1, 2, 3);
        assertTrue(list.remove(Integer.valueOf(2)));
        assertValues(list, 1, 3);
        assertFalse(list.remove(Integer.valueOf(9)));
        assertValues(list, 1, 3);
        assertFalse(list.remove("1"));
        assertValues(list, 1, 3);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testContainsAll() {
        var list = new IntArrayList(1, 2, 3);
        assertTrue(list.containsAll(List.of()));
        assertTrue(list.containsAll(List.of(1, 2)));
        assertTrue(list.containsAll(List.of(1, 2, 3)));
        assertFalse(list.containsAll(List.of(1, 9)));

        assertTrue(list.containsAll(1, 2));
        assertTrue(list.containsAll(1, 2, 3));
        assertFalse(list.containsAll(1, 9));
    }

    @Test
    public void testAddAll() {
        var list = new IntArrayList(new int[]{1});
        assertTrue(list.addAll(List.of(2, 3)));
        assertValues(list, 1, 2, 3);
        assertFalse(list.addAll(List.of()));

        assertTrue(list.addAll(4, 5));
        assertValues(list, 1, 2, 3, 4, 5);

        assertTrue(list.addAll(1, List.of(9)));
        assertValues(list, 1, 9, 2, 3, 4, 5);
    }

    @Test
    public void testRemoveAllCollection() {
        var list = new IntArrayList(1, 2, 3, 4, 5);
        assertTrue(list.removeAll(List.of(2, 4, 9)));
        assertValues(list, 1, 3, 5);
        assertFalse(list.removeAll(List.of(9)));
        assertEquals(3, sizeOf(list));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testRetainAll() {
        var list = new IntArrayList(1, 2, 3, 4, 5);
        assertTrue(list.retainAll(List.of(1, 3, 5, 9)));
        assertValues(list, 1, 3, 5);
        assertFalse(list.retainAll(List.of(1, 3, 5)));
        assertTrue(list.retainAll(List.of()));
        assertValues(list);
    }

    @Test
    public void testSort() {
        var list = new IntArrayList(3, 1, 2);
        list.sort(Comparator.naturalOrder());
        assertValues(list, 1, 2, 3);
        list.sort(Comparator.reverseOrder());
        assertValues(list, 3, 2, 1);
    }

    @SuppressWarnings("ReplaceInefficientStreamCount")
    @Test
    public void testStream() {
        var list = new IntArrayList(1, 2, 3);
        assertEquals(3, list.stream().count());
        assertIterableEquals(List.of(1, 2, 3), list.stream().toList());
        assertEquals(0, new IntArrayList().stream().count());
    }

    @Test
    public void testEqualsHashCodeToString() {
        var list = new IntArrayList(1, 2, 3);
        assertEquals(List.of(1, 2, 3), list);
        assertEquals(new IntArrayList(1, 2, 3), list);
        assertEquals(List.of(1, 2, 3).hashCode(), list.hashCode());
        assertNotEquals(List.of(1, 2), list);
        assertNotEquals(List.of(1, 2, 3, 4), list);
        assertNotEquals(List.of(), list);
        assertEquals("[1, 2, 3]", list.toString());

        var empty = new IntArrayList();
        assertEquals(List.of(), empty);
        assertEquals(List.of().hashCode(), empty.hashCode());
        assertEquals("[]", empty.toString());
    }

    @Test
    public void testSubList() {
        var list = new IntArrayList(1, 2, 3, 4, 5);
        var sub = list.subList(1, 4);
        assertEquals(3, sub.size());
        assertEquals(2, sub.get(0));
        assertEquals(3, sub.get(1));
        assertEquals(4, sub.get(2));

        // modifications on the sub list are visible in the parent list
        sub.set(0, 9);
        assertValues(list, 1, 9, 3, 4, 5);
        assertEquals(3, sub.remove(1));
        assertValues(list, 1, 9, 4, 5);

        assertTrue(list.subList(2, 2).isEmpty());
    }

    @Test
    public void testBoundaryValues() {
        var list = new IntArrayList(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
        assertEquals(Integer.MIN_VALUE, valuesOf(list)[0]);
        assertEquals(Integer.MAX_VALUE, valuesOf(list)[4]);
        assertTrue(list.contains(Integer.MIN_VALUE));
        assertTrue(list.contains(Integer.MAX_VALUE));
        assertTrue(list.containsAll(Integer.MIN_VALUE, Integer.MAX_VALUE));

        list.set(0, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, valuesOf(list)[0]);
        list.set(0, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, valuesOf(list)[0]);

        list.add(Integer.MAX_VALUE);
        assertEquals(6, sizeOf(list));
        assertEquals(Integer.MAX_VALUE, valuesOf(list)[5]);
        list.add(Integer.MIN_VALUE);
        assertEquals(7, sizeOf(list));
        assertEquals(Integer.MIN_VALUE, valuesOf(list)[6]);

    }

}
