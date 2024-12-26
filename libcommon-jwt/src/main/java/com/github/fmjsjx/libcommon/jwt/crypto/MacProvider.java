package com.github.fmjsjx.libcommon.jwt.crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

/**
 * The interface provides functionality of {@link Mac}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface MacProvider extends SecureProvider {

    /**
     * Returns the name of the MAC algorithm.
     *
     * @return the name of the MAC algorithm
     */
    @Override
    String getAlgorithm();

    /**
     * Constructs a secret key from the given byte array.
     *
     * @param keyBytes the key material of the secret key
     * @return a {@link SecretKey}
     */
    default SecretKey getSecretKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, getAlgorithm());
    }

    /**
     * Returns a {@link Mac} object that already be initialized with the
     * given key.
     *
     * @param key the key
     * @return the {@code Mac} object
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a
     *                                  {@code MacSpi} implementation for
     *                                  the specified algorithm
     * @throws InvalidKeyException      if the given key is inappropriate
     *                                  for initializing the MAC
     */
    default Mac getInstance(Key key) throws NoSuchAlgorithmException, InvalidKeyException {
        var mac = Mac.getInstance(getAlgorithm());
        mac.init(key);
        return mac;
    }

    /**
     * Returns the {@link MacFunction} instance with the specified key.
     *
     * @param key the key
     * @return the {@code MacFunction} instance
     */
    MacFunction getFunction(Key key);

    /**
     * Represents a function that processes an array of bytes and finishes the MAC operation.
     *
     * @author MJ Fang
     * @since 3.10
     */
    interface MacFunction extends Function<byte[], byte[]> {

        /**
         * Processes the given array of bytes and finishes the MAC operation.
         *
         * @param bytes data in bytes
         * @return the MAC result
         */
        byte[] apply(byte[] bytes);

    }

}
