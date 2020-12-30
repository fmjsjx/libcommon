package com.github.fmjsjx.libcommon.collection;

import static com.github.fmjsjx.libcommon.collection.IntHashSet.PRESENT;
import static io.netty.util.collection.LongObjectHashMap.DEFAULT_CAPACITY;
import static io.netty.util.collection.LongObjectHashMap.DEFAULT_LOAD_FACTOR;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap.PrimitiveEntry;

/**
 * This class implements the {@link LongSet} interface, backed by a hash table
 * (actually a {@link LongObjectHashMap} instance).
 */
public class LongHashSet extends AbstractSet<Long> implements LongSet {

    private final LongObjectHashMap<Object> map;

    /**
     * Constructs a new, empty set with the default initial capacity (8) and the
     * default load factor (0.5).
     */
    public LongHashSet() {
        map = new LongObjectHashMap<>();
    }

    /**
     * Constructs a new, empty set with the specified initial capacity and the
     * default load factor (0.5).
     * 
     * @param initialCapacity the initial capacity of the hash map
     */
    public LongHashSet(int initialCapacity) {
        map = new LongObjectHashMap<>(initialCapacity);
    }

    /**
     * Constructs a new, empty set with the specified initial capacity and the
     * specified load factor.
     * 
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor      the load factor of the hash map
     */
    public LongHashSet(int initialCapacity, float loadFactor) {
        map = new LongObjectHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new set containing the elements in the specified collection
     * 
     * @param c the collection whose elements are to be placed into this set
     */
    public LongHashSet(Collection<? extends Long> c) {
        this(Math.max((int) (c.size() / DEFAULT_LOAD_FACTOR), DEFAULT_CAPACITY));
        addAll(c);
    }

    /**
     * Constructs a new set containing the values in the specified array
     * 
     * @param values the array whose values are to be placed into this set
     */
    public LongHashSet(long... values) {
        this(Math.max((int) (values.length / DEFAULT_LOAD_FACTOR), DEFAULT_CAPACITY));
        addAll(values);
    }

    @Override
    public boolean contains(long value) {
        return map.containsKey(value);
    }

    @Override
    public boolean add(long value) {
        return map.put(value, PRESENT) == null;
    }

    @Override
    public boolean remove(long value) {
        return map.remove(value) == PRESENT;
    }

    @Override
    public LongStream longStream() {
        return StreamSupport.stream(map.entries().spliterator(), false).mapToLong(PrimitiveEntry::key);
    }

    @Override
    public Iterator<Long> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public void forEach(LongConsumer action) {
        for (var entry : map.entries()) {
            action.accept(entry.key());
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public void clear() {
        map.clear();
    }

}
