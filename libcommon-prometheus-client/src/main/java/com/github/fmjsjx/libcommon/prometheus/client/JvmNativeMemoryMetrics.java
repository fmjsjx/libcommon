package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.Unit;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JVM native memory metrics.
 *
 * @since 3.8.1
 */
public class JvmNativeMemoryMetrics {

    private static final String JVM_NATIVE_MEMORY_RESERVED_BYTES = "jvm_native_memory_reserved_bytes";
    private static final String JVM_NATIVE_MEMORY_COMMITTED_BYTES = "jvm_native_memory_committed_bytes";

    private static final Pattern pattern = Pattern.compile("\\s*([A-Z][A-Za-z\\s]*[A-Za-z]+).*reserved=(\\d+), committed=(\\d+)");

    /**
     * Package private. For testing only.
     */
    static final AtomicBoolean isEnabled = new AtomicBoolean(true);

    private final PrometheusProperties config;
    private final PlatformMBeanServerAdapter adapter;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmNativeMemoryMetrics(PrometheusProperties config, PlatformMBeanServerAdapter adapter, Labels constLabels,
                                   CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.adapter = adapter;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var isLabelEmpty = labelNames.length == 0;
        // first call will check if enabled and set the flag
        vmNativeMemorySummaryInBytesOrEmpty();
        if (isEnabled.get()) {
            var poolNames = Arrays.copyOf(labelNames, labelNames.length + 1);
            poolNames[labelNames.length] = "pool";
            GaugeWithCallback.builder(config)
                    .name(JVM_NATIVE_MEMORY_RESERVED_BYTES)
                    .help("Reserved bytes of a given JVM. Reserved memory represents the total amount of memory the JVM can potentially use.")
                    .unit(Unit.BYTES)
                    .constLabels(constLabels)
                    .labelNames(poolNames)
                    .callback(makeCallback(true, isLabelEmpty))
                    .register(registry);

            GaugeWithCallback.builder(config)
                    .name(JVM_NATIVE_MEMORY_COMMITTED_BYTES)
                    .help("Committed bytes of a given JVM. Committed memory represents the amount of memory the JVM is using right now.")
                    .unit(Unit.BYTES)
                    .constLabels(constLabels)
                    .labelNames(poolNames)
                    .callback(makeCallback(false, isLabelEmpty))
                    .register(registry);
        }
    }

    private Consumer<GaugeWithCallback.Callback> makeCallback(Boolean reserved, boolean isLabelEmpty) {
        if (isLabelEmpty) {
            return callback -> {
                String summary = vmNativeMemorySummaryInBytesOrEmpty();
                if (!summary.isEmpty()) {
                    Matcher matcher = pattern.matcher(summary);
                    while (matcher.find()) {
                        String category = matcher.group(1);
                        long value;
                        if (reserved) {
                            value = Long.parseLong(matcher.group(2));
                        } else {
                            value = Long.parseLong(matcher.group(3));
                        }
                        callback.call(value, category);
                    }
                }
            };
        }
        return callback -> {
            String summary = vmNativeMemorySummaryInBytesOrEmpty();
            if (!summary.isEmpty()) {
                Matcher matcher = pattern.matcher(summary);
                while (matcher.find()) {
                    String category = matcher.group(1);
                    var customLabelValues = customLabelsProvider.labelValues();
                    var labelValues = Arrays.copyOf(customLabelValues, customLabelValues.length + 1);
                    labelValues[customLabelValues.length] = category;
                    long value;
                    if (reserved) {
                        value = Long.parseLong(matcher.group(2));
                    } else {
                        value = Long.parseLong(matcher.group(3));
                    }
                    callback.call(value, labelValues);
                }
            }
        };
    }

    private String vmNativeMemorySummaryInBytesOrEmpty() {
        if (!isEnabled.get()) {
            return "";
        }
        try {
            // requires -XX:NativeMemoryTracking=summary
            String summary = adapter.vmNativeMemorySummaryInBytes();
            if (summary.isEmpty() || summary.trim().contains("Native memory tracking is not enabled")) {
                isEnabled.set(false);
                return "";
            } else {
                return summary;
            }
        } catch (Exception ex) {
            // ignore errors
            isEnabled.set(false);
            return "";
        }
    }

    interface PlatformMBeanServerAdapter {
        String vmNativeMemorySummaryInBytes();
    }

    static class DefaultPlatformMBeanServerAdapter implements PlatformMBeanServerAdapter {
        @Override
        public String vmNativeMemorySummaryInBytes() {
            try {
                return (String) ManagementFactory.getPlatformMBeanServer().invoke(
                        new ObjectName("com.sun.management:type=DiagnosticCommand"),
                        "vmNativeMemory",
                        new Object[]{new String[]{"summary", "scale=B"}},
                        new String[]{"[Ljava.lang.String;"});
            } catch (ReflectionException | MalformedObjectNameException | InstanceNotFoundException |
                     MBeanException e) {
                throw new IllegalStateException("Native memory tracking is not enabled", e);
            }
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

    public static class Builder {

        private final PrometheusProperties config;
        private final PlatformMBeanServerAdapter adapter;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this(config, new DefaultPlatformMBeanServerAdapter());
        }

        /**
         * Package private. For testing only.
         */
        Builder(PrometheusProperties config, PlatformMBeanServerAdapter adapter) {
            this.config = config;
            this.adapter = adapter;
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
            new JvmNativeMemoryMetrics(config, adapter, constLabels, customLabelsProvider).register(registry);
        }
    }
}