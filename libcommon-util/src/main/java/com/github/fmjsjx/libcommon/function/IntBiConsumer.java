package com.github.fmjsjx.libcommon.function;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two {@code int}-valued input arguments
 * and returns no result. This is the {@code int}-consuming primitive type
 * specialization of {@link BiConsumer}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(int, int)}.
 * 
 * @see BiConsumer
 * @since 2.5
 *
 */
@FunctionalInterface
public interface IntBiConsumer {

    /**
     * Performs this operation on the given arguments.
     * 
     * @param left  the first input argument
     * @param right the second input argument
     */
    void accept(int left, int right);

    /**
     * Returns a composed {@code IntBiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code IntBiConsumer} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default IntBiConsumer andThen(IntBiConsumer after) {
        Objects.requireNonNull(after);
        return (left, right) -> {
            accept(left, right);
            after.accept(left, right);
        };
    }

}
