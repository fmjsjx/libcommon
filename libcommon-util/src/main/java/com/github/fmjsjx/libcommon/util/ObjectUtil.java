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

    /**
     * Compares the given two objects.
     * 
     * @param a the first object
     * @param b the second object
     * @return {@code true} if a and b are both {@code null} or a equals b,
     *         {@code false} otherwise
     * @since 2.0
     */
    public static final boolean isEquals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * Compares the given two objects.
     * <p>
     * This method is equivalent to {@link #isEquals(Object, Object) !isEquals(a,
     * b)}.
     * 
     * @param a the first object
     * @param b the second object
     * @return {@code false} if a and b are both {@code null} or a equals b,
     *         {@code true} otherwise
     * @since 2.0
     */
    public static final boolean isNotEquals(Object a, Object b) {
        return !isEquals(a, b);
    }

    private ObjectUtil() {
    }

}
