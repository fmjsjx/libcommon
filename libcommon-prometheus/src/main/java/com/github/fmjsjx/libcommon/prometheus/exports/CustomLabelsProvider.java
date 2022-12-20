package com.github.fmjsjx.libcommon.prometheus.exports;

import java.util.List;

/**
 * Provides custom labels.
 *
 * @since 2.9
 */
public interface CustomLabelsProvider {

    /**
     * Returns custom label names.
     *
     * @return custom label names
     */
    List<String> labelNames();

    /**
     * Returns custom label values.
     *
     * @return custom label values
     */
    List<String> labelValues();

}

