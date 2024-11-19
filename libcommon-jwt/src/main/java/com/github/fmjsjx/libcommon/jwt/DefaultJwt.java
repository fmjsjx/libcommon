package com.github.fmjsjx.libcommon.jwt;

import java.util.Arrays;

/**
 * The default implementation of {@link Jwt}.
 *
 * @author MJ Fang
 * @since 3.10
 */
public class DefaultJwt implements Jwt {

    private final JoseHeader header;
    private final byte[] content;
    private final JwtClaimsSet claimsSet;

    /**
     * Constructs a new {@link DefaultJwt} instance with the specified
     * parameters given.
     *
     * @param header    the header
     * @param content   the content
     * @param claimsSet the claims set
     */
    public DefaultJwt(JoseHeader header, byte[] content, JwtClaimsSet claimsSet) {
        this.header = header;
        this.content = content;
        this.claimsSet = claimsSet;
    }

    @Override
    public JoseHeader getHeader() {
        return header;
    }

    @Override
    public byte[] getContent() {
        return content.clone();
    }

    @Override
    public JwtClaimsSet getClaimsSet() {
        return claimsSet;
    }

    @Override
    public String toString() {
        return "DefaultJwt(header=" + getHeader() + ", content=" + getContentSummary() + ", claimsSet=" + getClaimsSet() + ")";
    }

    private String getContentSummary() {
        return "ByteArray@" + Integer.toHexString(Arrays.hashCode(content)) + "(len: " + content.length + ")";
    }
}
