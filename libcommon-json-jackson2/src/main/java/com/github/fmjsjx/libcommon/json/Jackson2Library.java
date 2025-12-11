package com.github.fmjsjx.libcommon.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fmjsjx.libcommon.util.KotlinUtil;
import com.github.fmjsjx.libcommon.util.ReflectUtil;

/**
 * The implementation of {@link JsonLibrary} using Jackson2.
 */
public class Jackson2Library implements JsonLibrary<JsonNode> {

    /**
     * A JSON exception threw by the {@link Jackson2Library}.
     */
    public static final class Jackson2Exception extends JsonException {

        @Serial
        private static final long serialVersionUID = -3634220857125631467L;

        /**
         * Creates a new {@link Jackson2Exception} instance.
         *
         * @param message the message
         * @param cause   the cause
         */
        public Jackson2Exception(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates a new {@link Jackson2Exception} instance.
         *
         * @param cause the cause
         */
        public Jackson2Exception(Throwable cause) {
            super(cause);
        }

    }

    /**
     * Returns the default singleton {@link JsonMapper} instance.
     *
     * @return the default singleton {@code JsonMapper} instance
     * @since 4.0
     */
    public static final JsonMapper defaultJsonMapper() {
        return DefaultJsonMapperInstanceHolder.MAPPER;
    }

    private static final class DefaultJsonMapperInstanceHolder {
        private static final JsonMapper MAPPER = createDefaultJsonMapper();
    }

    private static final JsonMapper createDefaultJsonMapper() {
        var mapperBuilder = JsonMapper.builder()
                .defaultPropertyInclusion(JsonInclude.Value.construct(Include.NON_ABSENT, Include.NON_ABSENT))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ReflectUtil.<Module>findForName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module").ifPresent(moduleClass -> {
            try {
                mapperBuilder.addModule(moduleClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                // jackson-module-jdk8 not available
            }
        });
        ReflectUtil.<Module>findForName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule").ifPresent(moduleClass -> {
            try {
                mapperBuilder.addModule(moduleClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                // jackson-module-jsr310 not available
            }
        });
        if (KotlinUtil.isKotlinPresent()) {
            ReflectUtil.<Module>findForName("com.fasterxml.jackson.module.kotlin.KotlinModule").ifPresent(moduleClass -> {
                try {
                    mapperBuilder.addModule(moduleClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    // jackson-module-kotlin not available
                }
            });
        }
        if (JsoniterModule.isJsoniterAvailable()) {
            mapperBuilder.addModule(JsoniterModule.getInstance());
        }
        return mapperBuilder.build();
    }

    /**
     * Returns the singleton (default) {@link ObjectMapper} instance.
     *
     * @return the singleton (default) {@code ObjectMapper} instance
     * @deprecated since 4.0, please use {@link #defaultJsonMapper()} instead
     */
    public static final ObjectMapper defaultObjectMapper() {
        return defaultJsonMapper();
    }

    private static final class DefaultInstanceHolder {

        private static final Jackson2Library INSTANCE = new Jackson2Library();

    }

    /**
     * Returns the singleton (default) {@link Jackson2Library} instance.
     *
     * @return the singleton (default) {@code Jackson2Library} instance
     */
    public static final Jackson2Library getInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns the singleton (default) {@link Jackson2Library} instance.
     * <p>
     * This method is equivalent to {@link #getInstance()}.
     *
     * @return the singleton (default) {@code Jackson2Library} instance
     */
    public static final Jackson2Library defaultInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    private static final class CachedJavaTypesHolder {
        private static final ConcurrentMap<Type, JavaType> javaTypes = new ConcurrentHashMap<>();
    }

    /**
     * Converts the specified type to a {@link JavaType}.
     *
     * @param type the type
     * @return a {@code JavaType}
     */
    public static final JavaType toJavaType(Type type) {
        return CachedJavaTypesHolder.javaTypes.computeIfAbsent(type, defaultJsonMapper()::constructType);
    }

    private final JsonMapper jsonMapper;
    private TypeReferenceFactory typeReferenceFactory = TypeReferenceFactory.getDefault();

    /**
     * Creates a new {@link Jackson2Library} with the specified
     * {@code objectMapper}.
     *
     * @param objectMapper a {@code ObjectMapper}
     * @deprecated since 4.0, please use {@link #Jackson2Library(JsonMapper)}
     * constructor instead
     */
    @Deprecated
    public Jackson2Library(ObjectMapper objectMapper) {
        this((JsonMapper) objectMapper);
    }

    /**
     * Creates a new {@link Jackson2Library} with the specified
     * {@code jsonMapper}.
     *
     * @param jsonMapper a {@code JsonMapper}
     * @since 4.0
     */
    public Jackson2Library(JsonMapper jsonMapper) {
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "jsonMapper must not be null");
    }

    /**
     * Creates a new {@link Jackson2Library} with the default {@link ObjectMapper}.
     */
    public Jackson2Library() {
        this(defaultJsonMapper());
    }

