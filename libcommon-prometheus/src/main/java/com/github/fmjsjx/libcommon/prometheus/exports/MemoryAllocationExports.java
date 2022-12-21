package com.github.fmjsjx.libcommon.prometheus.exports;


import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.prometheus.client.Collector;
import io.prometheus.client.Counter;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;


/**
 * Exports metrics about JVM memory allocation.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new MemoryAllocationExports().register();
 *   // or with custom labels
 *   new MemoryAllocationExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 */
public class MemoryAllocationExports extends Collector {

    private final Counter allocatedCounter;

    /**
     * Constructs new {@link MemoryAllocationExports} instance without any custom labels.
     */
    public MemoryAllocationExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link MemoryAllocationExports} instance with specified {@code customLabelNames} and specified
     * * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public MemoryAllocationExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link MemoryAllocationExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public MemoryAllocationExports(CustomLabelsProvider customLabelsProvider) {
        Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        var customLabelNames = customLabelsProvider.labelNames();
        if (customLabelNames.contains("pool")) {
            throw new IllegalArgumentException("MemoryAllocationExports cannot have a custom label name: pool");
        }
        var labelNames = new String[customLabelNames.size() + 1];
        if (labelNames.length > 1) {
            System.arraycopy(customLabelNames.toArray(String[]::new), 0, labelNames, 0, labelNames.length - 1);
        }
        labelNames[labelNames.length - 1] = "pool";
        allocatedCounter = Counter.build().name("jvm_memory_pool_allocated_bytes_total")
                .help("Total bytes allocated in a given JVM memory pool. Only updated after GC, not continuously.")
                .labelNames(labelNames).create();
        var listener = new AllocationCountingNotificationListener(customLabelsProvider, allocatedCounter);
        for (GarbageCollectorMXBean garbageCollectorMXBean : getGarbageCollectorMXBeans()) {
            if (garbageCollectorMXBean instanceof NotificationEmitter) {
                ((NotificationEmitter) garbageCollectorMXBean).addNotificationListener(listener, null, null);
            }
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return allocatedCounter.collect();
    }

    static class AllocationCountingNotificationListener implements NotificationListener {
        private final Map<String, Long> lastMemoryUsage = new HashMap<>();
        private final CustomLabelsProvider customLabelsProvider;
        private final Counter counter;

        AllocationCountingNotificationListener(CustomLabelsProvider customLabelsProvider, Counter counter) {
            this.customLabelsProvider = customLabelsProvider;
            this.counter = counter;
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
                var customLabelValues = customLabelsProvider.labelValues();
                var labels = customLabelValues.toArray(new String[customLabelValues.size() + 1]);
                labels[labels.length - 1] = memoryPool;
                counter.labels(labels).inc(increase);
            }
        }

        private static long getAndSet(Map<String, Long> map, String key, long value) {
            Long last = map.put(key, value);
            return last == null ? 0 : last;
        }
    }

    protected List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return ManagementFactory.getGarbageCollectorMXBeans();
    }

}
