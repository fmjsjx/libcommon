package com.github.fmjsjx.libcommon.jwt.crypto;

/**
 * The implementation of {@link JwsCryptoAlgorithm} for RSA digital
 * signatures.
 *
 * @author MJ Fang
 * @since 3.10
 */
final class RsaCryptoAlgorithm extends AbstractCryptoAlgorithm implements JwsCryptoAlgorithm {

    RsaCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard) {
        super(name, description, jcaName, jdkStandard);
    }

    @Override
    public boolean isMac() {
        return false;
    }

    @Override
    public MacProvider getMacProvider() {
        return null;
    }


}
