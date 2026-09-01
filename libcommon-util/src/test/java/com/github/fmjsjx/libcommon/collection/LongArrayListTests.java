package com.github.fmjsjx.libcommon.collection;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.function.LongConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class LongArrayListTests {

    private static long[] valuesOf(LongArrayList list) {
        try {
            var field = LongArrayList.class.getDeclaredField("values");
            field.setAccessible(true);
            return (long[]) field.get(list);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int sizeOf(LongArrayList list) {
        try {
            var field = LongArrayList.class.getDeclaredField("size");
            field.setAccessible(true);
            return field.getInt(list);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertValues(LongArrayList list, long... expected) {
        assertEquals(expected.length, sizeOf(list));
        assertArrayEquals(expected, Arrays.copyOf(valuesOf(list), sizeOf(list)));
    }

    @Test
    public void testConstructors() {
        var list = new LongArrayList();
        assertValues(list);

        list = new LongArrayList(8);
        assertEquals(8, valuesOf(list).length);

        list = new LongArrayList(0);
        assertValues(list);

        assertThrows(IllegalArgumentException.class, () -> new LongArrayList(-1));

        list = new LongArrayList(1L, 2L, 3L);
        assertValues(list, 1, 2, 3);

        list = new LongArrayList(new long[0]);
        assertValues(list);

        list = new LongArrayList(new ArrayList<>());
        assertValues(list);

        list = new LongArrayList(List.of(1L, 2L, 3L));
        assertValues(list, 1, 2, 3);

        var other = new LongArrayList(1L, 2L, 3L);
        list = new LongArrayList(other);
        assertValues(list, 1, 2, 3);
        assertNotSame(valuesOf(other), valuesOf(list));
        assertEquals(other, list);
    }

    @Test
    public void testContains() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertTrue(list.contains(1L));
        assertTrue(list.contains(2L));
        assertTrue(list.contains(3L));
        assertFalse(list.contains(0L));
        assertFalse(list.contains(4L));
        assertFalse(new LongArrayList().contains(1L));

        var boundaries = new LongArrayList(Long.MIN_VALUE, 0L, Long.MAX_VALUE);
        assertTrue(boundaries.contains(Long.MIN_VALUE));
        assertTrue(boundaries.contains(Long.MAX_VALUE));
        assertFalse(boundaries.contains(1L));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testContainsObject() {
        var list = new LongArrayList(1L, 2L, 3L);

        // Long
        assertTrue(list.contains(Long.valueOf(1L)));
        assertTrue(list.contains(Long.valueOf(2L)));
        assertTrue(list.contains(Long.valueOf(3L)));
        assertFalse(list.contains(Long.valueOf(9L)));

        // Integer (always fits in long range)
        assertTrue(list.contains(Integer.valueOf(1)));
        assertTrue(list.contains(Integer.valueOf(2)));
        assertFalse(list.contains(Integer.valueOf(9)));

        // Short (always fits in long range)
        assertTrue(list.contains(Short.valueOf((short) 3)));
        assertFalse(list.contains(Short.valueOf((short) 9)));

        // Byte (always fits in long range)
        assertTrue(list.contains(Byte.valueOf((byte) 1)));
        assertFalse(list.contains(Byte.valueOf((byte) 9)));

        // BigInteger within long range
        assertTrue(list.contains(BigInteger.TWO));
        assertFalse(list.contains(BigInteger.valueOf(9L)));

        // BigInteger out of long range
        var beyondLongMax = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        var beyondLongMin = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
        assertThrows(ArithmeticException.class, () -> list.contains(beyondLongMax));
        assertThrows(ArithmeticException.class, () -> list.contains(beyondLongMin));

        // other types
        assertFalse(list.contains("2"));
        assertFalse(list.contains(2.0));
    }

    @Test
    public void testAdd() {
        var list = new LongArrayList();
        for (var i = 0; i < 3; i++) {
            assertTrue(list.add(i));
        }
        assertValues(list, 0, 1, 2);
    }

    @Test
    public void testAddIndexed() {
        var list = new LongArrayList(1L, 3L);
        list.add(1, 2L);
        assertValues(list, 1, 2, 3);
        list.add(0, 0L);
        assertValues(list, 0, 1, 2, 3);
        list.add(4, 4L);
        assertValues(list, 0, 1, 2, 3, 4);
    }

    @Test
    public void testAddLong() {
        var list = new LongArrayList();
        assertTrue(list.add(Long.valueOf(1L)));
        list.add(0, Long.valueOf(0L));
        list.add(2, Long.valueOf(2L));
        assertValues(list, 0, 1, 2);
    }

    @Test
    public void testValueAt() {
        var list = new LongArrayList(10L, 20L, 30L);
        assertEquals(10L, list.valueAt(0));
        assertEquals(20L, list.valueAt(1));
        assertEquals(30L, list.valueAt(2));
    }

    @Test
    public void testGet() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(Long.valueOf(1L), list.get(0));
        assertEquals(Long.valueOf(2L), list.get(1));
        assertEquals(Long.valueOf(3L), list.get(2));
    }

    @Test
    public void testSet() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(2L, list.set(1, 9L));
        assertValues(list, 1, 9, 3);
    }

    @Test
    public void testSetLong() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(Long.valueOf(2L), list.set(1, Long.valueOf(9L)));
        assertValues(list, 1, 9, 3);
    }

    @Test
    public void testIndexOf() {
        var list = new LongArrayList(1L, 2L, 3L, 2L, 1L);
        assertEquals(0, list.indexOf(1L));
        assertEquals(1, list.indexOf(2L));
        assertEquals(2, list.indexOf(3L));
        assertEquals(-1, list.indexOf(9L));
        assertEquals(-1, new LongArrayList().indexOf(1L));
    }

    @Test
    public void testLastIndexOf() {
        var list = new LongArrayList(1L, 2L, 3L, 2L, 1L);
        assertEquals(4, list.lastIndexOf(1L));
        assertEquals(3, list.lastIndexOf(2L));
        assertEquals(2, list.lastIndexOf(3L));
        assertEquals(-1, list.lastIndexOf(9L));
        assertEquals(-1, new LongArrayList().lastIndexOf(1L));
    }

    @Test
    public void testRemoveAt() {
        var list = new LongArrayList(1L, 2L, 3L, 4L, 5L);
        assertEquals(3L, list.removeAt(2));
        assertValues(list, 1, 2, 4, 5);
        assertEquals(1L, list.removeAt(0));
        assertEquals(4L, list.removeAt(1));
        assertValues(list, 2, 5);
    }

    @Test
    public void testRemoveFirst() {
        assertThrows(NoSuchElementException.class, () -> new LongArrayList().removeFirst());

        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(Long.valueOf(1L), list.removeFirst());
        assertValues(list, 2, 3);
        assertEquals(Long.valueOf(2L), list.removeFirst());
        assertEquals(Long.valueOf(3L), list.removeFirst());
        assertValues(list);
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    public void testRemoveLast() {
        assertThrows(NoSuchElementException.class, () -> new LongArrayList().removeLast());

        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(Long.valueOf(3L), list.removeLast());
        assertValues(list, 1, 2);
        assertEquals(Long.valueOf(2L), list.removeLast());
        assertEquals(Long.valueOf(1L), list.removeLast());
        assertValues(list);
        assertThrows(NoSuchElementException.class, list::removeLast);
    }

    @Test
    public void testRemoveFirstValue() {
        var list = new LongArrayList(1L, 2L, 3L, 2L);
        assertTrue(list.removeFirst(2L));
        assertValues(list, 1, 3, 2);
        assertTrue(list.removeFirst(2L));
        assertValues(list, 1, 3);
        assertFalse(list.removeFirst(2L));
        assertValues(list, 1, 3);
        assertFalse(list.removeFirst(9L));
        assertValues(list, 1, 3);
    }

    @Test
    public void testRemoveAllValue() {
        var list = new LongArrayList(1L, 2L, 2L, 2L, 3L, 2L);
        assertTrue(list.removeAllValue(2L));
        assertValues(list, 1, 3);

        var allRemoved = new LongArrayList(2L, 2L, 2L);
        assertTrue(allRemoved.removeAllValue(2L));
        assertValues(allRemoved);

        var unchanged = new LongArrayList(1L, 3L);
        assertFalse(unchanged.removeAllValue(2L));
        assertValues(unchanged, 1, 3);

        assertFalse(new LongArrayList().removeAllValue(1L));

        list = new LongArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertTrue(list.removeAllValue(3L));
        assertValues(list, 0, 1, 2, 4, 5, 6, 7, 8, 9, 0, 1, 2, 4, 5, 6, 7, 8, 9);
        assertTrue(list.removeAllValue(9L));
        assertValues(list, 0, 1, 2, 4, 5, 6, 7, 8, 0, 1, 2, 4, 5, 6, 7, 8);
        assertTrue(list.removeAllValue(0L));
        assertValues(list, 1, 2, 4, 5, 6, 7, 8, 1, 2, 4, 5, 6, 7, 8);
    }

    @Test
    public void testRemoveAllVarargs() {
        var list = new LongArrayList(1L, 2L, 3L, 4L, 5L);
        assertTrue(list.removeAll(2L, 4L));
        assertValues(list, 1, 3, 5);
        assertFalse(list.removeAll(9L));
        assertEquals(3, sizeOf(list));
        assertFalse(new LongArrayList().removeAll(1L, 2L));
    }

    @Test
    public void testLongStream() {
        var list = new LongArrayList(1L, 2L, 3L, 4L);
        assertEquals(4, list.longStream().count());
        assertEquals(10, list.longStream().sum());
        assertArrayEquals(new long[]{2, 4}, list.longStream().filter(v -> v % 2 == 0).toArray());
        assertEquals(0, new LongArrayList().longStream().count());
    }

    @Test
    public void testToLongArray() {
        var list = new LongArrayList(1L, 2L, 3L);
        var array = list.toLongArray();
        assertArrayEquals(new long[]{1, 2, 3}, array);
        // returned array must be a copy of the internal array
        array[0] = 9;
        assertValues(list, 1, 2, 3);

        assertArrayEquals(new long[0], new LongArrayList().toLongArray());
    }

    @SuppressWarnings("UseBulkOperation")
    @Test
    public void testForEachLongConsumer() {
        var list = new LongArrayList(1L, 2L, 3L);
        var sum = new long[1];
        list.forEach((LongConsumer) v -> sum[0] += v);
        assertEquals(6, sum[0]);

        var values = new ArrayList<Long>();
        list.forEach((LongConsumer) values::add);
        assertEquals(List.of(1L, 2L, 3L), values);

        new LongArrayList().forEach((LongConsumer) v -> fail("should not be called"));
    }

    @Test
    public void testSizeAndIsEmpty() {
        var list = new LongArrayList();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add(1L);
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    public void testClear() {
        var list = new LongArrayList(1L, 2L, 3L);
        list.clear();
        assertValues(list);

        assertTrue(list.add(1L));
        assertValues(list, 1);
    }

    @Test
    public void testEnsureCapacity() {
        var list = new LongArrayList();
        list.ensureCapacity(100);
        assertValues(list);
        assertTrue(valuesOf(list).length >= 100);
        var expected = new long[50];
        for (var i = 0; i < 50; i++) {
            expected[i] = i;
            list.add(i);
        }
        assertValues(list, expected);
    }

    @Test
    public void testGrow() {
        var list = new LongArrayList();
        var expected = new long[100];
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
        var list = new LongArrayList(1L, 2L, 3L);
        var clone = (LongArrayList) list.clone();
        assertNotSame(list, clone);
        assertEquals(list, clone);
        assertEquals(list.hashCode(), clone.hashCode());

        // the clone must be independent of the original list
        assertNotSame(valuesOf(list), valuesOf(clone));
        clone.add(4L);
        clone.set(0, 9L);
        assertValues(list, 1, 2, 3);
        assertValues(clone, 9, 2, 3, 4);
    }

    @Test
    public void testIndexOutOfBounds() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertThrows(IndexOutOfBoundsException.class, () -> list.valueAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.valueAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, 9L));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 9L));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(4, 9L));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 9L));
    }

    @Test
    public void testIterator() {
        var list = new LongArrayList(1L, 2L, 3L);
        var it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals(1L, it.next());
        assertEquals(2L, it.next());
        it.remove();
        assertValues(list, 1, 3);
        assertEquals(3L, it.next());
        assertFalse(it.hasNext());

        // fail-fast
        var it2 = list.iterator();
        assertEquals(1L, it2.next());
        list.add(4L);
        assertThrows(ConcurrentModificationException.class, it2::next);
    }

    @Test
    public void testListIterator() {
        var list = new LongArrayList(1L, 2L, 3L);
        var it = list.listIterator();
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(0, it.nextIndex());
        assertEquals(-1, it.previousIndex());
        assertEquals(1L, it.next());
        assertEquals(2L, it.next());
        assertTrue(it.hasPrevious());
        assertEquals(2L, it.previous());
        it.set(9L);
        assertValues(list, 1, 9, 3);
        assertEquals(9L, it.next());
        assertEquals(3L, it.next());
        assertFalse(it.hasNext());

        // fail-fast
        var it2 = list.listIterator();
        assertEquals(1L, it2.next());
        list.removeAt(0);
        assertThrows(ConcurrentModificationException.class, it2::next);
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "UnnecessaryBoxing"})
    @Test
    public void testRemoveObject() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertTrue(list.remove(Long.valueOf(2L)));
        assertValues(list, 1, 3);
        assertFalse(list.remove(Long.valueOf(9L)));
        assertValues(list, 1, 3);
        assertFalse(list.remove("1"));
        assertValues(list, 1, 3);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testContainsAll() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertTrue(list.containsAll(List.of()));
        assertTrue(list.containsAll(List.of(1L, 2L)));
        assertTrue(list.containsAll(List.of(1L, 2L, 3L)));
        assertFalse(list.containsAll(List.of(1L, 9L)));

        assertTrue(list.containsAll(1L, 2L));
        assertTrue(list.containsAll(1L, 2L, 3L));
        assertFalse(list.containsAll(1L, 9L));
    }

    @Test
    public void testAddAll() {
        var list = new LongArrayList(new long[]{1});
        assertTrue(list.addAll(List.of(2L, 3L)));
        assertValues(list, 1, 2, 3);
        assertFalse(list.addAll(List.of()));

        assertTrue(list.addAll(4L, 5L));
        assertValues(list, 1, 2, 3, 4, 5);

        assertTrue(list.addAll(1, List.of(9L)));
        assertValues(list, 1, 9, 2, 3, 4, 5);
    }

    @Test
    public void testRemoveAllCollection() {
        var list = new LongArrayList(1L, 2L, 3L, 4L, 5L);
        assertTrue(list.removeAll(List.of(2L, 4L, 9L)));
        assertValues(list, 1, 3, 5);
        assertFalse(list.removeAll(List.of(9L)));
        assertEquals(3, sizeOf(list));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Test
    public void testRetainAll() {
        var list = new LongArrayList(1L, 2L, 3L, 4L, 5L);
        assertTrue(list.retainAll(List.of(1L, 3L, 5L, 9L)));
        assertValues(list, 1, 3, 5);
        assertFalse(list.retainAll(List.of(1L, 3L, 5L)));
        assertTrue(list.retainAll(List.of()));
        assertValues(list);
    }

    @Test
    public void testSort() {
        var list = new LongArrayList(3L, 1L, 2L);
        list.sort(Comparator.naturalOrder());
        assertValues(list, 1, 2, 3);
        list.sort(Comparator.reverseOrder());
        assertValues(list, 3, 2, 1);
    }

    @SuppressWarnings("ReplaceInefficientStreamCount")
    @Test
    public void testStream() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(3, list.stream().count());
        assertIterableEquals(List.of(1L, 2L, 3L), list.stream().toList());
        assertEquals(0, new LongArrayList().stream().count());
    }

    @Test
    public void testEqualsHashCodeToString() {
        var list = new LongArrayList(1L, 2L, 3L);
        assertEquals(List.of(1L, 2L, 3L), list);
        assertEquals(new LongArrayList(1L, 2L, 3L), list);
        assertEquals(List.of(1L, 2L, 3L).hashCode(), list.hashCode());
        assertNotEquals(List.of(1L, 2L), list);
        assertNotEquals(List.of(1L, 2L, 3L, 4L), list);
        assertNotEquals(List.of(), list);
        assertEquals("[1, 2, 3]", list.toString());

        var empty = new LongArrayList();
        assertEquals(List.of(), empty);
        assertEquals(List.of().hashCode(), empty.hashCode());
        assertEquals("[]", empty.toString());
    }

    @Test
    public void testSubList() {
        var list = new LongArrayList(1L, 2L, 3L, 4L, 5L);
        var sub = list.subList(1, 4);
        assertEquals(3, sub.size());
        assertEquals(2L, sub.get(0));
        assertEquals(3L, sub.get(1));
        assertEquals(4L, sub.get(2));

        // modifications on the sub list are visible in the parent list
        sub.set(0, 9L);
        assertValues(list, 1, 9, 3, 4, 5);
        assertEquals(3L, sub.remove(1));
        assertValues(list, 1, 9, 4, 5);

        assertTrue(list.subList(2, 2).isEmpty());
    }

    @Test
    public void testBoundaryValues() {
        var list = new LongArrayList(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
        assertEquals(Long.MIN_VALUE, valuesOf(list)[0]);
        assertEquals(Long.MAX_VALUE, valuesOf(list)[4]);
        assertTrue(list.contains(Long.MIN_VALUE));
        assertTrue(list.contains(Long.MAX_VALUE));
        assertTrue(list.containsAll(Long.MIN_VALUE, Long.MAX_VALUE));

        list.set(0, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, valuesOf(list)[0]);
        list.set(0, Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, valuesOf(list)[0]);

        list.add(Long.MAX_VALUE);
        assertEquals(6, sizeOf(list));
        assertEquals(Long.MAX_VALUE, valuesOf(list)[5]);
        list.add(Long.MIN_VALUE);
        assertEquals(7, sizeOf(list));
        assertEquals(Long.MIN_VALUE, valuesOf(list)[6]);
    }

    @BeforeAll
    public static void enableJsoniterSupport() {
        if (!LongArrayList.LongArrayListJsoniterSupport.enabled()) {
            LongArrayList.LongArrayListJsoniterSupport.enable();
        }
    }

    @Test
    public void testJsoniterSupportEnabled() {
        assertTrue(LongArrayList.LongArrayListJsoniterSupport.enabled());
        // enable can only be called once
        assertThrows(IllegalStateException.class, LongArrayList.LongArrayListJsoniterSupport::enable);
    }

    @Test
    public void testJsoniterEncode() {
        assertEquals("[1,2,3]", JsonStream.serialize(new LongArrayList(1L, 2L, 3L)));
        assertEquals("[]", JsonStream.serialize(new LongArrayList()));
        assertEquals("[-9223372036854775808,-1,0,1,9223372036854775807]",
                JsonStream.serialize(new LongArrayList(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE)));
        assertEquals("{\"ids\":[1,2,3]}", JsonStream.serialize(Map.of("ids", new LongArrayList(1L, 2L, 3L))));
    }

    @Test
    public void testJsoniterDecode() {
        var list = JsonIterator.deserialize("[1,2,3]", LongArrayList.class);
        assertValues(list, 1, 2, 3);

        assertValues(JsonIterator.deserialize("[]", LongArrayList.class));

        assertValues(JsonIterator.deserialize("[-9223372036854775808,-1,0,1,9223372036854775807]", LongArrayList.class),
                Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE);

        // whitespace between elements must be accepted
        assertValues(JsonIterator.deserialize("[ 1 , 2 , 3 ]", LongArrayList.class), 1, 2, 3);

        // non-numeric elements must be rejected
        assertThrows(JsonException.class, () -> JsonIterator.deserialize("[1,\"a\"]", LongArrayList.class));
    }

    @Test
    public void testJsoniterRoundTrip() {
        var list = new LongArrayList(1L, 2L, 3L);
        var json = JsonStream.serialize(list);
        assertEquals("[1,2,3]", json);
        assertValues(JsonIterator.deserialize(json, LongArrayList.class), 1, 2, 3);

        var boundaries = new LongArrayList(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
        assertValues(JsonIterator.deserialize(JsonStream.serialize(boundaries), LongArrayList.class),
                Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE);

        assertValues(JsonIterator.deserialize(JsonStream.serialize(new LongArrayList()), LongArrayList.class));
    }

}
