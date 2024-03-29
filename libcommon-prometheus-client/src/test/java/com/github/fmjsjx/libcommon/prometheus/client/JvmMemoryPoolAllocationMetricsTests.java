package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.CounterSnapshot;
import io.prometheus.metrics.model.snapshots.MetricSnapshot;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JvmMemoryPoolAllocationMetricsTests {
    @Test
    public void testListenerLogic() {
        PrometheusRegistry registry = new PrometheusRegistry();
        Counter counter = Counter.builder().name("test").labelNames("scope", "pool").register(registry);
        var listener = new JvmMemoryPoolAllocationMetrics.AllocationCountingNotificationListener(counter, CustomLabels.of(List.of("scope"), List.of("test")));

        // Increase by 123
        listener.handleMemoryPool("TestPool", 0, 123);
        assertEquals(123, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // No increase
        listener.handleMemoryPool("TestPool", 123, 123);
        assertEquals(123, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // No increase, then decrease to 0
        listener.handleMemoryPool("TestPool", 123, 0);
        assertEquals(123, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // No increase, then increase by 7
        listener.handleMemoryPool("TestPool", 0, 7);
        assertEquals(130, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // Increase by 10, then decrease to 10
        listener.handleMemoryPool("TestPool", 17, 10);
        assertEquals(140, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // Increase by 7, then increase by 3
        listener.handleMemoryPool("TestPool", 17, 20);
        assertEquals(150, getCountByPool("test", "TestPool", registry.scrape()), 0.0);

        // Decrease to 17, then increase by 3
        listener.handleMemoryPool("TestPool", 17, 20);
        assertEquals(153, getCountByPool("test", "TestPool", registry.scrape()), 0.0);
    }

    private double getCountByPool(String metricName, String poolName, MetricSnapshots snapshots) {
        for (MetricSnapshot snapshot : snapshots) {
            if (snapshot.getMetadata().getPrometheusName().equals(metricName)) {
                for (CounterSnapshot.CounterDataPointSnapshot data : ((CounterSnapshot) snapshot).getDataPoints()) {
                    if (data.getLabels().get("pool").equals(poolName)) {
                        return data.getValue();
                    }
                }
            }
        }
        fail("pool " + poolName + " not found.");
        return 0.0;
    }
}
