package com.github.fmjsjx.libcommon.collection;

import static java.util.Collections.emptyIterator;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * This class consists exclusively of static methods that operate on or return
 * primitive collections ({@link IntSet} or {@link LongSet}).
 * 
 * @see IntSet
 * @see IntHashSet
 * @see LongSet
 * @see LongHashSet
 */
public class PrimitiveCollections {

    /**
     * Returns an unmodifiable view of the specified set.
     * 
     * @param s the set for which an unmodifiable view is to be returned
     * @return an unmodifiable view of the specified set
     */
    public static final IntSet unmodifiableSet(IntSet s) {
        if (s instanceof UnmodifiableIntSet) {
            return s;
        }
        return new UnmodifiableIntSet(s);
    }

    /**
     * Returns an unmodifiable view of the specified set.
     * 
     * @param s the set for which an unmodifiable view is to be returned
     * @return an unmodifiable view of the specified set
     */
    public static final LongSet unmodifiableSet(LongSet s) {
        if (s instanceof UnmodifiableLongSet) {
            return s;
        }
        return new UnmodifiableLongSet(s);
    }

    /**
     * Returns an empty set.
     * 
     * @return an empty set
     */
    public static final IntSet emptyIntSet() {
        return EmptyIntSet.INSTANCE;
    }

    /**
     * Returns an empty set.
     * 
     * @return an empty set
     */
    public static final LongSet emptyLongSet() {
        return EmptyLongSet.INSTANCE;
    }

    private static final class UnmodifiableIntSet implements IntSet {

        private final IntSet s;

        private UnmodifiableIntSet(IntSet s) {
            this.s = s;
        }

        @Override
        public int size() {
            return s.size();
        }

        @Override
        public boolean isEmpty() {
            return s.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return s.contains(o);
        }

        @Override
        public Iterator<Integer> iterator() {
            return new IteratorImpl(s.iterator());
        }

        @Override
        public Object[] toArray() {
            return s.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return s.toArray(a);
        }

        @Override
        public boolean add(Integer e) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return s.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Integer> c) {
            throw new UnsupportedOperationException("addAll");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("removeAll");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("clear");
        }

        @Override
        public boolean contains(int value) {
            return s.contains(value);
        }

        @Override
        public boolean add(int value) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(int value) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public IntStream intStream() {
            return s.intStream();
        }

        private class IteratorImpl implements Iterator<Integer> {

            final Iterator<Integer> iter;

            private IteratorImpl(Iterator<Integer> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

    }

    private static final class EmptyIntSet extends AbstractSet<Integer> implements IntSet {

        private static final EmptyIntSet INSTANCE = new EmptyIntSet();

        public Iterator<Integer> iterator() {
            return emptyIterator();
        }

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public void clear() {
        }

        public boolean contains(Object obj) {
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        public Object[] toArray() {
            return new Object[0];
        }

        public <T> T[] toArray(T[] a) {
            if (a.length > 0)
                a[0] = null;
            return a;
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super Integer> action) {
            Objects.requireNonNull(action);
        }

        @Override
        public boolean removeIf(Predicate<? super Integer> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public Spliterator<Integer> spliterator() {
            return Spliterators.emptySpliterator();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean contains(int value) {
            return false;
        }

        @Override
        public boolean add(int value) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll");
        }

        @Override
        public boolean remove(int value) {
            return false;
        }

        @Override
        public IntStream intStream() {
            return IntStream.empty();
        }

    }

    private static final class UnmodifiableLongSet implements LongSet {

        private final LongSet s;

        private UnmodifiableLongSet(LongSet s) {
            this.s = s;
        }

        @Override
        public int size() {
            return s.size();
        }

        @Override
        public boolean isEmpty() {
            return s.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return s.contains(o);
        }

        @Override
        public Iterator<Long> iterator() {
            return new IteratorImpl(s.iterator());
        }

        @Override
        public Object[] toArray() {
            return s.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return s.toArray(a);
        }

        @Override
        public boolean add(Long e) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return s.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Long> c) {
            throw new UnsupportedOperationException("addAll");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("removeAll");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("clear");
        }

        @Override
        public boolean contains(long value) {
            return s.contains(value);
        }

        @Override
        public boolean add(long value) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(long value) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public LongStream longStream() {
            return s.longStream();
        }

        private class IteratorImpl implements Iterator<Long> {

            final Iterator<Long> iter;

            private IteratorImpl(Iterator<Long> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Long next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

    }

    private static final class EmptyLongSet extends AbstractSet<Long> implements LongSet {

        private static final EmptyLongSet INSTANCE = new EmptyLongSet();

        public Iterator<Long> iterator() {
            return emptyIterator();
        }

        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public void clear() {
        }

        public boolean contains(Object obj) {
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        public Object[] toArray() {
            return new Object[0];
        }

        public <T> T[] toArray(T[] a) {
            if (a.length > 0)
                a[0] = null;
            return a;
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super Long> action) {
            Objects.requireNonNull(action);
        }

        @Override
        public boolean removeIf(Predicate<? super Long> filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public Spliterator<Long> spliterator() {
            return Spliterators.emptySpliterator();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean contains(long value) {
            return false;
        }

        @Override
        public boolean add(long value) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll");
        }

        @Override
        public boolean remove(long value) {
            return false;
        }

        @Override
        public LongStream longStream() {
            return LongStream.empty();
        }

    }

}
