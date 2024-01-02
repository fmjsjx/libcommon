package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.Unit;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * JVM Buffer Pool metrics.
 *
 * @since 3.7
 */
public class JvmBufferPoolMetrics {

    private static final String JVM_BUFFER_POOL_USED_BYTES = "jvm_buffer_pool_used_bytes";
    private static final String JVM_BUFFER_POOL_CAPACITY_BYTES = "jvm_buffer_pool_capacity_bytes";
    private static final String JVM_BUFFER_POOL_USED_BUFFERS = "jvm_buffer_pool_used_buffers";

    private final PrometheusProperties config;
    private final List<BufferPoolMXBean> bufferPoolBeans;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmBufferPoolMetrics(List<BufferPoolMXBean> bufferPoolBeans, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.bufferPoolBeans = bufferPoolBeans;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var isLabelEmpty = labelNames.length == 0;
        var names = Arrays.copyOf(labelNames, labelNames.length + 1);
        names[labelNames.length] = "pool";
        GaugeWithCallback.builder(config)
                .name(JVM_BUFFER_POOL_USED_BYTES)
                .help("Used bytes of a given JVM buffer pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(names)
                .callback(isLabelEmpty ? callback -> {
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        callback.call(pool.getMemoryUsed(), pool.getName());
                    }
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                        values[labelValues.length] = pool.getName();
                        callback.call(pool.getMemoryUsed(), values);
                    }
                })
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_BUFFER_POOL_CAPACITY_BYTES)
                .help("Bytes capacity of a given JVM buffer pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(names)
                .callback(isLabelEmpty ? callback -> {
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        callback.call(pool.getTotalCapacity(), pool.getName());
                    }
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                        values[labelValues.length] = pool.getName();
                        callback.call(pool.getTotalCapacity(), values);
                    }
                })
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_BUFFER_POOL_USED_BUFFERS)
                .help("Used buffers of a given JVM buffer pool.")
                .constLabels(constLabels)
                .labelNames(names)
                .callback(isLabelEmpty ? callback -> {
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        callback.call(pool.getCount(), pool.getName());
                    }
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    for (BufferPoolMXBean pool : bufferPoolBeans) {
                        var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                        values[labelValues.length] = pool.getName();
                        callback.call(pool.getCount(), values);
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
        private List<BufferPoolMXBean> bufferPoolBeans;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param bufferPoolBeans the test buffer pool beans
         * @return this builder
         */
        Builder bufferPoolBeans(List<BufferPoolMXBean> bufferPoolBeans) {
            this.bufferPoolBeans = bufferPoolBeans;
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
            List<BufferPoolMXBean> bufferPoolBeans = this.bufferPoolBeans;
            if (bufferPoolBeans == null) {
                bufferPoolBeans = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
            }
            new JvmBufferPoolMetrics(bufferPoolBeans, config, constLabels, customLabelsProvider).register(registry);
        }
    }
}
