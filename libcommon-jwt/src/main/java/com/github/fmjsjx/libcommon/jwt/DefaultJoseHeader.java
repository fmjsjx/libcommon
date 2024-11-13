package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.fmjsjx.libcommon.json.Fastjson2Library;
import com.github.fmjsjx.libcommon.jwt.exception.IllegalJwtException;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The default implementation of {@link JoseHeader}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class DefaultJoseHeader extends Fastjson2JsonRepresented implements JoseHeader {

    static {
        JSON.register(DefaultJoseHeader.class, (jsonWriter, object, fieldName, fieldType, features) -> {
            if (object == null) {
                jsonWriter.writeNull();
            } else {
                jsonWriter.writeRaw(((DefaultJoseHeader) object).rawJson);
            }
        });
    }

    /**
     * Parse the JSON byte array to {@link DefaultJoseHeader}.
     *
     * @param rawJson the raw JSON byte array
     * @return a {@code DefaultJoseHeader}
     */
    public static DefaultJoseHeader parse(byte[] rawJson) {
        Objects.requireNonNull(rawJson, "rawJson must not be null");
        try {
            return new DefaultJoseHeader(rawJson, Fastjson2Library.getInstance().loads(rawJson));
        } catch (Fastjson2Library.Fastjson2Exception e) {
            throw new IllegalJwtException("Unable to parse JOSE header from JSON value: " +
                    new String(rawJson, StandardCharsets.UTF_8), e);
        }
    }

    private DefaultJoseHeader(byte[] rawJson, JSONObject json) {
        super(rawJson, json);
    }

}
