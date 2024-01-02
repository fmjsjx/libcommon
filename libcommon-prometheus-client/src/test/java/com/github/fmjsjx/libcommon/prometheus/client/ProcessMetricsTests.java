package com.github.fmjsjx.libcommon.prometheus.client;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessMetricsTests {

    private final com.sun.management.UnixOperatingSystemMXBean sunOsBean = mock(com.sun.management.UnixOperatingSystemMXBean.class);
    private final java.lang.management.OperatingSystemMXBean javaOsBean = mock(java.lang.management.OperatingSystemMXBean.class);
    private final ProcessMetrics.Grepper linuxGrepper = mock(ProcessMetrics.Grepper.class);
    private final ProcessMetrics.Grepper windowsGrepper = mock(ProcessMetrics.Grepper.class);
    private final RuntimeMXBean runtimeBean = mock(RuntimeMXBean.class);

    @BeforeEach
    public void setUp() throws IOException {
        when(sunOsBean.getProcessCpuTime()).thenReturn(TimeUnit.MILLISECONDS.toNanos(72));
        when(sunOsBean.getOpenFileDescriptorCount()).thenReturn(127L);
        when(sunOsBean.getMaxFileDescriptorCount()).thenReturn(244L);
        when(runtimeBean.getStartTime()).thenReturn(37100L);
        when(linuxGrepper.lineStartingWith(any(File.class), eq("VmSize:"))).thenReturn("VmSize:     6036 kB");
        when(linuxGrepper.lineStartingWith(any(File.class), eq("VmRSS:"))).thenReturn("VmRSS:      1012 kB");
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        ProcessMetrics.builder()
                .osBean(sunOsBean)
                .runtimeBean(runtimeBean)
                .grepper(linuxGrepper)
                .constLabels(Labels.of("app", "libcommon", "scope", "test"))
                .linux(true)
                .register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = """
                # TYPE process_cpu_seconds counter
                # UNIT process_cpu_seconds seconds
                # HELP process_cpu_seconds Total user and system CPU time spent in seconds.
                process_cpu_seconds_total{app="libcommon",scope="test"} 0.072
                # TYPE process_max_fds gauge
                # HELP process_max_fds Maximum number of open file descriptors.
                process_max_fds{app="libcommon",scope="test"} 244.0
                # TYPE process_open_fds gauge
                # HELP process_open_fds Number of open file descriptors.
                process_open_fds{app="libcommon",scope="test"} 127.0
                # TYPE process_resident_memory_bytes gauge
                # UNIT process_resident_memory_bytes bytes
                # HELP process_resident_memory_bytes Resident memory size in bytes.
                process_resident_memory_bytes{app="libcommon",scope="test"} 1036288.0
                # TYPE process_start_time_seconds gauge
                # UNIT process_start_time_seconds seconds
                # HELP process_start_time_seconds Start time of the process since unix epoch in seconds.
                process_start_time_seconds{app="libcommon",scope="test"} 37.1
                # TYPE process_virtual_memory_bytes gauge
                # UNIT process_virtual_memory_bytes bytes
                # HELP process_virtual_memory_bytes Virtual memory size in bytes.
                process_virtual_memory_bytes{app="libcommon",scope="test"} 6180864.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));

        registry = new PrometheusRegistry();
        ProcessMetrics.builder()
                .osBean(sunOsBean)
                .runtimeBean(runtimeBean)
                .grepper(linuxGrepper)
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .linux(true)
                .register(registry);

        expected = """
                # TYPE process_cpu_seconds counter
                # UNIT process_cpu_seconds seconds
                # HELP process_cpu_seconds Total user and system CPU time spent in seconds.
                process_cpu_seconds_total{scope="test"} 0.072
                # TYPE process_max_fds gauge
                # HELP process_max_fds Maximum number of open file descriptors.
                process_max_fds{scope="test"} 244.0
                # TYPE process_open_fds gauge
                # HELP process_open_fds Number of open file descriptors.
                process_open_fds{scope="test"} 127.0
                # TYPE process_resident_memory_bytes gauge
                # UNIT process_resident_memory_bytes bytes
                # HELP process_resident_memory_bytes Resident memory size in bytes.
                process_resident_memory_bytes{scope="test"} 1036288.0
                # TYPE process_start_time_seconds gauge
                # UNIT process_start_time_seconds seconds
                # HELP process_start_time_seconds Start time of the process since unix epoch in seconds.
                process_start_time_seconds{scope="test"} 37.1
                # TYPE process_virtual_memory_bytes gauge
                # UNIT process_virtual_memory_bytes bytes
                # HELP process_virtual_memory_bytes Virtual memory size in bytes.
                process_virtual_memory_bytes{scope="test"} 6180864.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testMinimal() throws IOException {
        PrometheusRegistry registry = new PrometheusRegistry();
        ProcessMetrics.builder()
                .osBean(javaOsBean)
                .runtimeBean(runtimeBean)
                .grepper(windowsGrepper)
                .register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = """
                # TYPE process_start_time_seconds gauge
                # UNIT process_start_time_seconds seconds
                # HELP process_start_time_seconds Start time of the process since unix epoch in seconds.
                process_start_time_seconds 37.1
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        MetricNameFilter filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("process_max_fds")
                .build();

        PrometheusRegistry registry = new PrometheusRegistry();
        ProcessMetrics.builder()
                .osBean(sunOsBean)
                .runtimeBean(runtimeBean)
                .grepper(linuxGrepper)
                .register(registry);
        registry.scrape(filter);

        verify(sunOsBean, times(0)).getMaxFileDescriptorCount();
        verify(sunOsBean, times(1)).getOpenFileDescriptorCount();
    }

}
