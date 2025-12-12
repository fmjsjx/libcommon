package com.github.fmjsjx.libcommon.yaml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.fmjsjx.libcommon.util.ReflectUtil;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * The implementation of {@link YamlLibrary} using Jackson3.
 *
 * @author MJ Fang
 * @since 4.0
 */
public class Jackson3YamlLibrary implements YamlLibrary<JsonNode> {


    /**
     * Returns the singleton (default) {@link YAMLMapper} instance.
     *
     * @return the singleton (default) {@code YAMLMapper} instance
     */
    public static final YAMLMapper defaultYamlMapper() {
        return DefaultYamlMapperInstanceHolder.INSTANCE;
    }

    private static class DefaultYamlMapperInstanceHolder {
        private static final YAMLMapper INSTANCE = createDefaultYamlMapper();
    }

    private static final YAMLMapper createDefaultYamlMapper() {
        var mapperBuilder = YAMLMapper.builder()
                .changeDefaultPropertyInclusion((old) -> old.withValueInclusion(JsonInclude.Include.NON_ABSENT).withContentInclusion(JsonInclude.Include.NON_ABSENT))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER);
        return mapperBuilder.build();
    }

    private interface JavaTypeConverter {
        /**
         * Converts the specified type to a {@link JavaType}
         *
         * @param type the type
         * @return a {@code JavaType}
         */
        JavaType convert(Type type);
    }

    private static class CachedJavaTypeConverter implements JavaTypeConverter {

        private final ObjectMapper mapper;
        private final ConcurrentMap<Type, JavaType> cached = new ConcurrentHashMap<>();

        private CachedJavaTypeConverter(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public JavaType convert(Type type) {
            return cached.computeIfAbsent(type, mapper::constructType);
        }
    }

    private static class DelegatedJavaTypeConverter implements JavaTypeConverter {

        private final Function<Type, JavaType> converter;

        private DelegatedJavaTypeConverter(Function<Type, JavaType> converter) {
            this.converter = converter;
        }

        @Override
        public JavaType convert(Type type) {
            return converter.apply(type);
        }

    }

    private static final class Jackson3LibraryWrappedTypeConverterInstanceHolder {
        private static final JavaTypeConverter INSTANCE = new DelegatedJavaTypeConverter(com.github.fmjsjx.libcommon.json.Jackson3Library.getInstance()::toJavaType);
    }

    private final YAMLMapper yamlMapper;
    private final JavaTypeConverter javaTypeConverter;

    /**
     * Constructs a new {@link Jackson3YamlLibrary} with the default
     * {@link YAMLMapper} instance.
     */
    public Jackson3YamlLibrary() {
        this(defaultYamlMapper());
    }

    /**
     * Constructs a new {@link Jackson3YamlLibrary} with the specified
     * {@link YAMLMapper} given.
     *
     * @param yamlMapper the {@link YAMLMapper}
     */
    public Jackson3YamlLibrary(YAMLMapper yamlMapper) {
        this.yamlMapper = Objects.requireNonNull(yamlMapper, "yamlMapper must not be null");
        if (ReflectUtil.hasClassForName("com.github.fmjsjx.libcommon.json.Jackson3Library")) {
            this.javaTypeConverter = Jackson3LibraryWrappedTypeConverterInstanceHolder.INSTANCE;
        } else {
            this.javaTypeConverter = new CachedJavaTypeConverter(yamlMapper);
        }
    }

    /**
     * Returns the {@link YAMLMapper}.
     *
     * @return the {@code YAMLMapper}
     */
    public YAMLMapper yamlMapper() {
        return yamlMapper;
    }

    /**
     * Creates and returns a new {@link ObjectNode}.
     *
     * @return a new {@code ObjectNode}
     */
    public ObjectNode createObjectNode() {
        return yamlMapper().createObjectNode();
    }

    /**
     * Creates and returns a new {@link ArrayNode}.
     *
     * @return a new {@code ArrayNode}
     */
    public ArrayNode createArrayNode() {
        return yamlMapper().createArrayNode();
    }

    /**
     * Converts the specified type to a {@link JavaType}.
     *
     * @param type the type
     * @return a {@code JavaType}
     */
    public JavaType toJavaType(Type type) {
        return javaTypeConverter.convert(type);
    }

    /**
     * @return a {@code JsonNode}
     * @throws YamlException if any YAML decode error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(byte[] src) throws YamlException {
        try {
            return (T) yamlMapper().readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     * @throws YamlException if any YAML decode error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(InputStream src) throws YamlException {
        try {
            return (T) yamlMapper().readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @return a {@code JsonNode}
     * @throws YamlException if any YAML decode error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends JsonNode> T loads(String src) throws YamlException {
        try {
            return (T) yamlMapper().readTree(src);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws YamlException {
        try {
            return yamlMapper().readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
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
            return yamlMapper().readValue(src, toJavaType(type));
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
            return yamlMapper().readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    public <T> T loads(String src, Class<T> type) throws YamlException {
        try {
            return yamlMapper().readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML decode error occurs
     */
    @Override
    public <T> T loads(String src, Type type) throws YamlException {
        return loads(src, toJavaType(type));
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(String src, JavaType type) throws YamlException {
        try {
            return yamlMapper().readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws YamlException if any YAML decode error occurs
     */
    public <T> T loads(String src, TypeReference<T> type) throws YamlException {
        try {
            return yamlMapper().readValue(src, type);
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
            return yamlMapper().readValue(src, type);
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
            return yamlMapper().readValue(src, type);
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
            return yamlMapper().readValue(src, type);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws YamlException {
        try {
            return yamlMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    public String dumpsToString(Object obj) throws YamlException {
        try {
            return yamlMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }

    /**
     * @throws YamlException if any YAML encode error occurs
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws YamlException {
        try {
            yamlMapper().writeValue(out, obj);
        } catch (Exception e) {
            throw new YamlException(e);
        }
    }
}
