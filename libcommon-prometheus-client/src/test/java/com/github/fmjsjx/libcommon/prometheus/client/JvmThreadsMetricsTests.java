package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.model.registry.MetricNameFilter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.GaugeSnapshot;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.MetricSnapshot;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.github.fmjsjx.libcommon.prometheus.client.TestUtil.convertToOpenMetricsFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class JvmThreadsMetricsTests {
    private final ThreadMXBean mockThreadsBean = Mockito.mock(ThreadMXBean.class);
    private final ThreadInfo mockThreadInfoBlocked = Mockito.mock(ThreadInfo.class);
    private final ThreadInfo mockThreadInfoRunnable1 = Mockito.mock(ThreadInfo.class);
    private final ThreadInfo mockThreadInfoRunnable2 = Mockito.mock(ThreadInfo.class);

    @BeforeEach
    public void setUp() {
        when(mockThreadsBean.getThreadCount()).thenReturn(300);
        when(mockThreadsBean.getDaemonThreadCount()).thenReturn(200);
        when(mockThreadsBean.getPeakThreadCount()).thenReturn(301);
        when(mockThreadsBean.getTotalStartedThreadCount()).thenReturn(503L);
        when(mockThreadsBean.findDeadlockedThreads()).thenReturn(new long[]{1L, 2L, 3L});
        when(mockThreadsBean.findMonitorDeadlockedThreads()).thenReturn(new long[]{2L, 3L, 4L});
        when(mockThreadsBean.getAllThreadIds()).thenReturn(new long[]{3L, 4L, 5L});
        when(mockThreadInfoBlocked.getThreadState()).thenReturn(Thread.State.BLOCKED);
        when(mockThreadInfoRunnable1.getThreadState()).thenReturn(Thread.State.RUNNABLE);
        when(mockThreadInfoRunnable2.getThreadState()).thenReturn(Thread.State.RUNNABLE);
        when(mockThreadsBean.getThreadInfo(new long[]{3L, 4L, 5L}, 0)).thenReturn(new ThreadInfo[]{
                mockThreadInfoBlocked, mockThreadInfoRunnable1, mockThreadInfoRunnable2
        });
    }

    @Test
    public void testGoodCase() throws IOException {
        var registry = new PrometheusRegistry();
        JvmThreadsMetrics.builder()
                .threadBean(mockThreadsBean)
                .constLabels(Labels.of("scope", "test"))
                .isNativeImage(false)
                .register(registry);
        String expected = """
                # TYPE jvm_threads_current gauge
                # HELP jvm_threads_current Current thread count of a JVM
                jvm_threads_current{scope="test"} 300.0
                # TYPE jvm_threads_daemon gauge
                # HELP jvm_threads_daemon Daemon thread count of a JVM
                jvm_threads_daemon{scope="test"} 200.0
                # TYPE jvm_threads_deadlocked gauge
                # HELP jvm_threads_deadlocked Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers
                jvm_threads_deadlocked{scope="test"} 3.0
                # TYPE jvm_threads_deadlocked_monitor gauge
                # HELP jvm_threads_deadlocked_monitor Cycles of JVM-threads that are in deadlock waiting to acquire object monitors
                jvm_threads_deadlocked_monitor{scope="test"} 3.0
                # TYPE jvm_threads_peak gauge
                # HELP jvm_threads_peak Peak thread count of a JVM
                jvm_threads_peak{scope="test"} 301.0
                # TYPE jvm_threads_started counter
                # HELP jvm_threads_started Started thread count of a JVM
                jvm_threads_started_total{scope="test"} 503.0
                # TYPE jvm_threads_state gauge
                # HELP jvm_threads_state Current count of threads by state
                jvm_threads_state{scope="test",state="BLOCKED"} 1.0
                jvm_threads_state{scope="test",state="NEW"} 0.0
                jvm_threads_state{scope="test",state="RUNNABLE"} 2.0
                jvm_threads_state{scope="test",state="TERMINATED"} 0.0
                jvm_threads_state{scope="test",state="TIMED_WAITING"} 0.0
                jvm_threads_state{scope="test",state="UNKNOWN"} 0.0
                jvm_threads_state{scope="test",state="WAITING"} 0.0
                # EOF
                """;

        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));

        registry = new PrometheusRegistry();
        JvmThreadsMetrics.builder()
                .threadBean(mockThreadsBean)
                .customLabels(CustomLabels.of(List.of("scope"), List.of("test")))
                .isNativeImage(false)
                .register(registry);
        assertEquals(expected, convertToOpenMetricsFormat(registry.scrape()));
    }

    @Test
    public void testIgnoredMetricNotScraped() {
        MetricNameFilter filter = MetricNameFilter.builder()
                .nameMustNotBeEqualTo("jvm_threads_deadlocked")
                .build();

        PrometheusRegistry registry = new PrometheusRegistry();
        JvmThreadsMetrics.builder()
                .threadBean(mockThreadsBean)
                .isNativeImage(false)
                .register(registry);
        registry.scrape(filter);

        verify(mockThreadsBean, times(0)).findDeadlockedThreads();
        verify(mockThreadsBean, times(1)).getThreadCount();
    }

    @Test
    public void testInvalidThreadIds() {
        try {
            String javaVersion = System.getProperty("java.version"); // Example: "21.0.2"
            String majorJavaVersion = javaVersion.replaceAll("\\..*", ""); // Example: "21"
            if (Integer.parseInt(majorJavaVersion) >= 21) {
                // With Java 21 and newer you can no longer have invalid thread ids.
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        PrometheusRegistry registry = new PrometheusRegistry();
        JvmThreadsMetrics.builder().register(registry);

        // Number of threads to create with invalid thread ids
        int numberOfInvalidThreadIds = 2;

        Map<String, Double> expected = getCountByState(registry.scrape());
        expected.compute("UNKNOWN", (key, oldValue) -> oldValue == null ? numberOfInvalidThreadIds : oldValue + numberOfInvalidThreadIds);

        final CountDownLatch countDownLatch = new CountDownLatch(numberOfInvalidThreadIds);

        try {
            // Create and start threads with invalid thread ids (id=0, id=-1, etc.)
            for (int i = 0; i < numberOfInvalidThreadIds; i++) {
                new ThreadWithInvalidId(-i, new TestRunnable(countDownLatch)).start();
            }

            Map<String, Double> actual = getCountByState(registry.scrape());

            assertEquals(expected.size(), actual.size());
            for (String threadState : expected.keySet()) {
                assertEquals(expected.get(threadState), actual.get(threadState), 0.0);
            }
        } finally {
            for (int i = 0; i < numberOfInvalidThreadIds; i++) {
                countDownLatch.countDown();
            }
        }
    }

    private Map<String, Double> getCountByState(MetricSnapshots snapshots) {
        Map<String, Double> result = new HashMap<>();
        for (MetricSnapshot snapshot : snapshots) {
            if (snapshot.getMetadata().getName().equals("jvm_threads_state")) {
                for (GaugeSnapshot.GaugeDataPointSnapshot data : ((GaugeSnapshot) snapshot).getDataPoints()) {
                    String state = data.getLabels().get("state");
                    assertNotNull(state);
                    result.put(state, data.getValue());
                }
            }
        }
        return result;
    }

    private static class ThreadWithInvalidId extends Thread {

        private final long id;

        public ThreadWithInvalidId(long id, Runnable runnable) {
            super(runnable);
            setDaemon(true);
            this.id = id;
        }

        /**
         * Note that only Java versions < 21 call this to get the thread id.
         * With Java 21 and newer it's no longer possible to make an invalid thread id.
         */
        @Override
        public long getId() {
            return this.id;
        }
    }

    private record TestRunnable(CountDownLatch countDownLatch) implements Runnable {

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                // DO NOTHING
            }
        }
    }

}
