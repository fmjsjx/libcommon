package com.github.fmjsjx.libcommon.prometheus.client;

import io.prometheus.metrics.expositionformats.OpenMetricsTextFormatWriter;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    static String convertToOpenMetricsFormat(MetricSnapshots snapshots) throws IOException {
        var out = new ByteArrayOutputStream();
        var writer = new OpenMetricsTextFormatWriter(true, true);
        writer.write(out, snapshots);
        return out.toString(StandardCharsets.UTF_8);
    }

}
