package com.github.fmjsjx.libcommon.prometheus.exports;


import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.Predicate;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static io.prometheus.client.SampleNameFilter.ALLOW_ALL;

/**
 * Exports metrics about JVM buffers.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new BufferPoolsExports().register();
 *   // or with custom labels
 *   new BufferPoolsExports(List.of("application"), List.of("example-app")).register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jvm_buffer_pool_used_bytes{pool="mapped",} 0.0
 *   jvm_buffer_pool_used_bytes{pool="direct",} 0.0
 *   jvm_buffer_pool_capacity_bytes{pool="mapped",} 0.0
 *   jvm_buffer_pool_capacity_bytes{pool="direct",} 0.0
 *   jvm_buffer_pool_used_buffers{pool="mapped",} 0.0
 *   jvm_buffer_pool_used_buffers{pool="direct",} 0.0
 * </pre>
 */
public class BufferPoolsExports extends Collector {

    private static final String JVM_BUFFER_POOL_USED_BYTES = "jvm_buffer_pool_used_bytes";
    private static final String JVM_BUFFER_POOL_CAPACITY_BYTES = "jvm_buffer_pool_capacity_bytes";
    private static final String JVM_BUFFER_POOL_USED_BUFFERS = "jvm_buffer_pool_used_buffers";

    private static final Logger LOGGER = Logger.getLogger(io.prometheus.client.hotspot.BufferPoolsExports.class.getName());

    private final CustomLabelsProvider customLabelsProvider;
    private final List<Object> bufferPoolMXBeans = new ArrayList<>();
    private Method getName;
    private Method getMemoryUsed;
    private Method getTotalCapacity;
    private Method getCount;

    /**
     * Constructs new {@link BufferPoolsExports} instance without any custom labels.
     */
    public BufferPoolsExports() {
        this(EmptyCustomLabelsProvider.getInstance());
    }

    /**
     * Constructs new {@link BufferPoolsExports} instance with specified {@code customLabelNames} and specified
     * * {@code customLabelValues} given.
     *
     * @param customLabelNames  the custom label names
     * @param customLabelValues the custom label values
     */
    public BufferPoolsExports(List<String> customLabelNames, List<String> customLabelValues) {
        this(new DefaultCustomLabelsProvider(customLabelNames, customLabelValues));
    }

    /**
     * Constructs new {@link BufferPoolsExports} instance with custom labels provided by specified
     * {@code customLabelsProvider} given.
     *
     * @param customLabelsProvider the {@link CustomLabelsProvider}
     */
    public BufferPoolsExports(CustomLabelsProvider customLabelsProvider) {
        this.customLabelsProvider = Objects.requireNonNull(customLabelsProvider, "customLabelsProvider must not be null");
        if (customLabelsProvider.labelNames().contains("pool")) {
            throw new IllegalArgumentException("BufferPoolsExports cannot have a custom label name: pool");
        }
        try {
            final Class<?> bufferPoolMXBeanClass = Class.forName("java.lang.management.BufferPoolMXBean");
            bufferPoolMXBeans.addAll(accessBufferPoolMXBeans(bufferPoolMXBeanClass));

            getName = bufferPoolMXBeanClass.getMethod("getName");
            getMemoryUsed = bufferPoolMXBeanClass.getMethod("getMemoryUsed");
            getTotalCapacity = bufferPoolMXBeanClass.getMethod("getTotalCapacity");
            getCount = bufferPoolMXBeanClass.getMethod("getCount");

        } catch (ClassNotFoundException e) {
            LOGGER.fine("BufferPoolMXBean not available, no metrics for buffer pools will be exported");
        } catch (NoSuchMethodException e) {
            LOGGER.fine("Can not get necessary accessor from BufferPoolMXBean: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> accessBufferPoolMXBeans(final Class<?> bufferPoolMXBeanClass) {
        try {
            var getPlatformMXBeansMethod = ManagementFactory.class.getMethod("getPlatformMXBeans", Class.class);
            return (List<Object>) getPlatformMXBeansMethod.invoke(null, bufferPoolMXBeanClass);
        } catch (NoSuchMethodException e) {
            LOGGER.fine("ManagementFactory.getPlatformMXBeans not available, no metrics for buffer pools will be exported");
            return Collections.emptyList();
        } catch (IllegalAccessException e) {
            LOGGER.fine("ManagementFactory.getPlatformMXBeans not accessible, no metrics for buffer pools will be exported");
            return Collections.emptyList();
        } catch (InvocationTargetException e) {
            LOGGER.warning("ManagementFactory.getPlatformMXBeans could not be invoked, no metrics for buffer pools will be exported");
            return Collections.emptyList();
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return collect(null);
    }

    @Override
    public List<MetricFamilySamples> collect(Predicate<String> nameFilter) {
        var mfs = new ArrayList<MetricFamilySamples>();
        if (nameFilter == null) {
            nameFilter = ALLOW_ALL;
        }
        var customLabelNames = customLabelsProvider.labelNames();
        var customLabelValues = customLabelsProvider.labelValues();
        var labelNames = new ArrayList<String>(customLabelNames.size() + 1);
        labelNames.addAll(customLabelNames);
        labelNames.add("pool");
        GaugeMetricFamily used = null;
        if (nameFilter.test(JVM_BUFFER_POOL_USED_BYTES)) {
            used = new GaugeMetricFamily(JVM_BUFFER_POOL_USED_BYTES, "Used bytes of a given JVM buffer pool.", labelNames);
            mfs.add(used);
        }
        GaugeMetricFamily capacity = null;
        if (nameFilter.test(JVM_BUFFER_POOL_CAPACITY_BYTES)) {
            capacity = new GaugeMetricFamily(JVM_BUFFER_POOL_CAPACITY_BYTES, "Bytes capacity of a given JVM buffer pool.", labelNames);
            mfs.add(capacity);
        }
        GaugeMetricFamily buffers = null;
        if (nameFilter.test(JVM_BUFFER_POOL_USED_BUFFERS)) {
            buffers = new GaugeMetricFamily(JVM_BUFFER_POOL_USED_BUFFERS, "Used buffers of a given JVM buffer pool.", labelNames);
            mfs.add(buffers);
        }
        for (final Object pool : bufferPoolMXBeans) {
            var labelValues = new ArrayList<String>(customLabelValues.size() + 1);
            labelValues.addAll(customLabelValues);
            labelValues.add(getName(pool));
            if (used != null) {
                used.addMetric(labelValues, callLongMethod(getMemoryUsed, pool));
            }
            if (capacity != null) {
                capacity.addMetric(labelValues, callLongMethod(getTotalCapacity, pool));
            }
            if (buffers != null) {
                buffers.addMetric(labelValues, callLongMethod(getCount, pool));
            }
        }
        return mfs;
    }

    private long callLongMethod(final Method method, final Object pool) {
        try {
            return (Long) method.invoke(pool);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.fine("Couldn't call " + method.getName() + ": " + e.getMessage());
        }
        return 0L;
    }

    private String getName(final Object pool) {
        try {
            return (String) getName.invoke(pool);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.fine("Couldn't call getName " + e.getMessage());
        }
        return "<unknown>";
    }

}
