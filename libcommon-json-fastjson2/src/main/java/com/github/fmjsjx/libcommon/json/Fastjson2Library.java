package com.github.fmjsjx.libcommon.json;

import static com.alibaba.fastjson2.JSONWriter.Feature.WriteNonStringKeyAsString;
import static com.alibaba.fastjson2.TypeReference.parametricType;

import com.alibaba.fastjson2.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
    public JSONObject loads(InputStream src) throws Fastjson2Exception {
        try {
            return JSON.parseObject(src, readerFeatures);
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
     * Decodes data from input stream.
     *
     * @param <T>  the type of the data
     * @param src  the source input stream
     * @param type the type of the data
     * @return a data object as given type
     * @throws Fastjson2Exception if any JSON decode error occurs
     */
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