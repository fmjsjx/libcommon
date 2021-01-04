package com.github.fmjsjx.libcommon.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class for functions.
 */
public class FunctionUtil {

    /**
     * Returns a {@link Runnable} that always does nothing.
     * 
     * @return a runnable that always do nothing
     */
    public static final Runnable doNothing() {
        return () -> {
            // do nothing
        };
    }

    /**
     * Returns a {@link Consumer} that always ignores the input value and does
     * nothing.
     * 
     * @param <T> the type of the input
     * @return a consumer that always skip the input values and do nothing
     */
    public static final <T> Consumer<T> skip() {
        return i -> {
            // do nothing
        };
    }

    /**
     * Returns a {@link BiConsumer} that always ignores the both input values and
     * does nothing.
     * 
     * @param <T> the type of the first argument
     * @param <U> the type of the second argument
     * @return a {@link BiConsumer} that always ignores the both input values and
     *         does nothing
     */
    public static final <T, U> BiConsumer<T, U> skipBoth() {
        return (a, b) -> {
            // do nothing
        };
    }

}
