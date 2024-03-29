package com.github.fmjsjx.libcommon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
     * @see Class#forName(String)
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
     * Finds and returns the Class object associated with the class or interface
     * with the given string name, using the given class loader.
     *
     * @param <T>         the type of the returned class
     * @param className   the fully qualified name of the desired class
     * @param initialize  if {@code true} the class will be initialized
     * @param classLoader class loader from which the class must be loaded
     * @return an {@code Optional<Class<T>>}
     * @see Class#forName(String, boolean, ClassLoader)
     * @since 2.7
     */
    public static final <T> Optional<Class<T>> findForName(String className, boolean initialize, ClassLoader classLoader) {
        try {
            @SuppressWarnings("unchecked")
            var clazz = (Class<T>) Class.forName(className, initialize, classLoader);
            return Optional.of(clazz);
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Finds and returns the Class object associated with the class or interface
     * with the given string name, using the given class loader. Invoking this
     * method is equivalent to:<pre>{@code
     * ReflectUtil.findForName(className, false, classLoader);
     * }</pre>
     *
     * @param <T>         the type of the returned class
     * @param className   the fully qualified name of the desired class
     * @param classLoader class loader from which the class must be loaded
     * @return an {@code Optional<Class<T>>}
     * @see #findForName(String, boolean, ClassLoader)
     * @since 2.7
     */
    public static final <T> Optional<Class<T>> findForName(String className, ClassLoader classLoader) {
        return findForName(className, false, classLoader);
    }

    /**
     * Returns {@code true} if has the class or interface with the given string
     * name, {@code false} otherwise.
     * <p>
     * This method is equivalence to:<pre>{@code
     * findForName(className).isPresent();
     * }</pre>
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
     * Returns {@code true} if has the class or interface with the given string
     * name using the given class loader, {@code false} otherwise.
     * <p>
     * This method is equivalence to:<pre>{@code
     * findForName(className, classLoader).isPresent();
     * }</pre>
     *
     * @param className the fully qualified name of the desired class
     * @param classLoader class loader from which the class must be loaded
     * @return {@code true} if has the class or interface with the given string
     *         name, {@code false} otherwise
     * @see #findForName(String, ClassLoader)
     * @since 2.7
     */
    public static final boolean hasClassForName(String className, ClassLoader classLoader) {
        return findForName(className, classLoader).isPresent();
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

    /**
     * Invoke the specified {@link Method} against the supplied target object with the supplied arguments.
     * The target object can be null when invoking a static {@link Method}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments
     * @return the invocation result, if any
     * @since 2.8
     */
    public static final Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not access method or field: " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            var ex = e.getTargetException();
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            if (ex instanceof Error) {
                throw (Error) ex;
            }
            throw new UndeclaredThrowableException(ex);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Retrieve the value of a named attribute, given an annotation instance.
     *
     * @param annotation    the annotation instance from which to retrieve the value
     * @param attributeName the name of the attribute value to retrieve
     * @return the attribute value, or {@code null} if not found
     * @since 2.8
     */
    public static final Object annotationValue(Annotation annotation, String attributeName) {
        if (annotation == null || StringUtil.isEmpty(attributeName)) {
            return null;
        }
        try {
            var method = annotation.annotationType().getDeclaredMethod(attributeName);
            if (Proxy.isProxyClass(annotation.getClass())) {
                var handler = Proxy.getInvocationHandler(annotation);
                try {
                    return handler.invoke(annotation, method, null);
                } catch (Throwable e) {
                    // ignore and fall back to reflection below
                }
            }
            return invokeMethod(method, annotation);
        } catch (NoSuchMethodException | SecurityException e) {
            // ignore and returns null
            return null;
        }
    }

    private ReflectUtil() {
    }
}
