package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * The abstract implementation of {@link JsonRepresented} using
 * {@code fastjson2}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public abstract class Fastjson2JsonRepresented implements JsonRepresented {

    protected final byte[] rawJson;
    protected final JSONObject json;
    protected transient String rawJsonString;

    protected Fastjson2JsonRepresented(byte[] rawJson, JSONObject json) {
        this.rawJson = rawJson;
        this.json = json;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, Class<? extends T> type) {
        if (type == String.class) {
            return (T) getString(name);
        } else if (type == Integer.class) {
            return (T) json.getInteger(name);
        } else if (type == Long.class) {
            return (T) json.getLong(name);
        } else if (type == Double.class) {
            return (T) json.getDouble(name);
        } else if (type == Boolean.class) {
            return (T) getBoolean(name);
        } else {
            return json.getObject(name, type);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, Type type) {
        if (type instanceof Class<?> clazz) {
            return (T) get(name, clazz);
        }
        return json.getObject(name, type);
    }

    @Override
    public String getString(String name) {
        return json.getString(name);
    }

    @Override
    public OptionalInt getInt(String name) {
        var v = json.getInteger(name);
        return v == null ? OptionalInt.empty() : OptionalInt.of(v);
    }

    @Override
    public OptionalLong getLong(String name) {
        var v = json.getLong(name);
        return v == null ? OptionalLong.empty() : OptionalLong.of(v);
    }

    @Override
    public OptionalDouble getDouble(String name) {
        var v = json.getDouble(name);
        return v == null ? OptionalDouble.empty() : OptionalDouble.of(v);
    }

    @Override
    public Boolean getBoolean(String name) {
        return json.getBoolean(name);
    }

    @Override
    public <T> List<T> getList(String name, Class<T> elementType) {
        return json.getList(name, elementType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + rawJsonString() + ")";
    }

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
