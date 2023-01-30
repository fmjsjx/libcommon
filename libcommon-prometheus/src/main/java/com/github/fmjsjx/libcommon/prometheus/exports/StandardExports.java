package com.github.fmjsjx.libcommon.prometheus.exports;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Exports the standard exports common across all prometheus clients.
 * <p>
 * This includes stats like CPU time spent and memory usage.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new StandardExports().register();
 * }
 * </pre>
 */
public class StandardExports extends Collector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardExports.class.getName());

    private final CustomLabelsProvider customLabelsProvider;
    private final StatusReader statusReader;
    private final OperatingSystemMXBean osBean;
    private final RuntimeMXBean runtimeBean;
    private final boolean linux;

    /**
     * Constructs new {@link StandardExports} instance without custom labels.
     */
    public StandardExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link StandardExports} instance with custom labels.
     *
     * @param customLabelNames  custom label names
     * @param customLabelValues custom label values
     */
    public StandardExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link StandardExports} instance with custom labels.
     *
     * @param customLabelsProvider the custom labels provider
     */
    public StandardExports(CustomLabelsProvider customLabelsProvider) {
        this(customLabelsProvider, new StatusReader(),
                ManagementFactory.getOperatingSystemMXBean(),
                ManagementFactory.getRuntimeMXBean());
    }

    StandardExports(CustomLabelsProvider customLabelsProvider,
                    StatusReader statusReader, OperatingSystemMXBean osBean, RuntimeMXBean runtimeBean) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        this.statusReader = Objects.requireNonNull(statusReader, "statusReader must not be null");
        this.osBean = Objects.requireNonNull(osBean, "osBean must not be null");
        this.runtimeBean = Objects.requireNonNull(runtimeBean, "runtimeBean must not be null");
        this.linux = (osBean.getName().indexOf("Linux") == 0);
    }

    private final static double KB = 1024;

    @Override
    public List<MetricFamilySamples> collect() {
        var mfs = new ArrayList<MetricFamilySamples>();
        var customLabelNames = customLabelsProvider.labelNames();
        var customLabelValues = customLabelsProvider.labelValues();
        try {
            // There exist at least 2 similar but unrelated UnixOperatingSystemMXBean interfaces, in
            // com.sun.management and com.ibm.lang.management. Hence use reflection and recursively go
            // through implemented interfaces until the method can be made accessible and invoked.
            var processCpuTime = callLongGetter("getProcessCpuTime", osBean);
            mfs.add(new CounterMetricFamily("process_cpu_seconds_total",
                    "Total user and system CPU time spent in seconds.",
                    customLabelNames).addMetric(customLabelValues, processCpuTime / NANOSECONDS_PER_SECOND));
        } catch (Exception e) {
            LOGGER.debug("Could not access process cpu time", e);
        }

        mfs.add(new GaugeMetricFamily("process_start_time_seconds",
                "Start time of the process since unix epoch in seconds.",
                customLabelNames).addMetric(customLabelValues, runtimeBean.getStartTime() / MILLISECONDS_PER_SECOND));

        // There exist at least 2 similar but unrelated UnixOperatingSystemMXBean interfaces, in
        // com.sun.management and com.ibm.lang.management. Hence use reflection and recursively go
        // through implemented interfaces until the method can be made accessible and invoked.
        try {
            var openFdCount = callLongGetter("getOpenFileDescriptorCount", osBean);
            mfs.add(new GaugeMetricFamily("process_open_fds", "Number of open file descriptors.",
                    customLabelNames).addMetric(customLabelValues, openFdCount));
            Long maxFdCount = callLongGetter("getMaxFileDescriptorCount", osBean);
            mfs.add(new GaugeMetricFamily("process_max_fds", "Maximum number of open file descriptors.",
                    customLabelNames).addMetric(customLabelValues, maxFdCount));
        } catch (Exception e) {
            // Ignore, expected on non-Unix OSs.
        }

        // There's no standard Java or POSIX way to get memory stats,
        // so add support for just Linux for now.
        if (linux) {
            try {
                collectMemoryMetricsLinux(mfs, customLabelNames, customLabelValues);
            } catch (Exception e) {
                // If the format changes, log a warning and return what we can.
                LOGGER.warn("Error occurs when collect memory metrics on linux", e);
            }
        }
        return mfs;
    }

    static Long callLongGetter(String getterName, Object obj)
            throws NoSuchMethodException, InvocationTargetException {
        return callLongGetter(obj.getClass().getMethod(getterName), obj);
    }

    /**
     * Attempts to call a method either directly or via one of the implemented interfaces.
     * <p>
     * A Method object refers to a specific method declared in a specific class. The first invocation
     * might happen with method == SomeConcreteClass.publicLongGetter() and will fail if
     * SomeConcreteClass is not public. We then recurse over all interfaces implemented by
     * SomeConcreteClass (or extended by those interfaces and so on) until we eventually invoke
     * callMethod() with method == SomePublicInterface.publicLongGetter(), which will then succeed.
     * <p>
     * There is a built-in assumption that the method will never return null (or, equivalently, that
     * it returns the primitive data type, i.e. {@code long} rather than {@code Long}). If this
     * assumption doesn't hold, the method might be called repeatedly and the returned value will be
     * the one produced by the last call.
     */
    static Long callLongGetter(Method method, Object obj) throws InvocationTargetException {
        try {
            return (Long) method.invoke(obj);
        } catch (IllegalAccessException e) {
            // Expected, the declaring class or interface might not be public.
        }

        // Iterate over all implemented/extended interfaces and attempt invoking the method with the
        // same name and parameters on each.
        for (Class<?> clazz : method.getDeclaringClass().getInterfaces()) {
            try {
                Method interfaceMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
                Long result = callLongGetter(interfaceMethod, obj);
                if (result != null) {
                    return result;
                }
            } catch (NoSuchMethodException e) {
                // Expected, class might implement multiple, unrelated interfaces.
            }
        }

        return null;
    }

    void collectMemoryMetricsLinux(List<MetricFamilySamples> mfs, List<String> customLabelNames, List<String> customLabelValues) {
        // statm/stat report in pages, and it's non-trivial to get pagesize from Java
        // So we parse status instead.
        BufferedReader br = null;
        try {
            br = statusReader.procSelfStatusReader();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("VmSize:")) {
                    mfs.add(new GaugeMetricFamily("process_virtual_memory_bytes",
                            "Virtual memory size in bytes.",
                            customLabelNames)
                            .addMetric(customLabelValues, Float.parseFloat(line.split("\\s+")[1]) * KB));
                } else if (line.startsWith("VmRSS:")) {
                    mfs.add(new GaugeMetricFamily("process_resident_memory_bytes",
                            "Resident memory size in bytes.",
                            customLabelNames)
                            .addMetric(customLabelValues, Float.parseFloat(line.split("\\s+")[1]) * KB));
                }
            }
        } catch (IOException e) {
            LOGGER.debug("I/O error occurs when read status", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.debug("I/O error occurs when close reader: {}", br, e);
                }
            }
        }
    }

    static class StatusReader {
        BufferedReader procSelfStatusReader() throws FileNotFoundException {
            return new BufferedReader(new FileReader("/proc/self/status"));
        }
    }
}
