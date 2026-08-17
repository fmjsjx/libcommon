package com.github.fmjsjx.libcommon.collection;

import com.github.fmjsjx.libcommon.util.ArrayUtil;
import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.util.Objects.checkIndex;

/**
 * This class implements the {@link IntList} interface, backed by a resizable
 * {@code int} array that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public class IntArrayList extends AbstractList<@NonNull Integer>
        implements IntList, RandomAccess, Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 3609076143219247729L;

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final int[] EMPTY_VALUES = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final int[] DEFAULT_CAPACITY_EMPTY_VALUES = {};

    private int[] values;
    private int size;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IntArrayList() {
        values = DEFAULT_CAPACITY_EMPTY_VALUES;
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public IntArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            values = new int[initialCapacity];
        } else if (initialCapacity == 0) {
            values = EMPTY_VALUES;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * Constructs a list containing the values in the specified array.
     *
     * @param values the array whose values are to be placed into this list
     */
    public IntArrayList(int... values) {
        if (values.length != 0) {
            this.values = Arrays.copyOf(values, values.length);
            size = values.length;
        } else {
            this.values = EMPTY_VALUES;
        }
    }

    /**
     * Constructs a list containing the elements in the specified collection.
     *
     * @param c the collection whose elements are to be placed into this list
     */
    public IntArrayList(Collection<? extends @NonNull Integer> c) {
        if (c.isEmpty()) {
            values = EMPTY_VALUES;
        } else if (c instanceof IntArrayList other) {
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

    /**
     * Returns a shallow copy of this {@code IntArrayList} instance.
     *
     * @return a clone of this {@code IntArrayList} instance
     */
    @Override
    public @NonNull Object clone() {
        try {
            var clone = (IntArrayList) super.clone();
            clone.values = Arrays.copyOf(values, size);
            clone.modCount = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
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

    /**
     * This helper method split out from add(E) to keep method
     * bytecode size under 35 (the -XX:MaxInlineSize default value),
     * which helps when add(E) is called in a C1-compiled loop.
     *
     * @param value  value to be added
     * @param values the array to be added to
     * @param s      the size of the array to be added to
     */
    private void add(int value, int[] values, int s) {
        if (s == values.length) {
            values = grow();
        }
        values[s] = value;
        size = s + 1;
    }

    @Override
    public boolean add(int value) {
        modCount++;
        add(value, values, size);
        return true;
    }

    @Override
    public int valueAt(int index) {
        checkIndex(index, size);
        return valueData(index);
    }

    private int valueData(int index) {
        return values[index];
    }

    @Override
    public int set(int index, int value) {
        var oldValue = valueAt(index);
        values[index] = value;
        return oldValue;
    }

    @Override
    public void add(int index, int value) {
        rangeCheckForAdd(index);
        modCount++;
        var size = this.size;
        var values = this.values;
        if (size == values.length) {
            values = grow();
        }
        System.arraycopy(values, index, values, index + 1, size - index);
        values[index] = value;
        this.size = size + 1;
    }

    @Override
    public int indexOf(int value) {
        return indexOfRange(value, 0, size);
    }

    private int indexOfRange(int value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = start; i < end; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(int value) {
        return lastIndexOfRange(value, 0, size);
    }

    private int lastIndexOfRange(int value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = end - 1; i >= start; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int removeAt(int index) {
        checkIndex(index, size);
        var values = this.values;
        var oldValue = values[index];
        fastRemove(values, index);
        return oldValue;
    }

    private void fastRemove(int[] values, int index) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > index) {
            System.arraycopy(values, index + 1, values, index, newSize - index);
        }
        size = newSize;
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
    public boolean removeAll(int @NonNull ... values) {
        var modified = false;
        for (var value : values) {
            modified |= removeAllValue(value);
        }
        return modified;
    }

    @Override
    public @NonNull IntStream intStream() {
        return Arrays.stream(values, 0, size);
    }

    @Override
    public int @NonNull [] toIntArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public void forEach(@NonNull IntConsumer action) {
        for (var i = 0; i < size; i++) {
            action.accept(values[i]);
        }
    }

    @Override
    public boolean contains(@NonNull Object o) {
        return switch (o) {
            case Integer i -> contains(i.intValue());
            case Long l -> l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE && contains(l.intValue());
            case Short s -> contains(s.intValue());
            case Byte b -> contains(b.intValue());
            case BigInteger bi -> contains(bi.intValueExact());
            default -> false;
        };
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
    public @NonNull Integer removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            var values = this.values;
            var oldValue = values[0];
            fastRemove(values, 0);
            return oldValue;
        }
    }

    @Override
    public @NonNull Integer removeLast() {
        var last = size - 1;
        if (last < 0) {
            throw new NoSuchElementException();
        } else {
            var values = this.values;
            var oldValue = values[last];
            fastRemove(values, last);
            return oldValue;
        }
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

    /**
     * Increases the capacity of this {@code IntArrayList} instance, if
     * necessary, to ensure that it can hold at least the number of
     * elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > values.length
                && !(values == DEFAULT_CAPACITY_EMPTY_VALUES
                && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }

    private int[] grow(int minCapacity) {
        var oldCapacity = values.length;
        if (oldCapacity > 0 || values != DEFAULT_CAPACITY_EMPTY_VALUES) {
            var newCapacity = ArrayUtil.newLength(oldCapacity,
                    minCapacity - oldCapacity,
                    oldCapacity >> 1);
            return values = Arrays.copyOf(values, newCapacity);
        } else {
            return values = new int[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private int[] grow() {
        return grow(size + 1);
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private @NonNull String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

}
