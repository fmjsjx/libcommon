package com.github.fmjsjx.libcommon.jwt.crypto;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cryptographic algorithms.
 *
 * @author MJ Fang
 * @since 3.10
 */
public final class CryptoAlgorithms {

    private static final ConcurrentMap<String, CryptoAlgorithm> ALGORITHMS = new ConcurrentHashMap<>();

    static {
        // Register for JWS
        JwsCryptoAlgorithms.registerAll();
        // Now only support JWS, JWE may be supported in future version.
    }

    /**
     * Find the {@link CryptoAlgorithm} with the specified name given
     * from all registered Algorithms.
     *
     * @param name the name of the algorithm
     * @return an {@code Optional<CryptoAlgorithm>}
     */
    static Optional<CryptoAlgorithm> findAlgorithm(String name) {
        return Optional.ofNullable(ALGORITHMS.get(name));
    }

    /**
     * Register the specified {@link CryptoAlgorithm}.
     *
     * @param algorithm the {@link CryptoAlgorithm} instance to be
     *                  registered
     * @return the previous algorithm with the same name, or {@code null}
     * if there was no algorithm for the same name
     */
    public static CryptoAlgorithm registerAlgorithm(CryptoAlgorithm algorithm) {
        return ALGORITHMS.put(algorithm.getName(), algorithm);
    }

    private CryptoAlgorithms() {
    }

}
