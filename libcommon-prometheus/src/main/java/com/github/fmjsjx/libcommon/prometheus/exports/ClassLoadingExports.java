package com.github.fmjsjx.libcommon.prometheus.exports;


import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.Predicate;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.prometheus.client.SampleNameFilter.ALLOW_ALL;

/**
 * Exports metrics about JVM classloading.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new ClassLoadingExports().register();
 *   // or with custom labels
 *   new ClassLoadingExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jvm_classes_currently_loaded{} 1000
 *   jvm_classes_loaded_total{} 2000
 *   jvm_classes_unloaded_total{} 500
 * </pre>
 */
public class ClassLoadingExports extends Collector {

    private static final String JVM_CLASSES_CURRENTLY_LOADED = "jvm_classes_currently_loaded";
    private static final String JVM_CLASSES_LOADED_TOTAL = "jvm_classes_loaded_total";
    private static final String JVM_CLASSES_UNLOADED_TOTAL = "jvm_classes_unloaded_total";

    private final CustomLabelsProvider customLabelsProvider;
    private final ClassLoadingMXBean clBean;

    /**
     * Constructs new {@link ClassLoadingExports} instance without any custom labels.
     */
    public ClassLoadingExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link ClassLoadingExports} instance with specified {@code customLabelNames} and specified
     * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public ClassLoadingExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link ClassLoadingExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public ClassLoadingExports(CustomLabelsProvider customLabelsProvider) {
        this(customLabelsProvider, ManagementFactory.getClassLoadingMXBean());
    }


    /**
     * Constructs new {@link ClassLoadingExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     * @param clBean               a {@link ClassLoadingMXBean}
     */
    public ClassLoadingExports(CustomLabelsProvider customLabelsProvider, ClassLoadingMXBean clBean) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        this.clBean = Objects.requireNonNull(clBean, "clBean must not be null");
    }

    private void addClassLoadingMetrics(List<MetricFamilySamples> sampleFamilies, Predicate<String> nameFilter) {
        var labelNames = customLabelsProvider.labelNames();
        var labelValues = customLabelsProvider.labelValues();
        if (nameFilter.test(JVM_CLASSES_CURRENTLY_LOADED)) {
            sampleFamilies.add(new GaugeMetricFamily(
                    JVM_CLASSES_CURRENTLY_LOADED,
                    "The number of classes that are currently loaded in the JVM",
                    labelNames)
                    .addMetric(labelValues, clBean.getLoadedClassCount()));
        }
        if (nameFilter.test(JVM_CLASSES_LOADED_TOTAL)) {
            sampleFamilies.add(new CounterMetricFamily(
                    JVM_CLASSES_LOADED_TOTAL,
                    "The total number of classes that have been loaded since the JVM has started execution",
                    labelNames)
                    .addMetric(labelValues, clBean.getTotalLoadedClassCount()));
        }
        if (nameFilter.test(JVM_CLASSES_UNLOADED_TOTAL)) {
            sampleFamilies.add(new CounterMetricFamily(
                    JVM_CLASSES_UNLOADED_TOTAL,
                    "The total number of classes that have been unloaded since the JVM has started execution",
                    labelNames).addMetric(labelValues, clBean.getUnloadedClassCount()));
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return collect(null);
    }

    @Override
    public List<MetricFamilySamples> collect(Predicate<String> nameFilter) {
        var mfs = new ArrayList<MetricFamilySamples>(3);
        addClassLoadingMetrics(mfs, nameFilter == null ? ALLOW_ALL : nameFilter);
        return mfs;
    }
}

