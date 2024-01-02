package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.SummaryWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.Quantiles;
import io.prometheus.metrics.model.snapshots.Unit;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * JVM Garbage Collector metrics.
 *
 * @since 3.7
 */
public class JvmGarbageCollectorMetrics {

    private static final String JVM_GC_COLLECTION_SECONDS = "jvm_gc_collection_seconds";

    private final PrometheusProperties config;
    private final List<GarbageCollectorMXBean> garbageCollectorBeans;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmGarbageCollectorMetrics(List<GarbageCollectorMXBean> garbageCollectorBeans, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.garbageCollectorBeans = garbageCollectorBeans;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var isLabelEmpty = labelNames.length == 0;
        var names = Arrays.copyOf(labelNames, labelNames.length + 1);
        names[labelNames.length] = "gc";
        SummaryWithCallback.builder(config)
                .name(JVM_GC_COLLECTION_SECONDS)
                .help("Time spent in a given JVM garbage collector in seconds.")
                .constLabels(constLabels)
                .unit(Unit.SECONDS)
                .labelNames(names)
                .callback(isLabelEmpty ? callback -> {
                    for (GarbageCollectorMXBean gc : garbageCollectorBeans) {
                        callback.call(gc.getCollectionCount(), Unit.millisToSeconds(gc.getCollectionTime()), Quantiles.EMPTY, gc.getName());
                    }
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    for (GarbageCollectorMXBean gc : garbageCollectorBeans) {
                        var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                        values[labelValues.length] = gc.getName();
                        callback.call(gc.getCollectionCount(), Unit.millisToSeconds(gc.getCollectionTime()), Quantiles.EMPTY, values);
                    }
                })
                .register(registry);
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
        Builder garbageCollectorBeans(List<GarbageCollectorMXBean> garbageCollectorBeans) {
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
            new JvmGarbageCollectorMetrics(garbageCollectorBeans, config, constLabels, customLabelsProvider).register(registry);
        }

    }

}
