package com.github.fmjsjx.libcommon.yaml;

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
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * The implementation of {@link YamlLibrary} using Jackson2.
 */
public class Jackson2YamlLibrary implements YamlLibrary<JsonNode> {

    private static final class DefaultYamlMapperInstanceHolder {
        private static final YAMLMapper INSTANCE = createDefaultYamlMapper();
    }

    private static final YAMLMapper createDefaultYamlMapper() {
        var mapper = new YAMLMapper();
        mapper.setSerializationInclusion(Include.NON_ABSENT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(Feature.WRITE_DOC_START_MARKER);
        mapper.registerModules(new Jdk8Module(), new JavaTimeModule());
        return mapper;
    }

    /**
     * Returns the singleton (default) {@link YAMLMapper} instance.
     * 
     * @return the singleton (default) {@code YAMLMapper} instance
     */
    public static final YAMLMapper defaultYamlMapper() {
        return DefaultYamlMapperInstanceHolder.INSTANCE;
    }

    private static final class DefaultInstanceHolder {

        private static final Jackson2YamlLibrary INSTANCE = new Jackson2YamlLibrary(defaultYamlMapper());

    }

    /**
     * Returns the singleton (default) {@link Jackson2YamlLibrary} instance.
     * 
     * @return the singleton (default) {@code Jackson2YamlLibrary} instance
     */
    public static final Jackson2YamlLibrary getInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns the singleton (default) {@link Jackson2YamlLibrary} instance.
     * <p>
     * This method is equivalent to {@link #getInstance()}.
     * 
     * @return the singleton (default) {@code Jackson2YamlLibrary} instance
     */
    public static final Jackson2YamlLibrary defaultInstance() {
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
        return CachedJavaTypesHolder.javaTypes.computeIfAbsent(type, defaultYamlMapper()::constructType);
    }

    private final YAMLMapper yamlMapper;

    /**
     * Constructs a new {@link Jackson2YamlLibrary} with the specified {@link YAMLMapper} given.
     *
     * @param yamlMapper the {@link YAMLMapper}
     */
    public Jackson2YamlLibrary(YAMLMapper yamlMapper) {
        this.yamlMapper = Objects.requireNonNull(yamlMapper, "yamlMapper must not be null");
    }

    /**
     * Returns the {@code ObjectMapper}.
     * 
     * @return the {@code ObjectMapper}
     */
    public ObjectMapper yamlMapper() {
        return yamlMapper;
    }

    /**
     * Creates a new {@link ObjectNode} instance.
     * 
     * @return a new {@code ObjectNode} instance
     */
    public ObjectNode createObjectNode() {
        return yamlMapper().createObjectNode();
    }

    /**
     * Creates a new {@link ArrayNode} instance.
     * 
     * @return a new {@code ArrayNode} instance
     */
    public ArrayNode createArrayNode() {
        return yamlMapper().createArrayNode();
    }

    /**
     * @return a {@code JsonNode}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(byte[] src) throws YamlException {
        try {
            return (T) yamlMapper.readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(String src) throws YamlException {
        try {
            return (T) yamlMapper.readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(InputStream src) throws YamlException {
        try {
            return (T) yamlMapper.readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Type type) throws YamlException {
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(byte[] src, JavaType type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(byte[] src, TypeReference<T> type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Class<T> type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Type type) throws YamlException {
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from input stream.
     * 
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(InputStream src, JavaType type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * Decodes data from input stream.
     * 
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(InputStream src, TypeReference<T> type) throws YamlException {
        try {
            return yamlMapper.readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    @Override
    public byte[] dumpsToBytes(Object obj) throws YamlException {
        try {
            return yamlMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    @Override
    public String dumpsToString(Object obj) throws YamlException {
        try {
            return yamlMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    @Override
    public void dumps(Object obj, OutputStream out) throws YamlException {
        try {
            yamlMapper.writeValue(out, obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

}
