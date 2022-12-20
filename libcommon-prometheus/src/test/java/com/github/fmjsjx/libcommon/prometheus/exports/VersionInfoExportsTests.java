package com.github.fmjsjx.libcommon.prometheus.exports;

import static org.junit.jupiter.api.Assertions.*;

import io.prometheus.client.Collector;
import org.junit.jupiter.api.Test;

import java.util.List;


public class VersionInfoExportsTests {

    private final VersionInfoExports exports = new VersionInfoExports(List.of("application"), List.of("test"));

    @Test
    public void testCollect() {
        var mfs = exports.collect();
        assertNotNull(mfs);
        assertEquals(1, mfs.size());
        var metricFamilySamples = mfs.get(0);
        assertEquals("jvm", metricFamilySamples.name);
        assertEquals(Collector.Type.INFO, metricFamilySamples.type);
        assertEquals("VM version info", metricFamilySamples.help);
        assertEquals("", metricFamilySamples.unit);
        var samples = metricFamilySamples.samples;
        assertNotNull(samples);
        assertEquals(1, samples.size());
        var sample = samples.get(0);
        assertEquals(4, sample.labelNames.size());
        assertEquals("application", sample.labelNames.get(0));
        assertEquals("test", sample.labelValues.get(0));
        assertEquals("runtime", sample.labelNames.get(1));
        assertEquals("vendor", sample.labelNames.get(2));
        assertEquals("version", sample.labelNames.get(3));
    }

    @Test
    public void testIllegalLabelName() {
        try {
            new VersionInfoExports(List.of("version"), List.of("test"));
        } catch (IllegalArgumentException e) {
            assertEquals("VersionInfoExports cannot have a custom label name: version", e.getMessage());
        }
        try {
            new VersionInfoExports(List.of("vendor"), List.of("test"));
        } catch (IllegalArgumentException e) {
            assertEquals("VersionInfoExports cannot have a custom label name: vendor", e.getMessage());
        }
        try {
            new VersionInfoExports(List.of("runtime"), List.of("test"));
        } catch (IllegalArgumentException e) {
            assertEquals("VersionInfoExports cannot have a custom label name: runtime", e.getMessage());
        }
    }

}
