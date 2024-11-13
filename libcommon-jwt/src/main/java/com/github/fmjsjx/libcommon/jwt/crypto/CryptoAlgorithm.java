package com.github.fmjsjx.libcommon.jwt.crypto;

import com.github.fmjsjx.libcommon.jwt.exception.UnsupportedAlgorithmException;

/**
 * Defines cryptographic algorithms used to secure the JWT.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface CryptoAlgorithm {

    /**
     * Returns the {@link CryptoAlgorithm} instance with the specified name given.
     *
     * @param name the name of the algorithm
     * @return the specified {@code CryptoAlgorithm} instance
     * @throws UnsupportedAlgorithmException if the algorithm is still unsupported
     */
    static CryptoAlgorithm getInstance(String name) throws UnsupportedAlgorithmException {
        return CryptoAlgorithms.findAlgorithm(name)
                .orElseThrow(() -> new UnsupportedAlgorithmException("Unsupported algorithm with name `" + name + "`"));
    }

    /**
     * Returns the name of the JWA algorithm.
     *
     * @return the name of the JWA algorithm
     */
    String getName();

    /**
     * Returns the description of the JWA algorithm,
     *
     * @return the description of the JWA algorithm
     */
    String getDescription();

    /**
     * Returns the name of the JCA algorithm.
     *
     * @return the name of the JCA algorithm
     */
    String getJcaName();

    /**
     * Returns weather the algorithm is supported by standard JDK
     * distributions or not.
     *
     * @return {@code true} if the algorithm is supported by standard
     * JDK distributions, {@code false} otherwise.
     */
    boolean isJdkStandard();

}
