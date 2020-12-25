package com.github.fmjsjx.libcommon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberUtilTest {

    @Test
    public void testIntValue() {
        try {
            Long v = 123L;
            assertEquals(123, NumberUtil.intValue(v));
            v = null;
            assertEquals(0, NumberUtil.intValue(v));
            assertEquals(10, NumberUtil.intValue(v, 10));
        } catch (Exception e) {
            fail(e);
        }
    }

}
