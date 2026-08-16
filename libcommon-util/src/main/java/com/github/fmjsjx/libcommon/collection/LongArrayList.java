package com.github.fmjsjx.libcommon.collection;

import org.jspecify.annotations.NonNull;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

/**
 * This class implements the {@link LongList} interface, backed by a resizable
 * {@code long} array that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public class LongArrayList extends AbstractList<@NonNull Long> implements LongList {

    private static final int DEFAULT_INITIAL_CAPACITY = 10;

    private long[] values;
    private int size;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public LongArrayList() {
        values = new long[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     * 
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public LongArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        values = new long[initialCapacity];
    }

    /**
     * Constructs a list containing the values in the specified array.
     * 
     * @param values the array whose values are to be placed into this list
     */
    public LongArrayList(long... values) {
        this.values = Arrays.copyOf(values, values.length);
        size = values.length;
    }

    /**
     * Constructs a list containing the elements in the specified collection.
     * 
     * @param c the collection whose elements are to be placed into this list
     */
    public LongArrayList(Collection<? extends @NonNull Long> c) {
        if (c instanceof LongArrayList other) {
            values = Arrays.copyOf(other.values, other.size);
            size = other.size;
        } else {
            values = new long[c.size()];
            var i = 0;
            for (var e : c) {
                values[i++] = e;
            }
            size = i;
        }
    }

    @Override
    public boolean contains(long value) {
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(long value) {
        ensureCapacity(size + 1);
        values[size++] = value;
        modCount++;
        return true;
    }

    @Override
    public long valueAt(int index) {
        checkIndex(index, size);
        return values[index];
    }

    @Override
    public long set(int index, long value) {
        checkIndex(index, size);
        var oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    @Override
    public void add(int index, long value) {
        checkIndexForAdd(index, size);
        ensureCapacity(size + 1);
        System.arraycopy(values, index, values, index + 1, size - index);
        values[index] = value;
        size++;
        modCount++;
    }

    @Override
    public int indexOf(long value) {
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long value) {
        for (var i = size - 1; i >= 0; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long removeAt(int index) {
        checkIndex(index, size);
        var oldValue = values[index];
        System.arraycopy(values, index + 1, values, index, size - index - 1);
        size--;
        modCount++;
        return oldValue;
    }

    @Override
    public boolean removeFirst(long value) {
        var index = indexOf(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(long... values) {
        var modified = false;
        for (var value : values) {
            modified |= removeAllValue(value);
        }
        return modified;
    }

    @Override
    public LongStream longStream() {
        return Arrays.stream(values, 0, size);
    }

    @Override
    public long[] toLongArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public void forEach(LongConsumer action) {
        for (var i = 0; i < size; i++) {
            action.accept(values[i]);
        }
    }

    @Override
    public @NonNull Long get(int index) {
        return valueAt(index);
    }

    @Override
    public @NonNull Long set(int index, @NonNull Long element) {
        return set(index, element.longValue());
    }

    @Override
    public boolean add(@NonNull Long e) {
        return add(e.longValue());
    }

    @Override
    public void add(int index, @NonNull Long element) {
        add(index, element.longValue());
    }

    @Override
    public @NonNull Long remove(int index) {
        return removeAt(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        modCount++;
        size = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > values.length) {
            var newCapacity = values.length + (values.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            values = Arrays.copyOf(values, newCapacity);
        }
    }

    private static void checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private static void checkIndexForAdd(int index, int size) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

}
