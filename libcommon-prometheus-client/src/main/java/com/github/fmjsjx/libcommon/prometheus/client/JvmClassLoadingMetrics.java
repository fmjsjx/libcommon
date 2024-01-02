package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * JVM Class Loading metrics.
 *
 * @since 3.7
 */
public class JvmClassLoadingMetrics {

    private static final String JVM_CLASSES_CURRENTLY_LOADED = "jvm_classes_currently_loaded";
    private static final String JVM_CLASSES_LOADED_TOTAL = "jvm_classes_loaded_total";
    private static final String JVM_CLASSES_UNLOADED_TOTAL = "jvm_classes_unloaded_total";

    private final PrometheusProperties config;
    private final ClassLoadingMXBean classLoadingBean;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmClassLoadingMetrics(ClassLoadingMXBean classLoadingBean, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.classLoadingBean = classLoadingBean;
        this.config = config;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();

        GaugeWithCallback.builder(config)
                .name(JVM_CLASSES_CURRENTLY_LOADED)
                .help("The number of classes that are currently loaded in the JVM")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(classLoadingBean.getLoadedClassCount(), customLabelsProvider.labelValues()))
                .register(registry);

        CounterWithCallback.builder(config)
                .name(JVM_CLASSES_LOADED_TOTAL)
                .help("The total number of classes that have been loaded since the JVM has started execution")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(classLoadingBean.getTotalLoadedClassCount(), customLabelsProvider.labelValues()))
                .register(registry);

        CounterWithCallback.builder(config)
                .name(JVM_CLASSES_UNLOADED_TOTAL)
                .help("The total number of classes that have been unloaded since the JVM has started execution")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(classLoadingBean.getUnloadedClassCount(), customLabelsProvider.labelValues()))
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
        private ClassLoadingMXBean classLoadingBean;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param classLoadingBean the test class loading bean
         * @return this builder
         */
        Builder classLoadingBean(ClassLoadingMXBean classLoadingBean) {
            this.classLoadingBean = classLoadingBean;
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
            ClassLoadingMXBean classLoadingBean = this.classLoadingBean != null ? this.classLoadingBean : ManagementFactory.getClassLoadingMXBean();
            new JvmClassLoadingMetrics(classLoadingBean, config, constLabels, customLabelsProvider).register(registry);
        }

    }

}
