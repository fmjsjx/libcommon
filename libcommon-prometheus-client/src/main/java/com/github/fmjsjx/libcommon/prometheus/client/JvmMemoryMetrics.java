package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.Unit;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JVM memory metrics.
 *
 * @since 3.7
 */
public class JvmMemoryMetrics {

    private static final String JVM_MEMORY_OBJECTS_PENDING_FINALIZATION = "jvm_memory_objects_pending_finalization";
    private static final String JVM_MEMORY_USED_BYTES = "jvm_memory_used_bytes";
    private static final String JVM_MEMORY_COMMITTED_BYTES = "jvm_memory_committed_bytes";
    private static final String JVM_MEMORY_MAX_BYTES = "jvm_memory_max_bytes";
    private static final String JVM_MEMORY_INIT_BYTES = "jvm_memory_init_bytes";
    private static final String JVM_MEMORY_POOL_USED_BYTES = "jvm_memory_pool_used_bytes";
    private static final String JVM_MEMORY_POOL_COMMITTED_BYTES = "jvm_memory_pool_committed_bytes";
    private static final String JVM_MEMORY_POOL_MAX_BYTES = "jvm_memory_pool_max_bytes";
    private static final String JVM_MEMORY_POOL_INIT_BYTES = "jvm_memory_pool_init_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_USED_BYTES = "jvm_memory_pool_collection_used_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES = "jvm_memory_pool_collection_committed_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_MAX_BYTES = "jvm_memory_pool_collection_max_bytes";
    private static final String JVM_MEMORY_POOL_COLLECTION_INIT_BYTES = "jvm_memory_pool_collection_init_bytes";

