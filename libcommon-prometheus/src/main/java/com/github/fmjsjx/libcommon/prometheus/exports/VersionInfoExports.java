package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import io.prometheus.client.Info;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Exports JVM version info.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new VersionInfoExports().register();
 *   // or with custom labels
 *   new VersionInfoExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Metrics being exported:
 * <pre>
 *   jvm_info{runtime="Java(TM) SE Runtime Environment",vendor="Oracle Corporation",version="11.0.17+10-LTS-269",} 1.0
 * </pre>
 */
public final class VersionInfoExports extends Collector {

    private final CustomLabelsProvider customLabelsProvider;

    /**
     * Constructs new {@link VersionInfoExports} instance without any custom labels.
     */
    public VersionInfoExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link VersionInfoExports} instance with specified {@code customLabelNames} and specified
     * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public VersionInfoExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link VersionInfoExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public VersionInfoExports(CustomLabelsProvider customLabelsProvider) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        Stream.of("version", "vendor", "runtime")
                .filter(v -> this.customLabelsProvider.labelNames().contains(v))
                .findFirst().ifPresent(name -> {
                    throw new IllegalArgumentException("VersionInfoExports cannot have a custom label name: " + name);
                });
    }

    @Override
    public List<MetricFamilySamples> collect() {
        var customLabelsProvider = this.customLabelsProvider;
        var labelNames = customLabelsProvider.labelNames().toArray(String[]::new);
        Info i = Info.build().name("jvm").help("VM version info").labelNames(labelNames).create();
        var labels = customLabelsProvider.labelValues().toArray(String[]::new);
        i.labels(labels).info(
                "version", System.getProperty("java.runtime.version", "unknown"),
                "vendor", System.getProperty("java.vm.vendor", "unknown"),
                "runtime", System.getProperty("java.runtime.name", "unknown")
        );
        return i.collect();
    }

}
