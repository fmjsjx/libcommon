package com.github.fmjsjx.libcommon.util;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for hex conversions.
 *
 * @author MJ Fang
 * @since 3.9
 */
public final class HexUtil {

    static final class DigitsHolder {
        static final byte[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    }

    static final class EmptyBytesHolder {
        static final byte[] EMPTY_BYTES = new byte[0];
    }

    /**
     * Converts the byte array to hex string
     *
     * @param value the byte array
     * @return the hex string
     */
    public static String toHexString(byte[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 0) {
            return "";
        }
        return new String(toHexStringBytes0(value), StandardCharsets.US_ASCII);
    }

    private static byte[] toHexStringBytes0(byte[] src) {
        var dest = new byte[src.length << 1]; // x << 1 <=> x * 2
        toHexStringBytes0(src, dest, 0);
        return dest;
    }

    private static void toHexStringBytes0(byte[] src, byte[] dest, int offset) {
        var digits = DigitsHolder.digits;
        for (var i = 0; i < src.length; i++) {
            var b = src[i];
            var index = offset + (i << 1); // x << 1 <=> x * 2
            dest[index] = digits[(b >>> 4) & 0xf];
            dest[index + 1] = digits[b & 0xf];
        }
    }

    /**
     * Converts tht byte array to hex string bytes.
     *
     * @param src the source byte array
     * @return the hex string byte array
     */
    public static byte[] toHexStringBytes(byte[] src) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return src;
        }
        return toHexStringBytes0(src);
    }

    /**
     * Converts the byte array to hex string.
     *
     * @param src  the source value
     * @param dest the destination byte array
     */
    public static void toHexStringBytes(byte[] src, byte[] dest) {
        toHexStringBytes(src, dest, 0);
    }

    /**
     * Converts the byte array to hex string.
     *
     * @param src    the source value
     * @param dest   the destination byte array
     * @param offset the offset in the destination byte array to start at
     */
    public static void toHexStringBytes(byte[] src, byte[] dest, int offset) {
        var remaining = dest.length - offset;
        var need = src.length << 1;
        if (remaining < need) {
            throw new ArrayIndexOutOfBoundsException("remaining length must >= " + need + " but was " + remaining);
        }
        toHexStringBytes0(src, dest, offset);
    }

    /**
     * Parses the hex string argument as a signed decimal integer.
     *
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the integer value represented by the argument in hex
     */
    public static int parseToInt(String value, int defaultValue) {
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value, 16);
    }

    /**
     * Parses the hex string argument as a signed decimal {@code long}.
     *
     * @param value        the string value
     * @param defaultValue the default value if the string is null or empty
     * @return the {@code long} represented by the argument in hex
     */
    public static long parseToLong(String value, long defaultValue) {
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        return Long.parseLong(value, 16);
    }

    /**
     * Parses the hex string argument to byte array.
     *
     * @param value the hex string value
     * @return the byte array value represented by the argument in hex
     */
    public static byte[] parseToBytes(String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return EmptyBytesHolder.EMPTY_BYTES;
        }
        return parseToBytes0(value.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] parseToBytes0(byte[] src) {
        var len = (src.length >>> 1) + (src.length & 1);
        var dest = new byte[len];
        if ((src.length & 1) == 1) {
            dest[0] = parseToByte(src[0]);
            for (var i = 1; i < len; i++) {
                var index = i << 1;
                dest[i] = (byte) ((parseToByte(src[index - 1]) << 4) | parseToByte(src[index]));
            }
        } else {
            dest[0] = (byte) ((parseToByte(src[0]) << 4) | parseToByte(src[1]));
            for (var i = 1; i < len; i++) {
                var index = i << 1;
                dest[i] = (byte) ((parseToByte(src[index]) << 4) | parseToByte(src[index + 1]));
            }
        }
        return dest;
    }

    private static byte parseToByte(byte src) {
        if (src >= '0' && src <= '9') {
            return (byte) (src - '0');
        } else if (src >= 'a' && src <= 'f') {
            return (byte) (src - 'a' + 10);
        } else if (src >= 'A' && src <= 'F') {
            return (byte) (src - 'A' + 10);
        }
        throw new IllegalArgumentException("Illegal hex char: " + (char) src + " (" + src + ")");
    }

    /**
     * Parses the hex string byte array argument to byte array.
     *
     * @param src the hex string byte array value
     * @return the byte array value represented by the argument in hex
     */
    public static byte[] parseToBytes(byte[] src) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return EmptyBytesHolder.EMPTY_BYTES;
        }
        return parseToBytes0(src);
    }

    private HexUtil() {
    }

}
