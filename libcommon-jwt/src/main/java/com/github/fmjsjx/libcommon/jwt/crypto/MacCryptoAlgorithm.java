package com.github.fmjsjx.libcommon.jwt.crypto;

import javax.crypto.Mac;

/**
 * The implementation of {@link JwsCryptoAlgorithm} for {@link Mac}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
final class MacCryptoAlgorithm extends AbstractCryptoAlgorithm implements JwsCryptoAlgorithm {

    private final MacProvider macProvider;

    /**
     * Constructs a new {@link MacCryptoAlgorithm} instance with the
     * specified parameters given.
     *
     * @param name        the name of the JWA algorithm
     * @param description the description of the JWA algorithm
     * @param jcaName     the name of the JCA algorithm
     * @param jdkStandard weather the algorithm is supported by standard
     *                    JDK distributions or not
     */
    MacCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard) {
        this(name, description, jcaName, jdkStandard, new MacProviderImpl(jcaName));
    }

    /**
     * Constructs a new {@link MacCryptoAlgorithm} instance with the
     * specified parameters given.
     *
     * @param name        the name of the JWA algorithm
     * @param description the description of the JWA algorithm
     * @param jcaName     the name of the JCA algorithm
     * @param jdkStandard weather the algorithm is supported by standard
     *                    JDK distributions or not
     * @param macProvider the {@link MacProvider}
     */
    MacCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard, MacProvider macProvider) {
        super(name, description, jcaName, jdkStandard);
        this.macProvider = macProvider;
    }

    @Override
    public boolean isMac() {
        return true;
    }

    @Override
    public MacProvider getMacProvider() {
        return macProvider;
    }

    @Override
    public String toString() {
        return "MacCryptoAlgorithm(" +
                "name=" + getName() +
                ", description=" + getDescription() +
                ", jcaName=" + getName() +
                ", jdkStandard=" + isJdkStandard() +
                ", macProvider" + getMacProvider() +
                ")";
    }

    private static final class MacProviderImpl implements MacProvider {

        private final String algorithm;

        private MacProviderImpl(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String getAlgorithm() {
            return algorithm;
        }

        @Override
        public String toString() {
            return "MacProviderImpl(algorithm=" + getAlgorithm() + ")";
        }

    }

}
