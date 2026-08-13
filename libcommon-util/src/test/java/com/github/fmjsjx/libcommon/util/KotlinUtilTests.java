package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KotlinUtilTests {

    @Test
    public void testIsKotlinPresent() {
        try {
            assertTrue(KotlinUtil.isKotlinPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testIsKotlinReflectPresent() {
        try {
            assertTrue(KotlinUtil.isKotlinReflectPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

}
