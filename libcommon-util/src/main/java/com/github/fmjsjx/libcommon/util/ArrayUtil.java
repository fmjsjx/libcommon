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
    public static final int forEachUnless(LongForEachProcessor processor, long... array) {
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
    @SafeVarargs
    public static final <T> int forEachUnless(ForEachProcessor<? super T> processor, T... array) {
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
    public static final int forEachUntil(LongForEachProcessor processor, long... array) {
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
    @SafeVarargs
    public static final <T> int forEachUntil(ForEachProcessor<? super T> processor, T... array) {
        var len = array.length;
        for (int i = 0; i < len; i++) {
            if (processor.process(i, array[i])) {
                return i;
            }
        }
        return len;
    }

    private ArrayUtil() {
    }
}
