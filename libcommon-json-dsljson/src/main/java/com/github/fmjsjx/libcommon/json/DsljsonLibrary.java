package com.github.fmjsjx.libcommon.json;

import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import com.dslplatform.json.runtime.TypeDefinition;

/**
 * The implementation of {@link JsonLibrary} using DSL-JSON.
 */
public class DsljsonLibrary implements JsonLibrary<Map<String, ?>> {

    /**
     * A JSON exception threw by the {@link DsljsonLibrary}.
     */
    public static final class DsljsonException extends JsonException {

        private static final long serialVersionUID = -779902969383591399L;

        /**
         * Creates a new {@link DsljsonException} instance.
         * 
         * @param message the message
         * @param cause   the cause
         */
        public DsljsonException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates a new {@link DsljsonException} instance.
         * 
         * @param cause the cause
         */
        public DsljsonException(Throwable cause) {
            super(cause);
        }

    }

    private static final class InstanceHolder {
        private static final DsljsonLibrary INSTANCE = new DsljsonLibrary(defaultDslJson());
    }

    /**
     * Returns the singleton (default) {@code DSL-JSON} instance.
     * 
     * @return the singleton (default) {@code DSL-JSON} instance
     */
    public static final DslJson<Object> defaultDslJson() {
        return DefaultDslJsonInstanceHolder.INSTANCE;
    }

    private static final class DefaultDslJsonInstanceHolder {
        private static final DslJson<Object> INSTANCE = new DslJson<>(Settings.basicSetup());
    }

    /**
     * Returns the singleton (default) {@link DsljsonLibrary} instance.
     * 
     * @return the singleton (default) {@code DsljsonLibrary} instance
     */
    public static final DsljsonLibrary getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final Type mapType = ((ParameterizedType) DsljsonLibrary.class.getGenericInterfaces()[0])
            .getActualTypeArguments()[0];

    private final DslJson<Object> dslJson;

    /**
     * Creates a new {@link DsljsonLibrary} instance with the default
     * {@code DSL-JSON}.
     */
    public DsljsonLibrary() {
        this(defaultDslJson());
    }

    /**
     * Creates a new {@link DsljsonLibrary} instance with the specified
     * {@code DSL-JSON}
     * 
     * @param dslJson the {@code DSL-JSON}
     */
    public DsljsonLibrary(DslJson<Object> dslJson) {
        this.dslJson = dslJson;
    }

    /**
     * Returns the {@code DSL-JSON}.
     * 
     * @return the {@code DSL-JSON}
     */
    public DslJson<Object> dslJson() {
        return dslJson;
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Map<String, ?>> T loads(byte[] src) throws DsljsonException {
        try {
            return (T) dslJson.deserialize(mapType, src, src.length);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    public <T> T loads(byte[] src, Class<T> type) throws DsljsonException {
        try {
            return dslJson.deserialize(type, src, src.length);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T loads(byte[] src, Type type) throws DsljsonException {
        try {
            return (T) dslJson.deserialize(type, src, src.length);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>            the type of the data
     * @param src            the source byte array
     * @param typeDefinition the type definition of the data
     * @return a data object as given type
     * @throws DsljsonException if any JSON decode error occurs
     */
    public <T> T loads(byte[] src, TypeDefinition<T> typeDefinition) throws DsljsonException {
        return loads(src, typeDefinition.type);
    }

    /**
     * Decodes data from string.
     * 
     * @param <T>            the type of the data
     * @param src            the source string
     * @param typeDefinition the type definition of the data
     * @return a data object as given type
     * @throws DsljsonException if any JSON decode error occurs
     */
    public <T> T loads(String src, TypeDefinition<T> typeDefinition) throws DsljsonException {
        return loads(src, typeDefinition.type);
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    public byte[] dumpsToBytes(Object obj) throws DsljsonException {
        var jsonWriter = dslJson.newWriter();
        try {
            dslJson.serialize(jsonWriter, obj);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
        return jsonWriter.toByteArray();
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    public String dumpsToString(Object obj) throws DsljsonException {
        var jsonWriter = dslJson.newWriter();
        try {
            dslJson.serialize(jsonWriter, obj);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
        return jsonWriter.toString();
    }

    /**
     * @throws DsljsonException if any JSON decode error occurs
     */
    @Override
    public void dumps(Object obj, OutputStream out) throws DsljsonException {
        try {
            dslJson.serialize(obj, out);
        } catch (Exception e) {
            throw new DsljsonException(e);
        }
    }

}
