package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class HexUtilTests {

    static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    static byte[] randomBytes() {
        return randomBytes(16);
    }

    static byte[] randomBytes(int len) {
        SecureRandom ng = Holder.numberGenerator;
        byte[] randomBytes = new byte[len];
        while (randomBytes[0] == 0) {
            ng.nextBytes(randomBytes);
        }
        return randomBytes;
    }

    static String toHex(byte[] value) {
        var strings = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            strings[i] = String.format("%02x", value[i]);
        }
        return String.join("", strings);
    }

    @Test
    public void testToHexString() {
        assertNull(HexUtil.toHexString(null));
        assertEquals("", HexUtil.toHexString(new byte[0]));
        var value = randomBytes();
        var expected = toHex(value);
        assertEquals(expected, HexUtil.toHexString(value));
    }

    @Test
    public void testToHexStringBytes() {
        assertNull(HexUtil.toHexStringBytes(null));
        assertArrayEquals(new byte[0], HexUtil.toHexStringBytes(new byte[0]));
        var value = randomBytes();
        var expected = toHex(value).getBytes();
        assertArrayEquals(expected, HexUtil.toHexStringBytes(value));
        var dest0 = new byte[expected.length + 6];
        HexUtil.toHexStringBytes(value, dest0);
        assertArrayEquals(Arrays.copyOf(expected, dest0.length), dest0);
        var dest1 = new byte[expected.length + 6];
        HexUtil.toHexStringBytes(value, dest1, 6);
        var expected1 = new byte[expected.length + 6];
        System.arraycopy(expected, 0, expected1, 6, expected.length);
        assertArrayEquals(expected1, dest1);
        var dest2 = new byte[expected.length];
        try {
            HexUtil.toHexStringBytes(value, dest2, 1);
            fail("should throw exception");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals("remaining length must >= " + expected.length + " but was " + (expected.length - 1), e.getMessage());
        }
    }

    @Test
    public void testParseToInt() {
        assertEquals(0, HexUtil.parseToInt(null, 0));
        assertEquals(0, HexUtil.parseToInt("", 0));
        assertEquals(123, HexUtil.parseToInt(Integer.toHexString(123), 0));
        assertEquals(987654321, HexUtil.parseToInt(Integer.toHexString(987654321), 0));
    }

    @Test
    public void testParseToLong() {
        assertEquals(0, HexUtil.parseToLong(null, 0));
        assertEquals(0, HexUtil.parseToLong("", 0));
        assertEquals(1234567890123L, HexUtil.parseToLong(Long.toHexString(1234567890123L), 0));
        assertEquals(987654321098765L, HexUtil.parseToLong(Long.toHexString(987654321098765L), 0));
    }

    @Test
    public void testParseToBytes() {
        assertNull(HexUtil.parseToBytes((String) null));
        assertNull(HexUtil.parseToBytes((byte[]) null));
        assertArrayEquals(new byte[0], HexUtil.parseToBytes(""));
        assertArrayEquals(new byte[0], HexUtil.parseToBytes(new byte[0]));
        var expected = randomBytes();
        var value = toHex(expected);
        assertArrayEquals(expected, HexUtil.parseToBytes(value));
        assertArrayEquals(expected, HexUtil.parseToBytes(value.getBytes()));
        try {
            HexUtil.parseToBytes("g");
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal hex char: g (" + (int) 'g' + ")", e.getMessage());
        }
        try {
            HexUtil.parseToBytes("G".getBytes());
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal hex char: G (" + (int) 'G' + ")", e.getMessage());
        }
    }

}
