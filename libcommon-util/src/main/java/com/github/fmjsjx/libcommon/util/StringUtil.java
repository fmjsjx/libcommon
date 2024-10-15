package com.github.fmjsjx.libcommon.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for strings.
 */
public class StringUtil {

    /**
     * Returns {@code true} if the string is null, empty or contains only
     * {@link Character#isWhitespace(int) white space} codepoints, otherwise
     * {@code false}.
     * 
     * @param value the string
     * @return {@code true} if the string is null, empty or contains only
     *         {@link Character#isWhitespace(int) white space} codepoints, otherwise
     *         {@code false}
     */
    public static final boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Returns {@code true} if the string is not null, empty or contains only
     * {@link Character#isWhitespace(int) white space} codepoints, otherwise
     * {@code false}.
     * <p>
     * This method is equivalent to {@link #isBlank(String) !isBlank(value)}.
     * 
     * @param value the string
     * @return {@code true} if the string is not null, empty or contains only
     *         {@link Character#isWhitespace(int) white space} codepoints, otherwise
     *         {@code false}.
     */
    public static final boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /**
     * Returns {@code true} if the string is null or {@link String#length()
     * value.length()} is {@code 0}, otherwise {@code false}..
     * 
     * @param value the string
     * @return {@code true} if the string is null or {@link String#length()
     *         value.length()} is {@code 0}, otherwise {@code false}..
     */
    public static final boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Returns {@code true} if the string is not null or {@link String#length()
     * value.length()} is not {@code 0}, otherwise {@code false}..
     * 
     * @param value the string
     * @return {@code true} if the string is not null or {@link String#length()
     *         value.length()} is not {@code 0}, otherwise {@code false}..
     */
    public static final boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Returns {@code true} if the string contains only Unicode digits, otherwise
     * {@code false}.
     * 
     * <p>
     * A decimal point is not a Unicode digit and returns {@code false}.
     * <p>
     * {@code null} will return {@code false}. An empty CharSequence (length()=0)
     * will return {@code false}.
     * 
     * @param value the string
     * @return {@code true} if the string contains only Unicode digits, otherwise
     *         {@code false}
     */
    public static final boolean isNumberic(String value) {
        if (isEmpty(value)) {
            return false;
        }
        return value.chars().allMatch(NumbericFilters.IS_NUMBERIC);
    }

    private static final class NumbericFilters {
        private static final IntPredicate IS_NUMBERIC = i -> i >= '0' && i <= '9';
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values as {@code int} type.
     * 
     * @param value the string
     * @param regex the delimiting regular expression
     * @return an {@code int} array
     */
    public static final int[] splitInt(String value, String regex) {
        return Arrays.stream(value.split(regex)).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values as {@code long} type.
     * 
     * @param value the string
     * @param regex the delimiting regular expression
     * @return a {@code long} array
     */
    public static final long[] splitLong(String value, String regex) {
        return Arrays.stream(value.split(regex)).mapToLong(Long::parseLong).toArray();
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values using the specified {@code mapper}.
     * 
     * @param value  the string
     * @param regex  the delimiting regular expression
     * @param mapper a function to apply to each element
     * @return an {@code Object} array
     * @since 2.2
     */
    public static final Object[] split(String value, String regex, Function<String, Object> mapper) {
        return Arrays.stream(value.split(regex)).map(mapper).toArray();
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values using the specified {@code mapper}.
     * 
     * @param <T>       the type of elements
     * @param value     the string
     * @param regex     the delimiting regular expression
     * @param mapper    a function to apply to each element
     * @param generator a function which produces a new array of the desired type
     *                  and the provided length
     * @return a {@code T[]}
     * @since 2.2
     */
    public static final <T> T[] split(String value, String regex, Function<String, T> mapper,
            IntFunction<T[]> generator) {
        return Arrays.stream(value.split(regex)).map(mapper).toArray(generator);
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values using the specified {@code mapper}.
     * 
     * @param <T>    the type of elements
     * @param value  the string
     * @param regex  the delimiting regular expression
     * @param mapper a function to apply to each element
     * @return a {@code List<T>}
     * @since 2.2
     */
    public static final <T> List<T> splitToList(String value, String regex, Function<String, T> mapper) {
        return Arrays.stream(value.split(regex)).map(mapper).collect(Collectors.toList());
    }

    /**
     * Splits the string around matches of the given {@code regular expression} and
     * converts the values using the specified {@code mapper}.
     * 
     * @param <T>               the type of elements
     * @param <C>               the type of the collection to be returned
     * @param value             the string
     * @param regex             the delimiting regular expression
     * @param mapper            a function to apply to each element
     * @param collectionFactory a supplier providing a new empty {@code Collection}
     *                          into which the results will be inserted
     * @return a {@code Collection<T>}
     * @since 2.2
     */
    public static final <T, C extends Collection<T>> C splitToCollection(String value, String regex,
            Function<String, T> mapper, Supplier<C> collectionFactory) {
        return Arrays.stream(value.split(regex)).map(mapper).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * Converts the byte array to hex string.
     * 
     * @param value the byte array
     * @return the hex string
     */
    public static final String toHexString(byte[] value) {
        return HexUtil.toHexString(value);
    }

    /**
     * Converts the byte array to hex string.
     * 
     * @param src  the source value
     * @param dest the destination byte array
     */
    public static final void toHexStringBytes(byte[] src, byte[] dest) {
        HexUtil.toHexStringBytes(src, dest);
    }

    /**
     * Converts the byte array to hex string.
     * 
     * @param src    the source value
     * @param dest   the destination byte array
     * @param offset the offset in the destination byte array to start at
     */
    public static final void toHexStringBytes(byte[] src, byte[] dest, int offset) {
        HexUtil.toHexStringBytes(src, dest, offset);
    }

    /**
     * Parses the string argument as a signed decimal integer in hex.
     * 
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the integer value represented by the argument in hex
     */
    public static final int parseIntHex(String value, int defaultValue) {
        return HexUtil.parseToInt(value, defaultValue);
    }

    /**
     * Parses the string argument as a signed decimal integer.
     * 
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the integer value represented by the argument in decimal
     */
    public static final int parseInt(String value, int defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * Parses the string argument as a signed decimal {@code long} in hex.
     * 
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the {@code long} represented by the argument in hex
     */
    public static final long parseLongHex(String value, long defaultValue) {
        return HexUtil.parseToLong(value, defaultValue);
    }

    /**
     * Parses the string argument as a signed decimal {@code long}.
     * 
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the {@code long} represented by the argument in decimal
     */
    public static final long parseLong(String value, long defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return Long.parseLong(value);
    }

    /**
     * Converts a string to a double precision floating point value.
     * 
     * @param value        the string to convert
     * @param defaultValue the default value if the string is null or empty
     * @return the {@code double} precision value
     */
    public static final double parseDouble(String value, double defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    /**
     * Parses the string argument as a {@code boolean}. The {@code boolean} returned
     * represents the value {@code true} if the string argument is not {@code null}
     * and is equal, ignoring case, to the string {@code "true"} or {@code "1"}.
     * Otherwise, a {@code false} value is returned, including for a {@code null}
     * argument.
     * 
     * <p>
     * Examples:
     * 
     * <pre>
     *     {@code StringUtil.parseBoolean("true")} returns {@code true}.
     *     {@code StringUtil.parseBoolean("1")} returns {@code true}.
     *     {@code StringUtil.parseBoolean("TrUe")} returns {@code true}.
     *     {@code StringUtil.parseBoolean("0")} returns {@code false}.
     *     {@code StringUtil.parseBoolean(null)} returns {@code false}.
     *     {@code StringUtil.parseBoolean("")} returns {@code false}.
     *     {@code StringUtil.parseBoolean("yes")} returns {@code false}.
     * </pre>
     * 
     * @param value the string containing the {@code boolean} representation to be
     *              parsed
     * @return the {@code boolean} represented by the string argument
     */
    public static final boolean parseBoolean(String value) {
        if (value == null) {
            return false;
        }
        if ("1".equals(value)) {
            return true;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    /**
     * Compares the string a to the specified string b.
     * 
     * @param a the first string
     * @param b the second string
     * @return {@code true} if a and b are both {@code null} or a equals b,
     *         {@code false} otherwise
     * @since 2.0
     */
    public static final boolean isEquals(String a, String b) {
        return ObjectUtil.isEquals(a, b);
    }

    /**
     * Compares the string a to the specified string b.
     * <p>
     * This method is equivalent to {@link #isEquals(String, String) !isEquals(a,
     * b)}.
     * 
     * @param a the first string
     * @param b the second string
     * @return {@code false} if a and b are both {@code null} or a equals b,
     *         {@code true} otherwise
     * @since 2.0
     */
    public static final boolean isNotEquals(String a, String b) {
        return !isEquals(a, b);
    }

    /**
     * Compares the string a to the specified string b, ignoring case
     * considerations.
     * 
     * @param a the first string
     * @param b the second string
     * @return {@code true} if a and b are both {@code null} or a equals b,
     *         {@code false} otherwise
     * @since 2.0
     */
    public static final boolean isEqualsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    private StringUtil() {
    }

}
