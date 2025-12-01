package com.github.fmjsjx.libcommon.json;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;

import com.github.fmjsjx.libcommon.util.KotlinUtil;
import com.github.fmjsjx.libcommon.util.ReflectUtil;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The implementation of {@link JsonLibrary} using Jackson3
 *
 * @author MJ Fang
 * @since 3.17
 */
public class Jackson3Library implements JsonLibrary<JsonNode> {

    /**
     * A JSON exception threw by the {@link Jackson3Library}.
     */
    public static final class Jackson3Exception extends JsonException {

        @Serial
        private static final long serialVersionUID = -5099742838858519390L;

        /**
         * Creates a new {@link Jackson3Exception} instance.
         *
         * @param message the message
         * @param cause   the cause
         */
        public Jackson3Exception(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates a new {@link Jackson3Exception} instance.
         *
         * @param cause the cause
         */
        public Jackson3Exception(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Returns the default singleton {@link JsonMapper} instance.
     *
     * @return the default singleton {@code JsonMapper} instance
     */
    public static final JsonMapper defaultJsonMapper() {
        return DefaultJsonMapperInstanceHolder.MAPPER;
    }

    private static final class DefaultJsonMapperInstanceHolder {
        private static final JsonMapper MAPPER = createDefaultJsonMapper();
    }

    private static final JsonMapper createDefaultJsonMapper() {
        var mapperBuilder = JsonMapper.builder()
                .changeDefaultPropertyInclusion(old -> old.withValueInclusion(NON_ABSENT).withContentInclusion(NON_ABSENT))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if (KotlinUtil.isKotlinPresent()) {
            ReflectUtil.<JacksonModule>findForName("tools.jackson.module.kotlin.KotlinModule").ifPresent(moduleClass -> {
                try {
                    mapperBuilder.addModule(moduleClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    // jackson3-module-kotlin not available
                }
            });
        }
        if (Jackson3JsoniterModule.isJsoniterAvailable()) {
            mapperBuilder.addModule(Jackson3JsoniterModule.getInstance());
        }
        return mapperBuilder.build();
    }

    private static final class TypeReferenceImpl<T> extends TypeReference<T> {

        private final ParameterizedType type;

        private TypeReferenceImpl(ParameterizedType type) {
            this.type = type;
        }

        @Override
        public ParameterizedType getType() {
            return type;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "<" + type.getTypeName() + ">";
        }

    }

    private static final class TypeReferenceFactory {

        private final ConcurrentMap<ParameterizedType, TypeReference<?>> cached = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        private <T> TypeReference<T> create(ParameterizedType type) {
            return (TypeReference<T>) cached.computeIfAbsent(type, TypeReferenceImpl::new);
        }

    }

    private static final class TypeReferenceFactoryHolder {
        private static final TypeReferenceFactory INSTANCE = new TypeReferenceFactory();
    }

    private static final class JavaTypeFactory {

        private final JsonMapper jsonMapper;
        private final ConcurrentMap<Type, JavaType> cached = new ConcurrentHashMap<>();

        private JavaTypeFactory(JsonMapper jsonMapper) {
            this.jsonMapper = jsonMapper;
        }

        private JavaType create(Type type) {
            return cached.computeIfAbsent(type, jsonMapper::constructType);
        }
    }

    /**
     * Returns the singleton {@link Jackson3Library} instance.
     *
     * @return the singleton {@link Jackson3Library} instance.
     */
    public static final Jackson3Library getInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns the default singleton {@link Jackson3Library} instance.
     * <p>
     * Calling this method is equivalent to calling
     * {@link #getInstance()}.
     *
     * @return the default singleton {@link Jackson3Library} instance.
     */
    public static final Jackson3Library defaultInstance() {
        return getInstance();
    }

    private static final class DefaultInstanceHolder {
        private static final Jackson3Library INSTANCE = new Jackson3Library();
    }

    private final JsonMapper jsonMapper;
    private final TypeReferenceFactory typeReferenceFactory = TypeReferenceFactoryHolder.INSTANCE;
    private final JavaTypeFactory javaTypeFactory;

    /**
     * Creates a new {@link Jackson3Library} with the specified
     * {@link JsonMapper}.
     *
     * @param jsonMapper a {@code JsonMapper}
     */
    public Jackson3Library(JsonMapper jsonMapper) {
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "jsonMapper must not be null");
        this.javaTypeFactory = new JavaTypeFactory(jsonMapper);
    }

    /**
     * Creates a new {@link Jackson3Library} with the default
     * {@link JsonMapper}.
     */
    public Jackson3Library() {
        this(defaultJsonMapper());
    }

    /**
     * Returns the {@code JsonMapper}.
     *
     * @return the {@code JsonMapper}
     */
    public JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    /**
     * Converts the specified type to a {@link JavaType}.
     *
     * @param type the type
     * @return a {@code JavaType}
     */
    public JavaType toJavaType(Type type) {
        return javaTypeFactory.create(type);
    }

    /**
     * Create {@link TypeReference} for {@link List}s.
     *
     * @param <T>          the type of the elements
     * @param elementsType the elements type of the list
     * @return a {@code TypeReference<List<T>>}
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

    /**
     * Creates and returns a new {@link ObjectNode} instance.
     *
     * @return a new {@code ObjectNode} instance
     */
    public ObjectNode createObjectNode() {
        return jsonMapper.createObjectNode();
    }

    /**
     * Creates and returns a new {@link ArrayNode} instance.
     *
     * @return a new {@code ObjectNode} instance
     */
    public ArrayNode createArrayNode() {
        return jsonMapper.createArrayNode();
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(byte[] src) throws Jackson3Exception {
        try {
            return (T) getJsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(String src) throws Jackson3Exception {
        try {
            return (T) getJsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(InputStream src) throws Jackson3Exception {
        try {
            return (T) getJsonMapper().readTree(src);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loads(byte[] src, Type type) throws Jackson3Exception {
        if (type instanceof Class<?> clazz) {
            return (T) loads(src, clazz);
        }
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from byte array.
     *
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(byte[] src, JavaType type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * Decodes data from byte array.
     *
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(byte[] src, TypeReference<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @Override
    public <T> T loads(String src, Class<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loads(String src, Type type) throws Jackson3Exception {
        if (type instanceof Class<?> clazz) {
            return (T) loads(src, clazz);
        }
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(String src, JavaType type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(String src, TypeReference<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @Override
    public <T> T loads(InputStream src, Class<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loads(InputStream src, Type type) throws Jackson3Exception {
        if (type instanceof Class<?> clazz) {
            return (T) loads(src, clazz);
        }
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(InputStream src, JavaType type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Jackson3Exception if any error occurs when decoding JSON
     */
    public <T> T loads(InputStream src, TypeReference<T> type) throws Jackson3Exception {
        try {
            return getJsonMapper().readValue(src, type);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when encoding JSON
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws Jackson3Exception {
        try {
            return getJsonMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when encoding JSON
     */
    @Override
    public String dumpsToString(Object obj) throws Jackson3Exception {
        try {
            return getJsonMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

    /**
     * @throws Jackson3Exception if any error occurs when encoding JSON
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws Jackson3Exception {
        try {
            getJsonMapper().writeValue(out, obj);
        } catch (Exception e) {
            throw new Jackson3Exception(e);
        }
    }

}
