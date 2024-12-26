package com.github.fmjsjx.libcommon.jwt;

import com.github.fmjsjx.libcommon.jwt.exception.JwtException;

class InsecureJwtParser extends AbstractJwtParser {

    InsecureJwtParser(JsonRepresentedFactory<?> jsonRepresentedFactory, boolean allowExpired, long allowedClockSkewSeconds) {
        super(jsonRepresentedFactory, allowExpired, allowedClockSkewSeconds);
    }

    @Override
    public Jwt parse(String jwt) throws JwtException {
        var jwtParts = splitJwtParts(jwt);
        // skip verification stage
        var header = parseJoseHeader(jwtParts[0]);
        var content = decodeContent(jwtParts[1]);
        // this parser only support JWT Claims Set content type
        var claimsSet = parseAndValidateClaimsSet(content);
        return new DefaultJwt(header, content, claimsSet);
    }

}
