package com.github.fmjsjx.libcommon.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result. The first input argument is {@code int}-valued and usually consuming
 * the each index in an array or a list. The second input argument is is
 * {@code int}-valued usually consuming the each element in an array or a list.
 * This is the {@code int}-consuming primitive type specialization of
 * {@link ForEachAction}.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(int, int)}.
 * 
 * @see ForEachAction
 * @see IntBiConsumer
 * @since 2.5
 */
@FunctionalInterface
public interface IntForEachAction extends IntBiConsumer {

    /**
     * Performs this operation on the given arguments.
     *
     * @param index  the index
     * @param elemnt the element
     */
    @Override
    void accept(int index, int elemnt);

    /**
     * Returns a composed {@code IntForEachAction} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code IntForEachAction} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default IntForEachAction andThen(IntForEachAction after) {
        Objects.requireNonNull(after);

        return (index, element) -> {
            accept(index, element);
            after.accept(index, element);
        };
    }

}
