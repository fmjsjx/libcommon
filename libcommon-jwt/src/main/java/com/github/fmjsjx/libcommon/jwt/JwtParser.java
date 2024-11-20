package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.CryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidKeyException;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Objects;

/**
 * A parser parses {@link Jwt}s.
 *
 * @author MJ Fang
 * @since 3.10
 */
public interface JwtParser {

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
     */
    final class Builder {

        private CryptoAlgorithm singleAlgorithm;
        private Key singleKey;
        private JsonRepresentedFactory<?> jsonRepresentedFactory;
        private boolean allowExpired;
        private long allowedClockSkewSeconds;

        public Builder algorithm(CryptoAlgorithm singleAlgorithm) {
            this.singleAlgorithm = singleAlgorithm;
            return this;
        }

        public Builder key(Key singleKey) {
            this.singleKey = singleKey;
            return this;
        }

        public Builder fixedJsonRepresentedFactory(JsonRepresentedFactory<?> jsonRepresentedFactory) {
            this.jsonRepresentedFactory = jsonRepresentedFactory;
            return this;
        }

        private JsonRepresentedFactory<?> fixedJsonRepresentedFactory() {
            return Objects.requireNonNullElseGet(jsonRepresentedFactory, Fastjson2JsonRepresented::getFactory);
        }

        public Builder allowExpired() {
            return allowExpired(true);
        }

        public Builder disallowExpired() {
            return allowExpired(false);
        }

        private Builder allowExpired(boolean allowExpired) {
            this.allowExpired = allowExpired;
            return this;
        }

        public Builder allowedClockSkewSeconds(long allowedClockSkewSeconds) {
            this.allowedClockSkewSeconds = allowedClockSkewSeconds;
            return this;
        }

        public JwtParser build() {

            var singleKey = this.singleKey;
            var singleAlgorithm = this.singleAlgorithm;
            if (singleKey != null) {
                if (singleAlgorithm != null) {
                    // now only support JWS
                    var algorithm = (JwsCryptoAlgorithm) singleAlgorithm;
                    if (algorithm.isMac()) {
                        if (singleKey instanceof SecretKey secretKey) {
                            return new SimpleMacJwtParser(algorithm, secretKey, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
                        } else {
                            throw new InvalidKeyException("The key expected be a secret key but was " + singleKey.getClass());
                        }
                    } else {
                        return new SimpleSignatureJwtParser(algorithm, singleKey, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
                    }
                } else {
                    if (singleKey instanceof SecretKey secretKey) {
                        var algorithm = (JwsCryptoAlgorithm) CryptoAlgorithm.getInstanceByJcaName(secretKey.getAlgorithm());
                        return new SimpleMacJwtParser(algorithm, secretKey, fixedJsonRepresentedFactory(), allowExpired, allowedClockSkewSeconds);
                    } else {
                        throw new IllegalArgumentException("Missing JWT cryptographic algorithm");
                    }
                }
            }
            throw new IllegalArgumentException("Missing JWKS key");
        }

        private Builder() {
        }
    }

}
