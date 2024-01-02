package com.github.fmjsjx.libcommon.prometheus.client;

import java.util.List;
import java.util.Objects;

/**
 * The abstract implementation of {@link CustomLabelsProvider}.
 *
 * @since 3.7
 */
public abstract class AbstractCustomLabelsProvider implements CustomLabelsProvider {

    private final List<String> labelNames;

    /**
     * Constructs instance with specified {@code labelNames} given.
     *
     * @param labelNames the label names
     */
    protected AbstractCustomLabelsProvider(String... labelNames) {
        this(List.of(labelNames));
    }

    /**
     * Constructs instance with specified {@code labelNames} given.
     *
     * @param labelNames the label names
     */
    protected AbstractCustomLabelsProvider(List<String> labelNames) {
        this.labelNames = List.copyOf(Objects.requireNonNull(labelNames, "labelNames must not be null"));
    }

    @Override
    public String[] labelNames() {
        return labelNames.toArray(String[]::new);
    }

}
