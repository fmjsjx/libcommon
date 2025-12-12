package com.github.fmjsjx.libcommon.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for {@link Executor}s.
 */
public class ExecutorUtil {

    /**
     * Returns a new Executor that submits a task to the given base executor after
     * the given delay (or no delay if non-positive).
     *
     * @param delay    how long to delay, in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    public static final Executor delayedExecutor(long delay, TimeUnit unit, ScheduledExecutorService executor) {
        return new DelayedExecutor(delay, unit, executor);
    }

    private static final class DelayedExecutor implements Executor {

        private final long delay;
        private final TimeUnit unit;
        private final ScheduledExecutorService executor;

        private DelayedExecutor(long delay, TimeUnit unit, ScheduledExecutorService executor) {
            this.delay = delay;
            this.unit = unit;
            this.executor = executor;
        }

        @Override
        public void execute(Runnable command) {
            executor.schedule(command, delay, unit);
        }

    }

    private ExecutorUtil() {
    }

}
