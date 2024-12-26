package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidKeyException;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidSignatureException;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;
import com.github.fmjsjx.libcommon.jwt.exception.SecurityException;

import java.security.*;

class SimpleSignatureJwtParser extends AbstractJwtParser {

    private final JwsCryptoAlgorithm algorithm;
    private final boolean usePublicKey;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    SimpleSignatureJwtParser(JwsCryptoAlgorithm algorithm, Key key, JsonRepresentedFactory<?> jsonRepresentedFactory,
                             boolean allowExpired, long allowedClockSkewSeconds) {
        super(jsonRepresentedFactory, allowExpired, allowedClockSkewSeconds);
        this.algorithm = algorithm;
        if (key instanceof PublicKey pkey) {
            this.publicKey = pkey;
            this.privateKey = null;
            usePublicKey = true;
        } else if (key instanceof PrivateKey pkey) {
            this.publicKey = null;
            this.privateKey = pkey;
            usePublicKey = false;
        } else {
            throw new InvalidKeyException("The key must be either a public key or a private key");
        }
    }

    private boolean verify(byte[] signBaseBytes, String base64Signature) {
        try {
            if (usePublicKey) {
                return algorithm.getSignatureProvider().verify(publicKey, signBaseBytes, decodeContent(base64Signature));
            }
            var signString = encodeBase64(algorithm.getSignatureProvider().sign(privateKey, signBaseBytes));
            return base64Signature.equals(signString);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new SecurityException("Initialize Signature instance failed", e);
        } catch (java.security.InvalidKeyException e) {
            throw new InvalidKeyException("Fail to init signature with this key", e);
        } catch (SignatureException e) {
            throw new SecurityException("Fail to verify data and signature", e);
        }
    }

    @Override
    public Jwt parse(String jwt) throws JwtException {
        var jwtParts = splitJwtParts(jwt);
        // verify
        if (!verify(signBaseBytes(jwtParts), jwtParts[2])) {
            throw new InvalidSignatureException("The JWT digital signature does not match locally computed signature.");
        }
        var header = parseJoseHeader(jwtParts[0]);
        var content = decodeContent(jwtParts[1]);
        // this parser only support JWT Claims Set content type
        var claimsSet = parseAndValidateClaimsSet(content);
        return new DefaultJwt(header, content, claimsSet);
    }

}
