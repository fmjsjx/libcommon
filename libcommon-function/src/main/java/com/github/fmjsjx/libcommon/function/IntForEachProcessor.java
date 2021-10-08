package com.github.fmjsjx.libcommon.function;

import java.util.Objects;

/**
 * Represents a predicate (boolean-valued function) of two {@code int}-valued
 * arguments. This is the {@code int}-consuming primitive type specialization of
 * {@link ForEachProcessor}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #process(int, int)}.
 * 
 * @see ForEachProcessor
 * @see IntBiPredicate
 * @since 2.5
 *
 */
@FunctionalInterface
public interface IntForEachProcessor extends IntBiPredicate {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param index  the index
     * @param elemnt the element
     * @return {@code true} if the input arguments match the predicate, otherwise
     *         {@code false}
     */
    boolean process(int index, int elemnt);
    
    @Override
    default boolean test(int left, int right) {
        return process(left, right);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical AND
     * of this predicate and another. When evaluating the composed predicate, if
     * this predicate is {@code false}, then the {@code other} predicate is not
     * evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to
     * the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this predicate
     * @return a composed predicate that represents the short-circuiting logical AND
     *         of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default IntForEachProcessor and(IntForEachProcessor other) {
        Objects.requireNonNull(other);
        return (index, element) -> process(index, element) && other.process(index, element);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return a predicate that represents the logical negation of this predicate
     */
    default IntForEachProcessor negate() {
        return (index, element) -> !process(index, element);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical OR of
     * this predicate and another. When evaluating the composed predicate, if this
     * predicate is {@code true}, then the {@code other} predicate is not evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed to
     * the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this predicate
     * @return a composed predicate that represents the short-circuiting logical OR
     *         of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default IntForEachProcessor or(IntForEachProcessor other) {
        Objects.requireNonNull(other);
        return (index, element) -> process(index, element) || other.process(index, element);
    }

}
