package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JvmRuntimeInfoMetricTests {

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmRuntimeInfoMetric.builder()
                .version("1.8.0_382-b05")
                .vendor("Oracle Corporation")
                .runtime("OpenJDK Runtime Environment")
                .constLabels(Labels.of("scope", "test"))
                .register(registry);

        String expected = """
                # TYPE jvm_runtime info
                # HELP jvm_runtime JVM runtime info
                jvm_runtime_info{runtime="OpenJDK Runtime Environment",scope="test",vendor="Oracle Corporation",version="1.8.0_382-b05"} 1
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();

        JvmRuntimeInfoMetric.builder()
                .version("1.8.0_382-b05")
                .vendor("Oracle Corporation")
                .runtime("OpenJDK Runtime Environment")
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

}
