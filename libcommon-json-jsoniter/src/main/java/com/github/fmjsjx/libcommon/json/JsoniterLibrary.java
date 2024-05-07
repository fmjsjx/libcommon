package com.github.fmjsjx.libcommon.json;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

/**
 * The implementation of {@link JsonLibrary} using jsoniter.
 */
@SuppressWarnings("unchecked")
public class JsoniterLibrary implements JsonLibrary<Any> {

    /**
     * A JSON exception threw by the {@link JsoniterLibrary}.
     */
    public static final class JsoniterException extends JsonException {

        @Serial
        private static final long serialVersionUID = 2322005891481582607L;

        /**
         * Creates a new {@link JsoniterException} with the specified message and cause.
         * 
         * @param message the message
         * @param cause   the cause
         */
        public JsoniterException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates a new {@link JsoniterException} with the specified message.
         *
         * @param message the message
         * @since 3.2
         */
        public JsoniterException(String message) {
            super(message);
        }

        /**
         * Creates a new {@link JsoniterException} with the specified cause.
         * 
         * @param cause the cause
         */
        public JsoniterException(Throwable cause) {
            super(cause);
        }

    }

    private static final class DefaultInstanceHolder {
        private static final JsoniterLibrary INSTANCE = new JsoniterLibrary();

        static {
            JsoniterSpi.setDefaultConfig(JsoniterSpi.getDefaultConfig().copyBuilder().escapeUnicode(false).build());
            Jdk8TimeSupport.enableAll();
            Jdk8OptionalSupport.enableAll();
            // support encoding/decoding for jackson2 JsonNode
            if (Jackson2Support.isAvailable()) {
                Jackson2Support.enableAll();
            }
        }
    }

