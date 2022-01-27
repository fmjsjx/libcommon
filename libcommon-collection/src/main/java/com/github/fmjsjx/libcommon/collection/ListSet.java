package com.github.fmjsjx.libcommon.collection;

import java.util.Collection;
import java.util.List;
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
     * Returns an unmodifiable {@link ListSet} containing an arbitrary number of
     * elements.
     * 
     * @param <E>      the {@code ListSet}'s element type
     * @param elements elements the elements to be contained in the {@code ListSet}
     * @return a {@code ListSet} containing the specified elements
     */
    @SafeVarargs
    static <E> ListSet<E> of(E... elements) {
        if (elements.length == 0) {
            return of();
        }
        return new ImmutableCollections.ListSetN<>(elements);
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
        } else {
            return (ListSet<E>) of(coll.toArray());
        }
    }

}
