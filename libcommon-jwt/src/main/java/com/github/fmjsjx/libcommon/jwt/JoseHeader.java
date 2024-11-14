package com.github.fmjsjx.libcommon.jwt;

import static com.github.fmjsjx.libcommon.jwt.JoseHeaderNames.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;


/**
 * A JWT <a href="https://datatracker.ietf.org/doc/html/rfc7519#section-5">JOSE header</a>.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JoseHeader extends JsonRepresented {

    /**
     * Parse the JSON byte array to a {@link JoseHeader} instance by
     * using the default {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @return a {@code JoseHeader} instance
     */
    static JoseHeader parse(byte[] rawJson) {
        return DefaultJoseHeader.parse(rawJson);
    }


    /**
     * Parse the JSON byte array to a {@link JoseHeader} instance by
     * using the specified {@link JsonRepresentedFactory}.
     *
     * @param rawJson the raw JSON byte array
     * @param factory the {@link JsonRepresentedFactory}
     * @return a {@code JoseHeader} instance
     */
    static JoseHeader parse(byte[] rawJson, JsonRepresentedFactory<?> factory) {
        return DefaultJoseHeader.parse(rawJson, factory);
    }

    /**
     * Parse the JSON byte array to a lazy parsed {@link JoseHeader}
     * instance by using the default {@link JsonRepresentedFactory}.
     *
     * @param base64String the Base64url encoded string for JOSE header
     * @return a {@code JoseHeader} instance
     */
    static JoseHeader parseLazy(String base64String) {
        return parseLazy(base64String, Fastjson2JsonRepresented.getFactory());
    }


    /**
     * Parse the JSON byte array to a lazy parsed {@link JoseHeader}
     * instance by using the specified {@link JsonRepresentedFactory}.
     *
     * @param base64String the Base64url encoded string for JOSE header
     * @param factory      the {@link JsonRepresentedFactory}
     * @return a {@code JoseHeader} instance
     */
    static JoseHeader parseLazy(String base64String, JsonRepresentedFactory<?> factory) {
        return new LazyParsedJoseHeader(base64String, factory);
    }

    /**
     * Returns the "alg" (algorithm) header parameter value.
     *
     * @return the "alg" (algorithm) header parameter value
     */
    default String getAlgorithm() {
        return getString(ALGORITHM);
    }

    /**
     * Returns the "jku" (JWK Set URL) header parameter value.
     *
     * @return the "jku" (JWK Set URL) header parameter value
     */
    default String getJwkSetUrl() {
        return getString(JWK_SET_URL);
    }

    /**
     * Returns the "jwk" (JSON Web Key) header parameter value.
     *
     * @return the "jwk" (JSON Web Key) header parameter value
     */
    default String getJsonWebKey() {
        return getString(JSON_WEB_KEY);
    }

    /**
     * Returns the "kid" (Key ID) header parameter value.
     *
     * @return the "kid" (Key ID) header parameter value
     */
    default String getKeyId() {
        return getString(KEY_ID);
    }

    /**
     * Returns the "x5u" (X.509 URL) header parameter value.
     *
     * @return the "x5u" (X.509 URL) header parameter value
     */
    default String getX509Url() {
        return getString(X509_URL);
    }

    /**
     * Returns the "x5c" (X.509 Certificate Chain) header parameter value.
     *
     * @return the "x5c" (X.509 Certificate Chain) header parameter value
     */
    default List<String> getX509CertificateChain() {
        return getList(X509_CERTIFICATE_CHAIN, String.class);
    }

    /**
     * Returns the "x5t" (X.509 Certificate SHA-1 Thumbprint) header parameter value.
     *
     * @return the "x5t" (X.509 Certificate SHA-1 Thumbprint) header parameter value
     */
    default String getX509CertificateSha1Thumbprint() {
        return getString(X509_CERTIFICATE_SHA1_THUMBPRINT);
    }

    /**
     * Returns the "x5t#S256" (X.509 Certificate SHA-256 Thumbprint) header parameter value.
     *
     * @return the "x5t#S256" (X.509 Certificate SHA-256 Thumbprint) header parameter value
     */
    default String getX509CertificateSha256Thumbprint() {
        return getString(X509_CERTIFICATE_SHA256_THUMBPRINT);
    }

    /**
     * Returns the "typ" (Type) header parameter value.
     *
     * @return the "typ" (Type) header parameter value
     */
    default String getType() {
        return getString(TYPE);
    }

    /**
     * Returns the "cty" (Content Type) header parameter value.
     *
     * @return the "cty" (Content Type) header parameter value
     */
    default String getContentType() {
        return getString(CONTENT_TYPE);
    }

    /**
     * Returns the "crit" (Critical) header parameter value.
     *
     * @return the "crit" (Critical) header parameter value
     */
    default List<String> getCritical() {
        return getList(CRITICAL, String.class);
    }

    /**
     * Returns the specified header parameter value by the name given.
     */
    @Override
    <T> T get(String name, Class<? extends T> type);

    /**
     * Returns the specified header parameter value by the name given.
     */
    @Override
    <T> T get(String name, Type type);

    /**
     * Returns the specified header parameter value string by the name
     * given.
     */
    @Override
    String getString(String name);

    /**
     * Returns the specified header parameter value number as {@code int}
     * type by the name given.
     */
    @Override
    OptionalInt getInt(String name);

    /**
     * Returns the specified header parameter value number as {@code long}
     * type by the name given.
     */
    @Override
    OptionalLong getLong(String name);

    /**
     * Returns the specified header parameter value number as {@code double}
     * type by the name given.
     */
    @Override
    OptionalDouble getDouble(String name);

    /**
     * Returns the specified header parameter value number as {@link Boolean}
     * type by the name given.
     */
    @Override
    Boolean getBoolean(String name);

    /**
     * Returns the specified header parameter list value by the name given.
     */
    @Override
    <T> List<T> getList(String name, Class<T> elementType);

}
