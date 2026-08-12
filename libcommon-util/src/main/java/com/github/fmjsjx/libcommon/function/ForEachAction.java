package com.github.fmjsjx.libcommon.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result. The first input argument is {@code int}-valued and usually consuming
 * the each index in an array or a list. The second input argument is usually
 * consuming the each element in an array or a list.
 *
 * @param <T> the type of the element
 * 
 * @since 2.5
 */
@FunctionalInterface
public interface ForEachAction<T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param index  the index
     * @param elemnt the element
     */
    void accept(int index, T elemnt);

    /**
     * Returns a composed {@code ForEachAction} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code ForEachAction} that performs in sequence this
     *         operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ForEachAction<T> andThen(ForEachAction<? super T> after) {
        Objects.requireNonNull(after);

        return (index, element) -> {
            accept(index, element);
            after.accept(index, element);
        };
    }

}
