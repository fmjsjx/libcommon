package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.zip.CRC32;
import java.util.zip.CRC32C;

import org.junit.jupiter.api.Test;

public class ChecksumUtilTest {

    @Test
    public void testCrc32() {
        var util = ChecksumUtil.wrappedCrc32();
        CRC32 crc32 = new CRC32();

        crc32.update("This is a test text string!!!".getBytes());
        assertEquals(crc32.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));

        try {
            util.calculateValue((byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.calculateValue("ignored string".getBytes(), (byte[]) null, "ignored string".getBytes());
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.calculateValue("ignored string".getBytes(), "ignored string".getBytes(), (byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }
        crc32.update("This is a test text string!!!".getBytes());
        assertEquals(crc32.getValue(), ChecksumUtil.crc32("This is a test text string!!!".getBytes(),
                "This is a test text string!!!".getBytes()));

    }

    @Test
    public void testCrc32c() {
        var util = ChecksumUtil.wrappedCrc32c();
        CRC32C crc32c = new CRC32C();

        crc32c.update("This is a test text string!!!".getBytes());
        assertEquals(crc32c.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));

        try {
            util.calculateValue((byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.calculateValue("ignored string".getBytes(), (byte[]) null, "ignored string".getBytes());
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.calculateValue("ignored string".getBytes(), "ignored string".getBytes(), (byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.calculateValue("This is a test text string!!!".getBytes()));
        }
        crc32c.update("This is a test text string!!!".getBytes());
        assertEquals(crc32c.getValue(), ChecksumUtil.crc32c("This is a test text string!!!".getBytes(),
                "This is a test text string!!!".getBytes()));

    }

}
