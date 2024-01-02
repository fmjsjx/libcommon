package com.github.fmjsjx.libcommon.prometheus.client;

import java.util.List;
import java.util.function.Supplier;

/**
 * Implementations of {@link CustomLabelsProvider} that implement various useful providers.
 *
 * @since 3.7
 */
public final class CustomLabels {

    private static final class EmptyCustomLabelsProvider implements CustomLabelsProvider {

        private static final String[] emptyStringArray = new String[0];

        @Override
        public String[] labelNames() {
            return emptyStringArray;
        }

        @Override
        public String[] labelValues() {
            return emptyStringArray;
        }

    }

    private static final class EmptyCustomLabelsProviderInstanceHolder {
        private static final EmptyCustomLabelsProvider INSTANCE = new EmptyCustomLabelsProvider();
    }

    /**
     * Returns the empty provider.
     *
     * @return the empty provider
     */
    public static CustomLabelsProvider empty() {
        return EmptyCustomLabelsProviderInstanceHolder.INSTANCE;
    }

    /**
     * Returns the {@link CustomLabelsProvider} with the specified {@code labelNames} and the specified {@code labelValues} given.
     *
     * @param labelNames  the label names
     * @param labelValues the label values
     * @return the {@link CustomLabelsProvider}
     */
    public static CustomLabelsProvider of(List<String> labelNames, List<String> labelValues) {
        return new DefaultCustomLabelsProvider(labelNames, labelValues);
    }

    /**
     * Returns the {@link CustomLabelsProvider} with the specified {@code labelNames} and the specified {@code labelValuesSupplier} given.
     *
     * @param labelNames          the label names
     * @param labelValuesSupplier the supplier that returns label values
     * @return the {@link CustomLabelsProvider}
     */
    public static CustomLabelsProvider of(List<String> labelNames, Supplier<String[]> labelValuesSupplier) {
        return new DefaultCustomLabelsProvider(labelNames, labelValuesSupplier);
    }

    private CustomLabels() {
    }

}
