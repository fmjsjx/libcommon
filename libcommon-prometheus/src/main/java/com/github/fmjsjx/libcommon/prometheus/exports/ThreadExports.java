package com.github.fmjsjx.libcommon.prometheus.exports;


import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.Predicate;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

import static io.prometheus.client.SampleNameFilter.ALLOW_ALL;

/**
 * Exports metrics about JVM thread areas.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new ThreadExports().register();
 *   // or with custom labels
 *   new ThreadExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jvm_threads_current{} 300
 *   jvm_threads_daemon{} 200
 *   jvm_threads_peak{} 410
 *   jvm_threads_started_total{} 1200
 * </pre>
 */
public class ThreadExports extends Collector {

    /**
     * Constant {@code "UNKNOWN"}.
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * Constant {@code "jvm_threads_state"}.
     */
    public static final String JVM_THREADS_STATE = "jvm_threads_state";

    private static final String JVM_THREADS_CURRENT = "jvm_threads_current";
    private static final String JVM_THREADS_DAEMON = "jvm_threads_daemon";
    private static final String JVM_THREADS_PEAK = "jvm_threads_peak";
    private static final String JVM_THREADS_STARTED_TOTAL = "jvm_threads_started_total";
    private static final String JVM_THREADS_DEADLOCKED = "jvm_threads_deadlocked";
    private static final String JVM_THREADS_DEADLOCKED_MONITOR = "jvm_threads_deadlocked_monitor";

    private final CustomLabelsProvider customLabelsProvider;
    private final ThreadMXBean threadBean;

    /**
     * Constructs new {@link ThreadExports} instance without any custom labels.
     */
    public ThreadExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link ThreadExports} instance with specified {@code customLabelNames} and specified
     * * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public ThreadExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link ThreadExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public ThreadExports(CustomLabelsProvider customLabelsProvider) {
        this(customLabelsProvider, ManagementFactory.getThreadMXBean());
    }

    /**
     * Constructs new {@link ThreadExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     * @param threadBean           the {@link ThreadMXBean}
     */
    public ThreadExports(CustomLabelsProvider customLabelsProvider, ThreadMXBean threadBean) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        this.threadBean = Objects.requireNonNull(threadBean, "threadBean must not be null");
    }

    private void addThreadMetrics(List<MetricFamilySamples> sampleFamilies, Predicate<String> nameFilter) {
        var labelNames = customLabelsProvider.labelNames();
        var labelValues = customLabelsProvider.labelValues();
        if (nameFilter.test(JVM_THREADS_CURRENT)) {
            sampleFamilies.add(
                    new GaugeMetricFamily(
                            JVM_THREADS_CURRENT,
                            "Current thread count of a JVM",
                            labelNames)
                            .addMetric(labelValues, threadBean.getThreadCount()));
        }

        if (nameFilter.test(JVM_THREADS_DAEMON)) {
            sampleFamilies.add(
                    new GaugeMetricFamily(
                            JVM_THREADS_DAEMON,
                            "Daemon thread count of a JVM",
                            labelNames).addMetric(labelValues, threadBean.getDaemonThreadCount()));
        }

        if (nameFilter.test(JVM_THREADS_PEAK)) {
            sampleFamilies.add(
                    new GaugeMetricFamily(
                            JVM_THREADS_PEAK,
                            "Peak thread count of a JVM",
                            labelNames).addMetric(labelValues, threadBean.getPeakThreadCount()));
        }

        if (nameFilter.test(JVM_THREADS_STARTED_TOTAL)) {
            sampleFamilies.add(
                    new CounterMetricFamily(
                            JVM_THREADS_STARTED_TOTAL,
                            "Started thread count of a JVM",
                            labelNames).addMetric(labelValues, threadBean.getTotalStartedThreadCount()));
        }

        if (nameFilter.test(JVM_THREADS_DEADLOCKED)) {
            sampleFamilies.add(
                    new GaugeMetricFamily(
                            JVM_THREADS_DEADLOCKED,
                            "Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers",
                            labelNames).addMetric(labelValues, nullSafeArrayLength(threadBean.findDeadlockedThreads())));
        }

        if (nameFilter.test(JVM_THREADS_DEADLOCKED_MONITOR)) {
            sampleFamilies.add(
                    new GaugeMetricFamily(
                            JVM_THREADS_DEADLOCKED_MONITOR,
                            "Cycles of JVM-threads that are in deadlock waiting to acquire object monitors",
                            labelNames).addMetric(labelValues, nullSafeArrayLength(threadBean.findMonitorDeadlockedThreads())));
        }

        if (nameFilter.test(JVM_THREADS_STATE)) {
            var names = new ArrayList<String>(labelNames.size() + 1);
            names.addAll(labelNames);
            names.add("state");
            GaugeMetricFamily threadStateFamily = new GaugeMetricFamily(
                    JVM_THREADS_STATE,
                    "Current count of threads by state",
                    names);

            Map<String, Integer> threadStateCounts = getThreadStateCountMap();
            for (Map.Entry<String, Integer> entry : threadStateCounts.entrySet()) {
                var values = new ArrayList<String>(labelValues.size() + 1);
                values.addAll(labelValues);
                values.add(entry.getKey());
                threadStateFamily.addMetric(values, entry.getValue());
            }
            sampleFamilies.add(threadStateFamily);
        }
    }

    private Map<String, Integer> getThreadStateCountMap() {
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
        var allThreads = threadBean.getThreadInfo(threadIds, 0);

        // Initialize the map with all thread states
        var threadCounts = new LinkedHashMap<String, Integer>();
        for (var state : Thread.State.values()) {
            threadCounts.put(state.name(), 0);
        }

        // Collect the actual thread counts
        for (var curThread : allThreads) {
            if (curThread != null) {
                Thread.State threadState = curThread.getThreadState();
                threadCounts.put(threadState.name(), threadCounts.get(threadState.name()) + 1);
            }
        }

        // Add the thread count for invalid thread ids
        threadCounts.put(UNKNOWN, numberOfInvalidThreadIds);

        return threadCounts;
    }

    private static double nullSafeArrayLength(long[] array) {
        return null == array ? 0 : array.length;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return collect(null);
    }

    @Override
    public List<MetricFamilySamples> collect(Predicate<String> nameFilter) {
        var mfs = new ArrayList<MetricFamilySamples>();
        addThreadMetrics(mfs, nameFilter == null ? ALLOW_ALL : nameFilter);
        return mfs;
    }

}
