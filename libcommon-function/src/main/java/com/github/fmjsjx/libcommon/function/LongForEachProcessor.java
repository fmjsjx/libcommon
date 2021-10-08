package com.github.fmjsjx.libcommon.function;

import java.util.Objects;

/**
 * Represents a predicate (boolean-valued function) of two arguments. The first
 * input argument is {@code int}-valued and usually consuming the each index in
 * an array or a list. The second input argument is {@code long-valued} and
 * usually consuming the each element in an array or a list. This is the
 * {@code long}-consuming primitive type specialization of
 * {@link ForEachProcessor}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #process(int, long)}.
 * 
 * @see ForEachProcessor
 * @since 2.5
 *
 */
@FunctionalInterface
public interface LongForEachProcessor {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param index  the index
     * @param elemnt the element
     * @return {@code true} if the input arguments match the predicate, otherwise
     *         {@code false}
     */
    boolean process(int index, long elemnt);

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
    default LongForEachProcessor and(LongForEachProcessor other) {
        Objects.requireNonNull(other);
        return (index, elememt) -> process(index, elememt) && other.process(index, elememt);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return a predicate that represents the logical negation of this predicate
     */
    default LongForEachProcessor negate() {
        return (index, elememt) -> !process(index, elememt);
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
    default LongForEachProcessor or(LongForEachProcessor other) {
        Objects.requireNonNull(other);
        return (index, elememt) -> process(index, elememt) || other.process(index, elememt);
    }

}
