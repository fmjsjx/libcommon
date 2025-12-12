package com.github.fmjsjx.libcommon.jwt;

/**
 * JOSE header parameter names.
 *
 * @author MJ Fang
 * @since 3.10
 */
public final class JoseHeaderNames {
    /**
     * {@code "alg"} (Algorithm) Header Parameter
     */
    public static final String ALGORITHM = "alg";
    /**
     * {@code "jku"} (JWK Set URL) Header Parameter
     */
    public static final String JWK_SET_URL = "jku";
    /**
     * {@code "jwk"} (JSON Web Key) Header Parameter
     */
    public static final String JSON_WEB_KEY = "jwk";
    /**
     * {@code "kid"} (Key ID) Header Parameter
     */
    public static final String KEY_ID = "kid";
    /**
     * {@code "x5u"} (X.509 URL) Header Parameter
     */
    public static final String X509_URL = "x5u";
    /**
     * {@code "x5c"} (X.509 Certificate Chain) Header Parameter
     */
    public static final String X509_CERTIFICATE_CHAIN = "x5c";
    /**
     * {@code "x5t"} (X.509 Certificate SHA-1 Thumbprint) Header Parameter
     */
    public static final String X509_CERTIFICATE_SHA1_THUMBPRINT = "x5t";
    /**
     * {@code "x5t#S256"} (X.509 Certificate SHA-256 Thumbprint) Header Parameter
     */
    public static final String X509_CERTIFICATE_SHA256_THUMBPRINT = "x5t#S256";
    /**
     * {@code "typ"} (Type) Header Parameter
     */
    public static final String TYPE = "typ";
    /**
     * {@code "cty"} (Content Type) Header Parameter
     */
    public static final String CONTENT_TYPE = "cty";
    /**
     * {@code "crit"} (Critical) Header Parameter
     */
    public static final String CRITICAL = "crit";

    private JoseHeaderNames() {
    }

}
