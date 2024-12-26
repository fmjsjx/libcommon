package com.github.fmjsjx.libcommon.jwt.crypto;

/**
 * The base interface for secure provider.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface SecureProvider {

    /**
     * Returns the JCA algorithm name.
     *
     * @return the JCA algorithm name
     */
    String getAlgorithm();

}
