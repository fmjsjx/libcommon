package com.github.fmjsjx.libcommon.collection;

import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Interface for a primitive set that contains no duplicate {@code int}s.
 */
public interface IntSet extends Set<Integer> {

    /**
     * Returns {@code true} if this set contains the specified value, {@code false}
     * otherwise.
     * 
     * @param value the value
     * @return {@code true} if this set contains the specified value, {@code false}
     *         otherwise
     */
    boolean contains(int value);

    /**
     * Adds the specified value to this set if it is not already present.
     * 
     * @param value the value
     * @return {@code true} if this set did not already contain the specified value,
     *         {@code false} otherwise
     */
    boolean add(int value);

    /**
     * Removes the specified value from this set if it is present.
     * 
     * @param value the value
     * @return {@code true} if this set contained the specified value, {@code false}
     *         otherwise
     */
    boolean remove(int value);

    /**
     * Returns a sequential {@link IntStream} with this set as its source.
     * 
     * @return a sequential {@code IntStream} over the values in this set
     */
    IntStream intStream();

    /**
     * Returns an array containing the values of this set.
     * 
     * @return an array containing the values of this set
     */
    default int[] toIntArray() {
        return intStream().toArray();
    }

    /**
     * Performs the given action for each value of this set.
     * 
     * @param action The action to be performed for each value
     */
    default void forEach(IntConsumer action) {
        intStream().forEach(action);
    }

    /**
     * Adds the specified values to this set if this set changed.
     * 
     * @param values the values
     * @return {@code true} if this set changed as a result of the call
     */
    default boolean addAll(int... values) {
        var modified = false;
        for (var value : values) {
            modified |= add(value);
        }
        return modified;
    }

    /**
     * Removes from this set all of its value that are contained in the specified
     * values.
     * 
     * @param values the values
     * @return {@code true} if this set changed as a result of the call
     */
    default boolean containsAll(int... values) {
        for (var e : values)
            if (!contains(e))
                return false;
        return true;
    }

    /**
     * Removes all of this set's values that are also contained in the specified
     * set.
     * 
     * @param values the values
     * @return {@code true} if all of this set's values that are also contained in
     *         the specified array
     */
    default boolean removeAll(int... values) {
        boolean modified = false;
        for (var value : values) {
            modified |= remove(value);
        }
        return modified;
    }

}
