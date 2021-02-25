package com.github.fmjsjx.libcommon.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class DigestUtil {

    /**
     * All algorithms of digest.
     */
    public enum DigestAlgorithm implements Supplier<MessageDigest> {
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
        SHA256("SHA-256"),
        /**
         * SHA-512
         * 
         * @since 1.1
         */
        SHA512("SHA-512"),
        // May add others...
        ;

        public static final DigestAlgorithm fromAlgorithm(String algorithm) {
            switch (algorithm) {
            case "MD5":
                return MD5;
            case "SHA-1":
                return SHA1;
            case "SHA-256":
                return SHA256;
            case "SHA-512":
                return SHA512;
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

        @Override
        public MessageDigest get() {
            try {
                return MessageDigest.getInstance(algorithm());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Creates and returns a new {@link DigestUtil} instance with this
         * {@code algorithm}.
         * 
         * @return a {@code DigestUtil}
         * @since 1.1
         */
        public DigestUtil createUtil() {
            return new DigestUtil(get());
        }

    }

    /**
     * Creates and returns a new {@link DigestUtil} instance with the specified
     * {@code algorithm}.
     * 
     * @param algorithm the {@link DigestAlgorithm}
     * @return a {@code DigestUtil}
     * @deprecated Please use {@link DigestAlgorithm#createUtil()} instead since 1.1
     *             version.
     */
    @Deprecated
    public static final DigestUtil newInstance(DigestAlgorithm algorithm) {
        return algorithm.createUtil();
    }

    private static final class Md5UtilInstanceHolder {

        private static final ThreadLocal<DigestUtil> instance = ThreadLocal
                .withInitial(DigestAlgorithm.MD5::createUtil);

    }

    /**
     * Returns the {@link DigestUtil} with the {@code MD5} digest algorithm.
     * 
     * @return the {@link DigestUtil} with the {@code MD5} digest algorithm
     */
    public static final DigestUtil md5() {
        return Md5UtilInstanceHolder.instance.get();
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified string.
     * 
     * @param input the string
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] md5(String input) {
        return md5().digest(input);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified array of
     * bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] md5(byte[] input, int offset, int len) {
        return md5().digest(input, offset, len);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified arrays of
     * bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] md5(byte[] input, byte[]... otherInputs) {
        return md5().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] md5(ByteBuffer input, ByteBuffer... otherInputs) {
        return md5().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified string.
     * 
     * @param input the string
     * @return the hex string for the resulting hash value
     */
    public static final String md5AsHex(String input) {
        return md5().digestAsHex(input);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified array of
     * bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the hex string of bytes for the resulting hash value
     */
    public static final String md5AsHex(byte[] input, int offset, int len) {
        return md5().digestAsHex(input, offset, len);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified arrays of
     * bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the hex string for the resulting hash value
     */
    public static final String md5AsHex(byte[] input, byte[]... otherInputs) {
        return md5().digestAsHex(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code MD5} digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the hex string for the resulting hash value
     */
    public static final String md5AsHex(ByteBuffer input, ByteBuffer... otherInputs) {
        return md5().digestAsHex(input, otherInputs);
    }

    private static final class Sha1UtilInstanceHolder {

        private static final ThreadLocal<DigestUtil> instance = ThreadLocal
                .withInitial(DigestAlgorithm.SHA1::createUtil);

    }

    /**
     * Returns the {@link DigestUtil} with the {@code SHA-1} digest algorithm.
     * 
     * @return the {@link DigestUtil} with the {@code SHA-1} digest algorithm
     */
    public static final DigestUtil sha1() {
        return Sha1UtilInstanceHolder.instance.get();
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified string.
     * 
     * @param input the string
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha1(String input) {
        return sha1().digest(input);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified array of
     * bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha1(byte[] input, int offset, int len) {
        return sha1().digest(input, offset, len);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified arrays of
     * bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha1(byte[] input, byte[]... otherInputs) {
        return sha1().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha1(ByteBuffer input, ByteBuffer... otherInputs) {
        return sha1().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified string.
     * 
     * @param input the string
     * @return the hex string for the resulting hash value
     */
    public static final String sha1AsHex(String input) {
        return sha1().digestAsHex(input);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified array of
     * bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the hex string of bytes for the resulting hash value
     */
    public static final String sha1AsHex(byte[] input, int offset, int len) {
        return sha1().digestAsHex(input, offset, len);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified arrays of
     * bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the hex string for the resulting hash value
     */
    public static final String sha1AsHex(byte[] input, byte[]... otherInputs) {
        return sha1().digestAsHex(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-1} digest using the specified buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the hex string for the resulting hash value
     */
    public static final String sha1AsHex(ByteBuffer input, ByteBuffer... otherInputs) {
        return sha1().digestAsHex(input, otherInputs);
    }

    private static final class Sha256UtilInstanceHolder {

        private static final ThreadLocal<DigestUtil> instance = ThreadLocal
                .withInitial(DigestAlgorithm.SHA256::createUtil);

    }

    /**
     * Returns the {@link DigestUtil} with the {@code SHA-256} digest algorithm.
     * 
     * @return the {@link DigestUtil} with the {@code SHA-256} digest algorithm
     */
    public static final DigestUtil sha256() {
        return Sha256UtilInstanceHolder.instance.get();
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified string.
     * 
     * @param input the string
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha256(String input) {
        return sha256().digest(input);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified array
     * of bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha256(byte[] input, int offset, int len) {
        return sha256().digest(input, offset, len);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified arrays
     * of bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha256(byte[] input, byte[]... otherInputs) {
        return sha256().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified
     * buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the array of bytes for the resulting hash value
     */
    public static final byte[] sha256(ByteBuffer input, ByteBuffer... otherInputs) {
        return sha256().digest(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified string.
     * 
     * @param input the string
     * @return the hex string for the resulting hash value
     */
    public static final String sha256AsHex(String input) {
        return sha256().digestAsHex(input);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified array
     * of bytes, starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the hex string of bytes for the resulting hash value
     */
    public static final String sha256AsHex(byte[] input, int offset, int len) {
        return sha256().digestAsHex(input, offset, len);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified arrays
     * of bytes.
     * 
     * @param input       the first array of bytes
     * @param otherInputs the other arrays of bytes
     * @return the hex string for the resulting hash value
     */
    public static final String sha256AsHex(byte[] input, byte[]... otherInputs) {
        return sha256().digestAsHex(input, otherInputs);
    }

    /**
     * Calculates and returns the {@code SHA-256} digest using the specified
     * buffers.
     * 
     * @param input       the first {@link ByteBuffer}
     * @param otherInputs the other {@link ByteBuffer}s
     * @return the hex string for the resulting hash value
     */
    public static final String sha256AsHex(ByteBuffer input, ByteBuffer... otherInputs) {
        return sha256().digestAsHex(input, otherInputs);
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
     * Calculates and returns the digest using the specified array of bytes,
     * starting at the specified offset.
     * 
     * 
     * @param input  the array of bytes
     * @param offset the offset to start from in the array of bytes
     * @param len    the number of bytes to use, starting at
     * @return the hex string of bytes for the resulting hash value
     */
    public String digestAsHex(byte[] input, int offset, int len) {
        return StringUtil.toHexString(digest(input, offset, len));
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
