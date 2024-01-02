package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.CompilationMXBean;
import java.util.List;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JvmCompilationMetricsTests {
    private final CompilationMXBean mockCompilationBean = Mockito.mock(CompilationMXBean.class);

    @BeforeEach
    public void setUp() {
        when(mockCompilationBean.getTotalCompilationTime()).thenReturn(10000L);
        when(mockCompilationBean.isCompilationTimeMonitoringSupported()).thenReturn(true);
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmCompilationMetrics.builder()
                .compilationBean(mockCompilationBean)
                .constLabels(Labels.of("scope", "test"))
                .register(registry);

        String expected = """
                # TYPE jvm_compilation_time_seconds counter
                # UNIT jvm_compilation_time_seconds seconds
                # HELP jvm_compilation_time_seconds The total time in seconds taken for HotSpot class compilation
                jvm_compilation_time_seconds_total{scope="test"} 10.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();
        JvmCompilationMetrics.builder()
                .compilationBean(mockCompilationBean)
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        var filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_compilation_time_seconds_total")
                .build();

        var registry = new PrometheusRegistry();
        JvmCompilationMetrics.builder()
                .compilationBean(mockCompilationBean)
                .register(registry);
        var snapshots = registry.scrape(filter);

        verify(mockCompilationBean, times(0)).getTotalCompilationTime();
        assertEquals(0, snapshots.size());
    }

}
