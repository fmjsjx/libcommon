package com.github.fmjsjx.libcommon.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Utility class for random.
 */
public class RandomUtil {

    /**
     * This interface imposes a weight on the objects of each class that implements
     * it.
     */
    public interface Weighted {

        /**
         * Returns the weight value.
         * 
         * @return the weight
         */
        int weight();

    }

    private static final class DefaultRandomInstanceHolder {
        private static final Random instance = new Random();
    }

    private static final Random defaultRandom() {
        return DefaultRandomInstanceHolder.instance;
    }

    private static final Random ensure(Random r) {
        return r == null ? defaultRandom() : r;
    }

    /**
     * Returns a pseudo random, uniformly distributed {@code int} value between 0
     * (inclusive) and the specified value (exclusive), drawn from the specified
     * random number generator's sequence.
     * 
     * @param bound  the upper bound (exclusive), must be positive
     * @param random the random
     * @return the next pseudo random, uniformly distributed {@code int} value
     *         between zero (inclusive) and bound (exclusive) from the specified
     *         random number generator's sequence
     */
    public static final int randomInt(int bound, Random random) {
        return ensure(random).nextInt(bound);
    }

    /**
     * Returns a pseudo random, uniformly distributed {@code int} value between 0
     * (inclusive) and the specified value (exclusive), drawn from the default
     * random number generator's sequence.
     * 
     * @param bound bound the upper bound (exclusive), must be positive
     * @return the next pseudo random, uniformly distributed {@code int} value
     *         between zero (inclusive) and bound (exclusive) from the default
     *         random number generator's sequence
     */
    public static final int randomInt(int bound) {
        return randomInt(bound, null);
    }

    /**
     * Returns the next pseudo random, uniformly distributed {@code long} value from
     * the specified random number generator's sequence.
     * 
     * @param random the random
     * @return the next pseudo random, uniformly distributed {@code long} value from
     *         the specified random number generator's sequence
     */
    public static final long randomLong(Random random) {
        return ensure(random).nextLong();
    }

    /**
     * Returns the next pseudo random, uniformly distributed {@code long} value from
     * the default random number generator's sequence.
     * 
     * @return the next pseudo random, uniformly distributed {@code long} value from
     *         the default random number generator's sequence
     */
    public static final long randomLong() {
        return randomLong(null);
    }

    /**
     * Returns a pseudo random, uniformly distributed {@code int} value between the
     * specified {@code min} (inclusive) and the specified {@code max} (inclusive).
     * 
     * @param min the minimum value
     * @param max the maximum value
     * @return the pseudo random, uniformly distributed {@code int} value between
     *         the specified {@code min} (inclusive) and the specified {@code max}
     *         (inclusive)
     */
    public static final int randomInRange(int min, int max) {
        return randomInRange(min, max, null);
    }

