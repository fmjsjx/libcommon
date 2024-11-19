package com.github.fmjsjx.libcommon.jwt;

/**
 * An expanded (not compact/serialized) JSON Web Token.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface Jwt {

    /**
     * Returns the header.
     *
     * @return a {@link JoseHeader}
     */
    JoseHeader getHeader();

    /**
     * Returns a copy of the content byte array.
     *
     * @return a copy of the content byte array
     */
    byte[] getContent();

    /**
     * Returns the claims set.
     *
     * @return a {@link JwtClaimsSet}
     */
    JwtClaimsSet getClaimsSet();

}
