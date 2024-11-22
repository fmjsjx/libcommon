package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.CryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidSignatureException;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;
import com.github.fmjsjx.libcommon.jwt.exception.MissingRequiredAlgException;
import com.github.fmjsjx.libcommon.jwt.exception.SecurityException;
import com.github.fmjsjx.libcommon.jwt.util.KeyLocator;

import java.security.*;

class DefaultJwtParser extends AbstractJwtParser {

    private void verify(CryptoAlgorithm algorithm, Key key, String[] jwtParts) {
        var alg = (JwsCryptoAlgorithm) algorithm;
        if (alg.isMac()) {
            try {
                var mac = alg.getMacProvider().getInstance(key);
                var signData = mac.doFinal(signBaseBytes(jwtParts));
                var signString = encodeBase64(signData);
                if (!signString.equals(jwtParts[2])) {
                    throw new InvalidSignatureException("The JWT MAC does not match locally computed MAC.");
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new SecurityException("Initialize Mac instance failed", e);
            }
        } else {
            try {
                if (key instanceof PublicKey publicKey) {
                    if (!alg.getSignatureProvider().verify(publicKey, signBaseBytes(jwtParts), decodeContent(jwtParts[2]))) {
                        throw new InvalidSignatureException("The JWT digital signature does not match locally computed signature.");
                    }
                } else if (key instanceof PrivateKey privateKey) {
                    var signString = encodeBase64(alg.getSignatureProvider().sign(privateKey, signBaseBytes(jwtParts)));
                    if (!signString.equals(jwtParts[2])) {
                        throw new InvalidSignatureException("The JWT digital signature does not match locally computed signature.");
                    }
                } else {
                    throw new com.github.fmjsjx.libcommon.jwt.exception.InvalidKeyException("The key must be either a public key or a private key");
                }
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new SecurityException("Initialize Signature instance failed", e);
            } catch (InvalidKeyException e) {
                throw new com.github.fmjsjx.libcommon.jwt.exception.InvalidKeyException("Fail to init signature with this key", e);
            } catch (SignatureException e) {
                throw new SecurityException("Fail to verify data and signature", e);
            }
        }
    }

    private final KeyLocator keyLocator;

    DefaultJwtParser(KeyLocator keyLocator, JsonRepresentedFactory<?> jsonRepresentedFactory, boolean allowExpired, long allowedClockSkewSeconds) {
        super(jsonRepresentedFactory, allowExpired, allowedClockSkewSeconds);
        this.keyLocator = keyLocator;
    }

    @Override
    public Jwt parse(String jwt) throws JwtException {
        var jwtParts = splitJwtParts(jwt);
        var header = parseJoseHeader(jwtParts[0]);
        var alg = header.getAlgorithm();
        if (alg == null) {
            throw new MissingRequiredAlgException();
        }
        var algorithm = CryptoAlgorithm.getInstance(alg);
        var key = keyLocator.locate(header);
        // verify
        verify(algorithm, key, jwtParts);
        var content = decodeContent(jwtParts[1]);
        // this parser only support JWT Claims Set content type
        var claimsSet = parseAndValidateClaimsSet(content);
        return new DefaultJwt(header, content, claimsSet);
    }

}