    /**
     * Returns a pseudo random, uniformly distributed {@code int} value between the
     * specified {@code min} (inclusive) and the specified {@code max} (inclusive).
     * 
     * @param min    the minimum value
     * @param max    the maximum value
     * @param random the random
     * @return the pseudo random, uniformly distributed {@code int} value between
     *         the specified {@code min} (inclusive) and the specified {@code max}
     *         (inclusive)
     * 
     */
    public static final int randomInRange(int min, int max, Random random) {
        if (max == min) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("min(" + min + ") is greater than max(" + max + ")");
        }
        int bound = max + 1 - min;
        return randomInt(bound, random) + min;
    }

    /**
     * Returns a random element from the specified {@code int} array.
     * 
     * @param values the {@code int} array
     * @return a random element from the specified {@code int} array
     */
    public static final int randomOne(int[] values) {
        var length = values.length;
        switch (length) {
        case 0:
            throw new IllegalArgumentException("values must not be empty");
        case 1:
            return values[0];
        default:
            return values[randomInt(length)];
        }
    }

    /**
     * Returns a random element from the specified {@code long} array.
     * 
     * @param values the {@code long} array
     * @return a random element from the specified {@code long} array
     */
    public static final long randomOne(long[] values) {
        var length = values.length;
        switch (length) {
        case 0:
            throw new IllegalArgumentException("values must not be empty");
        case 1:
            return values[0];
        default:
            return values[randomInt(length)];
        }
    }

    /**
     * Returns a random element from the specified array.
     * 
     * @param <T>    the type of the element from the specified array
     * @param values the array
     * @return a random element from the specified array
     */
    public static final <T> T randomOne(T[] values) {
        var length = values.length;
        switch (length) {
        case 0:
            throw new IllegalArgumentException("values must not be empty");
        case 1:
            return values[0];
        default:
            return values[randomInt(length)];
        }
    }

    /**
     * Returns a random element from the specified list.
     * 
     * @param <T>    the type of the element from the specified list
     * @param values the list
     * @return a random element from the specified list
     */
    public static final <T> T randomOne(List<T> values) {
        Objects.requireNonNull(values, "values must not be null");
        var size = values.size();
        switch (size) {
        case 0:
            throw new IllegalArgumentException("values must not be empty");
        case 1:
            return values.get(0);
        default:
            return values.get(randomInt(size));
        }
    }

    /**
     * Returns a random element from the specified collection.
     * 
     * @param <T>    the type of the element from the specified collection
     * @param values the list
     * @return a random element from the specified collection
     */
    @SuppressWarnings("unchecked")
    public static final <T> T randomOne(Collection<T> values) {
        if (values instanceof List) {
            return randomOne((List<T>) values);
        }
        Objects.requireNonNull(values, "values must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        return (T) randomOne(values.toArray());
    }

    /**
     * Returns a random index from the specified weights.
     * 
     * @param weights the weights
     * @return a random index from the specified weights
     */
    public static final int randomIndex(int[] weights) {
        if (weights.length == 0) {
            throw new IllegalArgumentException("weights must not be empty");
        }
        int n = 0;
        for (int i : weights) {
            if (i < 0) {
                throw new IllegalArgumentException("all weights must >= 0: " + Arrays.toString(weights));
            }
            n += i;
            if (n < 0) {
                throw new IllegalArgumentException("too large weights: " + Arrays.toString(weights));
            }
        }
        if (n == 0) {
            throw new IllegalArgumentException("no effective weight in weights: " + Arrays.toString(weights));
        }
        n = randomInt(n);
        for (int i = 0; i < weights.length; i++) {
            n -= weights[i];
            if (n < 0) {
                return i;
            }
        }
        // can't reach this line
        return -1;
    }

    /**
     * Returns a random index from the specified array.
     * 
     * @param <T>    the type of the element from the specified array
     * @param values the array
     * @return a random index from the specified array
     */
    public static final <T extends Weighted> int randomIndex(T[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        var weights = Arrays.stream(values).mapToInt(Weighted::weight).toArray();
        return randomIndex(weights);
    }

    /**
     * Returns a random index from the specified list.
     * 
     * @param <T>    the type of the element from the specified list
     * @param values the list
     * @return a random index from the specified list
     */
    public static final <T extends Weighted> int randomIndex(List<T> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        var weights = values.stream().mapToInt(Weighted::weight).toArray();
        return randomIndex(weights);
    }

    /**
     * Returns a random element from the specified array.
     * 
     * @param <T>    the type of the element from the specified array
     * @param values the array
     * @return a random index from the specified array
     */
    @SafeVarargs
    public static final <T extends Weighted> T randomOneWeighted(T... values) {
        return values[randomIndex(values)];
    }

    /**
     * Returns a random element from the specified list.
     * 
     * @param <T>    the type of the element from the specified list
     * @param values the list
     * @return a random index from the specified list
     */
    public static final <T extends Weighted> T randomOneWeighted(List<T> values) {
        return values.get(randomIndex(values));
    }

    private RandomUtil() {
    }

}
