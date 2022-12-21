package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GarbageCollectorExportsTests {

    private final GarbageCollectorExports exports = new GarbageCollectorExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        var mfs = exports.collect();
        assertNotNull(mfs);
        assertEquals(1, mfs.size());
        var metricFamilySamples = mfs.get(0);
        assertEquals("jvm_gc_collection_seconds", metricFamilySamples.name);
        assertEquals(Collector.Type.SUMMARY, metricFamilySamples.type);
        assertEquals("Time spent in a given JVM garbage collector in seconds.", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        var samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertAll(samples.stream().map(sample -> () -> {
            assertEquals(2, sample.labelNames.size());
            assertEquals("application", sample.labelNames.get(0));
            assertEquals("test", sample.labelValues.get(0));
            assertEquals("gc", sample.labelNames.get(1));
        }));
    }

    @Test
    public void testIllegalLabelName() {
        try {
            new GarbageCollectorExports(List.of("gc"), List.of("test"));
            fail("Should throws IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("GarbageCollectorExports cannot have a custom label name: gc", e.getMessage());
        }
    }

}
