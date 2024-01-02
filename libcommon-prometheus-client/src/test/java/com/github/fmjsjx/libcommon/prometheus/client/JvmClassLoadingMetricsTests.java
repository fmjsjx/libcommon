package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.util.List;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JvmClassLoadingMetricsTests {
    private final ClassLoadingMXBean mockClassLoadingBean = Mockito.mock(ClassLoadingMXBean.class);

    @BeforeEach
    public void setUp() {
        when(mockClassLoadingBean.getLoadedClassCount()).thenReturn(1000);
        when(mockClassLoadingBean.getTotalLoadedClassCount()).thenReturn(2000L);
        when(mockClassLoadingBean.getUnloadedClassCount()).thenReturn(500L);
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmClassLoadingMetrics.builder()
                .classLoadingBean(mockClassLoadingBean)
                .constLabels(Labels.of("scope", "test"))
                .register(registry);

        String expected = """
                # TYPE jvm_classes_currently_loaded gauge
                # HELP jvm_classes_currently_loaded The number of classes that are currently loaded in the JVM
                jvm_classes_currently_loaded{scope="test"} 1000.0
                # TYPE jvm_classes_loaded counter
                # HELP jvm_classes_loaded The total number of classes that have been loaded since the JVM has started execution
                jvm_classes_loaded_total{scope="test"} 2000.0
                # TYPE jvm_classes_unloaded counter
                # HELP jvm_classes_unloaded The total number of classes that have been unloaded since the JVM has started execution
                jvm_classes_unloaded_total{scope="test"} 500.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();
        JvmClassLoadingMetrics.builder()
                .classLoadingBean(mockClassLoadingBean)
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        var filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_classes_currently_loaded")
                .build();

        var registry = new PrometheusRegistry();
        JvmClassLoadingMetrics.builder()
                .classLoadingBean(mockClassLoadingBean)
                .register(registry);
        registry.scrape(filter);

        verify(mockClassLoadingBean, times(0)).getLoadedClassCount();
        verify(mockClassLoadingBean, times(1)).getTotalLoadedClassCount();
    }

}