    /**
     * Returns the {@code ObjectMapper}.
     *
     * @return the {@code ObjectMapper}
     * @deprecated since 4.0, please use {@link #jsonMapper()} instead
     */
    @Deprecated
    public ObjectMapper objectMapper() {
        return jsonMapper();
    }

    /**
     * Returns the {@code JsonMapper}.
     *
     * @return the {@code JsonMapper}
     * @since 4.0
     */
    public JsonMapper jsonMapper() {
        return jsonMapper;
    }

    /**
     * Set the typeReferenceFactory.
     *
     * @param typeReferenceFactory the {@link TypeReferenceFactory}
     * @return this object
     * @since 3.1
     */
    public Jackson2Library typeReferenceFactory(TypeReferenceFactory typeReferenceFactory) {
        this.typeReferenceFactory = Objects.requireNonNull(typeReferenceFactory, "typeReferenceFactory must not be null");
        return this;
    }

    /**
     * Creates a new {@link ObjectNode} instance.
     *
     * @return a new {@code ObjectNode} instance
     */
    public ObjectNode createObjectNode() {
        return jsonMapper().createObjectNode();
    }

    /**
     * Creates a new {@link ArrayNode} instance.
     *
     * @return a new {@code ArrayNode} instance
     */
    public ArrayNode createArrayNode() {
        return jsonMapper().createArrayNode();
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(byte[] src) throws Jackson2Exception {
        try {
            return (T) jsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(String src) throws Jackson2Exception {
        try {
            return (T) jsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(InputStream src) throws Jackson2Exception {
        try {
            return (T) jsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Type type) throws Jackson2Exception {
        return loads(src, toJavaType(type));
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(String src, Class<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(String src, Type type) throws Jackson2Exception {
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(String src, JavaType type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(String src, TypeReference<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Decodes data from byte array.
     *
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(byte[] src, JavaType type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Decodes data from byte array.
     *
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(byte[] src, TypeReference<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Class<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Type type) throws JsonException {
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(InputStream src, JavaType type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    public <T> T loads(InputStream src, TypeReference<T> type) throws Jackson2Exception {
        try {
            return jsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON encode error occurs
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws Jackson2Exception {
        try {
            return jsonMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON encode error occurs
     */
    @Override
    public String dumpsToString(Object obj) throws Jackson2Exception {
        try {
            return jsonMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * @throws Jackson2Exception if any JSON encode error occurs
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws Jackson2Exception {
        try {
            jsonMapper().writeValue(out, obj);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Create {@link TypeReference} for {@link List}s.
     *
     * @param <T>          the type of the elements
     * @param elementsType the elements type of the list
     * @return a {@code TypeReference<List<T>>}
     * @since 2.8
     */
    public <T> TypeReference<List<T>> listTypeReference(Type elementsType) {
        return typeReferenceFactory.create(new ParameterizedTypeImpl(List.class, elementsType));
    }

    /**
     * Create {@link TypeReference} for {@link Collection}s.
     *
     * @param <T>            the type of the elements
     * @param <C>            the type of the collection
     * @param elementsType   the elements type of the collection
     * @param collectionType the type of the collection
     * @return a {@code TypeReference<C>}
     * @since 2.8
     */
    public <T, C extends Collection<T>> TypeReference<C> collectionTypeReference(Type elementsType, Class<? extends Collection<?>> collectionType) {
        return typeReferenceFactory.create(new ParameterizedTypeImpl(collectionType, elementsType));
    }

    /**
     * Create {@link TypeReference} for {@link Map}s.
     *
     * @param <K>       the type of the key
     * @param <V>       the type of the value
     * @param keyType   the type of the key
     * @param valueType the type of the value
     * @return a {@code TypeReference<Map<K, V>>}
     * @since 2.8
     */
    public <K, V> TypeReference<Map<K, V>> mapTypeReference(Type keyType, Type valueType) {
        return typeReferenceFactory.create(new ParameterizedTypeImpl(Map.class, keyType, valueType));
    }

    /**
     * Create {@link TypeReference} for {@link Map}s.
     *
     * @param <K>       the type of the key
     * @param <V>       the type of the value
     * @param <M>       the type of the map
     * @param keyType   the type of the key
     * @param valueType the type of the value
     * @param mapType   the type of the map
     * @return a {@code TypeReference<M>}
     * @since 2.8
     */
    public <K, V, M extends Map<K, V>> TypeReference<M> mapTypeReference(Type keyType, Type valueType, Class<? extends Map<?, ?>> mapType) {
        return typeReferenceFactory.create(new ParameterizedTypeImpl(mapType, keyType, valueType));
    }

    /**
     * Create {@link TypeReference}.
     *
     * @param <T>           the type
     * @param rawType       the raw type
     * @param typeArguments the type arguments
     * @return a {@code TypeReference<T>}
     */
    public <T> TypeReference<T> genericTypeReference(Class<?> rawType, Type... typeArguments) {
        return typeReferenceFactory.create(new ParameterizedTypeImpl(rawType, typeArguments));
    }

}
