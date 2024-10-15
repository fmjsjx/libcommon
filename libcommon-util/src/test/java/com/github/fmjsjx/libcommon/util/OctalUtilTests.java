package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static com.github.fmjsjx.libcommon.util.HexUtilTests.randomBytes;
import static com.github.fmjsjx.libcommon.util.HexUtilTests.toHex;
import static org.junit.jupiter.api.Assertions.*;

public class OctalUtilTests {

    @Test
    public void testToOctalString() {
        assertNull(OctalUtil.toOctalString(null));
        assertEquals("", OctalUtil.toOctalString(new byte[0]));
        var value = randomBytes();
        var expected = new BigInteger(toHex(value), 16).toString(8);
        assertEquals(expected, OctalUtil.toOctalString(value));
        var value1 = randomBytes(12);
        value1[0] = 31;
        var expected1 = new BigInteger(toHex(value1), 16).toString(8);
        assertEquals(expected1, OctalUtil.toOctalString(value1));
    }

    @Test
    public void testToOctalStringBytes() {
        assertNull(OctalUtil.toOctalStringBytes(null));
        assertArrayEquals(new byte[0], OctalUtil.toOctalStringBytes(new byte[0]));
        var value = randomBytes();
        var expected = new BigInteger(toHex(value), 16).toString(8).getBytes();
        assertArrayEquals(expected, OctalUtil.toOctalStringBytes(value));
    }

    @Test
    public void testParseToBytes() {
        assertNull(OctalUtil.parseToBytes((String) null));
        assertNull(OctalUtil.parseToBytes((byte[]) null));
        assertArrayEquals(new byte[0], OctalUtil.parseToBytes(""));
        assertArrayEquals(new byte[0], OctalUtil.parseToBytes(new byte[0]));
        var expected = randomBytes(12);
        var value = new BigInteger(toHex(expected), 16).toString(8);
        assertArrayEquals(expected, OctalUtil.parseToBytes(value));
        assertArrayEquals(expected, OctalUtil.parseToBytes(value.getBytes()));
        var expected1 = randomBytes(13);
        var value1 = new BigInteger(toHex(expected1), 16).toString(8);
        assertArrayEquals(expected1, OctalUtil.parseToBytes(value1));
        assertArrayEquals(expected1, OctalUtil.parseToBytes(value1.getBytes()));
        var expected2 = randomBytes(14);
        var value2 = new BigInteger(toHex(expected2), 16).toString(8);
        assertArrayEquals(expected2, OctalUtil.parseToBytes(value2));
        assertArrayEquals(expected2, OctalUtil.parseToBytes(value2.getBytes()));
        var expected3 = randomBytes(15);
        var value3 = new BigInteger(toHex(expected3), 16).toString(8);
        assertArrayEquals(expected3, OctalUtil.parseToBytes(value3));
        assertArrayEquals(expected3, OctalUtil.parseToBytes(value3.getBytes()));
        var expected4 = randomBytes(16);
        var value4 = new BigInteger(toHex(expected4), 16).toString(8);
        assertArrayEquals(expected4, OctalUtil.parseToBytes(value4));
        assertArrayEquals(expected4, OctalUtil.parseToBytes(value4.getBytes()));
        try {
            OctalUtil.parseToBytes("g");
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal octal char: g (" + (int) 'g' + ")", e.getMessage());
        }
        try {
            OctalUtil.parseToBytes("G".getBytes());
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal octal char: G (" + (int) 'G' + ")", e.getMessage());
        }
    }

}
