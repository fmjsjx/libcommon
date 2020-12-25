package com.github.fmjsjx.libcommon.util;

/**
 * Utility class for numbers.
 */
public class NumberUtil {

    /**
     * Converts and returns the given value as an {@code int}.
     *
     * @param value a number
     * @param defaultValue the default value
     * @return an {@code int}
     */
    public static final int intValue(Number value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    /**
     * Converts an returns the given value as an {@code int}.
     *
     * @param value a number
     * @return an {@code int}
     */
    public static final int intValue(Number value) {
        return intValue(value, 0);
    }

    private NumberUtil() {}

}
