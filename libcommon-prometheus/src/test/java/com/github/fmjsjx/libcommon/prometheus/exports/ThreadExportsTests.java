package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadExportsTests {

    private final ThreadExports exports = new ThreadExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        var mfs = exports.collect();
        assertNotNull(mfs);
        assertEquals(7, mfs.size());
        var metricFamilySamples = mfs.get(0);
        assertEquals("jvm_threads_current", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals("Current thread count of a JVM", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        var samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        var sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(1);
        assertEquals("jvm_threads_daemon", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals(
                "Daemon thread count of a JVM",
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
        assertEquals("jvm_threads_peak", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals(
                "Peak thread count of a JVM",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(3);
        assertEquals("jvm_threads_started", metricFamilySamples.name);
        assertEquals(Collector.Type.COUNTER, metricFamilySamples.type);
        assertEquals(
                "Started thread count of a JVM",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));


        metricFamilySamples = mfs.get(4);
        assertEquals("jvm_threads_deadlocked", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals(
                "Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(5);
        assertEquals("jvm_threads_deadlocked_monitor", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals(
                "Cycles of JVM-threads that are in deadlock waiting to acquire object monitors",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        sample = samples.get(0);
        assertEquals(1, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));

        metricFamilySamples = mfs.get(6);
        assertEquals("jvm_threads_state", metricFamilySamples.name);
        assertEquals(Collector.Type.GAUGE, metricFamilySamples.type);
        assertEquals(
                "Current count of threads by state",
                metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        samples = metricFamilySamples.samples;
        assertNotNull(samples);
        if (samples.size() > 0) {
            assertAll(samples.stream().map(s ->
                 () -> {
                    assertEquals(2, s.labelNames.size());
                    assertEquals("application", s.labelNames.get(0));
                    assertEquals("test", s.labelValues.get(0));
                    assertEquals("state", s.labelNames.get(1));
                }
            ));
        }
    }

}
