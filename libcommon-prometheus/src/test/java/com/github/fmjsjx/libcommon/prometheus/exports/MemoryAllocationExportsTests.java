package com.github.fmjsjx.libcommon.prometheus.exports;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryAllocationExportsTests {

    @Test
    public void testIllegalLabelName() {
        try {
            new MemoryAllocationExports(List.of("pool"), List.of("test"));
            fail("Should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("MemoryAllocationExports cannot have a custom label name: pool", e.getMessage());
        }
    }

}
