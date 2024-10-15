package com.github.fmjsjx.libcommon.util;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for octal conversions.
 *
 * @author MJ Fang
 * @since 3.9
 */
public class OctalUtil {

    /**
     * Converts the byte array to octal string
     *
     * @param value the byte array
     * @return the octal string
     */
    public static String toOctalString(byte[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 0) {
            return "";
        }
        return new String(toOctalStringBytes0(value), StandardCharsets.US_ASCII);
    }

    private static byte[] toOctalStringBytes0(byte[] src) {
        var offset = src.length % 3;
        if (offset == 0) {
            offset = 3;
        }
        var prefix = switch (offset) { // cases: 1,2,3
            case 1 -> src[0] & 0xff;
            case 2 -> ((src[0] & 0xff) << 8) | (src[1] & 0xff);
            default -> ((src[0] & 0xff) << 16) | ((src[1] & 0xff) << 8) | (src[2] & 0xff);
        };
        var len = ((src.length - offset) / 3) << 3; // x << 3 <=> x * 8
        int prefixLen = calculateOctalLength(prefix);
        len += prefixLen;
        var digits = HexUtil.DigitsHolder.digits;
        var dest = new byte[len];
        for (var i = 0; i < prefixLen; i++) {
            dest[prefixLen - i - 1] = digits[((prefix >>> (i * 3)) & 0b111)];
        }
        for (var i = offset; i < src.length; i += 3) {
            var b0 = src[i];
            var b1 = src[i + 1];
            var b2 = src[i + 2];
            var base = prefixLen + (((i - offset) / 3) << 3); // x << 3 <=> x * 8
            dest[base] = digits[(b0 >>> 5) & 0b111];
            dest[base + 1] = digits[(b0 >>> 2) & 0b111];
            dest[base + 2] = digits[((b0 & 0b11) << 1) | ((b1 >>> 7) & 0b1)];
            dest[base + 3] = digits[(b1 >>> 4) & 0b111];
            dest[base + 4] = digits[(b1 >>> 1) & 0b111];
            dest[base + 5] = digits[((b1 & 0b1) << 2) | ((b2 >>> 6) & 0b11)];
            dest[base + 6] = digits[(b2 >>> 3) & 0b111];
            dest[base + 7] = digits[b2 & 0b111];
        }
        return dest;
    }

    private static int calculateOctalLength(int prefix) {
        int prefixLen;
        if (prefix < 8) {
            prefixLen = 1;
        } else if (prefix < 8 << 3) {
            prefixLen = 2;
        } else if (prefix < 8 << 6) {
            prefixLen = 3;
        } else if (prefix < 8 << 9) {
            prefixLen = 4;
        } else if (prefix < 8 << 12) {
            prefixLen = 5;
        } else if (prefix < 8 << 15) {
            prefixLen = 6;
        } else if (prefix < 8 << 18) {
            prefixLen = 7;
        } else {
            prefixLen = 8;
        }
        return prefixLen;
    }

    /**
     * Converts the byte array to octal string bytes
     *
     * @param src the source byte array
     * @return the octal string byte array
     */
    public static byte[] toOctalStringBytes(byte[] src) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return HexUtil.EmptyBytesHolder.EMPTY_BYTES;
        }
        return toOctalStringBytes0(src);
    }

    /**
     * Parses the octal string argument to byte array.
     *
     * @param value the octal string value
     * @return the byte array value represented by the argument in octal
     */
    public static byte[] parseToBytes(String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return HexUtil.EmptyBytesHolder.EMPTY_BYTES;
        }
        return parseToBytes0(value.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] parseToBytes0(byte[] src) {
        var len = (src.length >>> 3) * 3;  // x >>> 3 <=> x / 8
        var offset = src.length % 8;
        var prefix = 0;
        if (offset > 0) {
            for (var i = 1; i <= offset; i++) {
                prefix <<= 3;
                prefix |= parseToByte(src[i - 1]);
            }
        }
        var prefixLen = 0;
        if (prefix > 0) {
            if (prefix <= 0xff) {
                prefixLen = 1;
            } else if (prefix <= 0xffff) {
                prefixLen = 2;
            } else {
                prefixLen = 3;
            }
        }
        len += prefixLen;
        var dest = new byte[len];
        if (prefixLen > 0) {
            for (var i = 0; i < prefixLen; i++) {
                dest[prefixLen - i - 1] = (byte) ((prefix >>> (i * 8)) & 0xff);
            }
        }
        for (var i = offset; i < src.length; i += 8) {
            var b0 = parseToByte(src[i]);
            var b1 = parseToByte(src[i + 1]);
            var b2 = parseToByte(src[i + 2]);
            var b3 = parseToByte(src[i + 3]);
            var b4 = parseToByte(src[i + 4]);
            var b5 = parseToByte(src[i + 5]);
            var b6 = parseToByte(src[i + 6]);
            var b7 = parseToByte(src[i + 7]);
            var base = prefixLen + (((i - offset) >>> 3) * 3); // x >>> 3 <=> x / 8
            dest[base] = (byte) ((b0 << 5) | (b1 << 2) | (b2 >>> 1));
            dest[base + 1] = (byte) (((b2 & 0b1) << 7) | (b3 << 4) | (b4 << 1) | (b5 >>> 2));
            dest[base + 2] = (byte) (((b5 & 0b11) << 6) | (b6 << 3) | b7);
        }
        return dest;
    }

    private static byte parseToByte(byte src) {
        if (src >= '0' && src <= '7') {
            return (byte) (src - '0');
        }
        throw new IllegalArgumentException("Illegal octal char: " + (char) src + " (" + src + ")");
    }

    /**
     * Parses the octal string byte array argument to byte array.
     *
     * @param src the octal string byte array value
     * @return the byte array value represented by the argument in octal
     */
    public static byte[] parseToBytes(byte[] src) {
        if (src == null) {
            return null;
        }
        if (src.length == 0) {
            return HexUtil.EmptyBytesHolder.EMPTY_BYTES;
        }
        return parseToBytes0(src);
    }

    private OctalUtil() {
    }

}
