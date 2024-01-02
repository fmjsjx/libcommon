package com.github.fmjsjx.libcommon.prometheus.client;

/**
 * Provides custom labels.
 *
 * @since 3.7
 */
public interface CustomLabelsProvider {

    /**
     * Returns custom label names.
     *
     * @return custom label names
     */
    String[] labelNames();

    /**
     * Returns custom label values.
     *
     * @return custom label values
     */
    String[] labelValues();

}
