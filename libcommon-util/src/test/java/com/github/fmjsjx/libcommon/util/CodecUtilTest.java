package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class CodecUtilTest {

    @Test
    public void testIntToBytes() {
        var b = CodecUtil.intToBytes(12345678);
        var buf = ByteBuffer.allocate(4);
        buf.putInt(12345678);
        assertArrayEquals(buf.array(), b);

        CodecUtil.intToBytes(87654321, b);
        buf.clear();
        buf.putInt(87654321);
        assertArrayEquals(buf.array(), b);
    }

    @Test
    public void testBytesToInt() {
        var b = new byte[] { 5, 57, 127, -79 };
        var v = CodecUtil.bytesToInt(b);
        assertEquals(ByteBuffer.wrap(b).getInt(), v);
    }

    @Test
    public void testLongToBytes() {
        var b = CodecUtil.longToBytes(1234567890123456L);
        var buf = ByteBuffer.allocate(8);
        buf.putLong(1234567890123456L);
        assertArrayEquals(buf.array(), b);

        CodecUtil.longToBytes(1234567890123456L, b);
        buf.clear();
        buf.putLong(1234567890123456L);
        assertArrayEquals(buf.array(), b);
    }

    @Test
    public void testBytesToLong() {
        var b = new byte[] { 0, 4, 98, -43, 60, -118, -70, -64 };
        var v = CodecUtil.bytesToLong(b);
        assertEquals(ByteBuffer.wrap(b).getLong(), v);
    }

}
