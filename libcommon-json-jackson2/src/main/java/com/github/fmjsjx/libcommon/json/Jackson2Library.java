package com.github.fmjsjx.libcommon.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The implementation of {@link JsonLibrary} using Jackson2.
 */
public class Jackson2Library implements JsonLibrary<JsonNode> {

    /**
     * A JSON exception threw by the {@link Jackson2Library}.
     */
    public static final class Jackson2Exception extends JsonException {

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

    private static final class DefaultObjectMapperInstanceHolder {
        private static final ObjectMapper INSTANCE = createDefaultObjectMapper();
    }

    private static final ObjectMapper createDefaultObjectMapper() {
        return new ObjectMapper().setSerializationInclusion(Include.NON_ABSENT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    /**
     * Returns the singleton (default) {@link ObjectMapper} instance.
     * 
     * @return the singleton (default) {@code ObjectMapper} instance
     */
    public static final ObjectMapper defaultObjectMapper() {
        return DefaultObjectMapperInstanceHolder.INSTANCE;
    }

    private static final class DefaultInstanceHolder {

        private static final Jackson2Library INSTANCE = new Jackson2Library(defaultObjectMapper());

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
        return CachedJavaTypesHolder.javaTypes.computeIfAbsent(type, defaultObjectMapper()::constructType);
    }

    private final ObjectMapper objectMapper;

    /**
     * Creates a new {@link Jackson2Library} with the specified
     * {@code objectMapper}.
     * 
     * @param objectMapper a {@code ObjectMapper}
     */
    public Jackson2Library(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    /**
     * Creates a new {@link Jackson2Library} with the default {@link ObjectMapper}.
     */
    public Jackson2Library() {
        this(defaultObjectMapper());
    }

    /**
     * Returns the {@code ObjectMapper}.
     * 
     * @return the {@code ObjectMapper}
     */
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    /**
     * Creates a new {@link ObjectNode} instance.
     * 
     * @return a new {@code ObjectNode} instance
     */
    public ObjectNode createObjectNode() {
        return objectMapper().createObjectNode();
    }

    /**
     * Creates a new {@link ArrayNode} instance.
     * 
     * @return a new {@code ArrayNode} instance
     */
    public ArrayNode createArrayNode() {
        return objectMapper().createArrayNode();
    }

    /**
     * @return a {@code JsonNode}
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(byte[] src) throws Jackson2Exception {
        try {
            return (T) objectMapper.readTree(src);
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
            return (T) objectMapper.readTree(src);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

    /**
     * Decodes data from input stream.
     * 
     * @param <T> the type of the data
     * @param src the source input stream
     * @return a {@code JsonNode}
     * @throws Jackson2Exception if any JSON decode error occurs
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(InputStream src) throws Jackson2Exception {
        try {
            return (T) objectMapper.readTree(src);
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
            return objectMapper.readValue(src, type);
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
            return objectMapper.readValue(src, type);
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
            return objectMapper.readValue(src, type);
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
    public <T> T loads(InputStream src, Class<T> type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
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
    public <T> T loads(InputStream src, JavaType type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
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
            return objectMapper.readValue(src, type);
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
            return objectMapper.writeValueAsBytes(obj);
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
            return objectMapper.writeValueAsString(obj);
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
            objectMapper.writeValue(out, obj);
        } catch (Exception e) {
            throw new Jackson2Exception(e);
        }
    }

}
