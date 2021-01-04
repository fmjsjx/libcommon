package com.github.fmjsjx.libcommon.util;

/**
 * Utility class for Array.
 */
public class ArrayUtil {

    /**
     * Returns the input array itself.
     * 
     * @param <T>    the element type of the array
     * @param values the input array
     * @return the input array itself
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] self(T... values) {
        return values;
    }

    private ArrayUtil() {
    }
}
