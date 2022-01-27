package com.github.fmjsjx.libcommon.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * 
 * @param <E> the type of elements maintained by this set
 *
 * @author MJ Fang
 * @see Set
 * @see ListSet
 * @since 2.6
 */
public class ArrayListSet<E> extends AbstractListSet<E> {

    /**
     * Constructs a new, empty {@link ArrayListSet} with an initial capacity of ten.
     */
    public ArrayListSet() {
        super(new ArrayList<>());
    }

    /**
     * Constructs a new, empty {@link ArrayListSet} with the specified initial
     * capacity.
     * 
     * @param initialCapacity the initial capacity of the list
     * 
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public ArrayListSet(int initialCapacity) {
        super(new ArrayList<>(initialCapacity));
    }

    /**
     * Constructs an {@link ArrayListSet} with the same elements as the specified
     * collection.
     * 
     * @param elements the elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     */
    @SafeVarargs
    public ArrayListSet(E... elements) {
        this(Arrays.asList(elements));
    }

    /**
     * Constructs an {@link ArrayListSet} with the same elements as the specified
     * collection.
     * 
     * @param c the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayListSet(Collection<E> c) {
        this(c.size());
        addAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c instanceof Set) {
            super.addAll(c);
        }
        return c.stream().distinct().mapToInt(e -> add(e) ? 1 : 0).sum() > 0;
    }

}
