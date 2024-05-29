package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;


import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class JvmNativeMemoryMetricsTest {

    @Test
    public void testNativeMemoryTrackingFail() throws IOException {
        JvmNativeMemoryMetrics.isEnabled.set(true);

        JvmNativeMemoryMetrics.PlatformMBeanServerAdapter adapter = Mockito.mock(JvmNativeMemoryMetrics.PlatformMBeanServerAdapter.class);
        when(adapter.vmNativeMemorySummaryInBytes()).thenThrow(new RuntimeException("mock"));

        PrometheusRegistry registry = new PrometheusRegistry();
        new JvmNativeMemoryMetrics.Builder(PrometheusProperties.get(), adapter).register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = "# EOF\n";

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));
    }

    @Test
    public void testNativeMemoryTrackingEmpty() throws IOException {
        JvmNativeMemoryMetrics.isEnabled.set(true);

        JvmNativeMemoryMetrics.PlatformMBeanServerAdapter adapter = Mockito.mock(JvmNativeMemoryMetrics.PlatformMBeanServerAdapter.class);
        when(adapter.vmNativeMemorySummaryInBytes()).thenReturn("");

        PrometheusRegistry registry = new PrometheusRegistry();
        new JvmNativeMemoryMetrics.Builder(PrometheusProperties.get(), adapter).register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = "# EOF\n";

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));
    }

    @Test
    public void testNativeMemoryTrackingDisabled() throws IOException {
        JvmNativeMemoryMetrics.isEnabled.set(true);

        JvmNativeMemoryMetrics.PlatformMBeanServerAdapter adapter = Mockito.mock(JvmNativeMemoryMetrics.PlatformMBeanServerAdapter.class);
        when(adapter.vmNativeMemorySummaryInBytes()).thenReturn("Native memory tracking is not enabled");

        PrometheusRegistry registry = new PrometheusRegistry();
        new JvmNativeMemoryMetrics.Builder(PrometheusProperties.get(), adapter).register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = "# EOF\n";

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));
    }

    @Test
    public void testNativeMemoryTrackingEnabled() throws IOException {
        JvmNativeMemoryMetrics.isEnabled.set(true);

        JvmNativeMemoryMetrics.PlatformMBeanServerAdapter adapter = Mockito.mock(JvmNativeMemoryMetrics.PlatformMBeanServerAdapter.class);
        when(adapter.vmNativeMemorySummaryInBytes()).thenReturn(
                """
                        Native Memory Tracking:

                        Total: reserved=10341970661, committed=642716389
                               malloc: 27513573 #22947
                               mmap:   reserved=10314457088, committed=615202816

                        -                 Java Heap (reserved=8531214336, committed=536870912)
                                                    (mmap: reserved=8531214336, committed=536870912)\s
                        \s
                        -                     Class (reserved=1073899939, committed=616867)
                                                    (classes #1630)
                                                    (  instance classes #1462, array classes #168)
                                                    (malloc=158115 #2350)\s
                                                    (mmap: reserved=1073741824, committed=458752)\s
                                                    (  Metadata:   )
                                                    (    reserved=67108864, committed=2818048)
                                                    (    used=2748008)
                                                    (    waste=70040 =2.49%)
                                                    (  Class space:)
                                                    (    reserved=1073741824, committed=458752)
                                                    (    used=343568)
                                                    (    waste=115184 =25.11%)
                        \s
                        -                    Thread (reserved=21020080, committed=847280)
                                                    (thread #20)
                                                    (stack: reserved=20971520, committed=798720)
                                                    (malloc=27512 #125)\s
                                                    (arena=21048 #37)
                        \s
                        -                      Code (reserved=253796784, committed=7836080)
                                                    (malloc=105944 #1403)\s
                                                    (mmap: reserved=253689856, committed=7729152)\s
                                                    (arena=984 #1)
                        \s
                        -                        GC (reserved=373343252, committed=76530708)
                                                    (malloc=22463508 #720)\s
                                                    (mmap: reserved=350879744, committed=54067200)\s
                        \s
                        -                  Compiler (reserved=1926356, committed=1926356)
                                                    (malloc=20428 #73)\s
                                                    (arena=1905928 #20)
                        \s
                        -                  Internal (reserved=242257, committed=242257)
                                                    (malloc=176721 #1808)\s
                                                    (mmap: reserved=65536, committed=65536)\s
                        \s
                        -                     Other (reserved=4096, committed=4096)
                                                    (malloc=4096 #2)\s
                        \s
                        -                    Symbol (reserved=1505072, committed=1505072)
                                                    (malloc=1136432 #14482)\s
                                                    (arena=368640 #1)
                        \s
                        -    Native Memory Tracking (reserved=373448, committed=373448)
                                                    (malloc=6280 #91)\s
                                                    (tracking overhead=367168)
                        \s
                        -        Shared class space (reserved=16777216, committed=12386304)
                                                    (mmap: reserved=16777216, committed=12386304)\s
                        \s
                        -               Arena Chunk (reserved=503216, committed=503216)
                                                    (malloc=503216)\s
                        \s
                        -                   Tracing (reserved=33097, committed=33097)
                                                    (malloc=369 #10)\s
                                                    (arena=32728 #1)
                        \s
                        -                 Arguments (reserved=160, committed=160)
                                                    (malloc=160 #5)\s
                        \s
                        -                    Module (reserved=169168, committed=169168)
                                                    (malloc=169168 #1266)\s
                        \s
                        -                 Safepoint (reserved=8192, committed=8192)
                                                    (mmap: reserved=8192, committed=8192)\s
                        \s
                        -           Synchronization (reserved=31160, committed=31160)
                                                    (malloc=31160 #452)\s
                        \s
                        -            Serviceability (reserved=600, committed=600)
                                                    (malloc=600 #6)\s
                        \s
                        -                 Metaspace (reserved=67120768, committed=2829952)
                                                    (malloc=11904 #12)\s
                                                    (mmap: reserved=67108864, committed=2818048)\s
                        \s
                        -      String Deduplication (reserved=632, committed=632)
                                                    (malloc=632 #8)\s
                        \s
                        -           Object Monitors (reserved=832, committed=832)
                                                    (malloc=832 #4)\s
                        \s

                        """
        );

        PrometheusRegistry registry = new PrometheusRegistry();
        new JvmNativeMemoryMetrics.Builder(PrometheusProperties.get(), adapter)
                .constLabels(Labels.of("scope", "test"))
                .register(registry);
        MetricSnapshots snapshots = registry.scrape();

        String expected = """
                \
                # TYPE jvm_native_memory_committed_bytes gauge
                # UNIT jvm_native_memory_committed_bytes bytes
                # HELP jvm_native_memory_committed_bytes Committed bytes of a given JVM. Committed memory represents the amount of memory the JVM is using right now.
                jvm_native_memory_committed_bytes{pool="Arena Chunk",scope="test"} 503216.0
                jvm_native_memory_committed_bytes{pool="Arguments",scope="test"} 160.0
                jvm_native_memory_committed_bytes{pool="Class",scope="test"} 616867.0
                jvm_native_memory_committed_bytes{pool="Code",scope="test"} 7836080.0
                jvm_native_memory_committed_bytes{pool="Compiler",scope="test"} 1926356.0
                jvm_native_memory_committed_bytes{pool="GC",scope="test"} 7.6530708E7
                jvm_native_memory_committed_bytes{pool="Internal",scope="test"} 242257.0
                jvm_native_memory_committed_bytes{pool="Java Heap",scope="test"} 5.36870912E8
                jvm_native_memory_committed_bytes{pool="Metaspace",scope="test"} 2829952.0
                jvm_native_memory_committed_bytes{pool="Module",scope="test"} 169168.0
                jvm_native_memory_committed_bytes{pool="Native Memory Tracking",scope="test"} 373448.0
                jvm_native_memory_committed_bytes{pool="Object Monitors",scope="test"} 832.0
                jvm_native_memory_committed_bytes{pool="Other",scope="test"} 4096.0
                jvm_native_memory_committed_bytes{pool="Safepoint",scope="test"} 8192.0
                jvm_native_memory_committed_bytes{pool="Serviceability",scope="test"} 600.0
                jvm_native_memory_committed_bytes{pool="Shared class space",scope="test"} 1.2386304E7
                jvm_native_memory_committed_bytes{pool="String Deduplication",scope="test"} 632.0
                jvm_native_memory_committed_bytes{pool="Symbol",scope="test"} 1505072.0
                jvm_native_memory_committed_bytes{pool="Synchronization",scope="test"} 31160.0
                jvm_native_memory_committed_bytes{pool="Thread",scope="test"} 847280.0
                jvm_native_memory_committed_bytes{pool="Total",scope="test"} 6.42716389E8
                jvm_native_memory_committed_bytes{pool="Tracing",scope="test"} 33097.0
                # TYPE jvm_native_memory_reserved_bytes gauge
                # UNIT jvm_native_memory_reserved_bytes bytes
                # HELP jvm_native_memory_reserved_bytes Reserved bytes of a given JVM. Reserved memory represents the total amount of memory the JVM can potentially use.
                jvm_native_memory_reserved_bytes{pool="Arena Chunk",scope="test"} 503216.0
                jvm_native_memory_reserved_bytes{pool="Arguments",scope="test"} 160.0
                jvm_native_memory_reserved_bytes{pool="Class",scope="test"} 1.073899939E9
                jvm_native_memory_reserved_bytes{pool="Code",scope="test"} 2.53796784E8
                jvm_native_memory_reserved_bytes{pool="Compiler",scope="test"} 1926356.0
                jvm_native_memory_reserved_bytes{pool="GC",scope="test"} 3.73343252E8
                jvm_native_memory_reserved_bytes{pool="Internal",scope="test"} 242257.0
                jvm_native_memory_reserved_bytes{pool="Java Heap",scope="test"} 8.531214336E9
                jvm_native_memory_reserved_bytes{pool="Metaspace",scope="test"} 6.7120768E7
                jvm_native_memory_reserved_bytes{pool="Module",scope="test"} 169168.0
                jvm_native_memory_reserved_bytes{pool="Native Memory Tracking",scope="test"} 373448.0
                jvm_native_memory_reserved_bytes{pool="Object Monitors",scope="test"} 832.0
                jvm_native_memory_reserved_bytes{pool="Other",scope="test"} 4096.0
                jvm_native_memory_reserved_bytes{pool="Safepoint",scope="test"} 8192.0
                jvm_native_memory_reserved_bytes{pool="Serviceability",scope="test"} 600.0
                jvm_native_memory_reserved_bytes{pool="Shared class space",scope="test"} 1.6777216E7
                jvm_native_memory_reserved_bytes{pool="String Deduplication",scope="test"} 632.0
                jvm_native_memory_reserved_bytes{pool="Symbol",scope="test"} 1505072.0
                jvm_native_memory_reserved_bytes{pool="Synchronization",scope="test"} 31160.0
                jvm_native_memory_reserved_bytes{pool="Thread",scope="test"} 2.102008E7
                jvm_native_memory_reserved_bytes{pool="Total",scope="test"} 1.0341970661E10
                jvm_native_memory_reserved_bytes{pool="Tracing",scope="test"} 33097.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(snapshots));

        registry = new PrometheusRegistry();
        new JvmNativeMemoryMetrics.Builder(PrometheusProperties.get(), adapter)
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);
        snapshots = registry.scrape();
        assertEquals(expected, convertToOpenMetricsFormat(snapshots));
    }
}