package com.github.fmjsjx.libcommon.prometheus.client;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JVM memory allocation metrics.
 *
 * @since 3.7
 */
public class JvmMemoryPoolAllocationMetrics {

    private static final String JVM_MEMORY_POOL_ALLOCATED_BYTES_TOTAL = "jvm_memory_pool_allocated_bytes_total";

    private final PrometheusProperties config;
    private final List<GarbageCollectorMXBean> garbageCollectorBeans;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmMemoryPoolAllocationMetrics(List<GarbageCollectorMXBean> garbageCollectorBeans, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.garbageCollectorBeans = garbageCollectorBeans;
        this.config = config;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var names = Arrays.copyOf(labelNames, labelNames.length + 1);
        names[labelNames.length] = "pool";

        Counter allocatedCounter = Counter.builder(config)
                .name(JVM_MEMORY_POOL_ALLOCATED_BYTES_TOTAL)
                .help("Total bytes allocated in a given JVM memory pool. Only updated after GC, not continuously.")
                .constLabels(constLabels)
                .labelNames(names)
                .register(registry);

        AllocationCountingNotificationListener listener = new AllocationCountingNotificationListener(allocatedCounter, customLabelsProvider);
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorBeans) {
            if (garbageCollectorMXBean instanceof NotificationEmitter) {
                ((NotificationEmitter) garbageCollectorMXBean).addNotificationListener(listener, null, null);
            }
        }
    }

    static class AllocationCountingNotificationListener implements NotificationListener {

        private final Map<String, Long> lastMemoryUsage = new HashMap<>();
        private final Counter counter;
        private final CustomLabelsProvider customLabelsProvider;
        private final boolean isCustomLabelsEmpty;

        AllocationCountingNotificationListener(Counter counter, CustomLabelsProvider customLabelsProvider) {
            this.counter = counter;
            this.customLabelsProvider = customLabelsProvider;
            this.isCustomLabelsEmpty = customLabelsProvider.labelNames().length == 0;
        }

        @Override
        public synchronized void handleNotification(Notification notification, Object handback) {
            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            GcInfo gcInfo = info.getGcInfo();
            Map<String, MemoryUsage> memoryUsageBeforeGc = gcInfo.getMemoryUsageBeforeGc();
            Map<String, MemoryUsage> memoryUsageAfterGc = gcInfo.getMemoryUsageAfterGc();
            for (Map.Entry<String, MemoryUsage> entry : memoryUsageBeforeGc.entrySet()) {
                String memoryPool = entry.getKey();
                long before = entry.getValue().getUsed();
                long after = memoryUsageAfterGc.get(memoryPool).getUsed();
                handleMemoryPool(memoryPool, before, after);
            }
        }

        // Visible for testing
        void handleMemoryPool(String memoryPool, long before, long after) {
            /*
             * Calculate increase in the memory pool by comparing memory used
             * after last GC, before this GC, and after this GC.
             * See ascii illustration below.
             * Make sure to count only increases and ignore decreases.
             * (Typically a pool will only increase between GCs or during GCs, not both.
             * E.g. eden pools between GCs. Survivor and old generation pools during GCs.)
             *
             *                         |<-- diff1 -->|<-- diff2 -->|
             * Timeline: |-- last GC --|             |---- GC -----|
             *                      ___^__        ___^____      ___^___
             * Mem. usage vars:    / last \      / before \    / after \
             */

            // Get last memory usage after GC and remember memory used after for next time
            long last = getAndSet(lastMemoryUsage, memoryPool, after);
            // Difference since last GC
            long diff1 = before - last;
            // Difference during this GC
            long diff2 = after - before;
            // Make sure to only count increases
            if (diff1 < 0) {
                diff1 = 0;
            }
            if (diff2 < 0) {
                diff2 = 0;
            }
            long increase = diff1 + diff2;
            if (increase > 0) {
                if (isCustomLabelsEmpty) {
                    counter.labelValues(memoryPool).inc(increase);
                } else {
                    var labelValues = customLabelsProvider.labelValues();
                    var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                    values[labelValues.length] = memoryPool;
                    counter.labelValues(values).inc(increase);
                }
            }
        }

        private static long getAndSet(Map<String, Long> map, String key, long value) {
            Long last = map.put(key, value);
            return last == null ? 0 : last;
        }
    }

    /**
     * Creates and returns a new builder with the default config.
     *
     * @return a builder with the default config
     */
    public static Builder builder() {
        return new Builder(PrometheusProperties.get());
    }

    /**
     * Creates and returns a new builder with the specified {@code config} given.
     *
     * @param config the {@link PrometheusProperties} config
     * @return a builder with the specified {@code config} given
     */
    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    /**
     * The builder.
     */
    public static class Builder {

        private final PrometheusProperties config;
        private List<GarbageCollectorMXBean> garbageCollectorBeans;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param garbageCollectorBeans the test garbage collector beans
         * @return this builder
         */
        Builder withGarbageCollectorBeans(List<GarbageCollectorMXBean> garbageCollectorBeans) {
            this.garbageCollectorBeans = garbageCollectorBeans;
            return this;
        }

        /**
         * Sets the const labels.
         *
         * @param constLabels the const labels
         * @return this builder
         */
        public Builder constLabels(Labels constLabels) {
            this.constLabels = constLabels == null ? Labels.EMPTY : constLabels;
            return this;
        }

        /**
         * Sets the custom labels.
         *
         * @param customLabelsProvider the custom labels provider
         * @return this builder
         */
        public Builder customLabels(CustomLabelsProvider customLabelsProvider) {
            this.customLabelsProvider = customLabelsProvider == null ? CustomLabels.empty() : customLabelsProvider;
            return this;
        }

        /**
         * Register to the default registry.
         */
        public void register() {
            register(PrometheusRegistry.defaultRegistry);
        }

        /**
         * Register to the specified {@code registry} given.
         *
         * @param registry the registry
         */
        public void register(PrometheusRegistry registry) {
            List<GarbageCollectorMXBean> garbageCollectorBeans = this.garbageCollectorBeans;
            if (garbageCollectorBeans == null) {
                garbageCollectorBeans = ManagementFactory.getGarbageCollectorMXBeans();
            }
            new JvmMemoryPoolAllocationMetrics(garbageCollectorBeans, config, constLabels, customLabelsProvider).register(registry);
        }

    }

}
