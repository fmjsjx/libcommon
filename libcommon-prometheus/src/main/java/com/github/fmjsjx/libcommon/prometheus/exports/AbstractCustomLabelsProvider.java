package com.github.fmjsjx.libcommon.prometheus.exports;

import java.util.List;
import java.util.Objects;

/**
 * The abstract implementation of {@link CustomLabelsProvider}.
 *
 * @since 2.9
 * @see DefaultCustomLabelsProvider
 * @see EmptyCustomLabelsProvider
 */
public abstract class AbstractCustomLabelsProvider implements CustomLabelsProvider {

    private final List<String> labelNames;

    protected AbstractCustomLabelsProvider(String... labelNames) {
        this(List.of(labelNames));
    }

    protected AbstractCustomLabelsProvider(List<String> labelNames) {
        this.labelNames = List.copyOf(Objects.requireNonNull(labelNames, "labelNames must not be null"));
    }

    @Override
    public List<String> labelNames() {
        return labelNames;
    }

}
