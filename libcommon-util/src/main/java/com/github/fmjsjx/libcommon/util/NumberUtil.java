package com.github.fmjsjx.libcommon.util;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Utility class for numbers.
 */
public class NumberUtil {

    /**
     * Converts and returns the given value as an {@code int}.
     *
     * @param value        a number
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
     * Converts and returns the given value as an {@code int}.
     *
     * @param value a number
     * @return an {@code int}
     */
    public static final int intValue(Number value) {
        return intValue(value, 0);
    }

    /**
     * Converts and returns the given value as an {@code OptionalInt}.
     * 
     * @param value a number
     * @return an {@code OptionalInt}
     */
    public static final OptionalInt optionalInt(Number value) {
        return value == null ? OptionalInt.empty() : OptionalInt.of(value.intValue());
    }

    /**
     * Converts and returns the given value as a {@code long}.
     *
     * @param value        a number
     * @param defaultValue the default value
     * @return a {@code long}
     */
    public static final long longValue(Number value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.longValue();
    }

    /**
     * Converts and returns the given value as a {@code long}.
     * 
     * @param value a number
     * @return a {@code long}
     */
    public static final long longValue(Number value) {
        return longValue(value, 0);
    }

    /**
     * Converts and returns the given value as an {@code OptionalLong}.
     * 
     * @param value a number
     * @return an {@code OptionalLong}
     */
    public static final OptionalLong optionalLong(Number value) {
        return value == null ? OptionalLong.empty() : OptionalLong.of(value.longValue());
    }

    /**
     * Converts and returns the given value as a {@code double}.
     * 
     * @param value        a number
     * @param defaultValue the default value
     * @return a {@code double}
     */
    public static final double doubleValue(Number value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.doubleValue();
    }

    /**
     * Converts and returns the given value as a {@code double}.
     * 
     * @param value a number
     * @return a {@code double}
     */
    public static final double doubleValue(Number value) {
        return doubleValue(value, 0);
    }

    /**
     * Converts and returns the given value as an {@code OptionalDouble}.
     * 
     * @param value a number
     * @return a {@code OptionalDouble}
     */
    public static final OptionalDouble optionalDouble(Number value) {
        return value == null ? OptionalDouble.empty() : OptionalDouble.of(value.doubleValue());
    }

    /**
     * Converts and returns the given value as a {@code byte}.
     * 
     * @param value        a number
     * @param defaultValue the default value
     * @return a {@code byte}
     */
    public static final byte byteValue(Number value, int defaultValue) {
        return (byte) intValue(value, defaultValue);
    }

    /**
     * Converts and returns the given value as a {@code byte}.
     * 
     * @param value        a number
     * @param defaultValue the default value
     * @return a {@code byte}
     */
    public static final short shortValue(Number value, int defaultValue) {
        return (short) intValue(value, defaultValue);
    }

    /**
     * Converts and returns the given value as a {@code float}.
     * 
     * @param value        a number
     * @param defaultValue the default value
     * @return a {@code float}
     */
    public static final float floatValue(Number value, double defaultValue) {
        return (float) doubleValue(value, defaultValue);
    }

    private NumberUtil() {
    }

}
