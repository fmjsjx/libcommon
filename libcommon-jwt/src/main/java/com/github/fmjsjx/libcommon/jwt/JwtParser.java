package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.CryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;
import com.github.fmjsjx.libcommon.jwt.util.KeyLocator;

import java.security.Key;
import java.util.Objects;

/**
 * A parser parses {@link Jwt}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JwtParser {

    /**
     * Creates a new {@link Builder} instance.
     *
     * @return a new {@link Builder} instance
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Parse and returns the {@link Jwt} from the specified JWT string
     * given.
     *
     * @param jwt the JWT string
     * @return a {@link Jwt} instance
     * @throws JwtException if any error occurs during the parsing
     *                      operation and the JWT <b>SHOULD</b> be
     *                      rejected
     */
    Jwt parse(String jwt) throws JwtException;

    /**
     * The builder builds {@link JwtParser}s.
     *
     * @author MJ Fang
     * @since 3.10
     */
    final class Builder {

        private JsonRepresentedFactory<?> jsonRepresentedFactory;
        private boolean allowExpired;
        private long allowedClockSkewSeconds;

        private KeyLocator keyLocator;

        private boolean simpleMode;
        private CryptoAlgorithm singleAlgorithm;
        private Key singleKey;

        public Builder keyLocator(KeyLocator keyLocator) {
            this.keyLocator = keyLocator;
            simpleMode = false;
            singleAlgorithm = null;
            singleKey = null;
            return this;
        }

        /**
         * Change to simple mode and sets the specified algorithm and key.
         *
         * @param algorithm the {@link CryptoAlgorithm} to be set
         * @param key       the {@link Key} to be set
         * @return this builder
         */
        public Builder simple(CryptoAlgorithm algorithm, Key key) {
            simpleMode = true;
            singleAlgorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
            singleKey = Objects.requireNonNull(key, "key must not be null");
            return this;
        }

        /**
         * Sets the {@link JsonRepresentedFactory}.
         *
         * @param jsonRepresentedFactory the {@link JsonRepresentedFactory} to be set
         * @return this builder
         */
        public Builder jsonRepresentedFactory(JsonRepresentedFactory<?> jsonRepresentedFactory) {
            this.jsonRepresentedFactory = jsonRepresentedFactory;
            return this;
        }

        private JsonRepresentedFactory<?> fixedJsonRepresentedFactory() {
            return Objects.requireNonNullElseGet(jsonRepresentedFactory, Fastjson2JsonRepresented::getFactory);
        }

        /**
         * Allow expired JWT.
         *
         * @return this builder
         */
        public Builder allowExpired() {
            return allowExpired(true);
        }

        /**
         * Disallow expired JWT.
         *
         * @return this builder
         */
        public Builder disallowExpired() {
            return allowExpired(false);
        }

        private Builder allowExpired(boolean allowExpired) {
            this.allowExpired = allowExpired;
            return this;
        }

        /**
         * Sets the allowed clock skew in seconds.
         *
         * @param allowedClockSkewSeconds the allowed clock skew in seconds
         * @return this builder
         */
        public Builder allowedClockSkewSeconds(long allowedClockSkewSeconds) {
            this.allowedClockSkewSeconds = allowedClockSkewSeconds;
            return this;
        }

        /**
         * Creates a new {@link JwtParser} by this builder.
         *
         * @return a new {@link JwtParser} instance
         */
        public JwtParser build() {
            if (simpleMode) {
                // now only support JWS
                var algorithm = (JwsCryptoAlgorithm) singleAlgorithm;
                if (algorithm.isMac()) {
                    return new SimpleMacJwtParser(algorithm, singleKey, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
                } else {
                    return new SimpleSignatureJwtParser(algorithm, singleKey, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
                }
            }
            var keyLocator = this.keyLocator;
            if (keyLocator == null) {
                throw new IllegalArgumentException("Missing required key locator");
            }
            return new DefaultJwtParser(keyLocator, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
        }

        private Builder() {
        }
    }

}
