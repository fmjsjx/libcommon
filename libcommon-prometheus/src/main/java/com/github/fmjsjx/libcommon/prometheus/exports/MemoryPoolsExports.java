package com.github.fmjsjx.libcommon.prometheus.exports;


import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.Predicate;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.prometheus.client.SampleNameFilter.ALLOW_ALL;

/**
 * Exports metrics about JVM memory areas.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new MemoryPoolsExports().register();
 *   // or with custom labels
 *   new MemoryPoolsExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jvm_memory_bytes_used{area="heap"} 2000000
 *   jvm_memory_bytes_committed{area="nonheap"} 200000
 *   jvm_memory_bytes_max{area="nonheap"} 2000000
 *   jvm_memory_pool_bytes_used{pool="PS Eden Space"} 2000
 * </pre>
 */
public class MemoryPoolsExports extends Collector {

    private static final String JVM_MEMORY_BYTES_USED = "jvm_memory_bytes_used";
    private static final String JVM_MEMORY_BYTES_COMMITTED = "jvm_memory_bytes_committed";
    private static final String JVM_MEMORY_BYTES_MAX = "jvm_memory_bytes_max";
    private static final String JVM_MEMORY_BYTES_INIT = "jvm_memory_bytes_init";

    // Note: The Prometheus naming convention is that units belong at the end of the metric name.
    // For new metrics like jvm_memory_pool_collection_used_bytes we follow that convention.
    // For old metrics like jvm_memory_pool_bytes_used we keep the names as they are to avoid a breaking change.

    private static final String JVM_MEMORY_POOL_BYTES_USED = "jvm_memory_pool_bytes_used";
    private static final String JVM_MEMORY_POOL_BYTES_COMMITTED = "jvm_memory_pool_bytes_committed";
    private static final String JVM_MEMORY_POOL_BYTES_MAX = "jvm_memory_pool_bytes_max";
    private static final String JVM_MEMORY_POOL_BYTES_INIT = "jvm_memory_pool_bytes_init";
    private static final String JVM_MEMORY_POOL_COLLECTION_USED_BYTES = "jvm_memory_pool_collection_used_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES = "jvm_memory_pool_collection_committed_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_MAX_BYTES = "jvm_memory_pool_collection_max_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_INIT_BYTES = "jvm_memory_pool_collection_init_bytes";

    private final CustomLabelsProvider customLabelsProvider;
    private final MemoryMXBean memoryBean;
    private final List<MemoryPoolMXBean> poolBeans;

