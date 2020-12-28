package com.github.fmjsjx.libcommon.util;

import java.nio.charset.StandardCharsets;
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
        String[] strings = value.split(regex);
        int[] values = new int[strings.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(strings[i]);
        }
        return values;
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
        String[] strings = value.split(regex);
        long[] values = new long[strings.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Long.parseLong(strings[i]);
        }
        return values;
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

    private StringUtil() {
    }

}
