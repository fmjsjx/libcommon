package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JVM Thread metrics.
 *
 * @since 3.7
 */
public class JvmThreadsMetrics {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String JVM_THREADS_STATE = "jvm_threads_state";
    private static final String JVM_THREADS_CURRENT = "jvm_threads_current";
    private static final String JVM_THREADS_DAEMON = "jvm_threads_daemon";
    private static final String JVM_THREADS_PEAK = "jvm_threads_peak";
    private static final String JVM_THREADS_STARTED_TOTAL = "jvm_threads_started_total";
    private static final String JVM_THREADS_DEADLOCKED = "jvm_threads_deadlocked";
    private static final String JVM_THREADS_DEADLOCKED_MONITOR = "jvm_threads_deadlocked_monitor";

    private final PrometheusProperties config;
    private final ThreadMXBean threadBean;
    private final boolean isNativeImage;
    private final Labels constLabels;
    private final CustomLabelsProvider customLabelsProvider;

    private JvmThreadsMetrics(boolean isNativeImage, ThreadMXBean threadBean, PrometheusProperties config, Labels constLabels, CustomLabelsProvider customLabelsProvider) {
        this.config = config;
        this.threadBean = threadBean;
        this.isNativeImage = isNativeImage;
        this.constLabels = constLabels;
        this.customLabelsProvider = customLabelsProvider;
    }

    private void register(PrometheusRegistry registry) {
        var labelNames = customLabelsProvider.labelNames();
        var isLabelEmpty = labelNames.length == 0;
        GaugeWithCallback.builder(config)
                .name(JVM_THREADS_CURRENT)
                .help("Current thread count of a JVM")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(threadBean.getThreadCount(), customLabelsProvider.labelValues()))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_THREADS_DAEMON)
                .help("Daemon thread count of a JVM")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(threadBean.getDaemonThreadCount(), customLabelsProvider.labelValues()))
                .register(registry);

        GaugeWithCallback.builder(config)
                .name(JVM_THREADS_PEAK)
                .help("Peak thread count of a JVM")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(threadBean.getPeakThreadCount(), customLabelsProvider.labelValues()))
                .register(registry);

        CounterWithCallback.builder(config)
                .name(JVM_THREADS_STARTED_TOTAL)
                .help("Started thread count of a JVM")
                .constLabels(constLabels)
                .labelNames(labelNames)
                .callback(callback -> callback.call(threadBean.getTotalStartedThreadCount(), customLabelsProvider.labelValues()))
                .register(registry);

        if (!isNativeImage) {
            GaugeWithCallback.builder(config)
                    .name(JVM_THREADS_DEADLOCKED)
                    .help("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers")
                    .constLabels(constLabels)
                    .labelNames(labelNames)
                    .callback(callback -> callback.call(nullSafeArrayLength(threadBean.findDeadlockedThreads()), customLabelsProvider.labelValues()))
                    .register(registry);

            GaugeWithCallback.builder(config)
                    .name(JVM_THREADS_DEADLOCKED_MONITOR)
                    .help("Cycles of JVM-threads that are in deadlock waiting to acquire object monitors")
                    .constLabels(constLabels)
                    .labelNames(labelNames)
                    .callback(callback -> callback.call(nullSafeArrayLength(threadBean.findMonitorDeadlockedThreads()), customLabelsProvider.labelValues()))
                    .register(registry);

            var names = Arrays.copyOf(labelNames, labelNames.length + 1);
            names[labelNames.length] = "state";
            GaugeWithCallback.builder(config)
                    .name(JVM_THREADS_STATE)
                    .help("Current count of threads by state")
                    .constLabels(constLabels)
                    .labelNames(names)
                    .callback(isLabelEmpty ? callback -> {
                        Map<String, Integer> threadStateCounts = getThreadStateCountMap(threadBean);
                        for (Map.Entry<String, Integer> entry : threadStateCounts.entrySet()) {
                            callback.call(entry.getValue(), entry.getKey());
                        }
                    } : callback -> {
                        Map<String, Integer> threadStateCounts = getThreadStateCountMap(threadBean);
                        var labelValues = customLabelsProvider.labelValues();
                        for (Map.Entry<String, Integer> entry : threadStateCounts.entrySet()) {
                            var values = Arrays.copyOf(labelValues, labelValues.length + 1);
                            values[labelValues.length] = entry.getKey();
                            callback.call(entry.getValue(), values);
                        }
                    })
                    .register(registry);
        }
    }

    private Map<String, Integer> getThreadStateCountMap(ThreadMXBean threadBean) {
        long[] threadIds = threadBean.getAllThreadIds();

        // Code to remove any thread id values <= 0
        int writePos = 0;
        for (int i = 0; i < threadIds.length; i++) {
            if (threadIds[i] > 0) {
                threadIds[writePos++] = threadIds[i];
            }
        }

        int numberOfInvalidThreadIds = threadIds.length - writePos;
        threadIds = Arrays.copyOf(threadIds, writePos);

        // Get thread information without computing any stack traces
        ThreadInfo[] allThreads = threadBean.getThreadInfo(threadIds, 0);

        // Initialize the map with all thread states
        HashMap<String, Integer> threadCounts = new HashMap<>();
        for (Thread.State state : Thread.State.values()) {
            threadCounts.put(state.name(), 0);
        }

        // Collect the actual thread counts
        for (ThreadInfo curThread : allThreads) {
            if (curThread != null) {
                Thread.State threadState = curThread.getThreadState();
                threadCounts.put(threadState.name(), threadCounts.get(threadState.name()) + 1);
            }
        }

        // Add the thread count for invalid thread ids
        threadCounts.put(UNKNOWN, numberOfInvalidThreadIds);

        return threadCounts;
    }

    private double nullSafeArrayLength(long[] array) {
        return null == array ? 0 : array.length;
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
        private Boolean isNativeImage;
        private ThreadMXBean threadBean;
        private Labels constLabels = Labels.EMPTY;
        private CustomLabelsProvider customLabelsProvider = CustomLabels.empty();

        private Builder(PrometheusProperties config) {
            this.config = config;
        }

        /**
         * Package private. For testing only.
         *
         * @param threadBean the test thread bean
         * @return this builder
         */
        Builder threadBean(ThreadMXBean threadBean) {
            this.threadBean = threadBean;
            return this;
        }

        /**
         * Package private. For testing only.
         *
         * @param isNativeImage for test
         * @return this builder
         */
        Builder isNativeImage(boolean isNativeImage) {
            this.isNativeImage = isNativeImage;
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
            ThreadMXBean threadBean = this.threadBean != null ? this.threadBean : ManagementFactory.getThreadMXBean();
            boolean isNativeImage = this.isNativeImage != null ? this.isNativeImage : NativeImageChecker.isGraalVmNativeImage;
            new JvmThreadsMetrics(isNativeImage, threadBean, config, constLabels, customLabelsProvider).register(registry);
        }
    }

}
