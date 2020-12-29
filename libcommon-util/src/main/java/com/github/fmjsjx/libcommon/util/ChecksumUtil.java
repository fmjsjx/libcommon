package com.github.fmjsjx.libcommon.util;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/**
 * Utility class for {@link Checksum}s.
 */
public class ChecksumUtil {

    /**
     * All types of {@link Checksum}s.
     */
    public enum CheckType {
        /**
         * {@code "CRC-32"}
         */
        CRC_32("CRC-32", CRC32::new),
        /**
         * {@code "CRC-32C"}
         */
        CRC_32C("CRC-32C", CRC32C::new);

        /**
         * Converts and returns a {@link CheckType} from the specified name.
         * <p>
         * The name must be one of {@code "CRC-32"} or {@code "CRC-32C"}.
         * 
         * @param name the name
         * @return a {@code CheckType}
         */
        public static final CheckType forName(String name) {
            switch (name) {
            case "CRC-32":
                return CRC_32;
            case "CRC-32C":
                return CRC_32C;
            default:
                throw new NoSuchElementException("No such CheckType for name " + name);
            }
        }

        private final String typeName;
        private final Supplier<Checksum> factory;

        private CheckType(String typeName, Supplier<Checksum> factory) {
            this.typeName = typeName;
            this.factory = factory;
        }

        /**
         * Returns the type name.
         * 
         * @return the type name
         */
        public String typeName() {
            return typeName;
        }

        /**
         * Returns the factory creates {@link Checksum}s.
         * 
         * @return the factory creates {@link Checksum}s
         */
        public Supplier<Checksum> factory() {
            return factory;
        }

        @Override
        public String toString() {
            return typeName;
        }

    }

    /**
     * Creates and returns a new {@link ChecksumUtil} instance with the specified
     * {@code checkType}.
     * 
     * @param checkType the {@link CheckType}
     * @return a new {@link ChecksumUtil} instance with the specified
     *         {@code checkType}
     */
    public static final ChecksumUtil newInstance(CheckType checkType) {
        return new ChecksumUtil(checkType, checkType.factory.get());
    }

    private static final class ThreadLocalUtil extends ThreadLocal<ChecksumUtil> {
        private final CheckType checkType;

        private ThreadLocalUtil(CheckType checkType) {
            this.checkType = checkType;
        }

        @Override
        protected ChecksumUtil initialValue() {
            return newInstance(checkType);
        }
    }

    private static final class CRC32UtilInstanceHolder {
        private static final ThreadLocalUtil instance = new ThreadLocalUtil(CheckType.CRC_32);
    }

    /**
     * Calculates and returns the {@code CRC-32} checksum value with the specified
     * array of bytes.
     * 
     * @param b   the byte array to calculate the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the calculate
     * @return the {@code CRC-32} checksum value
     */
    public static final long crc32(byte[] b, int off, int len) {
        return CRC32UtilInstanceHolder.instance.get().calculateValue(b, off, len);
    }

    /**
     * Calculates and returns the {@code CRC-32} checksum value with the specified
     * arrays of bytes.
     * 
     * @param b      the first byte array to update the checksum with
     * @param others the other byte arrays to update the checksum with
     * @return the {@code CRC-32} checksum value
     */
    public static final long crc32(byte[] b, byte[]... others) {
        return CRC32UtilInstanceHolder.instance.get().calculateValue(b, others);
    }

    /**
     * Calculates and returns the {@code CRC-32} checksum with the bytes from the
     * specified buffers.
     * 
     * @param buffer       the first {@link ByteBuffer} to update the checksum with
     * @param otherBuffers the other {@link ByteBuffer}s to update the checksum with
     * @return the the {@code CRC-32} checksum value
     */
    public static final long crc32(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        return CRC32UtilInstanceHolder.instance.get().calculateValue(buffer, otherBuffers);
    }

    private static final class CRC32CUtilInstanceHolder {
        private static final ThreadLocalUtil instance = new ThreadLocalUtil(CheckType.CRC_32C);
    }

    /**
     * Calculates and returns the {@code CRC-32C} checksum value with the specified
     * array of bytes.
     * 
     * @param b   the byte array to calculate the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the calculate
     * @return the {@code CRC-32C} checksum value
     */
    public static final long crc32c(byte[] b, int off, int len) {
        return CRC32CUtilInstanceHolder.instance.get().calculateValue(b, off, len);
    }

    /**
     * Calculates and returns the {@code CRC-32C} checksum value with the specified
     * arrays of bytes.
     * 
     * @param b      the first byte array to update the checksum with
     * @param others the other byte arrays to update the checksum with
     * @return the {@code CRC-32C} checksum value
     */
    public static final long crc32c(byte[] b, byte[]... others) {
        return CRC32CUtilInstanceHolder.instance.get().calculateValue(b, others);
    }

    /**
     * Calculates and returns the {@code CRC-32C} checksum with the bytes from the
     * specified buffers.
     * 
     * @param buffer       the first {@link ByteBuffer} to update the checksum with
     * @param otherBuffers the other {@link ByteBuffer}s to update the checksum with
     * @return the the {@code CRC-32C} checksum value
     */
    public static final long crc32c(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        return CRC32CUtilInstanceHolder.instance.get().calculateValue(buffer, otherBuffers);
    }

    private final CheckType type;
    private final Checksum delegate;

    private ChecksumUtil(CheckType type, Checksum delegate) {
        this.type = type;
        this.delegate = delegate;
    }

    /**
     * Calculates and returns the checksum value with the specified array of bytes.
     * 
     * @param b   the byte array to calculate the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the calculate
     * @return the checksum value
     */
    public long calculateValue(byte[] b, int off, int len) {
        var o = delegate;
        try {
            o.update(b, off, len);
            return o.getValue();
        } finally {
            o.reset();
        }
    }

    /**
     * Calculates and returns the checksum value with the specified arrays of bytes.
     * 
     * @param b      the first byte array to update the checksum with
     * @param others the other byte arrays to update the checksum with
     * @return the checksum value
     */
    public long calculateValue(byte[] b, byte[]... others) {
        var o = delegate;
        try {
            o.update(b);
            for (var other : others) {
                o.update(other);
            }
            return o.getValue();
        } finally {
            o.reset();
        }
    }

    /**
     * Calculates and returns the checksum with the bytes from the specified
     * buffers.
     * 
     * @param buffer       the first {@link ByteBuffer} to update the checksum with
     * @param otherBuffers the other {@link ByteBuffer}s to update the checksum with
     * @return the checksum value
     */
    public long calculateValue(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        var o = delegate;
        try {
            o.update(buffer);
            for (var other : otherBuffers) {
                o.update(other);
            }
            return o.getValue();
        } finally {
            o.reset();
        }
    }

    @Override
    public String toString() {
        return "ChecksumUtil(type=" + type + ")";
    }

}
