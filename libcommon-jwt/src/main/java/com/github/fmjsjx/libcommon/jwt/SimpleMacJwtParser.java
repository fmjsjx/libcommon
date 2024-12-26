package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.crypto.JwsCryptoAlgorithm;
import com.github.fmjsjx.libcommon.jwt.crypto.MacProvider.MacFunction;
import com.github.fmjsjx.libcommon.jwt.exception.InvalidSignatureException;
import com.github.fmjsjx.libcommon.jwt.exception.JwtException;

import java.security.Key;
import java.util.Arrays;

class SimpleMacJwtParser extends AbstractJwtParser {

    private final MacFunction macFunction;

    SimpleMacJwtParser(JwsCryptoAlgorithm algorithm, Key key,
                       JsonRepresentedFactory<?> jsonRepresentedFactory, boolean allowExpired,
                       long allowedClockSkewSeconds) {
        super(jsonRepresentedFactory, allowExpired, allowedClockSkewSeconds);
        this.macFunction = algorithm.getMacProvider().getFunction(key);
    }

    private boolean verify(byte[] signBaseBytes, String base64Signature) {
        var signData = macFunction.apply(signBaseBytes);
        return Arrays.equals(signData, decodeContent(base64Signature));
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
