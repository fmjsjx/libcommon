package com.github.fmjsjx.libcommon.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.IntPredicate;

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
        return !isNotEmpty(value);
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
     * Converts the byte array to hex string.
     * 
     * @param value the byte array
     * @return the hex string
     */
    public static final String toHexString(byte[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 0) {
            return "";
        }
        return toHexString0(value);
    }

    private static final String toHexString0(byte[] value) {
        final byte[] hexBytes = HexBytesHolder.HEX_BYTES;
        byte[] hexValue = new byte[value.length << 1];
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            int index = i * 2;
            hexValue[index] = hexBytes[(b >>> 0x4) & 0xf];
            hexValue[index + 1] = hexBytes[b & 0xf];
        }
        return new String(hexValue, StandardCharsets.US_ASCII);
    }

    private static final class HexBytesHolder {
        private static final byte[] HEX_BYTES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
    }

    /**
     * Parses the string argument as a signed decimal integer in hex.
     * 
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the integer value represented by the argument in hex
     */
    public static final int parseIntHex(String value, int defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value, 16);
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
        if (isEmpty(value)) {
            return defaultValue;
        }
        return Long.parseLong(value);
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

    private StringUtil() {
    }

}
