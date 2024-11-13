package com.github.fmjsjx.libcommon.jwt.crypto;

/**
 * Defines cryptographic algorithms used to secure the JWS.
 * <p>
 * JWS uses cryptographic algorithms to digitally sign or create a MAC of
 * the contents of the JWS Protected Header and the JWS Payload.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JwsCryptoAlgorithm extends CryptoAlgorithm {

    /**
     * Returns weather the algorithm is MAC or not.
     *
     * @return {@code true} if the algorithm is MAC, {@code false}
     * otherwise
     */
    boolean isMac();

    /**
     * Returns the {@link MacProvider}.
     * <p>
     * This method will always return {@code null} when {@link #isMac()}
     * returns {@code false}.
     *
     * @return the {@code MacProvider} instance
     */
    MacProvider getMacProvider();

}
