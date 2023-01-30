package com.github.fmjsjx.libcommon.collection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Container class for immutable collections.
 * 
 * @author MJ Fang
 * @since 2.6
 */
final class ImmutableCollections {

    static UnsupportedOperationException uoe() {
        return new UnsupportedOperationException();
    }

    static abstract class AbstractImmutableCollection<E> extends AbstractCollection<E> {
        // all mutating methods throw UnsupportedOperationException
        @Override
        public boolean add(E e) {
            throw uoe();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw uoe();
        }

        @Override
        public void clear() {
            throw uoe();
        }

        @Override
        public boolean remove(Object o) {
            throw uoe();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw uoe();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw uoe();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw uoe();
        }
    }

    static abstract class AbstractImmutableListSet<E> extends AbstractImmutableCollection<E> implements ListSet<E> {

        @Override
        public boolean contains(Object o) {
            return internalList().contains(o);
        }

        @Override
        public int size() {
            return internalList().size();
        }

        @Override
        public Iterator<E> iterator() {
            return internalList().iterator();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (!(o instanceof ListSet))
                return false;
            Collection<?> c = (Collection<?>) o;
            if (c.size() != size())
                return false;
            try {
                return containsAll(c);
            } catch (ClassCastException | NullPointerException unused) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int h = 0;
            for (var e : internalList()) {
                if (e != null) {
                    h += e.hashCode();
                }
            }
            return h;
        }
    }

    @SuppressWarnings("unchecked")
    static <E> ListSet<E> emptyListSet() {
        return (ListSet<E>) ListSetN.EMPTY_LIST_SET;
    }

    static final class ListSet12<E> extends AbstractImmutableListSet<E> implements Serializable {

        final List<E> internalList;
        final int size;

        ListSet12(E e0) {
            internalList = List.of(e0);
            size = 1;
        }

        ListSet12(E e0, E e1) {
            if (e0.equals(Objects.requireNonNull(e1))) { // implicit null check of e0
                throw new IllegalArgumentException("duplicate element: " + e0);
            }
            internalList = List.of(e0, e1);
            size = 2;
        }

        @Override
        public List<E> internalList() {
            return internalList;
        }

        @Override
        public int size() {
            return size;
        }

    }

    static final class ListSetN<E> extends AbstractImmutableListSet<E> implements Serializable {

        static ListSetN<?> EMPTY_LIST_SET = new ListSetN<>();

        final List<E> internalList;

        ListSetN(E[] input, boolean distinct) {
            internalList = input.length == 0 ? List.of()
                    : (distinct ? Arrays.stream(input).distinct().toList()
                            : List.of(input));
        }

        @SafeVarargs
        ListSetN(E... input) {
            this(input, true);
        }

        @Override
        public List<E> internalList() {
            return internalList;
        }

    }

    /** No instances. */
    private ImmutableCollections() {
    }

}
