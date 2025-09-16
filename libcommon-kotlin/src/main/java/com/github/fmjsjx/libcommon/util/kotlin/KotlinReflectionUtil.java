package com.github.fmjsjx.libcommon.util.kotlin;

import com.github.fmjsjx.libcommon.util.KotlinUtil;
import com.github.fmjsjx.libcommon.util.ReflectUtil;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.*;
import kotlin.reflect.jvm.KTypesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Reflection utility methods specific to Kotlin reflection. Requires Kotlin classes to be present to avoid linkage errors.
 *
 * @see KotlinUtil#isKotlinReflectPresent()
 * @since 2.8
 */
public final class KotlinReflectionUtil {

    private static final Integer KOTLIN_CLASS_HEADER_KIND_CLASS = 1;

    /**
     * Return true if the specified class is a supported Kotlin class. Currently supported are only regular
     * Kotlin classes. Other class types (synthetic, SAM, lambdas) are not supported via reflection.
     *
     * @param type the class
     * @return {@code true} if type is a supported Kotlin class.
     */
    public static boolean isSupportedKotlinClass(Class<?> type) {
        if (!KotlinUtil.isKotlinType(type)) {
            return false;
        }
        return Arrays.stream(type.getDeclaredAnnotations()) //
                .filter(annotation -> annotation.annotationType().getName().equals("kotlin.Metadata")) //
                .map(annotation -> ReflectUtil.annotationValue(annotation, "k")) //
                .anyMatch(KOTLIN_CLASS_HEADER_KIND_CLASS::equals);
    }

    /**
     * Return {@literal true} if the specified class is a Kotlin data class.
     *
     * @param type the class
     * @return {@literal true} if {@code type} is a Kotlin data class.
     */
    public static final boolean isDataClass(Class<?> type) {
        if (!KotlinUtil.isKotlinType(type)) {
            return false;
        }
        return JvmClassMappingKt.getKotlinClass(type).isData();
    }


    /**
     * Returns a {@link KFunction} instance corresponding to the given Java {@link Method} instance, or {@code null} if
     * this method cannot be represented by a Kotlin function.
     *
     * @param <R> the return type of the method.
     * @param method the method to look up.
     * @return the {@link KFunction} or {@code null} if the method cannot be looked up.
     */
    @SuppressWarnings("unchecked")
    public static final <R> Optional<KFunction<R>> findKotlinFunction(Method method) {
        var kotlinFunction = ReflectJvmMapping.getKotlinFunction(method);
        // Fallback to own lookup because there's no public Kotlin API for that kind of lookup until
        // https://youtrack.jetbrains.com/issue/KT-20768 gets resolved.
        if (kotlinFunction == null) {
            return (Optional<KFunction<R>>) findKFunction(method);
        }
        return Optional.of((KFunction<R>) kotlinFunction);
    }

    /**
     * Returns whether the {@link Method} is declared as suspend (Kotlin Coroutine).
     *
     * @param method the method to inspect.
     * @return {@literal true} if the method is declared as suspend.
     * @see KFunction#isSuspend()
     */
    public static boolean isSuspend(Method method) {
        if (KotlinUtil.isKotlinType(method.getDeclaringClass())) {
            return findKotlinFunction(method).map(KFunction::isSuspend).orElse(false);
        }
        return false;
    }

    /**
     * Returns the {@link Class return type} of a Kotlin {@link Method}. Supports regular and suspended methods.
     *
     * @param method the method to inspect, typically any synthetic JVM {@link Method}.
     * @return return type of the method.
     */
    public static Class<?> getReturnType(Method method) {
        var kotlinFunction = KotlinReflectionUtil.findKotlinFunction(method).orElse(null);
        if (kotlinFunction == null) {
            throw new IllegalArgumentException(String.format("Cannot resolve %s to a KFunction", method));
        }
        return JvmClassMappingKt.getJavaClass(KTypesJvm.getJvmErasure(kotlinFunction.getReturnType()));
    }

    /**
     * Lookup a {@link Method} to a {@link KFunction}.
     *
     * @param method the JVM {@link Method} to look up.
     * @return {@link Optional} wrapping a possibly existing {@link KFunction}.
     */
    private static final Optional<? extends KFunction<?>> findKFunction(Method method) {
        return JvmClassMappingKt.getKotlinClass(method.getDeclaringClass()).getMembers()
                .stream()
                .flatMap(KotlinReflectionUtil::toKFunctionStream)
                .filter(it -> isSame(it, method))
                .findFirst();
    }

    private static final boolean isSame(KFunction<?> function, Method method) {
        var javaMethod = ReflectJvmMapping.getJavaMethod(function);
        return javaMethod != null && javaMethod.equals(method);
    }

    private static final Stream<? extends KFunction<?>> toKFunctionStream(KCallable<?> it) {
        if (it instanceof KMutableProperty<?> property) {
            return Stream.of(property.getGetter(), property.getSetter());
        }
        if (it instanceof KProperty<?> property) {
            return Stream.of(property.getGetter());
        }
        if (it instanceof KFunction<?> func) {
            return Stream.of(func);
        }
        return Stream.empty();
    }

    private KotlinReflectionUtil() {
    }

}
