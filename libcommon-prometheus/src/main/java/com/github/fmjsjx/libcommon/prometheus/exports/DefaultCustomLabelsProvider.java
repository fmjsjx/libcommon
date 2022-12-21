package com.github.fmjsjx.libcommon.prometheus.exports;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The default implementation of {@link CustomLabelsProvider}.
 *
 * @see AbstractCustomLabelsProvider
 * @see EmptyCustomLabelsProvider
 * @since 2.9
 */
public class DefaultCustomLabelsProvider extends AbstractCustomLabelsProvider {

    private final Supplier<List<String>> labelValuesSupplier;

    /**
     * Constructs a new {@link DefaultCustomLabelsProvider} instance.
     *
     * @param labelNames          label names
     * @param labelValuesSupplier the supplier that returns label values
     */
    public DefaultCustomLabelsProvider(List<String> labelNames, Supplier<List<String>> labelValuesSupplier) {
        super(labelNames);
        this.labelValuesSupplier = Objects.requireNonNull(labelValuesSupplier, "labelValuesSupplier must not be null");
    }

    /**
     * Constructs a new {@link DefaultCustomLabelsProvider} instance.
     *
     * @param labelValuesSupplier the supplier that returns label values
     * @param labelNames          label names
     */
    public DefaultCustomLabelsProvider(Supplier<List<String>> labelValuesSupplier, String... labelNames) {
        super(labelNames);
        this.labelValuesSupplier = Objects.requireNonNull(labelValuesSupplier, "labelValuesSupplier must not be null");
    }

    /**
     * Constructs a new {@link DefaultCustomLabelsProvider} instance.
     *
     * @param labelNames  label names
     * @param labelValues label values
     */
    public DefaultCustomLabelsProvider(List<String> labelNames, List<String> labelValues) {
        super(labelNames);
        var values = List.copyOf(Objects.requireNonNull(labelValues, "labelValues must not be null"));
        this.labelValuesSupplier = () -> values;
    }

    @Override
    public List<String> labelValues() {
        return labelValuesSupplier.get();
    }

}
