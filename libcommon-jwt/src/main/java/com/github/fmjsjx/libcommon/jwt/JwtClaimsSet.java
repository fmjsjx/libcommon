package com.github.fmjsjx.libcommon.jwt;

import static com.github.fmjsjx.libcommon.jwt.JwtClaimNames.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * A JWT <a href="https://datatracker.ietf.org/doc/html/rfc7519#section-4">Claims Set</a>.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JwtClaimsSet extends JsonRepresented {

    /**
     * Parse the JSON byte array to {@link JwtClaimsSet} instance by
     * using the default {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @return a {@code JwtClaimsSet} instance
     */
    static JwtClaimsSet parse(byte[] rawJson) {
        return DefaultJwtClaimsSet.parse(rawJson);
    }

    /**
     * Parse the JSON byte array to {@link JwtClaimsSet} instance by
     * using the specified {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @param factory the {@link JsonRepresentedFactory}
     * @return a {@code JwtClaimsSet} instance
     */
    static JwtClaimsSet parse(byte[] rawJson, JsonRepresentedFactory<?> factory) {
        return DefaultJwtClaimsSet.parse(rawJson, factory);
    }

    /**
     * Returns the "iss" (issuer) claim value.
     *
     * @return the "iss" (issuer) claim value
     */
    default String getIssuer() {
        return getString(ISSUER);
    }

    /**
     * Returns the "sub" (subject) claim value.
     *
     * @return the "sub" (subject) claim value
     */
    default String getSubject() {
        return getString(SUBJECT);
    }

    /**
     * Returns the "aud" (audience) claim value.
     *
     * @return the "aud" (audience) claim value
     */
    default String getAudience() {
        return getString(AUDIENCE);
    }

    /**
     * Returns the "exp" (expiration time) claim value.
     *
     * @return the "exp" (expiration time) claim value
     */
    default OptionalLong getExpirationTime() {
        return getLong(EXPIRATION_TIME);
    }

    /**
     * Returns the "nbf" (not before) claim value.
     *
     * @return the "nbf" (not before) claim value
     */
    default OptionalLong getNotBefore() {
        return getLong(NOT_BEFORE);
    }

    /**
     * Returns the "iat" (issued at) claim value.
     *
     * @return the "iat" (issued at) claim value
     */
    default OptionalLong getIssuedAt() {
        return getLong(ISSUED_AT);
    }

    /**
     * Returns the "jti" (JWT ID) claim value.
     *
     * @return the "jti" (JWT ID) claim value
     */
    default String getJwtId() {
        return getString(JWT_ID);
    }

    /**
     * Returns the specified claim value by the name given.
     */
    @Override
    <T> T get(String name, Class<? extends T> type);

    /**
     * Returns the specified claim value by the name given.
     */
    @Override
    <T> T get(String name, Type type);

    /**
     * Returns the specified claim value string by the name given.
     */
    @Override
    String getString(String name);

    /**
     * Returns the specified claim value number as {@code int} type
     * by the name given.
     */
    @Override
    OptionalInt getInt(String name);

    /**
     * Returns the specified claim value number as {@code long} type
     * by the name given.
     */
    @Override
    OptionalLong getLong(String name);

    /**
     * Returns the specified claim value number as {@code double} type
     * by the name given.
     */
    @Override
    OptionalDouble getDouble(String name);

    /**
     * Returns the specified claim value number as {@link Boolean} type
     * by the name given.
     */
    @Override
    Boolean getBoolean(String name);

    /**
     * Returns the specified claim list value by the name given.
     */
    @Override
    <T> List<T> getList(String name, Class<T> elementType);

}
