package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSONObject;
import com.github.fmjsjx.libcommon.json.JsoniterLibrary;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.spi.TypeLiteral;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The implementation of {@link JsonRepresented} using
 * <a href="https://jsoniter.com/">jsoniter</a> {@link JSONObject}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class JsoniterJsonRepresented implements JsonRepresented {

    private static final JsonRepresentedFactory<JsoniterJsonRepresented> FACTORY =
            rawJson -> new JsoniterJsonRepresented(JsoniterLibrary.getInstance().loadsObject(rawJson));

    /**
     * Returns the factory.
     *
     * @return the factory
     */
    public static JsonRepresentedFactory<JsoniterJsonRepresented> getFactory() {
        return FACTORY;
    }

    private final Any json;

    private JsoniterJsonRepresented(Any json) {
        this.json = json;
    }

    private static boolean notFoundOrNull(Any any) {
        return any.valueType() == ValueType.INVALID || any.valueType() == ValueType.NULL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, Type type) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return null;
        }
        return (T) v.as(TypeLiteral.create(type));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, Class<? extends T> type) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return null;
        }
        if (type == String.class) {
            return (T) v.toString();
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(v.toInt());
        } else if (type == Long.class) {
            return (T) Long.valueOf(v.toLong());
        } else if (type == Double.class) {
            return (T) Double.valueOf(v.toDouble());
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(v.toBoolean());
        } else {
            return v.as(type);
        }
    }

    @Override
    public String getString(String name) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return null;
        }
        return v.toString();
    }

    @Override
    public OptionalInt getInt(String name) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(v.toInt());
    }

    @Override
    public OptionalLong getLong(String name) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(v.toLong());
    }

    @Override
    public OptionalDouble getDouble(String name) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(v.toDouble());
    }

    @Override
    public Boolean getBoolean(String name) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return null;
        }
        return v.toBoolean();
    }

    private static final ConcurrentMap<Type, TypeLiteral<?>> CACHED_LIST_TYPES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getList(String name, Class<T> elementType) {
        var v = json.get(name);
        if (notFoundOrNull(v)) {
            return null;
        }
        var type = CACHED_LIST_TYPES.computeIfAbsent(elementType, k -> JsoniterLibrary.getInstance().listTypeLiteral(k));
        return (List<T>) v.as(type);
    }

}
