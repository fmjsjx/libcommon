package com.github.fmjsjx.libcommon.prometheus.exports;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;


public class MemoryPoolsExportsTests {

    private final MemoryPoolsExports exports = new MemoryPoolsExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        assertAll(exports.collect().stream().map(mfs ->
                () -> assertAll(mfs.samples.stream().map(sample -> () -> {
                    assertEquals("application", sample.labelNames.get(0));
                    assertEquals("test", sample.labelValues.get(0));
                }))
        ));
    }

    @Test
    public void testIllegalLabelName() {
        try {
            new MemoryPoolsExports(List.of("area"), List.of("test"));
            fail("Should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("MemoryPoolsExports cannot have a custom label name: area", e.getMessage());
        }
        try {
            new MemoryPoolsExports(List.of("pool"), List.of("test"));
            fail("Should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("MemoryPoolsExports cannot have a custom label name: pool", e.getMessage());
        }
    }
}
