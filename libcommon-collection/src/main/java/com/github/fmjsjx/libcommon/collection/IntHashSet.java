package com.github.fmjsjx.libcommon.collection;

import static io.netty.util.collection.IntObjectHashMap.DEFAULT_CAPACITY;
import static io.netty.util.collection.IntObjectHashMap.DEFAULT_LOAD_FACTOR;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap.PrimitiveEntry;

/**
 * This class implements the {@link IntSet} interface, backed by a hash table
 * (actually a {@link IntObjectHashMap} instance).
 */
public class IntHashSet extends AbstractSet<Integer> implements IntSet {

    static final Object PRESENT = new Object();

    private final IntObjectHashMap<Object> map;

    /**
     * Constructs a new, empty set with the default initial capacity (8) and the
     * default load factor (0.5).
     */
    public IntHashSet() {
        map = new IntObjectHashMap<>();
    }

    /**
     * Constructs a new, empty set with the specified initial capacity and the
     * default load factor (0.5).
     * 
     * @param initialCapacity the initial capacity of the hash map
     */
    public IntHashSet(int initialCapacity) {
        map = new IntObjectHashMap<>(initialCapacity);
    }

    /**
     * Constructs a new, empty set with the specified initial capacity and the
     * specified load factor.
     * 
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor      the load factor of the hash map
     */
    public IntHashSet(int initialCapacity, float loadFactor) {
        map = new IntObjectHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new set containing the elements in the specified collection
     * 
     * @param c the collection whose elements are to be placed into this set
     */
    public IntHashSet(Collection<? extends Integer> c) {
        this(Math.max((int) (c.size() / DEFAULT_LOAD_FACTOR), DEFAULT_CAPACITY));
        addAll(c);
    }

    /**
     * Constructs a new set containing the values in the specified array
     * 
     * @param values the array whose values are to be placed into this set
     */
    public IntHashSet(int... values) {
        this(Math.max((int) (values.length / DEFAULT_LOAD_FACTOR), DEFAULT_CAPACITY));
        addAll(values);
    }

    @Override
    public boolean contains(int value) {
        return map.containsKey(value);
    }

    @Override
    public boolean add(int value) {
        return map.put(value, PRESENT) == null;
    }

    @Override
    public boolean add(Integer e) {
        return add(e.intValue());
    }
    
    @Override
    public boolean remove(int value) {
        return map.remove(value) == PRESENT;
    }

    @Override
    public IntStream intStream() {
        return StreamSupport.stream(map.entries().spliterator(), false).mapToInt(PrimitiveEntry::key);
    }

    @Override
    public Iterator<Integer> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public void forEach(IntConsumer action) {
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