    /**
     * Returns the singleton (default) {@link JsoniterLibrary} instance.
     * 
     * @return the singleton (default) {@code JsoniterLibrary} instance
     */
    public static final JsoniterLibrary getInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns the singleton (default) {@link JsoniterLibrary} instance.
     * <p>
     * This method is equivalent to {@link #getInstance()}.
     * 
     * @return the singleton (default) {@code JsoniterLibrary} instance
     */
    public static final JsoniterLibrary defaultInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    private static final byte[] NULL_BYTE_ARRAY = "null".getBytes();

    private final boolean useDefaultConfig;
    private final Config config;

    /**
     * Creates a new {@link JsoniterLibrary} instance with the default
     * configuration.
     */
    public JsoniterLibrary() {
        this.useDefaultConfig = true;
        this.config = null;
    }

    /**
     * Creates a new {@link JsoniterLibrary} instance with the specified
     * configuration.
     * 
     * @param config the configuration
     */
    public JsoniterLibrary(Config config) {
        this.useDefaultConfig = false;
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T extends Any> T loads(byte[] src) throws JsoniterException {
        try {
            if (useDefaultConfig) {
                return (T) JsonIterator.deserialize(src);
            }
            return (T) JsonIterator.deserialize(config, src);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws JsoniterException {
        try {
            if (useDefaultConfig) {
                return JsonIterator.deserialize(src, type);
            }
            return JsonIterator.deserialize(config, src, type);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Type type) throws JsoniterException {
        if (type instanceof Class) {
            return loads(src, (Class<T>) type);
        }
        TypeLiteral<T> typeLiteral = TypeLiteral.create(type);
        return loads(src, typeLiteral);
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T extends Any> T loads(InputStream src) throws JsonException {
        try {
            return loads(src.readAllBytes());
        } catch (IOException e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Class<T> type) throws JsonException {
        try {
            return loads(src.readAllBytes(), type);
        } catch (IOException e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Type type) throws JsonException {
        try {
            return loads(src.readAllBytes(), type);
        } catch (IOException e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>         the type of the data
     * @param src         the source byte array
     * @param typeLiteral the type literal of the data
     * @return a data object as given type
     * @throws JsoniterException if any JSON decode error occurs
     */
    public <T> T loads(byte[] src, TypeLiteral<T> typeLiteral) throws JsoniterException {
        try {
            if (useDefaultConfig) {
                return JsonIterator.deserialize(src, typeLiteral);
            }
            return JsonIterator.deserialize(config, src, typeLiteral);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * Decodes data from string.
     * 
     * @param <T>         the type of the data
     * @param src         the source string
     * @param typeLiteral the type literal of the data
     * @return a data object as given type
     * @throws JsoniterException if any JSON decode error occurs
     */
    public <T> T loads(String src, TypeLiteral<T> typeLiteral) {
        try {
            return loads(src.getBytes(StandardCharsets.UTF_8), typeLiteral);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws JsoniterException {
        if (obj == null) {
            return NULL_BYTE_ARRAY;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dumps(obj, out);
        return out.toByteArray();
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public String dumpsToString(Object obj) throws JsoniterException {
        try {
            if (useDefaultConfig) {
                return JsonStream.serialize(obj);
            }
            //noinspection DataFlowIssue
            return JsonStream.serialize(config, obj);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * @throws JsoniterException if any JSON decode error occurs
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws JsoniterException {
        try {
            if (useDefaultConfig) {
                JsonStream.serialize(obj, out);
            } else {
                JsonStream.serialize(config, obj, out);
            }
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    /**
     * Create {@link TypeLiteral} for {@link List}s.
     *
     * @param <T>          the type of the elements
     * @param elementsType the elements type of the list
     * @return a {@code TypeLiteral<List<T>>}
     * @since 2.8
     */
    public <T> TypeLiteral<List<T>> listTypeLiteral(Type elementsType) {
        return TypeLiteral.create(new ParameterizedTypeImpl(List.class, elementsType));
    }

    /**
     * Create {@link TypeLiteral} for {@link Collection}s.
     *
     * @param <T>            the type of the elements
     * @param <C>            the type of the collection
     * @param elementsType   the elements type of the collection
     * @param collectionType the type of the collection
     * @return a {@code TypeLiteral<C>}
     * @since 2.8
     */
    public <T, C extends Collection<T>> TypeLiteral<C> collectionTypeLiteral(Type elementsType, Class<? extends Collection<?>> collectionType) {
        return TypeLiteral.create(new ParameterizedTypeImpl(collectionType, elementsType));
    }

    /**
     * Create {@link TypeLiteral} for {@link Map}s.
     *
     * @param <K>       the type of the key
     * @param <V>       the type of the value
     * @param keyType   the type of the key
     * @param valueType the type of the value
     * @return a {@code TypeLiteral<Map<K, V>>}
     * @since 2.8
     */
    public <K, V> TypeLiteral<Map<K, V>> mapTypeLiteral(Type keyType, Type valueType) {
        return TypeLiteral.create(new ParameterizedTypeImpl(Map.class, keyType, valueType));
    }

    /**
     * Create {@link TypeLiteral} for {@link Map}s.
     *
     * @param <K>       the type of the key
     * @param <V>       the type of the value
     * @param <M>       the type of the map
     * @param keyType   the type of the key
     * @param valueType the type of the value
     * @param mapType   the type of the map
     * @return a {@code TypeLiteral<M>}
     * @since 2.8
     */
    public <K, V, M extends Map<K, V>> TypeLiteral<M> mapTypeLiteral(Type keyType, Type valueType, Class<? extends Map<?, ?>> mapType) {
        return TypeLiteral.create(new ParameterizedTypeImpl(mapType, keyType, valueType));
    }

    /**
     * Create {@link TypeLiteral}.
     *
     * @param <T>           the type
     * @param rawType       the raw type
     * @param typeArguments the type arguments
     * @return a {@code TypeLiteral<T>}
     * @since 2.8
     */
    public <T> TypeLiteral<T> genericTypeLiteral(Class<?> rawType, Type... typeArguments) {
        return TypeLiteral.create(new ParameterizedTypeImpl(rawType, typeArguments));
    }

    /**
     * Decodes {@link Any} as the specified type from a string.
     *
     * @param src  the source string
     * @param type the value type
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as the specified type
     * @since 3.2
     */
    public <T extends Any> T loadsAsType(String src, ValueType type) throws JsoniterException {
        return loadsAsType(src.getBytes(StandardCharsets.UTF_8), type);
    }

    /**
     * Decodes {@link Any} as the specified type from a byte array.
     *
     * @param src  the source byte array
     * @param type the value type
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as the specified type
     * @since 3.2
     */
    public <T extends Any> T loadsAsType(byte[] src, ValueType type) throws JsoniterException {
        T any = loads(src);
        if (any.valueType() != type) {
            throw new JsoniterException("JSON value expected be " + type + " but was " + any.valueType());
        }
        return any;
    }

    /**
     * Decodes {@link Any} as an object from a string.
     *
     * @param src  the source string
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as an object
     * @since 3.2
     */
    public <T extends Any> T loadsObject(String src) throws JsoniterException {
        return loadsAsType(src, ValueType.OBJECT);
    }

    /**
     * Decodes {@link Any} as an object from a byte array.
     *
     * @param src  the source byte array
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as an object
     * @since 3.2
     */
    public <T extends Any> T loadsObject(byte[] src) throws JsoniterException {
        return loadsAsType(src, ValueType.OBJECT);
    }

    /**
     * Decodes {@link Any} as an array from a string.
     *
     * @param src  the source string
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as an array
     * @since 3.2
     */
    public <T extends Any> T loadsArray(String src) throws JsoniterException {
        return loadsAsType(src, ValueType.ARRAY);
    }

    /**
     * Decodes {@link Any} as an array from a byte array.
     *
     * @param src  the source byte array
     * @param <T>  the type of the result
     * @return the {@code Any} instance
     * @throws JsoniterException if the source can't be decoded as an array
     * @since 3.2
     */
    public <T extends Any> T loadsArray(byte[] src) throws JsoniterException {
        return loadsAsType(src, ValueType.ARRAY);
    }

}
