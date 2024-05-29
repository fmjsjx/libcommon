package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Registers all JVM metrics. Example usage:
 * <pre>{@code
 *   JvmMetrics.builder().register();
 *   // or with const labels
 *   JvmMetrics.builder().constLabels(Labels.of("env", "dev")).register();
 *   // or with custom labels
 *   JvmMetrics.builder().customLabels(CustomLabels.of(List.of("env"), List.of("dev"))).register();
 * }</pre>
 *
 * @since 3.7
 */
public class JvmMetrics {

    private static final AtomicBoolean registeredWithTheDefaultRegistry = new AtomicBoolean(false);

    /**
     * Create and returns a new {@link Builder} instance.
     *
     * @return the new {@code Builder} instance
     */
    public static Builder builder() {
        return builder(PrometheusProperties.get());
    }

    /**
     * Create and returns a new {@link Builder} instance with the specified {@code config} given.
     *
     * @param config the {@link PrometheusProperties} configuration
     * @return the new {@code Builder} instance
     */
    public static Builder builder(PrometheusProperties config) {
        return new Builder(config);
    }

    /**
     * The builder.
     */
    public static class Builder {

        private final PrometheusProperties config;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Register all JVM metrics with the default registry.
         * <p>
         * It's safe to call this multiple times:
         * Only the first call will register the metrics, all subsequent calls will be ignored.
         */
        public void register() {
            if (!registeredWithTheDefaultRegistry.getAndSet(true)) {
                register(PrometheusRegistry.defaultRegistry);
            }
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
         * Register all JVM metrics with the {@code registry}.
         * <p>
         * You must make sure to call this only once per {@code registry}, otherwise it will
         * throw an Exception because you are trying to register duplicate metrics.
         *
         * @param registry the prometheus registry
         */
        public void register(PrometheusRegistry registry) {
            JvmThreadsMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmBufferPoolMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmClassLoadingMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmCompilationMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmGarbageCollectorMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmMemoryPoolAllocationMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmMemoryMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmNativeMemoryMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            JvmRuntimeInfoMetric.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
            ProcessMetrics.builder(config).constLabels(constLabels).customLabels(customLabelsProvider).register(registry);
        }
    }

}
