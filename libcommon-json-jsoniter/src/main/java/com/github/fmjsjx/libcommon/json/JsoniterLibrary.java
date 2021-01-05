package com.github.fmjsjx.libcommon.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.jsoniter.JsonIterator;
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

}
