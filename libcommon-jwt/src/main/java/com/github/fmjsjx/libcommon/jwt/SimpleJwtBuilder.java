package com.github.fmjsjx.libcommon.jwt;

import com.alibaba.fastjson2.JSONObject;
import com.github.fmjsjx.libcommon.json.Fastjson2Library;
import com.github.fmjsjx.libcommon.jwt.crypto.CryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.MacProvider.MacFunction;
import com.github.fmjsjx.libcommon.jwt.exception.SecurityException;
import com.github.fmjsjx.libcommon.jwt.exception.UnsupportedAlgorithmException;
import com.github.fmjsjx.libcommon.util.Base64Util;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Objects;

import static com.github.fmjsjx.libcommon.jwt.JoseHeaderNames.*;
import static com.github.fmjsjx.libcommon.jwt.JwtClaimNames.*;

/**
 * A simple JWT builder creates JWT strings in the simplest way
 * possible.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class SimpleJwtBuilder {

    /**
     * Creates and returns a new {@link SimpleJwtBuilder} instance.
     *
     * @return a new {@code SimpleJwtBuilder} instance
     */
    public static SimpleJwtBuilder create() {
        return new SimpleJwtBuilder();
    }

    private static final byte[] EMPTY_CONTENT_BYTES = "{}".getBytes();

    private JSONObject header;
    private byte[] cachedHeader;
    private byte[] content;
    private JSONObject claimsSet;
    private long expiredSeconds;

    /**
     * Sets the header.
     *
     * @param header the header object
     * @return this builder
     */
    public SimpleJwtBuilder appendHeaders(JSONObject header) {
        var old = this.header;
        var newHeader = new JSONObject();
        if (old != null && !old.isEmpty()) {
            newHeader.putAll(old);
        }
        newHeader.putAll(header);
        this.header = newHeader;
        return this;
    }

    /**
     * Sets the cached header byte array
     *
     * @param cachedHeader the cached header byte array
     * @return this builder
     */
    public SimpleJwtBuilder cachedHeader(byte[] cachedHeader) {
        this.cachedHeader = cachedHeader;
        return this;
    }

    /**
     * Sets the header parameter value.
     *
     * @param key   the key
     * @param value the value
     * @return this builder
     */
    public SimpleJwtBuilder header(String key, Object value) {
        ensureHeader().put(key, value);
        return this;
    }

    private JSONObject ensureHeader() {
        var header = this.header;
        if (header == null) {
            this.header = header = new JSONObject();
        }
        return header;
    }

    /**
     * Sets the {@code "kid"} (Key ID) Header Parameter.
     *
     * @param kid the {@code "kid"} (Key ID) Header Parameter value
     * @return this builder
     */
    public SimpleJwtBuilder kid(String kid) {
        return header(KEY_ID, kid);
    }

    /**
     * Sets the claims set.
     *
     * @param claimsSet the claims set
     * @return this builder
     */
    public SimpleJwtBuilder claimsSet(JSONObject claimsSet) {
        this.claimsSet = claimsSet;
        this.content = null;
        return this;
    }

    private JSONObject ensureClaimsSet() {
        var claimsSet = this.claimsSet;
        if (claimsSet == null) {
            this.claimsSet = claimsSet = new JSONObject();
        }
        return claimsSet;
    }

    /**
     * Sets the claims set value.
     *
     * @param key   the key
     * @param value the value
     * @return this builder
     */
    public SimpleJwtBuilder claimsSet(String key, Object value) {
        ensureClaimsSet().put(key, value);
        return this;
    }

    /**
     * Sets the {@code "iss"} (Issuer) Claim.
     *
     * @param issuer the {@code "iss"} (Issuer) Claim
     * @return this builder
     */
    public SimpleJwtBuilder issuer(String issuer) {
        return claimsSet(ISSUER, issuer);
    }

    /**
     * Sets the {@code "sub"} (Subject) Claim.
     *
     * @param subject the {@code "sub"} (Subject) Claim
     * @return this builder
     */
    public SimpleJwtBuilder subject(String subject) {
        return claimsSet(SUBJECT, subject);
    }

    /**
     * Sets the {@code "aud"} (Audience) Claim.
     *
     * @param audience the {@code "aud"} (Audience) Claim
     * @return this builder
     */
    public SimpleJwtBuilder audience(String audience) {
        return claimsSet(AUDIENCE, audience);
    }

    /**
     * Sets the {@code "nbf"} (Not Before) Claim.
     *
     * @param notBefore the {@code "nbf"} (Not Before) Claim
     * @return this builder
     */
    public SimpleJwtBuilder notBefore(Long notBefore) {
        return claimsSet(NOT_BEFORE, notBefore);
    }

    /**
     * Sets the {@code "jti"} (JWT ID) Claim.
     *
     * @param jwtId the {@code "jti"} (JWT ID) Claim
     * @return this builder
     */
    public SimpleJwtBuilder jwtId(String jwtId) {
        return claimsSet(JWT_ID, jwtId);
    }

    /**
     * Sets the expired seconds.
     *
     * @param expiredSeconds the expired seconds
     * @return this builder
     */
    public SimpleJwtBuilder expiredSeconds(long expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
        return this;
    }

    /**
     * Sets the content.
     *
     * @param content     the content byte array
     * @param contentType the content type string
     * @return this builder
     */
    public SimpleJwtBuilder content(byte[] content, String contentType) {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        this.claimsSet = null;
        this.expiredSeconds = 0;
        this.content = content;
        return header(CONTENT_TYPE, contentType);
    }

    /**
     * Creates and returns an insecure JWT string from this builder.
     *
     * @return an insecure JWT string
     */
    public String buildInsecure() {
        var payload = encodePayload();
        var header = this.cachedHeader;
        if (header == null) {
            header = encodeHeader("none");
        }
        var bytes = new byte[header.length + payload.length + 2];
        copyHeaderPayload(header, payload, bytes);
        bytes[bytes.length - 1] = '.';
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    private static void copyHeaderPayload(byte[] header, byte[] payload, byte[] dest) {
        System.arraycopy(header, 0, dest, 0, header.length);
        dest[header.length] = '.';
        System.arraycopy(payload, 0, dest, header.length + 1, payload.length);
    }

    private byte[] encodePayload() {
        var expiredSeconds = this.expiredSeconds;
        if (expiredSeconds > 0) {
            var now = DateTimeUtil.unixTime();
            ensureClaimsSet().fluentPut(ISSUED_AT, now).fluentPut(EXPIRATION_TIME, now + expiredSeconds);
        }
        byte[] content;
        var claimsSet = this.claimsSet;
        if (claimsSet != null) {
            content = Fastjson2Library.getInstance().dumpsToBytes(claimsSet);
            if (this.content != null) {
                ensureHeader().remove(CONTENT_TYPE);
            }
        } else {
            content = this.content;
        }
        if (content == null) {
            content = EMPTY_CONTENT_BYTES;
        }
        return Base64Util.encoder(true, true).encode(content);
    }

    private byte[] encodeHeader(String algorithm) {
        var bytes = Fastjson2Library.getInstance().dumpsToBytes(ensureHeader().fluentPut(ALGORITHM, algorithm));
        return Base64Util.encoder(true, true).encode(bytes);
    }

    /**
     * Creates and returns a JWT string from this builder.
     *
     * @param algorithm the cryptographic algorithms used to secure the JWT
     * @param key       the security key
     * @return a JWT string
     */
    public String build(JwsCryptoAlgorithm algorithm, Key key) {
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        Objects.requireNonNull(key, "key must not be null");
        var payload = encodePayload();
        var header = this.cachedHeader;
        if (header == null) {
            header = encodeHeader(algorithm.getName());
        }
        if (algorithm.isMac()) {
            return decodeToJwtString(header, payload, encodeHmac(algorithm, key, header, payload));
        } else {
            return decodeToJwtString(header, payload, encodeSignature(algorithm, key, header, payload));
        }
    }

    private static byte[] encodeHmac(JwsCryptoAlgorithm algorithm, Key key, byte[] header, byte[] payload) {
        var macFunction = algorithm.getMacProvider().getFunction(key);
        var sign = macFunction.apply(toSignBaseBytes(header, payload));
        return Base64Util.encoder(true, true).encode(sign);
    }

    private static byte[] toSignBaseBytes(byte[] header, byte[] payload) {
        var signBytes = new byte[header.length + payload.length + 1];
        copyHeaderPayload(header, payload, signBytes);
        return signBytes;
    }

    private static byte[] encodeSignature(JwsCryptoAlgorithm algorithm, Key key, byte[] header, byte[] payload) {
        if (key instanceof PrivateKey privateKey) {
            var signatureProvider = algorithm.getSignatureProvider();
            try {
                var signature = signatureProvider.getInstance(privateKey);
                signature.update(toSignBaseBytes(header, payload));
                return Base64Util.encoder(true, true).encode(signature.sign());
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                throw new SecurityException("Initialize Signature instance failed", e);
            } catch (SignatureException e) {
                throw new SecurityException("Fail to sign data", e);
            }
        }
        throw new com.github.fmjsjx.libcommon.jwt.exception.InvalidKeyException("The key must be a private key");
    }

    private static String decodeToJwtString(byte[] header, byte[] payload, byte[] signature) {
        var bytes = new byte[header.length + payload.length + signature.length + 2];
        copyHeaderPayload(header, payload, bytes);
        bytes[header.length + 1 + payload.length] = '.';
        System.arraycopy(signature, 0, bytes, header.length + payload.length + 2, signature.length);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Creates and returns a JWT string from this builder.
     *
     * @param algorithm   the JWS cryptographic algorithms used to secure
     *                    the JWT, <b>MUST</b> be MAC algorithm
     * @param macFunction the {@link MacFunction}
     * @return a JWT string
     */
    public String build(JwsCryptoAlgorithm algorithm, MacFunction macFunction) {
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        if (!algorithm.isMac()) {
            throw new IllegalArgumentException("must be MAC algorithm");
        }
        Objects.requireNonNull(macFunction, "macFunction must not be null");
        var payload = encodePayload();
        var header = this.cachedHeader;
        if (header == null) {
            header = encodeHeader(algorithm.getName());
        }
        var sign = macFunction.apply(toSignBaseBytes(header, payload));
        return decodeToJwtString(header, payload, Base64Util.encoder(true, true).encode(sign));
    }

}
