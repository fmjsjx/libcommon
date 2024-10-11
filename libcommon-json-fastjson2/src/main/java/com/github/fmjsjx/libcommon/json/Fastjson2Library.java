package com.github.fmjsjx.libcommon.json;

import static com.alibaba.fastjson2.JSONWriter.Feature.WriteNonStringKeyAsString;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteNulls;
import static com.alibaba.fastjson2.TypeReference.parametricType;

import com.alibaba.fastjson2.*;
import com.github.fmjsjx.libcommon.util.ReflectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link JsonLibrary} using Fastjson2.
 *
 * @author MJ Fang
 * @since 3.4
 */
public class Fastjson2Library implements JsonLibrary<JSONObject> {

    static {
        if (ReflectUtil.hasClassForName("com.jsoniter.any.Any")) {
            registerJsoniterAny();
        }
        if (ReflectUtil.hasClassForName("com.github.fmjsjx.libcommon.json.Jackson2Library")) {
            registerJackson2Library();
        }
        useISOFormatForDateTimeFields();
    }

    private static final void registerJsoniterAny() {
        var anyClassNames = List.of(
                "com.jsoniter.any.Any",
                "com.jsoniter.any.LazyAny",
                "com.jsoniter.any.NotFoundAny",
                "com.jsoniter.any.TrueAny",
                "com.jsoniter.any.FalseAny",
                "com.jsoniter.any.ArrayLazyAny",
                "com.jsoniter.any.DoubleAny",
                "com.jsoniter.any.FloatAny",
                "com.jsoniter.any.IntAny",
                "com.jsoniter.any.LongAny",
                "com.jsoniter.any.NullAny",
                "com.jsoniter.any.LongLazyAny",
                "com.jsoniter.any.DoubleLazyAny",
                "com.jsoniter.any.ObjectLazyAny",
                "com.jsoniter.any.StringAny",
                "com.jsoniter.any.StringLazyAny",
                "com.jsoniter.any.ArrayAny",
                "com.jsoniter.any.ObjectAny",
                "com.jsoniter.any.ListWrapperAny",
                "com.jsoniter.any.ArrayWrapperAny",
                "com.jsoniter.any.MapWrapperAny"
        );
        for (var anyClassName : anyClassNames) {
            try {
                var anyClass = Class.forName(anyClassName);
                JSON.register(anyClass, (jsonWriter, object, fieldName, fieldType, features) -> {
                    if (object == null) {
                        jsonWriter.writeNull();
                    } else {
                        jsonWriter.writeRaw(com.jsoniter.output.JsonStream.serialize(object));
                    }
                });
            } catch (ClassNotFoundException e) {
                // skip
            }
        }
        // since 3.6
        try {
            var anyClass = Class.forName("com.jsoniter.any.Any");
            JSON.register(anyClass, (jsonReader, fieldType, fieldName, features) -> {
                if (jsonReader.nextIfNull()) {
                    return com.jsoniter.any.Any.wrapNull();
                }
                if (jsonReader.isObject()) {
                    return com.jsoniter.JsonIterator.deserialize(jsonReader.readJSONObject().toJSONString(WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isArray()) {
                    return com.jsoniter.JsonIterator.deserialize(jsonReader.readJSONArray().toJSONString(WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isString()) {
                    return com.jsoniter.JsonIterator.deserialize(JSON.toJSONString(jsonReader.readString(), WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isNumber()) {
                    return com.jsoniter.JsonIterator.deserialize(JSON.toJSONString(jsonReader.readNumber(), WriteNonStringKeyAsString, WriteNulls));
                }
                return switch (jsonReader.current()) {
                    case 't', 'f' -> com.jsoniter.any.Any.wrap(jsonReader.readBoolValue());
                    default ->
                            com.jsoniter.JsonIterator.deserialize(JSON.toJSONString(jsonReader.readAny(), WriteNonStringKeyAsString, WriteNulls));
                };
            });
        } catch (ClassNotFoundException e) {
            // skip
        }
    }

    private static final void registerJackson2Library() {
        // since 3.6
        var jsonNodeClassNames = List.of(
                "com.fasterxml.jackson.databind.node.ArrayNode",
                "com.fasterxml.jackson.databind.node.BigIntegerNode",
                "com.fasterxml.jackson.databind.node.DoubleNode",
                "com.fasterxml.jackson.databind.node.DecimalNode",
                "com.fasterxml.jackson.databind.node.IntNode",
                "com.fasterxml.jackson.databind.node.LongNode",
                "com.fasterxml.jackson.databind.node.NullNode",
                "com.fasterxml.jackson.databind.node.POJONode",
                "com.fasterxml.jackson.databind.node.ShortNode",
                "com.fasterxml.jackson.databind.node.TextNode"
        );
        for (var jsonNodeClassName : jsonNodeClassNames) {
            try {
                var anyClass = Class.forName(jsonNodeClassName);
                JSON.register(anyClass, (jsonWriter, object, fieldName, fieldType, features) -> {
                    if (object == null) {
                        jsonWriter.writeNull();
                    } else {
                        jsonWriter.writeRaw(com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().dumpsToString(object));
                    }
                });
            } catch (ClassNotFoundException e) {
                // skip
            }
        }
        try {
            var anyClass = Class.forName("com.fasterxml.jackson.databind.node.ArrayNode");
            JSON.register(anyClass, (jsonReader, fieldType, fieldName, features) -> {
                if (jsonReader.nextIfNull()) {
                    return com.fasterxml.jackson.databind.node.NullNode.getInstance();
                }
                return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(jsonReader.readJSONArray().toJSONString(WriteNonStringKeyAsString, WriteNulls));
            });
        } catch (ClassNotFoundException e) {
            // skip
        }
        try {
            var anyClass = Class.forName("com.fasterxml.jackson.databind.node.ObjectNode");
            JSON.register(anyClass, (jsonReader, fieldType, fieldName, features) -> {
                if (jsonReader.nextIfNull()) {
                    return com.fasterxml.jackson.databind.node.NullNode.getInstance();
                }
                return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(jsonReader.readJSONObject().toJSONString(WriteNonStringKeyAsString, WriteNulls));
            });
        } catch (ClassNotFoundException e) {
            // skip
        }
        try {
            var anyClass = Class.forName("com.fasterxml.jackson.databind.JsonNode");
            JSON.register(anyClass, (jsonReader, fieldType, fieldName, features) -> {
                if (jsonReader.nextIfNull()) {
                    return com.fasterxml.jackson.databind.node.NullNode.getInstance();
                }
                if (jsonReader.isObject()) {
                    return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(jsonReader.readJSONObject().toJSONString(WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isArray()) {
                    return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(jsonReader.readJSONArray().toJSONString(WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isString()) {
                    return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(JSON.toJSONString(jsonReader.readString(), WriteNonStringKeyAsString, WriteNulls));
                }
                if (jsonReader.isNumber()) {
                    return com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(JSON.toJSONString(jsonReader.readNumber(), WriteNonStringKeyAsString, WriteNulls));
                }
                return switch (jsonReader.current()) {
                    case 't', 'f' -> com.fasterxml.jackson.databind.node.BooleanNode.valueOf(jsonReader.readBoolValue());
                    default ->
                            com.github.fmjsjx.libcommon.json.Jackson2Library.getInstance().loads(JSON.toJSONString(jsonReader.readAny(), WriteNonStringKeyAsString, WriteNulls));
                };
            });
        } catch (ClassNotFoundException e) {
            // skip
        }
    }

    private static final void useISOFormatForDateTimeFields() {
        useISOFormatForLocalDateTimeFields();
        useISOFormatForOffsetDateTimeFields();
        useISOFormatForZonedDateTimeFields();
    }

    private static final void useISOFormatForLocalDateTimeFields() {
        JSON.register(LocalDateTime.class, (jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeString(((LocalDateTime) object).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
    }

    private static final void useISOFormatForOffsetDateTimeFields() {
        JSON.register(OffsetDateTime.class, (jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeString(((OffsetDateTime) object).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }
        });
    }

    private static final void useISOFormatForZonedDateTimeFields() {
        JSON.register(ZonedDateTime.class, (jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeString(((ZonedDateTime) object).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
            }
        });
    }

    /**
     * A JSON exception threw by the {@link Fastjson2Library}.
     */
    public static final class Fastjson2Exception extends JsonException {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new {@link Fastjson2Exception} instance.
         *
         * @param message the message
         * @param cause   the cause
         */
        public Fastjson2Exception(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates a new {@link Fastjson2Exception} instance.
         *
         * @param cause the cause
         */
        public Fastjson2Exception(Throwable cause) {
            super(cause);
        }

    }

    private static final class DefaultInstanceHolder {
        private static final Fastjson2Library INSTANCE = new Fastjson2Library();
    }

    /**
     * Returns the singleton (default) {@link Fastjson2Library} instance.
     *
     * @return the singleton (default) {@code Fastjson2Library} instance.
     */
    public static final Fastjson2Library getInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /**
     * Returns the default (singleton) {@link Fastjson2Library} instance.
     * <p>
     * This method is equivalent to {@link #getInstance()}.
     *
     * @return the default (singleton) {@code Fastjson2Library} instance.
     */
    public static final Fastjson2Library defaultInstance() {
        return getInstance();
    }

    private final JSONReader.Feature[] readerFeatures;
    private final JSONWriter.Feature[] writerFeatures;

    /**
     * Returns the reader features.
     *
     * @return an array of {@link JSONReader.Feature}
     */
    public JSONReader.Feature[] readerFeatures() {
        return readerFeatures;
    }

    /**
     * Returns the writer features.
     *
     * @return an array of {@link JSONWriter.Feature}
     */
    public JSONWriter.Feature[] writerFeatures() {
        return writerFeatures;
    }

    /**
     * Creates a new {@link Fastjson2Library} with the specified features given.
     *
     * @param readerFeatures the reader features
     * @param writerFeatures the writer features
     */
    public Fastjson2Library(JSONReader.Feature[] readerFeatures, JSONWriter.Feature[] writerFeatures) {
        this.readerFeatures = readerFeatures;
        this.writerFeatures = writerFeatures;
    }

    /**
     * Creates a new {@link Fastjson2Library} with the default features.
     * <p>
     * Default read features:
     * <pre>none</pre>
     * Default write features:
     * <pre>- {@code WriteNonStringKeyAsString}</pre>
     */
    public Fastjson2Library() {
        this(new JSONReader.Feature[]{}, new JSONWriter.Feature[]{WriteNonStringKeyAsString});
    }

    /**
     * @return a {@link JSONObject}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JSONObject> T loads(byte[] src) throws Fastjson2Exception {
        try {
            return (T) JSON.parseObject(src, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @return a {@link JSONObject}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JSONObject> T loads(String src) throws Fastjson2Exception {
        try {
            return (T) JSON.parseObject(src, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes data from input stream.
     *
     * @param src the source input stream
     * @return a {@code JSONArray}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends JSONObject> T loads(InputStream src) throws Fastjson2Exception {
        try {
            return (T) JSON.parseObject(src, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Type type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes data from byte array.
     *
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> T loads(byte[] src, TypeReference<T> type) throws Fastjson2Exception {
        return loads(new String(src, StandardCharsets.UTF_8), type);
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(String src, Class<T> type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(String src, Type type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Class<T> type) throws JsonException {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes data from string.
     *
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> T loads(String src, TypeReference<T> type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes data from {@link InputStream}.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Fastjson2Exception if any JSON decode error occurs
     * @author MJ Fang
     * @since 3.8
     */
    public <T> T loads(InputStream src, TypeReference<T> type) throws Fastjson2Exception {
        try {
            return loads(src.readAllBytes(), type);
        } catch (IOException e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public <T> T loads(InputStream src, Type type) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link JSONArray} from byte array.
     *
     * @param src the source byte array
     * @return a {@code JSONArray}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public JSONArray loadsArray(byte[] src) throws Fastjson2Exception {
        return loadsArray(new String(src, StandardCharsets.UTF_8));
    }

    /**
     * Decodes {@link JSONArray} from string.
     *
     * @param src the source string
     * @return a {@code JSONArray}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public JSONArray loadsArray(String src) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link JSONArray} from input stream.
     *
     * @param src the source input stream
     * @return a {@code JSONArray}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public JSONArray loadsArray(InputStream src) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link List} from byte array.
     *
     * @param <T>  the type of the element
     * @param src  the source byte array
     * @param type the type of the element
     * @return a {@code List<T>}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> List<T> loadsList(byte[] src, Type type) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link List} from byte array.
     *
     * @param <T>  the type of the element
     * @param src  the source byte array
     * @param type the type of the element
     * @return a {@code List<T>}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> List<T> loadsList(byte[] src, Class<T> type) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link List} from string.
     *
     * @param <T>  the type of the element
     * @param src  the source string
     * @param type the type of the element
     * @return a {@code List<T>}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> List<T> loadsList(String src, Type type) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Decodes {@link List} from string.
     *
     * @param <T>  the type of the element
     * @param src  the source string
     * @param type the type of the element
     * @return a {@code List<T>}
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    public <T> List<T> loadsList(String src, Class<T> type) throws Fastjson2Exception {
        try {
            return JSON.parseArray(src, type, readerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws Fastjson2Exception {
        try {
            return JSON.toJSONBytes(obj, writerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public String dumpsToString(Object obj) throws Fastjson2Exception {
        try {
            return JSON.toJSONString(obj, writerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws Fastjson2Exception {
        try {
            JSON.writeTo(out, obj, writerFeatures);
        } catch (Exception e) {
            throw new Fastjson2Exception(e);
        }
    }

    /**
     * Create {@link TypeReference} for {@link List}s.
     *
     * @param <T>          the type of the elements
     * @param elementsType the elements type of the list
     * @return a {@code TypeReference<List<T>>}
     */
    public <T> TypeReference<List<T>> listTypeReference(Type elementsType) {
        return createTypeReference(parametricType(List.class, elementsType));
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
        return createTypeReference(parametricType(collectionType, elementsType));
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
        return createTypeReference(parametricType(Map.class, keyType, valueType));
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
        return createTypeReference(parametricType(mapType, keyType, valueType));
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
        return createTypeReference(parametricType(rawType, typeArguments));
    }

    @SuppressWarnings("unchecked")
    private static final <T> TypeReference<T> createTypeReference(Type type) {
        return (TypeReference<T>) TypeReference.get(type);
    }


}
