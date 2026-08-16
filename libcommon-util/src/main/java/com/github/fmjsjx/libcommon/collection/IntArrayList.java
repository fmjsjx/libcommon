package com.github.fmjsjx.libcommon.collection;

import org.jspecify.annotations.NonNull;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * This class implements the {@link IntList} interface, backed by a resizable
 * {@code int} array that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public class IntArrayList extends AbstractList<@NonNull Integer> implements IntList {

    private static final int DEFAULT_INITIAL_CAPACITY = 10;

    private int[] values;
    private int size;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IntArrayList() {
        values = new int[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     * 
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public IntArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        values = new int[initialCapacity];
    }

    /**
     * Constructs a list containing the values in the specified array.
     * 
     * @param values the array whose values are to be placed into this list
     */
    public IntArrayList(int... values) {
        this.values = Arrays.copyOf(values, values.length);
        size = values.length;
    }

    /**
     * Constructs a list containing the elements in the specified collection.
     * 
     * @param c the collection whose elements are to be placed into this list
     */
    public IntArrayList(Collection<? extends @NonNull Integer> c) {
        if (c instanceof IntArrayList other) {
            values = Arrays.copyOf(other.values, other.size);
            size = other.size;
        } else {
            values = new int[c.size()];
            var i = 0;
            for (var e : c) {
                values[i++] = e;
            }
            size = i;
        }
    }

    @Override
    public boolean contains(int value) {
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(int value) {
        ensureCapacity(size + 1);
        values[size++] = value;
        modCount++;
        return true;
    }

    @Override
    public int valueAt(int index) {
        checkIndex(index, size);
        return values[index];
    }

    @Override
    public int set(int index, int value) {
        checkIndex(index, size);
        var oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    @Override
    public void add(int index, int value) {
        checkIndexForAdd(index, size);
        ensureCapacity(size + 1);
        System.arraycopy(values, index, values, index + 1, size - index);
        values[index] = value;
        size++;
        modCount++;
    }

    @Override
    public int indexOf(int value) {
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(int value) {
        for (var i = size - 1; i >= 0; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int removeAt(int index) {
        checkIndex(index, size);
        var oldValue = values[index];
        System.arraycopy(values, index + 1, values, index, size - index - 1);
        size--;
        modCount++;
        return oldValue;
    }

    @Override
    public boolean removeFirst(int value) {
        var index = indexOf(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(int... values) {
        var modified = false;
        for (var value : values) {
            modified |= removeAllValue(value);
        }
        return modified;
    }

    @Override
    public IntStream intStream() {
        return Arrays.stream(values, 0, size);
    }

    @Override
    public int[] toIntArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public void forEach(IntConsumer action) {
        for (var i = 0; i < size; i++) {
            action.accept(values[i]);
        }
    }

    @Override
    public @NonNull Integer get(int index) {
        return valueAt(index);
    }

    @Override
    public @NonNull Integer set(int index, @NonNull Integer element) {
        return set(index, element.intValue());
    }

    @Override
    public boolean add(@NonNull Integer e) {
        return add(e.intValue());
    }

    @Override
    public void add(int index, @NonNull Integer element) {
        add(index, element.intValue());
    }

    @Override
    public @NonNull Integer remove(int index) {
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
