package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.CollectorRegistry;

import java.util.List;

/**
 * Registers the default Hotspot collectors.
 * <p>
 * This is intended to avoid users having to add in new
 * registrations every time a new exporter is added.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   DefaultExports.initialize();
 *   // or with custom labels
 *   DefaultExports.initialize(List.of("application"), List.of("example-app"));
 * }
 * </pre>
 */
public class DefaultExports {
    private static boolean initialized = false;

    /**
     * Register the default Hotspot collectors with the default
     * registry. It is safe to call this method multiple times, as
     * this will only register the collectors once.
     */
    public static synchronized void initialize() {
        initialize(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Register the default Hotspot collectors with the default
     * registry. It is safe to call this method multiple times, as
     * this will only register the collectors once.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public static synchronized void initialize(List<String> customLabelNames, List<String> customLabelValues) {
        initialize(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Register the default Hotspot collectors with the default
     * registry. It is safe to call this method multiple times, as
     * this will only register the collectors once.
     *
     * @param customLabelsProvider a {@link CustomLabelsProvider}
     */
    public static synchronized void initialize(CustomLabelsProvider customLabelsProvider) {
        initialize(customLabelsProvider, CollectorRegistry.defaultRegistry);
    }


    /**
     * Register the default Hotspot collectors with the default
     * registry. It is safe to call this method multiple times, as
     * this will only register the collectors once.
     *
     * @param customLabelsProvider a {@link CustomLabelsProvider}
     * @param registry             the registry of Collectors
     */
    public static synchronized void initialize(CustomLabelsProvider customLabelsProvider, CollectorRegistry registry) {
        if (!initialized) {
            register(customLabelsProvider, registry);
            initialized = true;
        }
    }

    private static void register(CustomLabelsProvider customLabelsProvider, CollectorRegistry registry) {
//        new StandardExports().register(registry);
//        new MemoryPoolsExports().register(registry);
//        new MemoryAllocationExports().register(registry);
        new BufferPoolsExports(customLabelsProvider).register(registry);
        new GarbageCollectorExports(customLabelsProvider).register(registry);
        new ThreadExports(customLabelsProvider).register(registry);
        new ClassLoadingExports(customLabelsProvider).register(registry);
        new VersionInfoExports(customLabelsProvider).register(registry);
    }

}