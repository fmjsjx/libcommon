package com.github.fmjsjx.libcommon.prometheus.exports;

import java.util.List;

/**
 * The implementation of {@link CustomLabelsProvider} with empty labels.
 *
 * @since 2.9
 */
public class EmptyCustomLabelsProvider implements CustomLabelsProvider {

    private static final class InstanceHolder {
        private static final EmptyCustomLabelsProvider INSTANCE = new EmptyCustomLabelsProvider();

    }

    /**
     * Returns the singleton instance of {@link EmptyCustomLabelsProvider}.
     *
     * @return the singleton instance of {@code EmptyCustomLabelsProvider}
     */
    public static final EmptyCustomLabelsProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private EmptyCustomLabelsProvider() {
    }

    @Override
    public List<String> labelNames() {
        return List.of();
    }

    @Override
    public List<String> labelValues() {
        return List.of();
    }

}
