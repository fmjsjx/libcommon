package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSON;
import com.github.fmjsjx.libcommon.json.JsonException;
import com.github.fmjsjx.libcommon.jwt.exception.IllegalJwtException;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The default implementation of {@link JwtClaimsSet}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class DefaultJwtClaimsSet extends AbstractJsonRepresented implements JwtClaimsSet {

    static {
        JSON.register(DefaultJwtClaimsSet.class, ((jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeRaw(((DefaultJwtClaimsSet) object).rawJson);
            }
        }));
    }

    /**
     * Parse the JSON byte array to {@link DefaultJwtClaimsSet} by using
     * the default {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @return a {@code DefaultJwtClaimsSet}
     */
    public static DefaultJwtClaimsSet parse(byte[] rawJson) {
        return parse(rawJson, Fastjson2JsonRepresented.getFactory());
    }

    /**
     * Parse the JSON byte array to {@link DefaultJwtClaimsSet} by using
     * the specified {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @param factory the {@link JsonRepresentedFactory}
     * @return a {@code DefaultJwtClaimsSet}
     */
    public static DefaultJwtClaimsSet parse(byte[] rawJson, JsonRepresentedFactory<?> factory) {
        Objects.requireNonNull(rawJson, "rawJson must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        try {
            return new DefaultJwtClaimsSet(rawJson, factory.create(rawJson));
        } catch (JsonException e) {
            throw new IllegalJwtException("Unable to parse JWT Claims Set from JSON value: " +
                    new String(rawJson, StandardCharsets.UTF_8), e);
        }
    }

    private DefaultJwtClaimsSet(byte[] rawJson, JsonRepresented delegated) {
        super(rawJson, delegated);
    }

}
