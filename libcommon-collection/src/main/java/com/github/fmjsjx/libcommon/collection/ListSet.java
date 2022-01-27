package com.github.fmjsjx.libcommon.collection;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Set} backed by a {@link List}.
 * 
 * @param <E> the type of elements maintained by this set
 *
 * @author MJ Fang
 * @see Set
 * @see List
 * @see ArrayListSet
 * @since 2.6
 */
public interface ListSet<E> extends Set<E> {

    /**
     * Returns the internal {@link List}.
     * 
     * @return the internal {@code List}
     */
    List<E> internalList();

    /**
     * Returns an unmodifiable {@link ListSet} containing zero elements.
     *
     * @param <E> the {@code ListSet}'s element type
     * @return an empty {@code ListSet}
     */
    static <E> ListSet<E> of() {
        return ImmutableCollections.emptyListSet();
    }

    /**
     * Returns an unmodifiable {@link ListSet} containing one element.
     * 
     * @param <E> the {@code ListSet}'s element type
     * @param e1  the single element
     * @return a {@code ListSet} containing the specified element
     */
    static <E> ListSet<E> of(E e1) {
        return new ImmutableCollections.ListSet12<>(e1);
    }

    /**
     * Returns an unmodifiable {@link ListSet} containing two elements.
     * 
     * @param <E> the {@code ListSet}'s element type
     * @param e1  the first element
     * @param e2  the second element
     * @return a {@code ListSet} containing the specified elements
     */
    static <E> ListSet<E> of(E e1, E e2) {
        return new ImmutableCollections.ListSet12<>(e1, e2);
    }

    /**
     * Returns an unmodifiable {@link ListSet} containing an arbitrary number of
     * elements.
     * 
     * @param <E>      the {@code ListSet}'s element type
     * @param elements elements the elements to be contained in the {@code ListSet}
     * @return a {@code ListSet} containing the specified elements
     */
    @SafeVarargs
    static <E> ListSet<E> of(E... elements) {
        switch (elements.length) {
        case 0:
            return ImmutableCollections.emptyListSet();
        case 1:
            return new ImmutableCollections.ListSet12<>(elements[0]);
        case 2:
            var e0 = elements[0];
            var e1 = elements[1];
            if (e0.equals(Objects.requireNonNull(e1))) { // implicit null check of e0
                return new ImmutableCollections.ListSet12<>(e0);
            }
            return new ImmutableCollections.ListSet12<>(e0, e1);
        default:
            return new ImmutableCollections.ListSetN<>(elements);
        }
    }

    /**
     * Returns an unmodifiable {@link ListSet} containing the elements of the given
     * Collection.
     * 
     * @param <E>  the {@code ListSet}'s element type
     * @param coll a {@code Collection} from which elements are drawn, must be
     *             non-null
     * @return a {@code ListSet} containing the elements of the given
     *         {@code Collection}
     */
    @SuppressWarnings("unchecked")
    static <E> ListSet<E> copyOf(Collection<? extends E> coll) {
        if (coll instanceof ImmutableCollections.AbstractImmutableListSet) {
            return (ListSet<E>) coll;
        } else if (coll instanceof Set) {
            return (ListSet<E>) new ImmutableCollections.ListSetN<>(coll.toArray(), false);
        } else {
            return (ListSet<E>) of(coll.toArray());
        }
    }

}
