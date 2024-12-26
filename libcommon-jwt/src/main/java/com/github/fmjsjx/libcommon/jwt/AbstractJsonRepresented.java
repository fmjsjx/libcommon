package com.github.fmjsjx.libcommon.jwt;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * The abstract implementation of {@link JsonRepresented}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public abstract class AbstractJsonRepresented implements JsonRepresented {

    protected final byte[] rawJson;
    protected final JsonRepresented delegated;

    protected transient String rawJsonString;

    /**
     * Constructs a new {@link AbstractJsonRepresented} instance with the
     * specified {@code rawJson} and the specified {@code delegated}
     * {@link JsonRepresented} given.
     *
     * @param rawJson   the raw JSON byte array
     * @param delegated the delegated {@link JsonRepresented}
     */
    protected AbstractJsonRepresented(byte[] rawJson, JsonRepresented delegated) {
        this.rawJson = rawJson;
        this.delegated = delegated;
    }

    @Override
    public <T> T get(String name, Class<? extends T> type) {
        return delegated.get(name, type);
    }

    @Override
    public <T> T get(String name, Type type) {
        return delegated.get(name, type);
    }

    @Override
    public String getString(String name) {
        return delegated.getString(name);
    }

    @Override
    public OptionalInt getInt(String name) {
        return delegated.getInt(name);
    }

    @Override
    public OptionalLong getLong(String name) {
        return delegated.getLong(name);
    }

    @Override
    public OptionalDouble getDouble(String name) {
        return delegated.getDouble(name);
    }

    @Override
    public Boolean getBoolean(String name) {
        return delegated.getBoolean(name);
    }

    @Override
    public <T> List<T> getList(String name, Class<T> elementType) {
        return delegated.getList(name, elementType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(rawJson=" + rawJsonString() + ", delegated=" + delegated + ")";
    }

    /**
     * Caches and returns a string built from the raw JSON byte array.
     *
     * @return a cached raw JSON string
     */
    protected String rawJsonString() {
        //noinspection DuplicatedCode
        var rawJsonString = this.rawJsonString;
        if (rawJsonString == null) {
            synchronized (this) {
                rawJsonString = this.rawJsonString;
                if (rawJsonString == null) {
                    this.rawJsonString = rawJsonString = new String(rawJson);
                }
            }
        }
        return rawJsonString;
    }

}
