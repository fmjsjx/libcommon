package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.Info;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import java.util.Arrays;

/**
 * JVM Runtime Info metric.
 *
 * @since 3.7
 */
public class JvmRuntimeInfoMetric {

    private static final String JVM_RUNTIME_INFO = "jvm_runtime_info";

    private final PrometheusProperties config;
    private final String version;
    private final String vendor;
    private final String runtime;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmRuntimeInfoMetric(String version, String vendor, String runtime, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.version = version;
        this.vendor = vendor;
        this.runtime = runtime;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var names = Arrays.copyOf(labelNames, labelNames.length + 3);
        names[labelNames.length] = "version";
        names[labelNames.length + 1] = "vendor";
        names[labelNames.length + 2] = "runtime";
        Info jvmInfo = Info.builder(config)
                .name(JVM_RUNTIME_INFO)
                .help("JVM runtime info")
                .constLabels(constLabels)
                .labelNames(names)
                .register(registry);

        var labelValues = customLabelsProvider.labelValues();
        var values = Arrays.copyOf(labelValues, labelValues.length + 3);
        values[labelValues.length] = version;
        values[labelValues.length + 1] = vendor;
        values[labelValues.length + 2] = runtime;
        jvmInfo.setLabelValues(values);
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
        private String version;
        private String vendor;
        private String runtime;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param version the test version
         * @return this builder
         */
        Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * Package private. For testing only.
         *
         * @param vendor the test vendor
         * @return this builder
         */
        Builder vendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        /**
         * Package private. For testing only.
         *
         * @param runtime the test runtime
         * @return this builder
         */
        Builder runtime(String runtime) {
            this.runtime = runtime;
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
            String version = this.version != null ? this.version : System.getProperty("java.runtime.version", "unknown");
            String vendor = this.vendor != null ? this.vendor : System.getProperty("java.vm.vendor", "unknown");
            String runtime = this.runtime != null ? this.runtime : System.getProperty("java.runtime.name", "unknown");
            new JvmRuntimeInfoMetric(version, vendor, runtime, config, constLabels, customLabelsProvider).register(registry);
        }

    }

}
