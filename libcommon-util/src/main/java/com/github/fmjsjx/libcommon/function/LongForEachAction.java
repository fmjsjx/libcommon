package com.github.fmjsjx.libcommon.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result. The first input argument is {@code long}-valued and usually consuming
 * the each index in an array or a list. The second input argument is is
 * {@code long}-valued usually consuming the each element in an array or a list.
 * This is the {@code long}-consuming primitive type specialization of
 * {@link ForEachAction}.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(long, long)}.
 * 
 * @see ForEachAction
 * @see LongBiConsumer
 * @since 2.5
 */
@FunctionalInterface
public interface LongForEachAction extends LongBiConsumer {

    /**
     * Performs this operation on the given arguments.
     *
     * @param index  the index
     * @param elemnt the element
     */
    @Override
    void accept(long index, long elemnt);

    /**
     * Returns a composed {@code LongForEachAction} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code LongForEachAction} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default LongForEachAction andThen(LongForEachAction after) {
        Objects.requireNonNull(after);

        return (index, element) -> {
            accept(index, element);
            after.accept(index, element);
        };
    }

}
