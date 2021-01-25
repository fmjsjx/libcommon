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

    /**
     * Returns {@code true} if the specified array contains the specified value,
     * {@code false} otherwise.
     * 
     * @param value the value
     * @param array the array
     * @return {@code true} if the specified array contains the specified value,
     *         {@code false} otherwise
     */
    public static final boolean in(int value, int... array) {
        for (var v : array) {
            if (value == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified array contains the specified value,
     * {@code false} otherwise.
     * 
     * @param value the value
     * @param array the array
     * @return {@code true} if the specified array contains the specified value,
     *         {@code false} otherwise
     */
    public static final boolean in(long value, long... array) {
        for (var v : array) {
            if (value == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code false} if the specified array contains the specified value,
     * {@code true} otherwise.
     * 
     * @param value the value
     * @param array the array
     * @return {@code false} if the specified array contains the specified value,
     *         {@code true} otherwise
     */
    public static final boolean notIn(int value, int... array) {
        for (var v : array) {
            if (value == v) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code false} if the specified array contains the specified value,
     * {@code true} otherwise.
     * 
     * @param value the value
     * @param array the array
     * @return {@code false} if the specified array contains the specified value,
     *         {@code true} otherwise
     */
    public static final boolean notIn(long value, long... array) {
        for (var v : array) {
            if (value == v) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified value is between with begin (include)
     * and end (exclude), {@code false} otherwise.
     * 
     * @param value        the value
     * @param beginInclude the begin number (include)
     * @param endExclude   the end number (exclude)
     * @return {@code true} if the specified value is between with begin (include)
     *         and end (exclude), {@code false} otherwise
     */
    public static final boolean inRange(int value, int beginInclude, int endExclude) {
        return value >= beginInclude && value < endExclude;
    }

    /**
     * Returns {@code true} if the specified value is between with begin (include)
     * and end (include), {@code false} otherwise.
     * 
     * @param value the value
     * @param begin the begin number (include)
     * @param end   the end number (include)
     * @return {@code true} if the specified value is between with begin (include)
     *         and end (include), {@code false} otherwise
     */
    public static final boolean inRangeInclude(int value, int begin, int end) {
        return value >= begin && value <= end;
    }

    /**
     * Returns {@code true} if the specified value is between with begin (exclude)
     * and end (exclude), {@code false} otherwise.
     * 
     * @param value the value
     * @param begin the begin number (exclude)
     * @param end   the end number (exclude)
     * @return {@code true} if the specified value is between with begin (exclude)
     *         and end (exclude), {@code false} otherwise
     */
    public static final boolean inRangeExclude(int value, int begin, int end) {
        return value > begin && value < end;
    }

    /**
     * Returns {@code false} if the specified value is between with begin (include)
     * and end (exclude), {@code in} otherwise.
     * 
     * @param value        the value
     * @param beginInclude the begin number (include)
     * @param endExclude   the end number (exclude)
     * @return {@code false} if the specified value is between with begin (include)
     *         and end (exclude), {@code true} otherwise
     */
    public static final boolean notInRange(int value, int beginInclude, int endExclude) {
        return value < beginInclude || value >= endExclude;
    }

    /**
     * Returns {@code false} if the specified value is between with begin (include)
     * and end (include), {@code in} otherwise.
     * 
     * @param value the value
     * @param begin the begin number (include)
     * @param end   the end number (include)
     * @return {@code false} if the specified value is between with begin (include)
     *         and end (include), {@code true} otherwise
     */
    public static final boolean notInRangeInclude(int value, int begin, int end) {
        return value < begin || value > end;
    }

    /**
     * Returns {@code false} if the specified value is between with begin (exclude)
     * and end (exclude), {@code in} otherwise.
     * 
     * @param value the value
     * @param begin the begin number (exclude)
     * @param end   the end number (exclude)
     * @return {@code false} if the specified value is between with begin (exclude)
     *         and end (exclude), {@code true} otherwise
     */
    public static final boolean notInRangeExclude(int value, int begin, int end) {
        return value <= begin || value >= end;
    }

    /**
     * Returns the sum of the given values.
     * 
     * @param values the values
     * @return the sum of the given values
     */
    public static final int sum(int... values) {
        var sum = 0;
        for (int v : values) {
            sum += v;
        }
        return sum;
    }

    /**
     * Returns the sum of the given values.
     * 
     * @param values the values
     * @return the sum of the given values
     */
    public static final long sum(long... values) {
        var sum = 0L;
        for (long v : values) {
            sum += v;
        }
        return sum;
    }

    private NumberUtil() {
    }

}
