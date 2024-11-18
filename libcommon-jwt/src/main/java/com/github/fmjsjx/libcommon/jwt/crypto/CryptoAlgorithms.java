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
    private static final ConcurrentMap<String, CryptoAlgorithm> JCA_ALGORITHMS = new ConcurrentHashMap<>();

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
     * Find the {@link CryptoAlgorithm} with the specified JCA name given
     * from all registered Algorithms.
     *
     * @param jcaName the name of the JCA algorithm
     * @return an {@code Optional<CryptoAlgorithm>}
     */
    static Optional<CryptoAlgorithm> findAlgorithmByJcaName(String jcaName) {
        return Optional.ofNullable(JCA_ALGORITHMS.get(jcaName));
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
        JCA_ALGORITHMS.put(algorithm.getJcaName(), algorithm);
        return ALGORITHMS.put(algorithm.getName(), algorithm);
    }

    /**
     * Constants for all embedded algorithms in JWA (RFC 7518) standard
     * <a href="https://www.rfc-editor.org/rfc/rfc7518.html#section-3">
     * Cryptographic Algorithms for Digital Signatures and MACs
     * </a>
     */
    public static final class JWSs {

        /**
         * HMAC using SHA-256
         */
        public static JwsCryptoAlgorithm HS256 = JwsCryptoAlgorithms.HS256;
        /**
         * HMAC using SHA-384
         */
        public static JwsCryptoAlgorithm HS384 = JwsCryptoAlgorithms.HS384;
        /**
         * HMAC using SHA-512
         */
        public static JwsCryptoAlgorithm HS512 = JwsCryptoAlgorithms.HS512;
        /**
         * RSASSA-PKCS1-v1_5 using SHA-256
         */
        public static JwsCryptoAlgorithm RS256 = JwsCryptoAlgorithms.RS256;
        /**
         * RSASSA-PKCS1-v1_5 using SHA-384
         */
        public static JwsCryptoAlgorithm RS384 = JwsCryptoAlgorithms.RS384;
        /**
         * RSASSA-PKCS1-v1_5 using SHA-512
         */
        public static JwsCryptoAlgorithm RS512 = JwsCryptoAlgorithms.RS512;

        /**
         * RSASSA-PSS using SHA-256 and MGF1 with SHA-256
         */
        public static JwsCryptoAlgorithm PS256 = JwsCryptoAlgorithms.PS256;
        /**
         * RSASSA-PSS using SHA-384 and MGF1 with SHA-384
         */
        public static JwsCryptoAlgorithm PS384 = JwsCryptoAlgorithms.PS384;
        /**
         * RSASSA-PSS using SHA-512 and MGF1 with SHA-512
         */
        public static JwsCryptoAlgorithm PS512 = JwsCryptoAlgorithms.PS512;

        private JWSs() {
        }

    }

    private CryptoAlgorithms() {
    }

}
