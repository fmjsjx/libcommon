package com.github.fmjsjx.libcommon.util;

/**
 * Utility class for Object.
 */
public class ObjectUtil {

    /**
     * Returns a string representation of the object.
     * 
     * @param obj the object
     * @return a string representation of the object
     */
    public static final String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> type = obj.getClass();
        if (type.isArray()) {
            return ArrayUtil.toString(obj);
        }
        return obj.toString();
    }

    private ObjectUtil() {
    }

}
