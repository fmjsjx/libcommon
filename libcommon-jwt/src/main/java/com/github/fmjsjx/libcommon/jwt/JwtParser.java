package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.exception.JwtException;

/**
 * A parser parses {@link Jwt}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JwtParser {

    /**
     * Parse and returns the {@link Jwt} from the specified JWT string
     * given.
     *
     * @param jwt the JWT string
     * @return a {@link Jwt} instance
     * @throws JwtException if any error occurs during the parsing
     *                      operation and the JWT <b>SHOULD</b> be
     *                      rejected
     */
    Jwt parse(String jwt) throws JwtException;

}
