package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSON;

import java.lang.reflect.Type;
import java.util.*;

/**
 * The implementation of {@link JoseHeader} which the JSON object will be
 * lazy parsed.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class LazyParsedJoseHeader implements JoseHeader {

    static {
        JSON.register(LazyParsedJoseHeader.class, (jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeAny(((LazyParsedJoseHeader) object).parsedDelegated());
            }
        });
    }

    private final String base64HeaderString;
    private final JsonRepresentedFactory<?> jsonRepresentedFactory;

    private JoseHeader delegated;

    LazyParsedJoseHeader(String base64HeaderString, JsonRepresentedFactory<?> jsonRepresentedFactory) {
        this.base64HeaderString = base64HeaderString;
        this.jsonRepresentedFactory = jsonRepresentedFactory;
    }

    private JoseHeader parsedDelegated() {
        var delegated = this.delegated;
        if (delegated == null) {
            synchronized (this) {
                delegated = this.delegated;
                if (delegated == null) {
                    var rawJson = Base64.getUrlDecoder().decode(base64HeaderString);
                    this.delegated = delegated = JoseHeader.parse(rawJson, jsonRepresentedFactory);
                }
            }
        }
        return delegated;
    }

    @Override
    public <T> T get(String name, Class<? extends T> type) {
        return parsedDelegated().get(name, type);
    }

    @Override
    public <T> T get(String name, Type type) {
        return parsedDelegated().get(name, type);
    }

    @Override
    public String getString(String name) {
        return parsedDelegated().getString(name);
    }

    @Override
    public OptionalInt getInt(String name) {
        return parsedDelegated().getInt(name);
    }

    @Override
    public OptionalLong getLong(String name) {
        return parsedDelegated().getLong(name);
    }

    @Override
    public OptionalDouble getDouble(String name) {
        return parsedDelegated().getDouble(name);
    }

    @Override
    public Boolean getBoolean(String name) {
        return parsedDelegated().getBoolean(name);
    }

    @Override
    public <T> List<T> getList(String name, Class<T> elementType) {
        return parsedDelegated().getList(name, elementType);
    }

    @Override
    public String toString() {
        return "LazyParsedJoseHeader(delegated=" + parsedDelegated() + ")";
    }

}
