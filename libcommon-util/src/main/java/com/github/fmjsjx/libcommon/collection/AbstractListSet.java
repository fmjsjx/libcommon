package com.github.fmjsjx.libcommon.collection;

import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.IntFunction;

/**
 * This class implements {@link ListSet} interface backed by an
 * {@link ArrayList}.
 *
 * @param <E> the type of elements maintained by this set
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

    @SuppressWarnings("SuspiciousMethodCalls")
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
    public @NonNull Iterator<E> iterator() {
        return internalList.iterator();
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Object @NonNull [] toArray() {
        return internalList.toArray();
    }

    /**
     * @since 2.6.1
     */
    @Override
    public <T> T @NonNull [] toArray(T @NonNull [] a) {
        return internalList.toArray(a);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public <T> T[] toArray(@NonNull IntFunction<T[]> generator) {
        return internalList.toArray(generator);
    }

}
