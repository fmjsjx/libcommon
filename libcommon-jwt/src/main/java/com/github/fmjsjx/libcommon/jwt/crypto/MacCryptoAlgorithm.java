package com.github.fmjsjx.libcommon.jwt.crypto;

import com.github.fmjsjx.libcommon.jwt.exception.SecurityException;
import com.github.fmjsjx.libcommon.util.concurrent.EasyThreadLocal;

import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The implementation of {@link JwsCryptoAlgorithm} for {@link Mac}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
final class MacCryptoAlgorithm extends AbstractCryptoAlgorithm implements JwsCryptoAlgorithm {

    /**
     * Creates and returns a new {@link MacCryptoAlgorithm} instance with "HMAC using SHA" algorithm.
     *
     * @param digestBitLength the digest bit length
     * @return the {@code MacCryptoAlgorithm} instance
     */
    static MacCryptoAlgorithm createHS(int digestBitLength) {
        return new MacCryptoAlgorithm("HS" + digestBitLength, "HMAC using SHA-" + digestBitLength, "HmacSHA" + digestBitLength, true);
    }

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
        private final ConcurrentMap<Key, MacFunction> macFunctions = new ConcurrentHashMap<>();

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

        @Override
        public MacFunction getFunction(Key key) {
            return macFunctions.computeIfAbsent(key, this::generateFunction);
        }

        private MacFunction generateFunction(Key key) {
            var threadLocalMac = EasyThreadLocal.create(() -> {
                try {
                    return getInstance(key);
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new SecurityException("Initialize Mac instance failed", e);
                }
            });
            return bytes -> threadLocalMac.get().doFinal(bytes);
        }

    }

}
