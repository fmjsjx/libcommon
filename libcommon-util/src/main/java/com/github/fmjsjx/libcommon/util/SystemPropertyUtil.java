package com.github.fmjsjx.libcommon.util;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods to retrieve and parse the values of the Java
 * system properties.
 * 
 * @since 2.2
 */
public class SystemPropertyUtil {

    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyUtil.class);

    /**
     * Returns {@code true} if and only if the system property with the specified
     * {@code key} exists.
     * 
     * @param key the key
     * @return {@code true} if and only if the system property with the specified
     *         {@code key} exists
     */
    public static boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to {@code null} if the property access fails.
     * 
     * @param key the key
     * @return the property value or {@code null}
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to the specified default value if the property access
     * fails.
     *
     * @param key the key
     * @param def the default value
     * @return the property value. {@code def} if there's no such property or if
     *         access to the specified property is not allowed.
     */
    public static String get(final String key, String def) {
        Objects.requireNonNull(key, "key must not be null");
        return System.getProperty(key, def);
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to the specified default value if the property access
     * fails.
     *
     * @param key the key
     * @param def the default value
     * @return the property value. {@code def} if there's no such property or if
     *         access to the specified property is not allowed.
     */
    public static boolean getBoolean(String key, boolean def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim().toLowerCase();
        if (value.isEmpty()) {
            return def;
        }

        if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
            return true;
        }

        if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
            return false;
        }

        logger.warn("Unable to parse the boolean system property '{}':{} - using the default value: {}", key, value,
                def);

        return def;
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to the specified default value if the property access
     * fails.
     *
     * @param key the key
     * @param def the default value
     * @return the property value. {@code def} if there's no such property or if
     *         access to the specified property is not allowed.
     */
    public static int getInt(String key, int def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            // Ignore
        }

        logger.warn("Unable to parse the integer system property '{}':{} - using the default value: {}", key, value,
                def);

        return def;
    }

    /**
     * Returns the value of the Java system property with the specified {@code key},
     * while falling back to the specified default value if the property access
     * fails.
     *
     * @param key the key
     * @param def the default value
     * @return the property value. {@code def} if there's no such property or if
     *         access to the specified property is not allowed.
     */
    public static long getLong(String key, long def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            // Ignore
        }

        logger.warn("Unable to parse the long integer system property '{}':{} - using the default value: {}", key,
                value, def);

        return def;
    }

    private SystemPropertyUtil() {
        // Unused
    }
}
