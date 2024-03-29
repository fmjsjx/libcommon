package com.github.fmjsjx.libcommon.collection;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * This class implements {@link ListSet} interface backed by an
 * {@link ArrayList}.
 * 
 * @param <E> the type of elements maintained by this set
 *
 * @author MJ Fang
 * @see ListSet
 * @see ArrayListSet
 * @since 2.6
 */
public abstract class AbstractListSet<E> extends AbstractSet<E> implements ListSet<E> {

    /**
     * The list for internal use.
     */
    protected final List<E> internalList;

    /**
     * Constructs {@link AbstractListSet} instances with the specified {@code internalList} given.
     *
     * @param internalList the list for internal use
     */
    protected AbstractListSet(List<E> internalList) {
        this.internalList = Objects.requireNonNull(internalList, "internalList must not be null");
    }

    @Override
    public List<E> internalList() {
        return internalList;
    }

    @Override
    public boolean add(E e) {
        var list = internalList;
        if (list.contains(e)) {
            return false;
        }
        list.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        var list = internalList;
        var index = list.indexOf(o);
        if (index == -1) {
            return false;
        }
        list.remove(index);
        return true;
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    @Override
    public boolean contains(Object o) {
        return internalList.contains(o);
    }

    @Override
    public int size() {
        return internalList.size();
    }

    @Override
    public Iterator<E> iterator() {
        return internalList.iterator();
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Object[] toArray() {
        return internalList.toArray();
    }

    /**
     * @since 2.6.1
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return internalList.toArray(a);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return internalList.toArray(generator);
    }

}
