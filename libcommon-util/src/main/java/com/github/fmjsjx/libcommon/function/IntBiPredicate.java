package com.github.fmjsjx.libcommon.function;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Represents a predicate (boolean-valued function) of two {@code int}-valued
 * arguments. This is the {@code int}-consuming primitive type specialization of
 * {@link BiPredicate}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #test(int, int)}.
 *
 * @see BiPredicate
 * @since 2.5
 */
@FunctionalInterface
public interface IntBiPredicate {

    /**
     * Evaluates this predicate on the given arguments.
     * 
     * @param left  the first input argument
     * @param right the second input argument
     * @return {@code true} if the input arguments match the predicate, otherwise
     *         {@code false}
     */
    boolean test(int left, int right);

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
    default IntBiPredicate and(IntBiPredicate other) {
        Objects.requireNonNull(other);
        return (left, right) -> test(left, right) && other.test(left, right);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return a predicate that represents the logical negation of this predicate
     */
    default IntBiPredicate negate() {
        return (left, right) -> !test(left, right);
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
    default IntBiPredicate or(IntBiPredicate other) {
        Objects.requireNonNull(other);
        return (left, right) -> test(left, right) || other.test(left, right);
    }

}
