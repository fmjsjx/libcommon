package com.github.fmjsjx.libcommon.util;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    public enum CheckType implements Supplier<Checksum> {
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

        /**
         * @since 1.1
         */
        @Override
        public Checksum get() {
            return factory.get();
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
     * @deprecated Please use {@link #wrappedChecksum(CheckType)} instead since 1.1
     *             version.
     */
    @Deprecated
    public static final ChecksumUtil newInstance(CheckType checkType) {
        return new ChecksumUtil(wrappedChecksum(checkType));
    }

    /**
     * Creates and returns a new {@link WrappedChecksum} instance with the specified
     * {@code checkType}.
     * 
     * @param <T>       the type of the wrapped checksum object
     * @param checkType the {@link CheckType}
     * @return a new {@link WrappedChecksum} instance with the specified
     *         {@code checkType}
     * @since 1.1
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Checksum> WrappedChecksum<T> wrappedChecksum(CheckType checkType) {
        return new WrappedChecksum<T>(checkType, (T) checkType.get());
    }

    private static final WrappedChecksum<CRC32> wrappedCRC32() {
        return wrappedChecksum(CheckType.CRC_32);
    }

    private static final WrappedChecksum<CRC32C> wrappedCRC32C() {
        return wrappedChecksum(CheckType.CRC_32C);
    }

    private static final class CRC32UtilInstanceHolder {
        private static final ThreadLocal<WrappedChecksum<CRC32>> instance = ThreadLocal
                .withInitial(ChecksumUtil::wrappedCRC32);
    }

    /**
     * Returns the {@link ChecksumUtil} with the {@code CRC-32} checksum type.
     * 
     * @return the {@link ChecksumUtil} with the {@code CRC-32} checksum type
     * 
     * @deprecated Please use {@link #wrappedCrc32()} instead since 1.1 version.
     */
    @Deprecated
    public static final ChecksumUtil crc32() {
        return new ChecksumUtil(wrappedCrc32());
    }

    /**
     * Returns the thread local {@link WrappedChecksum} with the {@code CRC-32}
     * checksum type.
     * 
     * @return the thread local {@link WrappedChecksum} with the {@code CRC-32}
     *         checksum type
     * 
     * @since 1.1
     */
    public static final WrappedChecksum<CRC32> wrappedCrc32() {
        return CRC32UtilInstanceHolder.instance.get();
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
        return wrappedCrc32().calculateValue(b, off, len);
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
        return wrappedCrc32().calculateValue(b, others);
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
        return wrappedCrc32().calculateValue(buffer, otherBuffers);
    }

    private static final class CRC32CUtilInstanceHolder {
        private static final ThreadLocal<WrappedChecksum<CRC32C>> instance = ThreadLocal
                .withInitial(ChecksumUtil::wrappedCRC32C);
    }

    /**
     * Returns the {@link ChecksumUtil} with the {@code CRC-32C} checksum type.
     * 
     * @return the {@link ChecksumUtil} with the {@code CRC-32C} checksum type
     * 
     * @deprecated Please use {@link #wrappedCrc32c()} instead since 1.1 version.
     */
    @Deprecated
    public static final ChecksumUtil crc32c() {
        return new ChecksumUtil(wrappedCrc32c());
    }

    /**
     * Returns the thread local {@link WrappedChecksum} with the {@code CRC-32C}
     * checksum type.
     * 
     * @return the thread local {@link WrappedChecksum} with the {@code CRC-32C}
     *         checksum type
     * 
     * @since 1.1
     */
    public static final WrappedChecksum<CRC32C> wrappedCrc32c() {
        return CRC32CUtilInstanceHolder.instance.get();
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
        return wrappedCrc32c().calculateValue(b, off, len);
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
        return wrappedCrc32c().calculateValue(b, others);
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
        return wrappedCrc32c().calculateValue(buffer, otherBuffers);
    }

    /**
     * Wrapped Checksum.
     * 
     * @param <T> the type of the wrapped {@code checksum} object
     * 
     * @since 1.1
     * 
     * @see Checksum
     * @see CRC32
     * @see CRC32C
     */
    public static final class WrappedChecksum<T extends Checksum> implements Checksum {

        private final CheckType type;
        private final T wrapped;

        WrappedChecksum(CheckType type, T wrapped) {
            this.type = Objects.requireNonNull(type, "type must not be null");
            this.wrapped = Objects.requireNonNull(wrapped, "wrapped must not be null");
        }

        /**
         * Returns the wrapped {@code checksum} object.
         * 
         * @return the wrapped {@code checksum} object
         */
        public T wrapped() {
            return wrapped;
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
            var o = wrapped();
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
            var o = wrapped();
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
            var o = wrapped();
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
            return "WrappedChecksum(type=" + type + ", wrapped=" + wrapped + ")";
        }

        @Override
        public void update(int b) {
            wrapped.update(b);
        }

        @Override
        public void update(byte[] b, int off, int len) {
            wrapped.update(b, off, len);
        }

        @Override
        public long getValue() {
            return wrapped.getValue();
        }

        @Override
        public void reset() {
            wrapped.reset();
        }

    }

    private final WrappedChecksum<? extends Checksum> wc;

    @Deprecated
    private ChecksumUtil(WrappedChecksum<? extends Checksum> wc) {
        this.wc = wc;
    }

    /**
     * Calculates and returns the checksum value with the specified array of bytes.
     * 
     * @param b   the byte array to calculate the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the calculate
     * @return the checksum value
     */
    @Deprecated
    public long calculateValue(byte[] b, int off, int len) {
        return wc.calculateValue(b, off, len);
    }

    /**
     * Calculates and returns the checksum value with the specified arrays of bytes.
     * 
     * @param b      the first byte array to update the checksum with
     * @param others the other byte arrays to update the checksum with
     * @return the checksum value
     */
    @Deprecated
    public long calculateValue(byte[] b, byte[]... others) {
        return wc.calculateValue(b, others);
    }

    /**
     * Calculates and returns the checksum with the bytes from the specified
     * buffers.
     * 
     * @param buffer       the first {@link ByteBuffer} to update the checksum with
     * @param otherBuffers the other {@link ByteBuffer}s to update the checksum with
     * @return the checksum value
     */
    @Deprecated
    public long calculateValue(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        return wc.calculateValue(buffer, otherBuffers);
    }

    @Override
    public String toString() {
        return "ChecksumUtil(type=" + wc.type + ")";
    }

}
