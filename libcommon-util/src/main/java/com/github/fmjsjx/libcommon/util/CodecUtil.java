package com.github.fmjsjx.libcommon.util;

/**
 * Utility class for Encode/Decode.
 */
public class CodecUtil {

    /**
     * Encode int to byte array.
     * 
     * @param value the int value
     * @return a byte array
     */
    public static final byte[] intToBytes(int value) {
        var b = new byte[4];
        b[0] = (byte) (value >>> 24);
        b[1] = (byte) (value >>> 16);
        b[2] = (byte) (value >>> 8);
        b[3] = (byte) value;
        return b;
    }

    /**
     * Encode int to byte array.
     * 
     * @param value the int value
     * @param dest  the byte array write to
     */
    public static final void intToBytes(int value, byte[] dest) {
        intToBytes(value, dest, 0);
    }

    /**
     * Encode int to byte array.
     * 
     * @param value  the int value
     * @param dest   the byte array write to
     * @param offset the offset in the destination byte array to start at
     */
    public static final void intToBytes(int value, byte[] dest, int offset) {
        var remaining = dest.length - offset;
        if (remaining < 4) {
            throw new ArrayIndexOutOfBoundsException("remaining length must >= 4 but was " + remaining);
        }
        dest[offset] = (byte) (value >>> 24);
        dest[offset + 1] = (byte) (value >>> 16);
        dest[offset + 2] = (byte) (value >>> 8);
        dest[offset + 3] = (byte) value;
    }

    /**
     * Decode byte array to int value.
     * 
     * @param src the source value
     * @return an int
     */
    public static final int bytesToInt(byte[] src) {
        return bytesToInt(src, 0);
    }

    /**
     * Decode byte array to int value.
     * 
     * @param src    the source value
     * @param offset the offset in the source byte array to start at
     * @return an int
     */
    public static final int bytesToInt(byte[] src, int offset) {
        var remaining = src.length - offset;
        if (remaining < 4) {
            throw new ArrayIndexOutOfBoundsException("remaining length must >= 4 but was " + remaining);
        }
        return ((src[offset] & 0xff) << 24) | ((src[offset + 1] & 0xff) << 16) | ((src[offset + 2] & 0xff) << 8)
                | (src[offset + 3] & 0xff);
    }

    /**
     * Encode long to byte array.
     * 
     * @param value the long value
     * @return a byte array
     */
    public static final byte[] longToBytes(long value) {
        var b = new byte[8];
        b[0] = (byte) (value >>> 56);
        b[1] = (byte) (value >>> 48);
        b[2] = (byte) (value >>> 40);
        b[3] = (byte) (value >>> 32);
        b[4] = (byte) (value >>> 24);
        b[5] = (byte) (value >>> 16);
        b[6] = (byte) (value >>> 8);
        b[7] = (byte) value;
        return b;
    }

    /**
     * Encode long to byte array.
     * 
     * @param value the long value
     * @param dest  the byte array write to
     */
    public static final void longToBytes(long value, byte[] dest) {
        longToBytes(value, dest, 0);
    }

    /**
     * Encode long to byte array.
     * 
     * @param value  the long value
     * @param dest   the byte array write to
     * @param offset the offset in the destination byte array to start at
     */
    public static final void longToBytes(long value, byte[] dest, int offset) {
        var remaining = dest.length - offset;
        if (remaining < 8) {
            throw new ArrayIndexOutOfBoundsException("remaining length must >= 8 but was " + remaining);
        }
        dest[offset] = (byte) (value >>> 56);
        dest[offset + 1] = (byte) (value >>> 48);
        dest[offset + 2] = (byte) (value >>> 40);
        dest[offset + 3] = (byte) (value >>> 32);
        dest[offset + 4] = (byte) (value >>> 24);
        dest[offset + 5] = (byte) (value >>> 16);
        dest[offset + 6] = (byte) (value >>> 8);
        dest[offset + 7] = (byte) value;
    }

    /**
     * Decode byte array to long value.
     * 
     * @param src the source value
     * @return an long
     */
    public static final long bytesToLong(byte[] src) {
        return bytesToLong(src, 0);
    }

    /**
     * Decode byte array to long value.
     * 
     * @param src    the source value
     * @param offset the offset in the source byte array to start at
     * @return an long
     */
    public static final long bytesToLong(byte[] src, int offset) {
        var remaining = src.length - offset;
        if (remaining < 8) {
            throw new ArrayIndexOutOfBoundsException("remaining length must >= 8 but was " + remaining);
        }
        return ((src[offset] & 0xffL) << 56) | ((src[offset + 1] & 0xffL) << 48) | ((src[offset + 2] & 0xffL) << 40)
                | ((src[offset + 3] & 0xffL) << 32) | ((src[offset + 4] & 0xffL) << 24)
                | ((src[offset + 5] & 0xff) << 16) | ((src[offset + 6] & 0xff) << 8) | (src[offset + 7] & 0xff);
    }

    private CodecUtil() {
    }
}
