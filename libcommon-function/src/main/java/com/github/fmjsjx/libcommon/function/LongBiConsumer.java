package com.github.fmjsjx.libcommon.function;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two {@code long}-valued input arguments
 * and returns no result. This is the {@code long}-consuming primitive type
 * specialization of {@link BiConsumer}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(long, long)}.
 * 
 * @see BiConsumer
 * @since 2.5
 *
 */
@FunctionalInterface
public interface LongBiConsumer {

    /**
     * Performs this operation on the given arguments.
     * 
     * @param left  the first input argument
     * @param right the second input argument
     */
    void accept(long left, long right);

    /**
     * Returns a composed {@code LongBiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code LongBiConsumer} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default LongBiConsumer andThen(LongBiConsumer after) {
        Objects.requireNonNull(after);
        return (left, right) -> {
            accept(left, right);
            after.accept(left, right);
        };
    }

}
