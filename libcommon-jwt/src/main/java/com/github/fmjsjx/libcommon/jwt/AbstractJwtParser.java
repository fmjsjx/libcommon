package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.exception.ExpiredJwtException;
import com.github.fmjsjx.libcommon.jwt.exception.IllegalJwtException;
import com.github.fmjsjx.libcommon.util.Base64Util;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;

import java.util.Base64;

/**
 * The abstract implementation of {@link JwtParser}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public abstract class AbstractJwtParser implements JwtParser {

    /**
     * Encodes the specified byte array into a Base64 url safe string.
     *
     * @param content the byte array to encode
     * @return a base64 string
     */
    protected static String encodeBase64(byte[] content) {
        return Base64Util.encoder(true, true).encodeToString(content);
    }

    /**
     * Encode the signing base bytes from JWT parts.
     *
     * @param jwtParts JWT parts
     * @return the byte array to be signing
     */
    protected static byte[] signBaseBytes(String[] jwtParts) {
        var headerBytes = jwtParts[0].getBytes();
        var payloadBytes = jwtParts[1].getBytes();
        var signBaseBytes = new byte[headerBytes.length + 1 + payloadBytes.length];
        System.arraycopy(headerBytes, 0, signBaseBytes, 0, headerBytes.length);
        signBaseBytes[headerBytes.length] = '.';
        System.arraycopy(payloadBytes, 0, signBaseBytes, headerBytes.length + 1, payloadBytes.length);
        return signBaseBytes;
    }

    /**
     * The {@link JsonRepresentedFactory}.
     */
    protected final JsonRepresentedFactory<?> jsonRepresentedFactory;

    /**
     * If allow expired JWT or not.
     */
    protected final boolean allowExpired;

    /**
     * The allowed clock skew for expired validation in seconds.
     */
    protected final long allowedClockSkewSeconds;

    /**
     * Constructs new parser with the specified parameters given.
     *
     * @param jsonRepresentedFactory  the {@link JsonRepresentedFactory}
     * @param allowExpired            {@code true} if allow expired JWT,
     *                                {@code false} otherwise
     * @param allowedClockSkewSeconds the allowed clock skew for expired
     *                                validation in seconds
     */
    protected AbstractJwtParser(JsonRepresentedFactory<?> jsonRepresentedFactory, boolean allowExpired,
                                long allowedClockSkewSeconds) {
        this.jsonRepresentedFactory = jsonRepresentedFactory;
        this.allowExpired = allowExpired;
        this.allowedClockSkewSeconds = Math.max(0, allowedClockSkewSeconds);
    }

    /**
     * Splits the JWT string.
     *
     * @param jwt the JWT string to be split
     * @return the string array contains components of JWT, that means
     * the length of the array is always 3
     * @throws IllegalJwtException if the length of the string array is
     *                             not equal to 3
     */
    protected String[] splitJwtParts(String jwt) throws IllegalJwtException {
        var components = jwt.split("\\.", 3);
        if (components.length != 3) {
            throw new IllegalJwtException("Illegal JWT format");
        }
        return components;
    }

    /**
     * Parse JOSE header.
     *
     * @param base64String the Base64url encoded string for JOSE header
     * @return a {@code JoseHeader} instance
     */
    protected JoseHeader parseJoseHeader(String base64String) {
        return JoseHeader.parseLazy(base64String, jsonRepresentedFactory);
    }

    /**
     * Decodes content (Base64 url-safe encoded String) into a
     * newly-allocated byte array.
     *
     * @param base64String the content base64 string
     * @return the decoded bytes
     */
    protected byte[] decodeContent(String base64String) {
        return Base64.getUrlDecoder().decode(base64String);
    }

    /**
     * Parse and validate the JWT claims set from the given raw json.
     *
     * @param rawJson the raw JSON byte array
     * @return a {@code JwtClaimsSet} instance
     */
    protected JwtClaimsSet parseAndValidateClaimsSet(byte[] rawJson) {
        return checkIfExpired(JwtClaimsSet.parse(rawJson, jsonRepresentedFactory));
    }

    /**
     * Check if the specified claims set is expired.
     *
     * @param claimsSet the claims set
     * @return the input claims set
     * @throws ExpiredJwtException if is already expired
     */
    protected JwtClaimsSet checkIfExpired(JwtClaimsSet claimsSet) throws ExpiredJwtException {
        if (!allowExpired) {
            claimsSet.getExpirationTime().ifPresent(this::checkIfExpired);
        }
        return claimsSet;
    }

    /**
     * Check if is expired.
     *
     * @param exp the expiration time (Unix Time)
     * @throws ExpiredJwtException if is already expired
     */
    protected void checkIfExpired(long exp) throws ExpiredJwtException {
        var now = DateTimeUtil.unixTime();
        if (now >= exp - allowedClockSkewSeconds) {
            var msg = "This JWT expired " + (now - exp) + " seconds ago at " + DateTimeUtil.local(exp);
            throw new ExpiredJwtException(msg);
        }
    }

}
