package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JvmGarbageCollectorMetricsTests {
    private final GarbageCollectorMXBean mockGcBean1 = Mockito.mock(GarbageCollectorMXBean.class);
    private final GarbageCollectorMXBean mockGcBean2 = Mockito.mock(GarbageCollectorMXBean.class);

    @BeforeEach
    public void setUp() {
        when(mockGcBean1.getName()).thenReturn("MyGC1");
        when(mockGcBean1.getCollectionCount()).thenReturn(100L);
        when(mockGcBean1.getCollectionTime()).thenReturn(TimeUnit.SECONDS.toMillis(10));
        when(mockGcBean2.getName()).thenReturn("MyGC2");
        when(mockGcBean2.getCollectionCount()).thenReturn(200L);
        when(mockGcBean2.getCollectionTime()).thenReturn(TimeUnit.SECONDS.toMillis(20));
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmGarbageCollectorMetrics.builder()
                .garbageCollectorBeans(Arrays.asList(mockGcBean1, mockGcBean2))
                .constLabels(Labels.of("scope", "test"))
                .register(registry);

        String expected = """
                # TYPE jvm_gc_collection_seconds summary
                # UNIT jvm_gc_collection_seconds seconds
                # HELP jvm_gc_collection_seconds Time spent in a given JVM garbage collector in seconds.
                jvm_gc_collection_seconds_count{gc="MyGC1",scope="test"} 100
                jvm_gc_collection_seconds_sum{gc="MyGC1",scope="test"} 10.0
                jvm_gc_collection_seconds_count{gc="MyGC2",scope="test"} 200
                jvm_gc_collection_seconds_sum{gc="MyGC2",scope="test"} 20.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();
        JvmGarbageCollectorMetrics.builder()
                .garbageCollectorBeans(Arrays.asList(mockGcBean1, mockGcBean2))
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        var filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_gc_collection_seconds")
                .build();

        var registry = new PrometheusRegistry();
        JvmGarbageCollectorMetrics.builder()
                .garbageCollectorBeans(Arrays.asList(mockGcBean1, mockGcBean2))
                .register(registry);
        var snapshots = registry.scrape(filter);

        verify(mockGcBean1, times(0)).getCollectionTime();
        verify(mockGcBean1, times(0)).getCollectionCount();
        assertEquals(0, snapshots.size());
    }

}
