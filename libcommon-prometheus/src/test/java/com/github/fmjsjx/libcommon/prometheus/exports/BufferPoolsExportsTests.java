package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BufferPoolsExportsTests {

    private final BufferPoolsExports exports = new BufferPoolsExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        var mfs = exports.collect();
        assertNotNull(mfs);
        assertEquals(3, mfs.size());
        var metricFamilySamples = mfs.get(0);
        assertEquals("jvm_buffer_pool_used_bytes", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals("Used bytes of a given JVM buffer pool.", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        var samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertAll(samples.stream().map(sample -> () -> {
            assertEquals(2, sample.labelNames.size());
            assertEquals("application", sample.labelNames.get(0));
            assertEquals("test", sample.labelValues.get(0));
            assertEquals("pool", sample.labelNames.get(1));
        }));

        metricFamilySamples = mfs.get(1);
        assertEquals("jvm_buffer_pool_capacity_bytes", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals("Bytes capacity of a given JVM buffer pool.", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertAll(samples.stream().map(sample -> () -> {
            assertEquals(2, sample.labelNames.size());
            assertEquals("application", sample.labelNames.get(0));
            assertEquals("test", sample.labelValues.get(0));
            assertEquals("pool", sample.labelNames.get(1));
        }));

        metricFamilySamples = mfs.get(2);
        assertEquals("jvm_buffer_pool_used_buffers", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals("Used buffers of a given JVM buffer pool.", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertAll(samples.stream().map(sample -> () -> {
            assertEquals(2, sample.labelNames.size());
            assertEquals("application", sample.labelNames.get(0));
            assertEquals("test", sample.labelValues.get(0));
            assertEquals("pool", sample.labelNames.get(1));
        }));
    }

}
