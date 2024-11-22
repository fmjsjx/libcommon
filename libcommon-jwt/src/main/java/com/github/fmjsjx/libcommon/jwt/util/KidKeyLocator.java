package com.github.fmjsjx.libcommon.jwt.util;

import com.github.fmjsjx.libcommon.jwt.JoseHeader;
import com.github.fmjsjx.libcommon.jwt.exception.MissingRequiredKidException;
import com.github.fmjsjx.libcommon.jwt.exception.NoSuchKeyException;

import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The abstract implementation of {@link KeyLocator} that always lookup
 * the associated {@link Key} instance by {@code kid}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public abstract class KidKeyLocator implements KeyLocator {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Lookup and returns the associated {@link Key} instance by the
     * specified {@code keyId} given.
     *
     * @param keyId the ID of the key
     * @return an {@code Optional<Key>}
     */
    protected abstract Optional<Key> lookup(String keyId);

    /**
     * Obtain a {@link MissingRequiredKidException} instance through
     * the specified {@code header} given.
     *
     * @param header the {@link JoseHeader}
     * @return a {@code MissingRequiredKidException} instance
     */
    protected MissingRequiredKidException missingRequiredKidException(JoseHeader header) {
        return new MissingRequiredKidException();
    }

    /**
     * @throws MissingRequiredKidException if missing the necessary
     *                                     "kid" parameter value in
     *                                     the JOSE header
     */
    @Override
    public Key locate(JoseHeader header) throws MissingRequiredKidException {
        var keyId = header.getKeyId();
        if (keyId == null) {
            throw missingRequiredKidException(header);
        }
        return lookup(keyId).orElseThrow(() -> new NoSuchKeyException("Cannot find a key with id `" + keyId + "`"));
    }

    /**
     * The builder creates {@link KidKeyLocator}s.
     *
     * @author MJ Fang
     * @since 3.10
     */
    public static final class Builder {

        /**
         * Creates and returns a new {@link SimpleMapBuilder} instance.
         *
         * @return a new {@code SimpleMapBuilder} instance
         */
        public SimpleMapBuilder simple() {
            return new SimpleMapBuilder();
        }

        /**
         * Creates and returns a new {@link SimpleMapBuilder} instance
         * with the mapping of the specified {@code keyId} and the
         * specified {@code key}.
         *
         * @param keyId the ID of the key
         * @param key   the key
         * @return a new {@code SimpleMapBuilder} instance
         */
        public SimpleMapBuilder simple(String keyId, Key key) {
            return simple().key(keyId, key);
        }

        /**
         * Creates and returns a new {@link SimpleMapBuilder} instance
         * with all the mappings of the specified {@code keys}.
         *
         * @param keys the key map
         * @return a new {@code SimpleMapBuilder} instance
         */
        public SimpleMapBuilder simple(Map<String, Key> keys) {
            return simple().keys(keys);
        }

        /**
         * Creates and returns a new {@link CustomizedBuilder} instance.
         *
         * @return a new {@code CustomizedBuilder} instance
         */
        public CustomizedBuilder customized() {
            return new CustomizedBuilder();
        }

        /**
         * Creates and returns a new {@link CustomizedBuilder} instance
         * with the specified lookup function.
         *
         * @param fnLookup the lookup function
         * @return a new {@code CustomizedBuilder} instance
         */
        public CustomizedBuilder customized(Function<String, Key> fnLookup) {
            return customized().fnLookup(fnLookup);
        }

    }

    /**
     * The builder creates {@link SimpleMapKidKeyLocator}s.
     *
     * @author MJ Fang
     * @since 3.10
     */
    public static final class SimpleMapBuilder {

        private Map<String, Key> keys;

        /**
         * Appends all the mappings of the specified {@code keys}.
         *
         * @param keys the key map
         * @return this builder
         */
        public SimpleMapBuilder keys(Map<String, Key> keys) {
            if (this.keys == null) {
                this.keys = new LinkedHashMap<>(keys);
            } else {
                this.keys.putAll(keys);
            }
            return this;
        }

        /**
         * Appends the mapping of the specified  {@code keyId} and the
         * specified {@code key}.
         *
         * @param keyId the ID of the key
         * @param key   the key
         * @return this builder
         */
        public SimpleMapBuilder key(String keyId, Key key) {
            var keys = this.keys;
            if (keys == null) {
                this.keys = keys = new LinkedHashMap<>();
            }
            keys.put(keyId, key);
            return this;
        }

        /**
         * Creates a new {@link KidKeyLocator} instance.
         *
         * @return a new {@link KidKeyLocator} instance
         */
        public KidKeyLocator build() {
            var keys = this.keys;
            if (keys == null || keys.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one key mapping to creates the locator");
            }
            return new SimpleMapKidKeyLocator(keys);
        }

        private SimpleMapBuilder() {
        }

    }

    private static final class SimpleMapKidKeyLocator extends KidKeyLocator {

        private final Map<String, Key> keys;

        private SimpleMapKidKeyLocator(Map<String, Key> keys) {
            this.keys = keys;
        }

        @Override
        protected Optional<Key> lookup(String keyId) {
            return Optional.ofNullable(keys.get(keyId));
        }

        @Override
        public String toString() {
            return "SimpleMapKidKeyLocator(keys=" + keys + ")";
        }

    }

    /**
     * The builder creates {@link CustomizedKidKeyLocator}s.
     *
     * @author MJ Fang
     * @since 3.10
     */
    public static final class CustomizedBuilder {

        private Function<String, Key> fnLookup;
        private Function<JoseHeader, MissingRequiredKidException> missingKidExceptionFactory;

        /**
         * Sets the lookup function.
         *
         * @param fnLookup the lookup function
         * @return this builder
         */
        public CustomizedBuilder fnLookup(Function<String, Key> fnLookup) {
            this.fnLookup = fnLookup;
            return this;
        }

        /**
         * Sets the factory that creates {@link MissingRequiredKidException}s.
         *
         * @param missingKidExceptionFactory the factory that creates {@link MissingRequiredKidException}s
         * @return this builder
         */
        public CustomizedBuilder missingKidExceptionFactory(Function<JoseHeader, MissingRequiredKidException> missingKidExceptionFactory) {
            this.missingKidExceptionFactory = missingKidExceptionFactory;
            return this;
        }

        /**
         * Creates a new {@link KidKeyLocator} instance.
         *
         * @return a new {@link KidKeyLocator} instance
         */
        public KidKeyLocator build() {
            return new CustomizedKidKeyLocator(fnLookup, missingKidExceptionFactory);
        }

        private CustomizedBuilder() {
        }

    }

    private static final class CustomizedKidKeyLocator extends KidKeyLocator {

        private final Function<String, Key> fnLookup;
        private final Function<JoseHeader, MissingRequiredKidException> missingKidExceptionFactory;

        private CustomizedKidKeyLocator(Function<String, Key> fnLookup, Function<JoseHeader, MissingRequiredKidException> missingKidExceptionFactory) {
            this.fnLookup = Objects.requireNonNull(fnLookup, "fnLookup must not be null");
            this.missingKidExceptionFactory = missingKidExceptionFactory;
        }

        @Override
        protected MissingRequiredKidException missingRequiredKidException(JoseHeader header) {
            var factory = missingKidExceptionFactory;
            return factory == null ? super.missingRequiredKidException(header) : factory.apply(header);
        }

        @Override
        protected Optional<Key> lookup(String keyId) {
            return Optional.ofNullable(fnLookup.apply(keyId));
        }

        @Override
        public String toString() {
            return "CustomizedKidKeyLocator(fnLookup=" + fnLookup + ", missingKidExceptionFactory=" + missingKidExceptionFactory + ")";
        }
    }

}
