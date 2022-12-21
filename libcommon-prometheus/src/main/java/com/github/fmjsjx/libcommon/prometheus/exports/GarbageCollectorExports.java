package com.github.fmjsjx.libcommon.prometheus.exports;


import io.prometheus.client.Collector;
import io.prometheus.client.Predicate;
import io.prometheus.client.SummaryMetricFamily;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Exports metrics about JVM garbage collectors.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new GarbageCollectorExports().register();
 *   // or with custom labels
 *   new GarbageCollectorExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jvm_gc_collection_seconds_count{gc="PS1"} 200
 *   jvm_gc_collection_seconds_sum{gc="PS1"} 6.7
 * </pre>
 */
public class GarbageCollectorExports extends Collector {

    private static final String JVM_GC_COLLECTION_SECONDS = "jvm_gc_collection_seconds";

    private final CustomLabelsProvider customLabelsProvider;
    private final List<GarbageCollectorMXBean> garbageCollectors;

    /**
     * Constructs new {@link GarbageCollectorExports} instance without any custom labels.
     */
    public GarbageCollectorExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link GarbageCollectorExports} instance with specified {@code customLabelNames} and specified
     * * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public GarbageCollectorExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link GarbageCollectorExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public GarbageCollectorExports(CustomLabelsProvider customLabelsProvider) {
        this(customLabelsProvider, ManagementFactory.getGarbageCollectorMXBeans());
    }

    /**
     * Constructs new {@link GarbageCollectorExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     * @param garbageCollectors    the list contains {@link GarbageCollectorMXBean}s
     */
    public GarbageCollectorExports(CustomLabelsProvider customLabelsProvider, List<GarbageCollectorMXBean> garbageCollectors) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        this.garbageCollectors = List.copyOf(Objects.requireNonNull(garbageCollectors, "garbageCollectors must not be null"));
        if (customLabelsProvider.labelNames().contains("gc")) {
            throw new IllegalArgumentException("GarbageCollectorExports cannot have a custom label name: gc");
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return collect(null);
    }

    @Override
    public List<MetricFamilySamples> collect(Predicate<String> nameFilter) {
        var mfs = new ArrayList<MetricFamilySamples>();
        if (nameFilter == null || nameFilter.test(JVM_GC_COLLECTION_SECONDS)) {
            var customLabelNames = customLabelsProvider.labelNames();
            var customLabelValues = customLabelsProvider.labelValues();
            var labelNames = new ArrayList<String>(customLabelNames.size() + 1);
            labelNames.addAll(customLabelNames);
            labelNames.add("gc");
            var gcCollection = new SummaryMetricFamily(
                    JVM_GC_COLLECTION_SECONDS,
                    "Time spent in a given JVM garbage collector in seconds.",
                    labelNames);
            for (final GarbageCollectorMXBean gc : garbageCollectors) {
                var labelValues = new ArrayList<String>(labelNames.size());
                labelValues.addAll(customLabelValues);
                labelValues.add(gc.getName());
                gcCollection.addMetric(labelValues, gc.getCollectionCount(), gc.getCollectionTime() / MILLISECONDS_PER_SECOND);
            }
            mfs.add(gcCollection);
        }
        return mfs;
    }
}
