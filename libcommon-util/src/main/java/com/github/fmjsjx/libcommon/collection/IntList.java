package com.github.fmjsjx.libcommon.collection;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Interface for a primitive list of {@code int} values, a specialization of
 * {@code List<Integer>} that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public interface IntList extends List<@NonNull Integer> {

    /**
     * Returns {@code true} if this list contains the specified value, {@code false}
     * otherwise.
     * 
     * @param value the value
     * @return {@code true} if this list contains the specified value, {@code false}
     *         otherwise
     */
    boolean contains(int value);

    /**
     * Appends the specified value to the end of this list.
     * 
     * @param value the value
     * @return {@code true} if this list changed as a result of the call
     */
    boolean add(int value);

    /**
     * Returns the value at the specified position in this list.
     * 
     * @param index the index of the value to return
     * @return the value at the specified position in this list
     */
    int valueAt(int index);

    /**
     * Replaces the value at the specified position in this list with the
     * specified value.
     * 
     * @param index the index of the value to replace
     * @param value the value to be stored at the specified position
     * @return the value previously at the specified position
     */
    int set(int index, int value);

    /**
     * Inserts the specified value at the specified position in this list.
     * 
     * @param index the index at which the specified value is to be inserted
     * @param value the value to be inserted
     */
    void add(int index, int value);

    /**
     * Returns the index of the first occurrence of the specified value in this
     * list, or {@code -1} if this list does not contain the value.
     * 
     * @param value the value to search for
     * @return the index of the first occurrence of the specified value in this
     *         list, or {@code -1} if this list does not contain the value
     */
    int indexOf(int value);

    /**
     * Returns the index of the last occurrence of the specified value in this
     * list, or {@code -1} if this list does not contain the value.
     * 
     * @param value the value to search for
     * @return the index of the last occurrence of the specified value in this
     *         list, or {@code -1} if this list does not contain the value
     */
    int lastIndexOf(int value);

    /**
     * Removes the value at the specified position in this list.
     * 
     * @param index the index of the value to be removed
     * @return the value previously at the specified position
     */
    int removeAt(int index);

    /**
     * Removes the first occurrence of the specified value from this list, if it
     * is present.
     * 
     * @param value the value
     * @return {@code true} if this list contained the specified value, {@code false}
     *         otherwise
     */
    boolean removeFirst(int value);

    /**
     * Removes all occurrences of the specified value from this list.
     * 
     * @param value the value
     * @return {@code true} if this list contained the specified value, {@code false}
     *         otherwise
     */
    default boolean removeAllValue(int value) {
        var modified = false;
        while (removeFirst(value)) {
            modified = true;
        }
        return modified;
    }

    /**
     * Returns a sequential {@link IntStream} with this list as its source.
     * 
     * @return a sequential {@code IntStream} over the values in this list
     */
    @NonNull IntStream intStream();

    /**
     * Returns an array containing the values of this list.
     * 
     * @return an array containing the values of this list
     */
    default int @NonNull [] toIntArray() {
        return intStream().toArray();
    }

    /**
     * Performs the given action for each value of this list.
     * 
     * @param action The action to be performed for each value
     */
    default void forEach(@NonNull IntConsumer action) {
        intStream().forEach(action);
    }

    /**
     * Appends all of the specified values to the end of this list.
     * 
     * @param values the values
     * @return {@code true} if this list changed as a result of the call
     */
    default boolean addAll(int @NonNull ... values) {
        var modified = false;
        for (var value : values) {
            modified |= add(value);
        }
        return modified;
    }

    /**
     * Returns {@code true} if this list contains all of the specified values,
     * {@code false} otherwise.
     * 
     * @param values the values
     * @return {@code true} if this list contains all of the specified values,
     *         {@code false} otherwise
     */
    default boolean containsAll(int @NonNull ... values) {
        for (var e : values) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes from this list all of its values that are contained in the
     * specified values.
     * 
     * @param values the values
     * @return {@code true} if this list changed as a result of the call
     */
    boolean removeAll(int @NonNull ... values);

}
