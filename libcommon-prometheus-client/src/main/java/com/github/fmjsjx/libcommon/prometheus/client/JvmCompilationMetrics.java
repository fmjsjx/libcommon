package com.github.fmjsjx.libcommon.prometheus.client;


import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.Unit;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

import static io.prometheus.metrics.model.snapshots.Unit.millisToSeconds;

/**
 * JVM Compilation metrics.
 *
 * @since 3.7
 */
public class JvmCompilationMetrics {

    private static final String JVM_COMPILATION_TIME_SECONDS_TOTAL = "jvm_compilation_time_seconds_total";

    private final PrometheusProperties config;
    private final CompilationMXBean compilationBean;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmCompilationMetrics(CompilationMXBean compilationBean, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.compilationBean = compilationBean;
        this.config = config;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {

        if (compilationBean == null || !compilationBean.isCompilationTimeMonitoringSupported()) {
            return;
        }

        var labelNames = customLabelsProvider.labelNames();
        CounterWithCallback.builder(config)
                .name(JVM_COMPILATION_TIME_SECONDS_TOTAL)
                .help("The total time in seconds taken for HotSpot class compilation")
                .constLabels(constLabels)
                .unit(Unit.SECONDS)
                .labelNames(labelNames)
                .callback(callback -> callback.call(millisToSeconds(compilationBean.getTotalCompilationTime()), customLabelsProvider.labelValues()))
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
        private CompilationMXBean compilationBean;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param compilationBean the test compilation bean
         * @return this builder
         */
        Builder compilationBean(CompilationMXBean compilationBean) {
            this.compilationBean = compilationBean;
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
            CompilationMXBean compilationBean = this.compilationBean != null ? this.compilationBean : ManagementFactory.getCompilationMXBean();
            new JvmCompilationMetrics(compilationBean, config, constLabels, customLabelsProvider).register(registry);
        }
    }

}
