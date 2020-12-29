package com.github.fmjsjx.libcommon.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DigestUtil {

    /**
     * All algorithms of digest.
     */
    public enum DigestAlgorithm {
        /**
         * MD5
         */
        MD5("MD5"),
        /**
         * SHA-1
         */
        SHA1("SHA-1"),
        /**
         * SHA-256
         */
        SHA256("SHA-256");

        public static final DigestAlgorithm fromAlgorithm(String algorithm) {
            switch (algorithm) {
            case "MD5":
                return MD5;
            case "SHA-1":
                return SHA1;
            case "SHA-256":
                return SHA256;
            default:
                throw new NoSuchElementException("no such algorithm `" + algorithm + "`");
            }
        }

        private final String algorithm;

        private DigestAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        /**
         * Returns the algorithm
         * 
         * @return the algorithm
         */
        public String algorithm() {
            return algorithm;
        }

        @Override
        public String toString() {
            return algorithm;
        }

    }

    /**
     * Creates and returns a new {@link DigestUtil} instance with the specified
     * {@code algorithm}.
     * 
     * @param algorithm the {@link DigestAlgorithm}
     * @return a {@code DigestUtil}
     */
    public static final DigestUtil newInstance(DigestAlgorithm algorithm) {
        try {
            return new DigestUtil(MessageDigest.getInstance(algorithm.algorithm()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class ThreadLocalUtil extends ThreadLocal<DigestUtil> {

        private final DigestAlgorithm algorithm;

        private ThreadLocalUtil(DigestAlgorithm algorithm) {
            this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        }

        @Override
        protected DigestUtil initialValue() {
            return newInstance(algorithm);
        }

    }

    private final MessageDigest digest;

    private DigestUtil(MessageDigest digest) {
        this.digest = digest;
    }

    /**
     * Calculates and returns the digest using the specified array of bytes,
     * starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the array of bytes for the resulting hash value
     */
    public byte[] digest(byte[] input, int offset, int len) {
        var digest = this.digest;
        try {
            digest.update(input, offset, len);
            return digest.digest();
        } finally {
            digest.reset();
        }
    }

    /**
     * Calculates and returns the digest using the specified arrays of bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the array of bytes for the resulting hash value
     */
    public byte[] digest(byte[] input, byte[]... otherInputs) {
        var digest = this.digest;
        try {
            digest.update(input);
            for (var otherInput : otherInputs) {
                digest.update(otherInput);
            }
            return digest.digest();
        } finally {
            digest.reset();
        }
    }

    /**
     * Calculates and returns the digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the array of bytes for the resulting hash value
     */
    public byte[] digest(ByteBuffer input, ByteBuffer... otherInputs) {
        var digest = this.digest;
        try {
            digest.update(input);
            for (var otherInput : otherInputs) {
                digest.update(otherInput);
            }
            return digest.digest();
        } finally {
            digest.reset();
        }
    }

    /**
     * Calculates and returns the digest using the specified string.
     * 
     * @param input the string
     * @return the array of bytes for the resulting hash value
     */
    public byte[] digest(String input) {
        var b = input.getBytes();
        return digest(b, 0, b.length);
    }

    /**
     * Calculates and returns the digest using the specified arrays of bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the hex string for the resulting hash value
     */
    public String digestAsHex(byte[] input, byte[]... otherInputs) {
        return StringUtil.toHexString(digest(input, otherInputs));
    }

    /**
     * Calculates and returns the digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the hex string for the resulting hash value
     */
    public String digestAsHex(ByteBuffer input, ByteBuffer... otherInputs) {
        return StringUtil.toHexString(digest(input, otherInputs));
    }

    /**
     * Calculates and returns the digest using the specified string.
     * 
     * @param input the string
     * @return the hex string for the resulting hash value
     */
    public String digestAsHex(String input) {
        return StringUtil.toHexString(digest(input));
    }

}
