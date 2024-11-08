package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class Base64UtilTests {

    @Test
    public void testEncoder() throws Exception {
        assertEquals(Base64.getEncoder(), Base64Util.encoder(false, false));
        assertEquals(Base64.getUrlEncoder(), Base64Util.encoder(true, false));
        var encoder = Base64Util.encoder(false, true);
        var field = encoder.getClass().getDeclaredField("isURL");
        field.setAccessible(true);
        assertFalse(field.getBoolean(encoder));
        field = encoder.getClass().getDeclaredField("doPadding");
        field.setAccessible(true);
        assertFalse(field.getBoolean(encoder));
        encoder = Base64Util.encoder(true, true);
        field = encoder.getClass().getDeclaredField("isURL");
        field.setAccessible(true);
        assertTrue(field.getBoolean(encoder));
        field = encoder.getClass().getDeclaredField("doPadding");
        field.setAccessible(true);
        assertFalse(field.getBoolean(encoder));
    }

}
