package com.github.fmjsjx.libcommon.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Utility for reflect.
 */
public class ReflectUtil {

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

    /**
     * Constructs a new instance for the specified class with the given string name.
     * 
     * @param <T>       the type of the returned instance
     * @param className the fully qualified name of the desired class
     * @param initargs  array of objects to be passed as arguments to the
     *                  constructor call
     * @return an {@code Optional<T>}
     */
    public static final <T> Optional<T> constructForClassName(String className, Object... initargs) {
        Optional<Class<T>> oclass = findForName(className);
        if (initargs.length == 0) {
            return oclass.map(ReflectUtil::constructForClass);
        }
        return oclass.map(clazz -> {
            try {
                return clazz.getConstructor(toTypes(initargs)).newInstance(initargs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final <T> T constructForClass(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Class<?>[] toTypes(Object... args) {
        return Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
    }

    /**
     * Constructs a new instance for the specified class with the given string name.
     * 
     * @param <T>       the type of the returned instance
     * @param className the fully qualified name of the desired class
     * @param initargs  array of objects to be passed as arguments to the
     *                  constructor call
     * @return an {@code Optional<T>}
     */
    public static final <T> Optional<T> constructDeclaredForClassName(String className, Object... initargs) {
        Optional<Class<T>> oclass = findForName(className);
        if (initargs.length == 0) {
            return oclass.map(ReflectUtil::constructDeclaredForClass);
        }
        return oclass.map(clazz -> {
            try {
                var constructor = clazz.getDeclaredConstructor(toTypes(initargs));
                constructor.setAccessible(true);
                return constructor.newInstance(initargs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final <T> T constructDeclaredForClass(Class<T> clazz) {
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the underlying method represented by the {@code Method} object, that
     * reflects the specified public member method of the class or interface
     * represented by the specified {@code Class} object, on the specified object
     * with the specified parameters.
     * 
     * @param <T>        the type of the class
     * @param <R>        the type of the result
     * @param clazz      the class
     * @param methodName the name of the method
     * @param obj        the object the underlying method is invoked from
     * @param args       the arguments used for the method call
     * @return the result of the method on {@code obj} with parameters {@code args}
     */
    @SuppressWarnings("unchecked")
    public static final <T, R> R callMethod(Class<T> clazz, String methodName, Object obj, Object... args) {
        try {
            Method method;
            if (args.length == 0) {
                method = clazz.getMethod(methodName);
            } else {
                method = clazz.getMethod(methodName, toTypes(args));
            }
            return (R) method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the underlying method represented by the {@code Method} object, that
     * reflects the specified public static member method of the class or interface
     * represented by the specified {@code Class} object, with the specified
     * parameters.
     * 
     * @param <T>        the type of the class
     * @param <R>        the type of the result
     * @param clazz      the class
     * @param methodName the name of the method
     * @param args       the arguments used for the method call
     * @return the result of the static method parameters {@code args}
     */
    public static final <T, R> R callStaticMethod(Class<T> clazz, String methodName, Object... args) {
        return callMethod(clazz, methodName, null, args);
    }

    /**
     * Invokes the underlying method represented by the {@code Method} object, that
     * reflects the specified declared member method of the class or interface
     * represented by the specified {@code Class} object, on the specified object
     * with the specified parameters.
     * 
     * @param <T>        the type of the class
     * @param <R>        the type of the result
     * @param clazz      the class
     * @param methodName the name of the method
     * @param obj        the object the underlying method is invoked from
     * @param args       the arguments used for the method call
     * @return the result of the declared method on {@code obj} with parameters
     *         {@code args}
     */
    @SuppressWarnings("unchecked")
    public static final <T, R> R callDeclaredMethod(Class<T> clazz, String methodName, Object obj, Object... args) {
        try {
            Method method;
            if (args.length == 0) {
                method = clazz.getDeclaredMethod(methodName);
            } else {
                method = clazz.getDeclaredMethod(methodName, toTypes(args));
            }
            method.setAccessible(true);
            return (R) method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the underlying method represented by the {@code Method} object, that
     * reflects the specified declared static member method of the class or
     * interface represented by the specified {@code Class} object, on the specified
     * object with the specified parameters.
     * 
     * @param <T>        the type of the class
     * @param <R>        the type of the result
     * @param clazz      the class
     * @param methodName the name of the method
     * @param args       the arguments used for the method call
     * @return the result of the declared static method parameters {@code args}
     */
    public static final <T, R> R callDeclaredStaticMethod(Class<T> clazz, String methodName, Object... args) {
        return callDeclaredMethod(clazz, methodName, null, args);
    }

    /**
     * Returns the actual type argument to the specified {@code parameterizedType}.
     * 
     * @param <R>               type of the argument type
     * @param parameterizedType must be {@link ParameterizedType}
     * @param index             the index of the type arguments
     * @return the actual type argument
     */
    @SuppressWarnings("unchecked")
    public static final <R extends Type> R getActualTypeArgument(Type parameterizedType, int index) {
        return (R) ((ParameterizedType) parameterizedType).getActualTypeArguments()[index];
    }

    private ReflectUtil() {
    }
}
