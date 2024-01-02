package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.util.Arrays;
import java.util.List;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JvmBufferPoolMetricsTests {
    private final BufferPoolMXBean directBuffer = Mockito.mock(BufferPoolMXBean.class);
    private final BufferPoolMXBean mappedBuffer = Mockito.mock(BufferPoolMXBean.class);

    @BeforeEach
    public void setUp() {
        when(directBuffer.getName()).thenReturn("direct");
        when(directBuffer.getCount()).thenReturn(2L);
        when(directBuffer.getMemoryUsed()).thenReturn(1234L);
        when(directBuffer.getTotalCapacity()).thenReturn(3456L);
        when(mappedBuffer.getName()).thenReturn("mapped");
        when(mappedBuffer.getCount()).thenReturn(3L);
        when(mappedBuffer.getMemoryUsed()).thenReturn(2345L);
        when(mappedBuffer.getTotalCapacity()).thenReturn(4567L);
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmBufferPoolMetrics.builder()
                .bufferPoolBeans(Arrays.asList(mappedBuffer, directBuffer))
                .constLabels(Labels.of("scope", "test"))
                .register(registry);

        String expected = """
                # TYPE jvm_buffer_pool_capacity_bytes gauge
                # UNIT jvm_buffer_pool_capacity_bytes bytes
                # HELP jvm_buffer_pool_capacity_bytes Bytes capacity of a given JVM buffer pool.
                jvm_buffer_pool_capacity_bytes{pool="direct",scope="test"} 3456.0
                jvm_buffer_pool_capacity_bytes{pool="mapped",scope="test"} 4567.0
                # TYPE jvm_buffer_pool_used_buffers gauge
                # HELP jvm_buffer_pool_used_buffers Used buffers of a given JVM buffer pool.
                jvm_buffer_pool_used_buffers{pool="direct",scope="test"} 2.0
                jvm_buffer_pool_used_buffers{pool="mapped",scope="test"} 3.0
                # TYPE jvm_buffer_pool_used_bytes gauge
                # UNIT jvm_buffer_pool_used_bytes bytes
                # HELP jvm_buffer_pool_used_bytes Used bytes of a given JVM buffer pool.
                jvm_buffer_pool_used_bytes{pool="direct",scope="test"} 1234.0
                jvm_buffer_pool_used_bytes{pool="mapped",scope="test"} 2345.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
        registry = new PrometheusRegistry();
        JvmBufferPoolMetrics.builder()
                .bufferPoolBeans(Arrays.asList(mappedBuffer, directBuffer))
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        var filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_buffer_pool_used_bytes")
                .build();

        var registry = new PrometheusRegistry();
        JvmBufferPoolMetrics.builder()
                .bufferPoolBeans(Arrays.asList(directBuffer, mappedBuffer))
                .register(registry);
        registry.scrape(filter);

        verify(directBuffer, times(0)).getMemoryUsed();
        verify(mappedBuffer, times(0)).getMemoryUsed();
        verify(directBuffer, times(1)).getTotalCapacity();
        verify(mappedBuffer, times(1)).getTotalCapacity();
    }

}
