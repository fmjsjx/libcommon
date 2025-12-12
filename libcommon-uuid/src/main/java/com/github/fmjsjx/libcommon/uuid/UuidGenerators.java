package com.github.fmjsjx.libcommon.uuid;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import java.util.UUID;

/**
 * The factory class for constructing {@link UuidGenerator}s.
 *
 * @author MJ Fang
 * @since 3.8
 */
public final class UuidGenerators {

    private static class JdkGeneratorInstanceHolder {
        private static final UuidGenerator INSTANCE = new JdkProvidedUuidGenerator();
    }

    private static class JdkProvidedUuidGenerator implements UuidGenerator {

        private final UUID example = generate();

        @Override
        public UUID generate() {
            return UUID.randomUUID();
        }

        @Override
        public int version() {
            return example.version();
        }

    }

    /**
     * Returns the jdk provided {@link UuidGenerator UUID generator}.
     *
     * @return the jdk provided  {@code UuidGenerator} instance
     */
    public static UuidGenerator jdkGenerator() {
        return JdkGeneratorInstanceHolder.INSTANCE;
    }

    private record FasterxmlWrappedGenerator(NoArgGenerator generator) implements UuidGenerator {

        @Override
        public UUID generate() {
            return generator.generate();
        }

        @Override
        public int version() {
            return generator.getType().raw();
        }

    }

    /**
     * Factory method for constructing version 1 {@link UuidGenerator UUID generator}.
     *
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV1Generator() {
        return fasterxmlWrappedGenerator(Generators.defaultTimeBasedGenerator());
    }

    /**
     * Factory method for constructing version 1 {@link UuidGenerator UUID generator}.
     *
     * @param address the 6 bytes (48-bit) address
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV1Generator(byte[] address) {
        return fasterxmlWrappedGenerator(Generators.timeBasedGenerator(EthernetAddress.valueOf(address)));
    }

    /**
     * Factory method for constructing version 1 {@link UuidGenerator UUID generator}.
     *
     * @param address a 'standard' ethernet MAC address string (like '00:C0:F0:3D:5B:7C')
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV1Generator(String address) {
        return fasterxmlWrappedGenerator(Generators.timeBasedGenerator(EthernetAddress.valueOf(address)));
    }

    private static class UuidV1GeneratorInstanceHolder {
        private static final UuidGenerator INSTANCE = uuidV1Generator();
    }

    /**
     * Returns the default version 1 {@link UuidGenerator UUID generator}
     *
     * @return the default version 1 {@code UuidGenerator} instance
     */
    public static UuidGenerator defaultUuidV1Generator() {
        return UuidV1GeneratorInstanceHolder.INSTANCE;
    }

    /**
     * Factory method for constructing version 4 {@link UuidGenerator UUID generator}.
     *
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV4Generator() {
        return fasterxmlWrappedGenerator(Generators.randomBasedGenerator());
    }

    private static class UuidV4GeneratorInstanceHolder {
        private static final UuidGenerator INSTANCE = uuidV4Generator();
    }

    /**
     * Returns the default version 4 {@link UuidGenerator UUID generator}
     *
     * @return the default version 4 {@code UuidGenerator} instance
     */
    public static UuidGenerator defaultUuidV4Generator() {
        return UuidV4GeneratorInstanceHolder.INSTANCE;
    }

    /**
     * Factory method for constructing version 6 {@link UuidGenerator UUID generator}.
     *
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV6Generator() {
        return fasterxmlWrappedGenerator(Generators.timeBasedReorderedGenerator(EthernetAddress.fromPreferredInterface()));
    }

    /**
     * Factory method for constructing version 6 {@link UuidGenerator UUID generator}.
     *
     * @param address the 6 bytes (48-bit) address
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV6Generator(byte[] address) {
        return fasterxmlWrappedGenerator(Generators.timeBasedReorderedGenerator(EthernetAddress.valueOf(address)));
    }

    /**
     * Factory method for constructing version 6 {@link UuidGenerator UUID generator}.
     *
     * @param address a 'standard' ethernet MAC address string (like '00:C0:F0:3D:5B:7C')
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV6Generator(String address) {
        return fasterxmlWrappedGenerator(Generators.timeBasedReorderedGenerator(EthernetAddress.valueOf(address)));
    }

    private static class UuidV6GeneratorInstanceHolder {
        private static final UuidGenerator INSTANCE = uuidV6Generator();
    }

    /**
     * Returns the default version 6 {@link UuidGenerator UUID generator}
     *
     * @return the default version 6 {@code UuidGenerator} instance
     */
    public static UuidGenerator defaultUuidV6Generator() {
        return UuidV6GeneratorInstanceHolder.INSTANCE;
    }

    /**
     * Factory method for constructing version 7 {@link UuidGenerator UUID generator}.
     *
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator uuidV7Generator() {
        return fasterxmlWrappedGenerator(Generators.timeBasedEpochGenerator());
    }

    private static class UuidV7GeneratorInstanceHolder {
        private static final UuidGenerator INSTANCE = uuidV7Generator();
    }

    /**
     * Returns the default version 7 {@link UuidGenerator UUID generator}
     *
     * @return the default version 7 {@code UuidGenerator} instance
     */
    public static UuidGenerator defaultUuidV7Generator() {
        return UuidV7GeneratorInstanceHolder.INSTANCE;
    }

    /**
     * Factory method for constructing fasterxml wrapped {@link UuidGenerator}.
     *
     * @param generator the {@link NoArgGenerator fastxml UUID Generator}
     * @return the {@code UuidGenerator} instance
     */
    public static UuidGenerator fasterxmlWrappedGenerator(NoArgGenerator generator) {
        return new FasterxmlWrappedGenerator(generator);
    }

    private UuidGenerators() {
    }

}
