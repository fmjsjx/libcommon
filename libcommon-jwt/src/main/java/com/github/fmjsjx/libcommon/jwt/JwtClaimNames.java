package com.github.fmjsjx.libcommon.jwt;

/**
 * JWT claim names.
 *
 * @author MJ Fang
 * @since 3.10
 */
public final class JwtClaimNames {

    /**
     * {@code "iss"} (Issuer) Claim.
     */
    public static final String ISSUER = "iss";

    /**
     * {@code "sub"} (Subject) Claim.
     */
    public static final String SUBJECT = "sub";

    /**
     * {@code "aud"} (Audience) Claim.
     */
    public static final String AUDIENCE = "aud";

    /**
     * {@code "exp"} (Expiration Time) Claim.
     */
    public static final String EXPIRATION_TIME = "exp";

    /**
     * {@code "nbf"} (Not Before) Claim.
     */
    public static final String NOT_BEFORE = "nbf";

    /**
     * {@code "iat"} (Issued At) Claim.
     */
    public static final String ISSUED_AT = "iat";

    /**
     * {@code "jti"} (JWT ID) Claim.
     */
    public static final String JWT_ID = "jti";

    private JwtClaimNames() {
    }

}
