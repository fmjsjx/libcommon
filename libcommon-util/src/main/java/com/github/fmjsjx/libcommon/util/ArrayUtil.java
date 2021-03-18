package com.github.fmjsjx.libcommon.util;

import java.util.Arrays;

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

    /**
     * Returns a string representation of the contents of the specified array.
     * 
     * @param arrayObj the array object
     * @return a string representation of the contents of the specified array
     */
    public static final String toString(Object arrayObj) {
        Class<?> type = arrayObj.getClass();
        if (type.isArray()) {
            if (type == byte[].class) {
                return Arrays.toString((byte[]) arrayObj);
            } else if (type == short[].class) {
                return Arrays.toString((short[]) arrayObj);
            } else if (type == int[].class) {
                return Arrays.toString((int[]) arrayObj);
            } else if (type == long[].class) {
                return Arrays.toString((long[]) arrayObj);
            } else if (type == char[].class) {
                return Arrays.toString((char[]) arrayObj);
            } else if (type == float[].class) {
                return Arrays.toString((float[]) arrayObj);
            } else if (type == double[].class) {
                return Arrays.toString((double[]) arrayObj);
            } else if (type == boolean[].class) {
                return Arrays.toString((boolean[]) arrayObj);
            } else { // obj is an array of object references
                return Arrays.deepToString((Object[]) arrayObj);
            }
        } else {
            throw new IllegalArgumentException("Expect array but was " + type);
        }
    }

    private ArrayUtil() {
    }
}
