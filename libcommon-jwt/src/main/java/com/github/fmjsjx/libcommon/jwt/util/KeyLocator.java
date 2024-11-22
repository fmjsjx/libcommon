package com.github.fmjsjx.libcommon.jwt.util;

import com.github.fmjsjx.libcommon.jwt.JoseHeader;

import java.security.Key;

/**
 * A {@link KeyLocator} can return a {@link Key} referenced in a JOSE
 * Header that is necessary to process the associated JWT.
 *
 * @author MJ Fang
 * @see KidKeyLocator
 * @since 3.10
 */
@FunctionalInterface
public interface KeyLocator {

    /**
     * Returns a {@link Key} referenced in the specified {@code header}.
     *
     * @param header the {@link JoseHeader}
     * @return an {@code Optional<Key>}
     */
    Key locate(JoseHeader header);

}
