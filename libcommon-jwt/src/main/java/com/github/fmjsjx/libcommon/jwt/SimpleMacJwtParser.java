package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidSignatureException;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;
import com.github.fmjsjx.libcommon.jwt.exception.SecurityException;
import com.github.fmjsjx.libcommon.util.concurrent.EasyThreadLocal;

import javax.crypto.Mac;
import java.security.Key;

class SimpleMacJwtParser extends AbstractJwtParser {

    private final JwsCryptoAlgorithm algorithm;
    private final Key key;

    private final EasyThreadLocal<Mac> threadLocalMac;

    SimpleMacJwtParser(JwsCryptoAlgorithm algorithm, Key key,
                       JsonRepresentedFactory<?> jsonRepresentedFactory, boolean allowExpired,
                       long allowedClockSkewSeconds) {
        super(jsonRepresentedFactory, allowExpired, allowedClockSkewSeconds);
        this.algorithm = algorithm;
        this.key = key;
        this.threadLocalMac = EasyThreadLocal.create(this::getMacInstance);
    }

    private Mac getMacInstance() {
        try {
            return algorithm.getMacProvider().getInstance(key);
        } catch (Exception e) {
            throw new SecurityException("Initialize Mac instance failed", e);
        }
    }

    private boolean verify(byte[] signBaseBytes, String base64Signature) {
        var signData = threadLocalMac.get().doFinal(signBaseBytes);
        var signString = encodeBase64(signData);
        return signString.equals(base64Signature);
    }

    @Override
    public Jwt parse(String jwt) throws JwtException {
        var jwtParts = splitJwtParts(jwt);
        if (!verify(signBaseBytes(jwtParts), jwtParts[2])) {
            throw new InvalidSignatureException("The JWT MAC does not match locally computed MAC.");
        }
        var header = parseJoseHeader(jwtParts[0]);
        var content = decodeContent(jwtParts[1]);
        // this parser only support JWT Claims Set content type
        var claimsSet = parseAndValidateClaimsSet(content);
        return new DefaultJwt(header, content, claimsSet);
    }

}
