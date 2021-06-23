package com.github.fmjsjx.libcommon.bson;

/**
 * BSON dot notation.
 * 
 * @since 2.0
 */
public final class DotNotation {

    /**
     * Constructs a new {@link DotNotation} with {@code null} value
     * 
     * @return a {@code DotNotation}
     */
    public static final DotNotation root() {
        return RootInstanceHolder.instance;
    }

    private static final class RootInstanceHolder {
        private static final DotNotation instance = new DotNotation(null);
    }

    /**
     * Constructs a new {@link DotNotation} by the given parameters.
     * 
     * @param root the root name
     * @param keys the array of the keys
     * @return a {@code DotNotation}
     */
    public static final DotNotation of(DotNotation root, Object... keys) {
        return of(root.value, keys);
    }

    /**
     * Constructs a new {@link DotNotation} by the given parameters.
     * 
     * @param root the root name
     * @param keys the array of the keys
     * @return a {@code DotNotation}
     */
    public static final DotNotation of(String root, Object... keys) {
        var b = new StringBuilder().append(root);
        for (var key : keys) {
            b.append('.');
            if (key instanceof Number) {
                b.append(((Number) key).intValue());
            } else {
                b.append(key.toString());
            }
        }
        return new DotNotation(b.toString());
    }

    private final String value;

    DotNotation(String value) {
        this.value = value == null ? null : value.intern();
    }

    /**
     * Returns the string value of this dot notation.
     * 
     * @return the string value of this dot notation
     */
    public String value() {
        return value;
    }

    /**
     * Returns if this dot notation is root path or not.
     * 
     * @return {@code true} if this dot notation is root path, {@code false}
     *         otherwise
     */
    public boolean isRoot() {
        return value == null;
    }

    /**
     * Resolves the given name against this dot notation.
     * 
     * @param name the field name to be resolved
     * @return a new {@code DotNotation}
     */
    public DotNotation resolve(String name) {
        return new DotNotation(isRoot() ? name : value + "." + name);
    }

    /**
     * Resolves the given index against this dot notation.
     * 
     * @param index the index to be resolved
     * @return a new {@code DotNotation}
     */
    public DotNotation resolve(int index) {
        return new DotNotation(isRoot() ? Integer.toString(index) : value + "." + index);
    }

    /**
     * Returns the string value of this dot notation.
     * 
     * @return the string value of this dot notation
     */
    @Override
    public String toString() {
        return value();
    }

}