    private final PrometheusProperties config;
    private final MemoryMXBean memoryBean;
    private final List<MemoryPoolMXBean> poolBeans;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmMemoryMetrics(List<MemoryPoolMXBean> poolBeans, MemoryMXBean memoryBean, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.poolBeans = poolBeans;
        this.memoryBean = memoryBean;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var isLabelEmpty = labelNames.length == 0;

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_OBJECTS_PENDING_FINALIZATION)
                .help("The number of objects waiting in the finalizer queue.")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(memoryBean.getObjectPendingFinalizationCount(), customLabelsProvider.labelValues()))
                .register(registry);

        var areaNames = Arrays.copyOf(labelNames, labelNames.length + 1);
        areaNames[labelNames.length] = "area";
        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_USED_BYTES)
                .help("Used bytes of a given JVM memory area.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(areaNames)
                .callback(isLabelEmpty ? callback -> {
                    callback.call(memoryBean.getHeapMemoryUsage().getUsed(), "heap");
                    callback.call(memoryBean.getNonHeapMemoryUsage().getUsed(), "nonheap");
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    var heapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    heapValues[labelValues.length] = "heap";
                    var nonheapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    nonheapValues[labelValues.length] = "nonheap";
                    callback.call(memoryBean.getHeapMemoryUsage().getUsed(), heapValues);
                    callback.call(memoryBean.getNonHeapMemoryUsage().getUsed(), nonheapValues);
                })
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_COMMITTED_BYTES)
                .help("Committed (bytes) of a given JVM memory area.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(areaNames)
                .callback(isLabelEmpty ? callback -> {
                    callback.call(memoryBean.getHeapMemoryUsage().getCommitted(), "heap");
                    callback.call(memoryBean.getNonHeapMemoryUsage().getCommitted(), "nonheap");
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    var heapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    heapValues[labelValues.length] = "heap";
                    var nonheapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    nonheapValues[labelValues.length] = "nonheap";
                    callback.call(memoryBean.getHeapMemoryUsage().getCommitted(), heapValues);
                    callback.call(memoryBean.getNonHeapMemoryUsage().getCommitted(), nonheapValues);
                })
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_MAX_BYTES)
                .help("Max (bytes) of a given JVM memory area.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(areaNames)
                .callback(isLabelEmpty ? callback -> {
                    callback.call(memoryBean.getHeapMemoryUsage().getMax(), "heap");
                    callback.call(memoryBean.getNonHeapMemoryUsage().getMax(), "nonheap");
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    var heapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    heapValues[labelValues.length] = "heap";
                    var nonheapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    nonheapValues[labelValues.length] = "nonheap";
                    callback.call(memoryBean.getHeapMemoryUsage().getMax(), heapValues);
                    callback.call(memoryBean.getNonHeapMemoryUsage().getMax(), nonheapValues);
                })
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_INIT_BYTES)
                .help("Initial bytes of a given JVM memory area.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(areaNames)
                .callback(isLabelEmpty ? callback -> {
                    callback.call(memoryBean.getHeapMemoryUsage().getInit(), "heap");
                    callback.call(memoryBean.getNonHeapMemoryUsage().getInit(), "nonheap");
                } : callback -> {
                    var labelValues = customLabelsProvider.labelValues();
                    var heapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    heapValues[labelValues.length] = "heap";
                    var nonheapValues = Arrays.copyOf(labelValues, labelValues.length + 1);
                    nonheapValues[labelValues.length] = "nonheap";
                    callback.call(memoryBean.getHeapMemoryUsage().getInit(), heapValues);
                    callback.call(memoryBean.getNonHeapMemoryUsage().getInit(), nonheapValues);
                })
                .register(registry);

        var poolNames = Arrays.copyOf(labelNames, labelNames.length + 1);
        poolNames[labelNames.length] = "pool";

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_USED_BYTES)
                .help("Used bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getUsed, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_COMMITTED_BYTES)
                .help("Committed bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getCommitted, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_MAX_BYTES)
                .help("Max bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getMax, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_INIT_BYTES)
                .help("Initial bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getUsage, MemoryUsage::getInit, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_COLLECTION_USED_BYTES)
                .help("Used bytes after last collection of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getUsed, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_COLLECTION_COMMITTED_BYTES)
                .help("Committed after last collection bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getCommitted, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_COLLECTION_MAX_BYTES)
                .help("Max bytes after last collection of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getMax, isLabelEmpty))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_MEMORY_POOL_COLLECTION_INIT_BYTES)
                .help("Initial after last collection bytes of a given JVM memory pool.")
                .constLabels(constLabels)
                .unit(Unit.BYTES)
                .labelNames(poolNames)
                .callback(makeCallback(poolBeans, MemoryPoolMXBean::getCollectionUsage, MemoryUsage::getInit, isLabelEmpty))
                .register(registry);
    }

    private Consumer<GaugeWithCallback.Callback> makeCallback(List<MemoryPoolMXBean> poolBeans, Function<MemoryPoolMXBean, MemoryUsage> memoryUsageFunc, Function<MemoryUsage, Long> valueFunc, boolean isLabelEmpty) {
        if (isLabelEmpty) {
            return callback -> {
                for (MemoryPoolMXBean pool : poolBeans) {
                    MemoryUsage poolUsage = memoryUsageFunc.apply(pool);
                    if (poolUsage != null) {
                        callback.call(valueFunc.apply(poolUsage), pool.getName());
                    }
                }
            };
        } else {
            return callback -> {
                var labelValues = customLabelsProvider.labelValues();
                for (MemoryPoolMXBean pool : poolBeans) {
                    MemoryUsage poolUsage = memoryUsageFunc.apply(pool);
                    if (poolUsage != null) {
                        var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                        values[labelValues.length] = pool.getName();
                        callback.call(valueFunc.apply(poolUsage), values);
                    }
                }
            };
        }
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
        private MemoryMXBean memoryBean;
        private List<MemoryPoolMXBean> poolBeans;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param memoryBean the test memory bean
         * @return this builder
         */
        Builder withMemoryBean(MemoryMXBean memoryBean) {
            this.memoryBean = memoryBean;
            return this;
        }

        /**
         * Package private. For testing only.
         */
        Builder withMemoryPoolBeans(List<MemoryPoolMXBean> memoryPoolBeans) {
            this.poolBeans = memoryPoolBeans;
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
            MemoryMXBean memoryMXBean = this.memoryBean != null ? this.memoryBean : ManagementFactory.getMemoryMXBean();
            List<MemoryPoolMXBean> poolBeans = this.poolBeans != null ? this.poolBeans : ManagementFactory.getMemoryPoolMXBeans();
            new JvmMemoryMetrics(poolBeans, memoryMXBean, config, constLabels, customLabelsProvider).register(registry);
        }

    }

}
