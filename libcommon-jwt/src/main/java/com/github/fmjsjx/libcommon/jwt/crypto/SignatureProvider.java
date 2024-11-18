package com.github.fmjsjx.libcommon.jwt.crypto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * The interface provides functionality of {@link Signature}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface SignatureProvider extends SecureProvider {

    /**
     * Returns the name of the Signature algorithm.
     *
     * @return the name of the Signature algorithm
     */
    @Override
    String getAlgorithm();

    /**
     * Returns the {@link KeyFactory}.
     *
     * @return the key factory
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a
     *                                  {@code KeyFactorySpi}
     *                                  implementation for the specified
     *                                  algorithm
     */
    KeyFactory getKeyFactory() throws NoSuchAlgorithmException;

    /**
     * Generates a public key object from the provided byte array.
     *
     * @param keyBytes the byte array
     * @return a {@link PublicKey}
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a
     *                                  {@code KeyFactorySpi}
     *                                  implementation for the specified
     *                                  algorithm
     * @throws InvalidKeySpecException  if the given key specification is
     *                                  inappropriate for this key
     *                                  factory to produce a public key
     */
    default PublicKey generatePublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getKeyFactory().generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    /**
     * Returns a Signature object that implements the specified signature
     * algorithm.
     *
     * @return a {@link Signature}
     * @throws NoSuchAlgorithmException           if no {@code Provider}
     *                                            supports a
     *                                            {@code KeyFactorySpi}
     *                                            implementation for the
     *                                            specified algorithm
     * @throws InvalidAlgorithmParameterException if the given parameters
     *                                            are inappropriate for
     *                                            this signature engine
     */
    default Signature getInstance() throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        return Signature.getInstance(getAlgorithm());
    }

    /**
     * Returns a Signature object that implements the specified signature
     * algorithm and be initialized for verification.
     *
     * @param publicKey the public key of the identity whose signature is
     *                  going to be verified
     * @return a {@link Signature} object that implements the specified
     * signature  algorithm and be initialized for verification
     * @throws NoSuchAlgorithmException           if no {@code Provider}
     *                                            supports a
     *                                            {@code KeyFactorySpi}
     *                                            implementation for the
     *                                            specified algorithm
     * @throws InvalidAlgorithmParameterException if the given parameters
     *                                            are inappropriate for
     *                                            this signature engine
     * @throws InvalidKeyException                if the key is invalid
     */
    default Signature getInstance(PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException {
        var signature = getInstance();
        signature.initVerify(publicKey);
        return signature;
    }


    /**
     * Returns a Signature object that implements the specified signature
     * algorithm and be initialized for signing.
     *
     * @param privateKey the private key of the identity whose signature
     *                   is going to be generated
     * @return a {@link Signature} object that implements the specified
     * signature  algorithm and be initialized for verification
     * @throws NoSuchAlgorithmException           if no {@code Provider}
     *                                            supports a
     *                                            {@code KeyFactorySpi}
     *                                            implementation for the
     *                                            specified algorithm
     * @throws InvalidAlgorithmParameterException if the given parameters
     *                                            are inappropriate for
     *                                            this signature engine
     * @throws InvalidKeyException                if the key is invalid
     */
    default Signature getInstance(PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException {
        var signature = getInstance();
        signature.initSign(privateKey);
        return signature;
    }

    /**
     * Verifies the passed-in signature.
     *
     * @param publicKey the public key of the identity whose signature is
     *                  going to be verified
     * @param data      the data to be verified
     * @param signature the signature bytes to be verified
     * @return {@code true} if the signature was verified, {@code false}
     * if not
     * @throws NoSuchAlgorithmException           if no {@code Provider}
     *                                            supports a
     *                                            {@code KeyFactorySpi}
     *                                            implementation for the
     *                                            specified algorithm
     * @throws InvalidAlgorithmParameterException if the given parameters
     *                                            are inappropriate for
     *                                            this signature engine
     * @throws InvalidKeyException                if the key is invalid
     * @throws SignatureException                 if this signature
     *                                            object is not
     *                                            initialized properly,
     *                                            the passed-in signature
     *                                            is improperly encoded
     *                                            or of the wrong type,
     *                                            if this signature
     *                                            algorithm is unable to
     *                                            process the input data
     *                                            provided, etc.
     */
    default boolean verify(PublicKey publicKey, byte[] data, byte[] signature) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        var sig = getInstance(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    /**
     * Returns the signature bytes of the data.
     *
     * @param privateKey the private key of the identity whose signature is going
     *                   to be generated
     * @param data       the data to be signed
     * @return the signature bytes of the signing operation's result
     * @throws NoSuchAlgorithmException           if no {@code Provider}
     *                                            supports a
     *                                            {@code KeyFactorySpi}
     *                                            implementation for the
     *                                            specified algorithm
     * @throws InvalidAlgorithmParameterException if the given parameters
     *                                            are inappropriate for
     *                                            this signature engine
     * @throws InvalidKeyException                if the key is invalid
     * @throws SignatureException                 if this signature
     *                                            object is not
     *                                            initialized properly,
     *                                            or if this signature
     *                                            algorithm is unable to
     *                                            process the input data
     *                                            provided
     */
    default byte[] sign(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        var sig = getInstance(privateKey);
        sig.update(data);
        return sig.sign();
    }

}
