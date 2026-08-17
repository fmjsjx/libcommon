package com.github.fmjsjx.libcommon.util;

import java.util.Arrays;

import com.github.fmjsjx.libcommon.function.ForEachAction;
import com.github.fmjsjx.libcommon.function.ForEachProcessor;
import com.github.fmjsjx.libcommon.function.IntForEachAction;
import com.github.fmjsjx.libcommon.function.IntForEachProcessor;
import com.github.fmjsjx.libcommon.function.LongForEachAction;
import com.github.fmjsjx.libcommon.function.LongForEachProcessor;

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

    /**
     * Performs the given action for each index and element of the given {@code int}
     * array.
     *
     * @param array  an {@code int} array
     * @param action the action to be performed for each index and element
     */
    public static final void forEach(int[] array, IntForEachAction action) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Performs the given action for each index and element of the given
     * {@code long} array.
     *
     * @param array  an {@code long} array
     * @param action the action to be performed for each index and element
     */
    public static final void forEach(long[] array, LongForEachAction action) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Performs the given action for each index and element of the given array.
     *
     * @param <T>    the element type of the array
     * @param array  the array
     * @param action the action to be performed for each index and element
     */
    public static final <T> void forEach(T[] array, ForEachAction<T> action) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Performs the given action for each index and element of the given {@code int}
     * array.
     *
     * @param array  an {@code int} array
     * @param action the action to be performed for each index and element
     */
    public static final void forEach(IntForEachAction action, int... array) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Performs the given action for each index and element of the given
     * {@code long} array.
     *
     * @param array  an {@code long} array
     * @param action the action to be performed for each index and element
     */
    public static final void forEach(LongForEachAction action, long... array) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Performs the given action for each index and element of the given array.
     *
     * @param <T>    the element type of the array
     * @param action the action to be performed for each index and element
     * @param array  the array
     */
    @SafeVarargs
    public static final <T> void forEach(ForEachAction<T> action, T... array) {
        for (int i = 0; i < array.length; i++) {
            action.accept(i, array[i]);
        }
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link IntForEachProcessor#process(int, int)} returned {@code false}.
     */
    public static final int forEachUnless(int[] array, IntForEachProcessor processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (!processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link LongForEachProcessor#process(int, long)} returned
     *         {@code false}.
     */
    public static final int forEachUnless(long[] array, LongForEachProcessor processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (!processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param <T>       the element type of the array
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link ForEachProcessor#process(int, Object)} returned {@code false}.
     */
    public static final <T> int forEachUnless(T[] array, ForEachProcessor<? super T> processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (!processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link IntForEachProcessor#process(int, int)} returned {@code false}.
     */
    public static final int forEachUnless(IntForEachProcessor processor, int... array) {
        return forEachUnless(array, processor);
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link LongForEachProcessor#process(int, long)} returned
     *         {@code false}.
     */
    public static final int forEachUnless(LongForEachProcessor processor, long... array) {
        return forEachUnless(array, processor);
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param <T>       the element type of the array
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link ForEachProcessor#process(int, Object)} returned {@code false}.
     */
    @SafeVarargs
    public static final <T> int forEachUnless(ForEachProcessor<? super T> processor, T... array) {
        return forEachUnless(array, processor);
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link IntForEachProcessor#process(int, int)} returned {@code true}.
     */
    public static final int forEachUntil(int[] array, IntForEachProcessor processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link LongForEachProcessor#process(int, long)} returned
     *         {@code true}.
     */
    public static final int forEachUntil(long[] array, LongForEachProcessor processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param <T>       the element type of the array
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link ForEachProcessor#process(int, Object)} returned {@code true}.
     */
    public static final <T> int forEachUntil(T[] array, ForEachProcessor<? super T> processor) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link IntForEachProcessor#process(int, int)} returned {@code true}.
     */
    public static final int forEachUntil(IntForEachProcessor processor, int... array) {
        return forEachUntil(array, processor);
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link LongForEachProcessor#process(int, long)} returned
     *         {@code true}.
     */
    public static final int forEachUntil(LongForEachProcessor processor, long... array) {
        return forEachUntil(array, processor);
    }

    /**
     * Iterates over each index and element of the specified {@code array} with the
     * specified {@code processor}.
     *
     * @param <T>       the element type of the array
     * @param array     the array
     * @param processor the processor
     * @return The length of the {@code array} if the processor iterated to the end
     *         of the {@code array}. The last-visited index If the
     *         {@link ForEachProcessor#process(int, Object)} returned {@code true}.
     */
    @SafeVarargs
    public static final <T> int forEachUntil(ForEachProcessor<? super T> processor, T... array) {
        return forEachUntil(array, processor);
    }

    /**
     * A soft maximum array length imposed by array growth computations.
     * Some JVMs (such as HotSpot) have an implementation limit that will cause
     * {@code OutOfMemoryError("Requested array size exceeds VM limit")}
     * to be thrown if a request is made to allocate an array of some length near
     * {@code Integer.MAX_VALUE}, even if there is sufficient heap available. The actual
     * limit might depend on some JVM implementation-specific characteristics such
     * as the object header size. The soft maximum value is chosen conservatively so
     * as to be smaller than any implementation limit that is likely to be encountered.
     */
    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    /**
     * Computes a new array length given an array's current length, a minimum growth
     * amount, and a preferred growth amount. The computation is done in an overflow-safe
     * fashion.
     * <p>
     * This method is used by objects that contain an array that might need to be grown
     * in order to fulfill some immediate need (the minimum growth amount) but would also
     * like to request more space (the preferred growth amount) in order to accommodate
     * potential future needs. The returned length is usually clamped at the soft maximum
     * length in order to avoid hitting the JVM implementation limit. However, the soft
     * maximum will be exceeded if the minimum growth amount requires it.
     * <p>
     * If the preferred growth amount is less than the minimum growth amount, the
     * minimum growth amount is used as the preferred growth amount.
     * <p>
     * The preferred length is determined by adding the preferred growth amount to the
     * current length. If the preferred length does not exceed the soft maximum length
     * (SOFT_MAX_ARRAY_LENGTH) then the preferred length is returned.
     * <p>
     * If the preferred length exceeds the soft maximum, we use the minimum growth
     * amount. The minimum required length is determined by adding the minimum growth
     * amount to the current length. If the minimum required length exceeds Integer.MAX_VALUE,
     * then this method throws OutOfMemoryError. Otherwise, this method returns the greater of
     * the soft maximum or the minimum required length.
     * <p>
     * Note that this method does not do any array allocation itself; it only does array
     * length growth computations. However, it will throw OutOfMemoryError as noted above.
     * <p>
     * Note also that this method cannot detect the JVM's implementation limit, and it
     * may compute and return a length value up to and including Integer.MAX_VALUE that
     * might exceed the JVM's implementation limit. In that case, the caller will likely
     * attempt an array allocation with that length and encounter an OutOfMemoryError.
     * Of course, regardless of the length value returned from this method, the caller
     * may encounter OutOfMemoryError if there is insufficient heap to fulfill the request.
     *
     * @param oldLength  current length of the array (must be nonnegative)
     * @param minGrowth  minimum required growth amount (must be positive)
     * @param prefGrowth preferred growth amount
     * @return the new array length
     * @throws OutOfMemoryError if the new length would exceed Integer.MAX_VALUE
     */
    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        // preconditions not checked because of inlining
        // assert oldLength >= 0
        // assert minGrowth > 0

        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            // put code cold in a separate method
            return hugeLength(oldLength, minGrowth);
        }
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) { // overflow
            throw new OutOfMemoryError(
                    "Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
    }

    private ArrayUtil() {
    }
}
