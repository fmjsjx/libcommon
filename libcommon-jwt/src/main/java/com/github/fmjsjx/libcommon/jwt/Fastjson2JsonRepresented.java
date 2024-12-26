package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSONObject;
import com.github.fmjsjx.libcommon.json.Fastjson2Library;

import java.lang.reflect.Type;
import java.util.*;

/**
 * The implementation of {@link JsonRepresented} using
 * <a href="https://github.com/alibaba/fastjson2">fastjson2</a>
 * {@link JSONObject}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class Fastjson2JsonRepresented implements JsonRepresented {

    private static final JsonRepresentedFactory<Fastjson2JsonRepresented> FACTORY =
            rawJson -> new Fastjson2JsonRepresented(Fastjson2Library.getInstance().loads(rawJson));

    /**
     * Returns the factory.
     *
     * @return the factory
     */
    public static JsonRepresentedFactory<Fastjson2JsonRepresented> getFactory() {
        return FACTORY;
    }

    private final JSONObject json;

    private Fastjson2JsonRepresented(JSONObject json) {
        this.json = Objects.requireNonNull(json, "json must not be null");
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

}
