package com.github.fmjsjx.libcommon.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

/**
 * Utility class for {@link Collector}s.
 */
public class CollectorUtil {

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@link LinkedHashMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     * 
     * @param <T>       the type of the values
     * @param <K>       the output type of the key mapping function
     * @param keyMapper a mapping function to produce keys
     * @return a {@code Collector} that accumulates elements into a
     *         {@link LinkedHashMap} whose keys and values are the result of
     *         applying the provided mapping functions to the input elements
     */
    public static final <T, K> Collector<T, ?, Map<K, T>> toLinkedHashMap(Function<? super T, ? extends K> keyMapper) {
        return Collectors.toMap(keyMapper, Function.identity(), throwingMerger(), LinkedHashMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@link LinkedHashMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     * 
     * @param <T>         the type of the input elements
     * @param <K>         the output type of the key mapping function
     * @param <U>         the output type of the value mapping function
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a {@code Collector} that accumulates elements into a
     *         {@link LinkedHashMap} whose keys and values are the result of
     *         applying the provided mapping functions to the input elements
     */
    public static final <T, K, U> Collector<T, ?, Map<K, U>> toLinkedHashMap(Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@link IntObjectMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     * 
     * @param <T>       the type of the input elements
     * @param keyMapper a mapping function to produce keys
     * @return a {@code Collector} that accumulates elements into a
     *         {@link IntObjectMap} whose keys and values are the result of applying
     *         the provided mapping functions to the input elements.
     */
    public static final <T> Collector<T, ?, IntObjectMap<T>> toIntObjectMap(ToIntFunction<T> keyMapper) {
        return Collectors.toMap(keyMapper::applyAsInt, Function.identity(), throwingMerger(), IntObjectHashMap::new);
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a
     * {@link IntObjectMap} whose keys and values are the result of applying the
     * provided mapping functions to the input elements.
     * 
     * @param <T>         the type of the input elements
     * @param <U>         the output type of the value mapping function
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a {@code Collector} that accumulates elements into a
     *         {@link IntObjectMap} whose keys and values are the result of applying
     *         the provided mapping functions to the input elements.
     */
    public static final <T, U> Collector<T, ?, IntObjectMap<U>> toIntObjectMap(ToIntFunction<T> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper::applyAsInt, valueMapper, throwingMerger(), IntObjectHashMap::new);
    }

    /**
     * Returns the merger that always throwing {@code IllegalStateException}.
     * 
     * @param <T> the type of the elements
     * @return the merger that always throwing {@code IllegalStateException}
     */
    public static final <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    private CollectorUtil() {
    }

}
