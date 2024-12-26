package com.github.fmjsjx.libcommon.jwt.crypto;

/**
 * The abstract implementation of {@link CryptoAlgorithm}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public abstract class AbstractCryptoAlgorithm implements CryptoAlgorithm {

    protected final String name;
    protected final String description;
    protected final String jcaName;
    protected final boolean jdkStandard;

    /**
     * Constructs a new {@link AbstractCryptoAlgorithm} with the specified
     * parameters given.
     *
     * @param name        the name of the JWA algorithm
     * @param description the description of the JWA algorithm
     * @param jcaName     the name of the JCA algorithm
     * @param jdkStandard weather the algorithm is supported by standard
     *                    JDK distributions or not
     */
    protected AbstractCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard) {
        this.name = name;
        this.description = description;
        this.jcaName = jcaName;
        this.jdkStandard = jdkStandard;
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

}
