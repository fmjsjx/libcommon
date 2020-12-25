package com.github.fmjsjx.libcommon.util;

public class NumberUtil {

    public static final int intValue(Number value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    public static final int intValue(Number value) {
        return intValue(value, 0);
    }

    private NumberUtil() {}

}
