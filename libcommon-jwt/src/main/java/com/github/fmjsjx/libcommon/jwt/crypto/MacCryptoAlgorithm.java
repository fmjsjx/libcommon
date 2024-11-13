package com.github.fmjsjx.libcommon.jwt.crypto;

import javax.crypto.Mac;

/**
 * The implementation of {@link JwsCryptoAlgorithm} for {@link Mac}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class MacCryptoAlgorithm implements JwsCryptoAlgorithm {

    private final String name;
    private final String description;
    private final String jcaName;
    private final boolean jdkStandard;
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
    public MacCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard) {
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
    protected MacCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard, MacProvider macProvider) {
        this.name = name;
        this.description = description;
        this.jcaName = jcaName;
        this.jdkStandard = jdkStandard;
        this.macProvider = macProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getJcaName() {
        return jcaName;
    }

    @Override
    public boolean isJdkStandard() {
        return jdkStandard;
    }

    @Override
    public final boolean isMac() {
        return true;
    }

    @Override
    public MacProvider getMacProvider() {
        return macProvider;
    }

    private record MacProviderImpl(String algorithm) implements MacProvider {
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
}
