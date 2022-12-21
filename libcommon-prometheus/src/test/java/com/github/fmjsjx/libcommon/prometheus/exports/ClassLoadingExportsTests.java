package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassLoadingExportsTests {

    private final ClassLoadingExports exports = new ClassLoadingExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        var mfs = exports.collect();
        assertNotNull(mfs);
        assertEquals(3, mfs.size());
        var metricFamilySamples = mfs.get(0);
        assertEquals("jvm_classes_currently_loaded", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals("The number of classes that are currently loaded in the JVM", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        var samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        var sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(1);
        assertEquals("jvm_classes_loaded", metricFamilySamples.name);
        assertEquals(Collector.Type.COUNTER, metricFamilySamples.type);
        assertEquals(
                "The total number of classes that have been loaded since the JVM has started execution",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(2);
        assertEquals("jvm_classes_unloaded", metricFamilySamples.name);
        assertEquals(Collector.Type.COUNTER, metricFamilySamples.type);
        assertEquals(
                "The total number of classes that have been unloaded since the JVM has started execution",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));
    }

}
