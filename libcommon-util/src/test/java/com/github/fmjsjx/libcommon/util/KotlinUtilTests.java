package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KotlinUtilTests {

    @Test
    public void testIsKotlinPresent() {
        try {
            assertFalse(KotlinUtil.isKotlinPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testIsKotlinReflectPresent() {
        try {
            assertFalse(KotlinUtil.isKotlinReflectPresent());
        } catch (Exception e) {
            fail(e);
        }
    }

}
