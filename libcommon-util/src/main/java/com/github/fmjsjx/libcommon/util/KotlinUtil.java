package com.github.fmjsjx.libcommon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Utility for Kotlin.
 *
 * @since 2.7
 */
public class KotlinUtil {

    private static final Class<? extends Annotation> kotlinMetadata;

    private static final boolean kotlinReflectPresent;

    static {
        ClassLoader classLoader = KotlinUtil.class.getClassLoader();
        kotlinMetadata = ReflectUtil.<Annotation>findForName("kotlin.Metadata", classLoader).orElse(null);
        kotlinReflectPresent = ReflectUtil.hasClassForName("kotlin.reflect.full.KClasses", classLoader);
    }

    /**
     * Determine whether Kotlin is present in general.
     *
     * @return {@code true} if Kotlin is present in general, {@code false} otherwise
     * @since 2.7
     */
    public static final boolean isKotlinPresent() {
        return kotlinMetadata != null;
    }

    /**
     * Determine whether Kotlin reflection is present.
     *
     * @return {@code true} if Kotlin reflection is present, {@code false} otherwise
     * @since 2.7
     */
    public static final boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    /**
     * Determine whether the given {@code Class} is a Kotlin type (with Kotlin
     * metadata present on it).
     *
     * @param clazz the class
     * @return {@code true} if the given {@code Class} is a Kotlin type,
     *         {@code false} otherwise
     * @since 2.7
     */
    public static final boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata != null && clazz.getDeclaredAnnotation(kotlinMetadata) != null);
    }

    /**
     * Return {@code true} if the method is a suspending function.
     *
     * @param method the method
     * @return {@code true} if the method is a suspending function, {@code false}
     *         otherwise
     * @since 2.7
     */
    public static boolean isSuspendingFunction(Method method) {
        if (isKotlinType(method.getDeclaringClass())) {
            Class<?>[] types = method.getParameterTypes();
            return types.length > 0 && "kotlin.coroutines.Continuation".equals(types[types.length - 1].getName());
        }
        return false;
    }

    private KotlinUtil() {
    }

}
