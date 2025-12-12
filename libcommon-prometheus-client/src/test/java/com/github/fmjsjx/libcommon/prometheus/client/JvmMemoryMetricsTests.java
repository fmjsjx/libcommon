package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.*;
import java.util.Arrays;
import java.util.List;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JvmMemoryMetricsTests {
    private final MemoryMXBean mockMemoryBean = Mockito.mock(MemoryMXBean.class);
    private final MemoryPoolMXBean mockPoolsBeanEdenSpace = Mockito.mock(MemoryPoolMXBean.class);
    private final MemoryPoolMXBean mockPoolsBeanOldGen = Mockito.mock(MemoryPoolMXBean.class);
    private final MemoryUsage memoryUsageHeap = Mockito.mock(MemoryUsage.class);
    private final MemoryUsage memoryUsageNonHeap = Mockito.mock(MemoryUsage.class);
    private final MemoryUsage memoryUsagePoolEdenSpace = Mockito.mock(MemoryUsage.class);
    private final MemoryUsage memoryUsagePoolOldGen = Mockito.mock(MemoryUsage.class);
    private final MemoryUsage memoryUsagePoolCollectionEdenSpace = Mockito.mock(MemoryUsage.class);
    private final MemoryUsage memoryUsagePoolCollectionOldGen = Mockito.mock(MemoryUsage.class);

    @BeforeEach
    public void setUp() {
        when(mockMemoryBean.getHeapMemoryUsage()).thenReturn(memoryUsageHeap);
        when(mockMemoryBean.getNonHeapMemoryUsage()).thenReturn(memoryUsageNonHeap);

        long val = 2L;

        when(memoryUsageHeap.getUsed()).thenReturn(val++);
        when(memoryUsageHeap.getMax()).thenReturn(val++);
        when(memoryUsageHeap.getCommitted()).thenReturn(val++);
        when(memoryUsageHeap.getInit()).thenReturn(val++);

        when(memoryUsageNonHeap.getUsed()).thenReturn(val++);
        when(memoryUsageNonHeap.getMax()).thenReturn(val++);
        when(memoryUsageNonHeap.getCommitted()).thenReturn(val++);
        when(memoryUsageNonHeap.getInit()).thenReturn(val++);

        when(memoryUsagePoolEdenSpace.getUsed()).thenReturn(val++);
        when(memoryUsagePoolEdenSpace.getMax()).thenReturn(val++);
        when(memoryUsagePoolEdenSpace.getCommitted()).thenReturn(val++);
        when(memoryUsagePoolEdenSpace.getInit()).thenReturn(val++);

        when(memoryUsagePoolOldGen.getUsed()).thenReturn(val++);
        when(memoryUsagePoolOldGen.getMax()).thenReturn(val++);
        when(memoryUsagePoolOldGen.getCommitted()).thenReturn(val++);
        when(memoryUsagePoolOldGen.getInit()).thenReturn(val++);

        when(memoryUsagePoolCollectionEdenSpace.getUsed()).thenReturn(val++);
        when(memoryUsagePoolCollectionEdenSpace.getMax()).thenReturn(val++);
        when(memoryUsagePoolCollectionEdenSpace.getCommitted()).thenReturn(val++);
        when(memoryUsagePoolCollectionEdenSpace.getInit()).thenReturn(val++);

        when(memoryUsagePoolCollectionOldGen.getUsed()).thenReturn(val++);
        when(memoryUsagePoolCollectionOldGen.getMax()).thenReturn(val++);
        when(memoryUsagePoolCollectionOldGen.getCommitted()).thenReturn(val++);
        //noinspection UnusedAssignment
        when(memoryUsagePoolCollectionOldGen.getInit()).thenReturn(val++);

        when(mockPoolsBeanEdenSpace.getName()).thenReturn("PS Eden Space");
        when(mockPoolsBeanEdenSpace.getUsage()).thenReturn(memoryUsagePoolEdenSpace);
        when(mockPoolsBeanEdenSpace.getCollectionUsage()).thenReturn(memoryUsagePoolCollectionEdenSpace);

        when(mockPoolsBeanOldGen.getName()).thenReturn("PS Old Gen");
        when(mockPoolsBeanOldGen.getUsage()).thenReturn(memoryUsagePoolOldGen);
        when(mockPoolsBeanOldGen.getCollectionUsage()).thenReturn(memoryUsagePoolCollectionOldGen);
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmMemoryMetrics.builder()
                .constLabels(Labels.of("scope", "test"))
                .withMemoryBean(mockMemoryBean)
                .withMemoryPoolBeans(Arrays.asList(mockPoolsBeanEdenSpace, mockPoolsBeanOldGen))
                .register(registry);

        String expected = """
                # TYPE jvm_memory_committed_bytes gauge
                # UNIT jvm_memory_committed_bytes bytes
                # HELP jvm_memory_committed_bytes Committed (bytes) of a given JVM memory area.
                jvm_memory_committed_bytes{area="heap",scope="test"} 4.0
                jvm_memory_committed_bytes{area="nonheap",scope="test"} 8.0
                # TYPE jvm_memory_init_bytes gauge
                # UNIT jvm_memory_init_bytes bytes
                # HELP jvm_memory_init_bytes Initial bytes of a given JVM memory area.
                jvm_memory_init_bytes{area="heap",scope="test"} 5.0
                jvm_memory_init_bytes{area="nonheap",scope="test"} 9.0
                # TYPE jvm_memory_max_bytes gauge
                # UNIT jvm_memory_max_bytes bytes
                # HELP jvm_memory_max_bytes Max (bytes) of a given JVM memory area.
                jvm_memory_max_bytes{area="heap",scope="test"} 3.0
                jvm_memory_max_bytes{area="nonheap",scope="test"} 7.0
                # TYPE jvm_memory_pool_collection_committed_bytes gauge
                # UNIT jvm_memory_pool_collection_committed_bytes bytes
                # HELP jvm_memory_pool_collection_committed_bytes Committed after last collection bytes of a given JVM memory pool.
                jvm_memory_pool_collection_committed_bytes{pool="PS Eden Space",scope="test"} 20.0
                jvm_memory_pool_collection_committed_bytes{pool="PS Old Gen",scope="test"} 24.0
                # TYPE jvm_memory_pool_collection_init_bytes gauge
                # UNIT jvm_memory_pool_collection_init_bytes bytes
                # HELP jvm_memory_pool_collection_init_bytes Initial after last collection bytes of a given JVM memory pool.
                jvm_memory_pool_collection_init_bytes{pool="PS Eden Space",scope="test"} 21.0
                jvm_memory_pool_collection_init_bytes{pool="PS Old Gen",scope="test"} 25.0
                # TYPE jvm_memory_pool_collection_max_bytes gauge
                # UNIT jvm_memory_pool_collection_max_bytes bytes
                # HELP jvm_memory_pool_collection_max_bytes Max bytes after last collection of a given JVM memory pool.
                jvm_memory_pool_collection_max_bytes{pool="PS Eden Space",scope="test"} 19.0
                jvm_memory_pool_collection_max_bytes{pool="PS Old Gen",scope="test"} 23.0
                # TYPE jvm_memory_pool_collection_used_bytes gauge
                # UNIT jvm_memory_pool_collection_used_bytes bytes
                # HELP jvm_memory_pool_collection_used_bytes Used bytes after last collection of a given JVM memory pool.
                jvm_memory_pool_collection_used_bytes{pool="PS Eden Space",scope="test"} 18.0
                jvm_memory_pool_collection_used_bytes{pool="PS Old Gen",scope="test"} 22.0
                # TYPE jvm_memory_pool_committed_bytes gauge
                # UNIT jvm_memory_pool_committed_bytes bytes
                # HELP jvm_memory_pool_committed_bytes Committed bytes of a given JVM memory pool.
                jvm_memory_pool_committed_bytes{pool="PS Eden Space",scope="test"} 12.0
                jvm_memory_pool_committed_bytes{pool="PS Old Gen",scope="test"} 16.0
                # TYPE jvm_memory_pool_init_bytes gauge
                # UNIT jvm_memory_pool_init_bytes bytes
                # HELP jvm_memory_pool_init_bytes Initial bytes of a given JVM memory pool.
                jvm_memory_pool_init_bytes{pool="PS Eden Space",scope="test"} 13.0
                jvm_memory_pool_init_bytes{pool="PS Old Gen",scope="test"} 17.0
                # TYPE jvm_memory_pool_max_bytes gauge
                # UNIT jvm_memory_pool_max_bytes bytes
                # HELP jvm_memory_pool_max_bytes Max bytes of a given JVM memory pool.
                jvm_memory_pool_max_bytes{pool="PS Eden Space",scope="test"} 11.0
                jvm_memory_pool_max_bytes{pool="PS Old Gen",scope="test"} 15.0
                # TYPE jvm_memory_pool_used_bytes gauge
                # UNIT jvm_memory_pool_used_bytes bytes
                # HELP jvm_memory_pool_used_bytes Used bytes of a given JVM memory pool.
                jvm_memory_pool_used_bytes{pool="PS Eden Space",scope="test"} 10.0
                jvm_memory_pool_used_bytes{pool="PS Old Gen",scope="test"} 14.0
                # TYPE jvm_memory_used_bytes gauge
                # UNIT jvm_memory_used_bytes bytes
                # HELP jvm_memory_used_bytes Used bytes of a given JVM memory area.
                jvm_memory_used_bytes{area="heap",scope="test"} 2.0
                jvm_memory_used_bytes{area="nonheap",scope="test"} 6.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();
        JvmMemoryMetrics.builder()
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .withMemoryBean(mockMemoryBean)
                .withMemoryPoolBeans(Arrays.asList(mockPoolsBeanEdenSpace, mockPoolsBeanOldGen))
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

    }

    @Test
    public void testIgnoredMetricNotScraped() {
        MetricNameFilter filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_memory_pool_used_bytes")
                .build();

        PrometheusRegistry registry = new PrometheusRegistry();
        JvmMemoryMetrics.builder()
                .withMemoryBean(mockMemoryBean)
                .withMemoryPoolBeans(Arrays.asList(mockPoolsBeanEdenSpace, mockPoolsBeanOldGen))
                .register(registry);
        registry.scrape(filter);

        verify(memoryUsagePoolEdenSpace, times(0)).getUsed();
        verify(memoryUsagePoolOldGen, times(0)).getUsed();
        verify(memoryUsagePoolEdenSpace, times(1)).getMax();
        verify(memoryUsagePoolOldGen, times(1)).getMax();
    }

}
