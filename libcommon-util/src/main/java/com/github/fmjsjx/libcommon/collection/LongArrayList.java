package com.github.fmjsjx.libcommon.collection;

import com.github.fmjsjx.libcommon.util.ArrayUtil;
import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

import static com.github.fmjsjx.libcommon.collection.IntArrayList.fastRemoveByIndices;
import static java.util.Objects.checkIndex;

/**
 * This class implements the {@link LongList} interface, backed by a resizable
 * {@code long} array that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public class LongArrayList extends AbstractList<@NonNull Long>
        implements LongList, RandomAccess, Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 8990172562371752475L;

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final long[] EMPTY_VALUES = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final long[] DEFAULT_CAPACITY_EMPTY_VALUES = {};

    private long[] values;
    private int size;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public LongArrayList() {
        values = DEFAULT_CAPACITY_EMPTY_VALUES;
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public LongArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            values = new long[initialCapacity];
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
    public LongArrayList(long... values) {
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
    public LongArrayList(Collection<? extends @NonNull Long> c) {
        if (c.isEmpty()) {
            values = EMPTY_VALUES;
        } else if (c instanceof LongArrayList other) {
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

    /**
     * Returns a shallow copy of this {@code LongArrayList} instance.
     *
     * @return a clone of this {@code LongArrayList} instance
     */
    @Override
    public @NonNull Object clone() {
        try {
            var clone = (LongArrayList) super.clone();
            clone.values = Arrays.copyOf(values, size);
            clone.modCount = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public boolean contains(long value) {
        return ArrayUtil.contains(values, 0, size, value);
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
    private void add(long value, long[] values, int s) {
        if (s == values.length) {
            values = grow();
        }
        values[s] = value;
        size = s + 1;
    }

    @Override
    public boolean add(long value) {
        modCount++;
        add(value, values, size);
        return true;
    }

    @Override
    public long valueAt(int index) {
        checkIndex(index, size);
        return valueData(index);
    }

    long valueData(int index) {
        return values[index];
    }

    @Override
    public long set(int index, long value) {
        var oldValue = valueAt(index);
        values[index] = value;
        return oldValue;
    }

    @Override
    public void add(int index, long value) {
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
    public int indexOf(long value) {
        return indexOfRange(value, 0, size);
    }

    private int indexOfRange(long value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = start; i < end; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long value) {
        return lastIndexOfRange(value, 0, size);
    }

    private int lastIndexOfRange(long value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = end - 1; i >= start; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long removeAt(int index) {
        checkIndex(index, size);
        var values = this.values;
        var oldValue = values[index];
        fastRemove(values, index);
        return oldValue;
    }

    private void fastRemove(long[] values, int index) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > index) {
            System.arraycopy(values, index + 1, values, index, newSize - index);
        }
        size = newSize;
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
    public boolean removeAllValue(long value) {
        var values = this.values;
        var size = this.size;
        var toRemoveIndices = new IntArrayList();
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                toRemoveIndices.add(i);
            }
        }
        return fastRemove(values, 0, size, toRemoveIndices);
    }

    private boolean fastRemove(long @NonNull [] values, @SuppressWarnings("SameParameterValue") int start, int end, @NonNull IntArrayList toRemoveIndices) {
        if (toRemoveIndices.isEmpty()) {
            return false;
        }
        modCount++;
        fastRemoveByIndices(values, start, end, toRemoveIndices);
        size -= toRemoveIndices.size();
        return true;
    }

    @Override
    public boolean removeAll(long @NonNull ... values) {
        var modified = false;
        for (var value : values) {
            modified |= removeAllValue(value);
        }
        return modified;
    }

    @Override
    public @NonNull LongStream longStream() {
        return Arrays.stream(values, 0, size);
    }

    @Override
    public long @NonNull [] toLongArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public void forEach(@NonNull LongConsumer action) {
        for (var i = 0; i < size; i++) {
            action.accept(values[i]);
        }
    }

    @Override
    public boolean contains(@NonNull Object o) {
        return switch (o) {
            case Long l -> contains(l.longValue());
            case Integer i -> contains(i.longValue());
            case Short s -> contains(s.longValue());
            case Byte b -> contains(b.longValue());
            case BigInteger bi -> contains(bi.longValueExact());
            default -> false;
        };
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
    public @NonNull Long removeFirst() {
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
    public @NonNull Long removeLast() {
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

    private long[] grow(int minCapacity) {
        var oldCapacity = values.length;
        if (oldCapacity > 0 || values != DEFAULT_CAPACITY_EMPTY_VALUES) {
            var newCapacity = ArrayUtil.newLength(oldCapacity,
                    minCapacity - oldCapacity,
                    oldCapacity >> 1);
            return values = Arrays.copyOf(values, newCapacity);
        } else {
            return values = new long[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private long[] grow() {
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