    /**
     * Constructs new {@link MemoryPoolsExports} instance without any custom labels.
     */
    public MemoryPoolsExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link MemoryPoolsExports} instance with specified {@code customLabelNames} and specified
     * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public MemoryPoolsExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link MemoryPoolsExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public MemoryPoolsExports(CustomLabelsProvider customLabelsProvider) {
        this(customLabelsProvider, ManagementFactory.getMemoryMXBean(), ManagementFactory.getMemoryPoolMXBeans());
    }

    /**
     * Constructs new {@link MemoryPoolsExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     * @param memoryBean           the {@link MemoryMXBean}
     * @param poolBeans            the list contains {@link MemoryPoolMXBean}s
     */
    public MemoryPoolsExports(CustomLabelsProvider customLabelsProvider, MemoryMXBean memoryBean,
                              List<MemoryPoolMXBean> poolBeans) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        Stream.of("area", "pool").filter(customLabelsProvider.labelNames()::contains).findFirst().
                ifPresent(name -> {
                    throw new IllegalArgumentException("MemoryPoolsExports cannot have a custom label name: " + name);
                });
        this.memoryBean = memoryBean;
        this.poolBeans = poolBeans;
    }

    void addMemoryAreaMetrics(List<MetricFamilySamples> sampleFamilies, Predicate<String> nameFilter,
                              List<String> customLabelNames, List<String> customLabelValues) {
        var heapUsage = memoryBean.getHeapMemoryUsage();
        var nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        var labelNames = new ArrayList<String>(customLabelNames.size() + 1);
        labelNames.addAll(customLabelNames);
        labelNames.add("area");
        var heapLabels = new ArrayList<String>(customLabelValues.size() + 1);
        heapLabels.addAll(customLabelValues);
        heapLabels.add("heap");
        var nonheapLabels = new ArrayList<String>(customLabelValues.size() + 1);
        nonheapLabels.addAll(customLabelValues);
        nonheapLabels.add("nonheap");
        if (nameFilter.test(JVM_MEMORY_BYTES_USED)) {
            var used = new GaugeMetricFamily(
                    JVM_MEMORY_BYTES_USED,
                    "Used bytes of a given JVM memory area.",
                    labelNames);
            used.addMetric(heapLabels, heapUsage.getUsed());
            used.addMetric(nonheapLabels, nonHeapUsage.getUsed());
            sampleFamilies.add(used);
        }

        if (nameFilter.test(JVM_MEMORY_BYTES_COMMITTED)) {
            var committed = new GaugeMetricFamily(
                    JVM_MEMORY_BYTES_COMMITTED,
                    "Committed (bytes) of a given JVM memory area.",
                    labelNames);
            committed.addMetric(heapLabels, heapUsage.getCommitted());
            committed.addMetric(nonheapLabels, nonHeapUsage.getCommitted());
            sampleFamilies.add(committed);
        }

        if (nameFilter.test(JVM_MEMORY_BYTES_MAX)) {
            var max = new GaugeMetricFamily(
                    JVM_MEMORY_BYTES_MAX,
                    "Max (bytes) of a given JVM memory area.",
                    labelNames);
            max.addMetric(heapLabels, heapUsage.getMax());
            max.addMetric(nonheapLabels, nonHeapUsage.getMax());
            sampleFamilies.add(max);
        }

        if (nameFilter.test(JVM_MEMORY_BYTES_INIT)) {
            var init = new GaugeMetricFamily(
                    JVM_MEMORY_BYTES_INIT,
                    "Initial bytes of a given JVM memory area.",
                    labelNames);
            init.addMetric(heapLabels, heapUsage.getInit());
            init.addMetric(nonheapLabels, nonHeapUsage.getInit());
            sampleFamilies.add(init);
        }
    }

    void addMemoryPoolMetrics(List<MetricFamilySamples> sampleFamilies, Predicate<String> nameFilter,
                              List<String> customLabelNames, List<String> customLabelValues) {
        boolean anyPoolMetricPassesFilter = false;
        var labelNames = new ArrayList<String>(customLabelNames.size() + 1);
        labelNames.addAll(customLabelNames);
        labelNames.add("pool");
        GaugeMetricFamily used = null;
        if (nameFilter.test(JVM_MEMORY_POOL_BYTES_USED)) {
            used = new GaugeMetricFamily(JVM_MEMORY_POOL_BYTES_USED,
                    "Used bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(used);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily committed = null;
        if (nameFilter.test(JVM_MEMORY_POOL_BYTES_COMMITTED)) {
            committed = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_BYTES_COMMITTED,
                    "Committed bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(committed);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily max = null;
        if (nameFilter.test(JVM_MEMORY_POOL_BYTES_MAX)) {
            max = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_BYTES_MAX,
                    "Max bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(max);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily init = null;
        if (nameFilter.test(JVM_MEMORY_POOL_BYTES_INIT)) {
            init = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_BYTES_INIT,
                    "Initial bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(init);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily collectionUsed = null;
        if (nameFilter.test(JVM_MEMORY_POOL_COLLECTION_USED_BYTES)) {
            collectionUsed = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_COLLECTION_USED_BYTES,
                    "Used bytes after last collection of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(collectionUsed);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily collectionCommitted = null;
        if (nameFilter.test(JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES)) {
            collectionCommitted = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES,
                    "Committed after last collection bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(collectionCommitted);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily collectionMax = null;
        if (nameFilter.test(JVM_MEMORY_POOL_COLLECTION_MAX_BYTES)) {
            collectionMax = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_COLLECTION_MAX_BYTES,
                    "Max bytes after last collection of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(collectionMax);
            anyPoolMetricPassesFilter = true;
        }
        GaugeMetricFamily collectionInit = null;
        if (nameFilter.test(JVM_MEMORY_POOL_COLLECTION_INIT_BYTES)) {
            collectionInit = new GaugeMetricFamily(
                    JVM_MEMORY_POOL_COLLECTION_INIT_BYTES,
                    "Initial after last collection bytes of a given JVM memory pool.",
                    labelNames);
            sampleFamilies.add(collectionInit);
            anyPoolMetricPassesFilter = true;
        }
        if (anyPoolMetricPassesFilter) {
            for (final MemoryPoolMXBean pool : poolBeans) {
                MemoryUsage poolUsage = pool.getUsage();
                if (poolUsage != null) {
                    addPoolMetrics(used, committed, max, init, pool.getName(), poolUsage, customLabelValues);
                }
                MemoryUsage collectionPoolUsage = pool.getCollectionUsage();
                if (collectionPoolUsage != null) {
                    addPoolMetrics(collectionUsed, collectionCommitted, collectionMax, collectionInit, pool.getName(), collectionPoolUsage, customLabelValues);
                }
            }
        }
    }

    private void addPoolMetrics(GaugeMetricFamily used, GaugeMetricFamily committed, GaugeMetricFamily max, GaugeMetricFamily init, String poolName, MemoryUsage poolUsage, List<String> customLabelValues) {
        List<String> labels = null;
        if (used != null) {
            labels = fixLabels(null, customLabelValues, poolName);
            used.addMetric(labels, poolUsage.getUsed());
        }
        if (committed != null) {
            labels = fixLabels(labels, customLabelValues, poolName);
            committed.addMetric(labels, poolUsage.getCommitted());
        }
        if (max != null) {
            labels = fixLabels(labels, customLabelValues, poolName);
            max.addMetric(labels, poolUsage.getMax());
        }
        if (init != null) {
            labels = fixLabels(labels, customLabelValues, poolName);
            init.addMetric(labels, poolUsage.getInit());
        }
    }

    private static final List<String> fixLabels(List<String> labels, List<String> customLabelValues, String poolName) {
        if (labels == null) {
            labels = new ArrayList<>(customLabelValues.size() + 1);
            labels.addAll(customLabelValues);
            labels.add(poolName);
        }
        return labels;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return collect(null);
    }

    @Override
    public List<MetricFamilySamples> collect(Predicate<String> nameFilter) {
        var mfs = new ArrayList<MetricFamilySamples>();
        var customLabelNames = customLabelsProvider.labelNames();
        var customLabelValues = customLabelsProvider.labelValues();
        addMemoryAreaMetrics(mfs, nameFilter == null ? ALLOW_ALL : nameFilter, customLabelNames, customLabelValues);
        addMemoryPoolMetrics(mfs, nameFilter == null ? ALLOW_ALL : nameFilter, customLabelNames, customLabelValues);
        return mfs;
    }
}

