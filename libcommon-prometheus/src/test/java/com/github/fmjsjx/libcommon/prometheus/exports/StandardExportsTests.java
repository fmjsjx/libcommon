package com.github.fmjsjx.libcommon.prometheus.exports;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardExportsTests {

    private final StandardExports exports = new StandardExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        assertAll(exports.collect().stream().map(mfs ->
                () -> assertAll(mfs.samples.stream().map(sample -> () -> {
                    assertEquals("application", sample.labelNames.get(0));
                    assertEquals("test", sample.labelValues.get(0));
                }))
        ));
    }

}
