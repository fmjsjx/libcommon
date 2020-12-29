package com.github.fmjsjx.libcommon.util;

import java.util.Optional;

/**
 * Utility for {@link Class}.
 */
public class ClassUtil {

    /**
     * Finds and returns the Class object associated with the class or interface
     * with the given string name.
     * 
     * @param <T>       the type of the returned class
     * @param className the fully qualified name of the desired class
     * @return an {@code Optional<Class<T>>}
     */
    public static final <T> Optional<Class<T>> findForName(String className) {
        try {
            @SuppressWarnings("unchecked")
            var clazz = (Class<T>) Class.forName(className);
            return Optional.of(clazz);
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns {@code true} if has the class or interface with the given string
     * name, {@code false} otherwise.
     * <p>
     * This method is equivalence to:
     * 
     * <pre>
     *   {@code findForName(className).isPresent()};
     * </pre>
     * 
     * @param className the fully qualified name of the desired class
     * @return {@code true} if has the class or interface with the given string
     *         name, {@code false} otherwise
     * @see #findForName(String)
     */
    public static final boolean hasClassForName(String className) {
        return findForName(className).isPresent();
    }

    private ClassUtil() {
    }
}
