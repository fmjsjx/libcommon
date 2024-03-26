package com.github.fmjsjx.libcommon.uuid;

import java.util.UUID;

/**
 * The interface generates {@link UUID}s.
 *
 * @author MJ Fang
 * @since 3.8
 */
public interface UuidGenerator {

    /**
     * Generates and returns a new {@link UUID} instance.
     *
     * @return the {@code UUID} instance
     */
    UUID generate();

    /**
     * Returns the version number of {@link UUID}s that this generator instance will produce.
     *
     * @return the version number of {@code UUID}s that this generator instance will produce
     */
    int version();

}
