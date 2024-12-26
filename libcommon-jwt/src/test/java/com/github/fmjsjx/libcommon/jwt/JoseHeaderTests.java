package com.github.fmjsjx.libcommon.jwt;

import static com.github.fmjsjx.libcommon.jwt.JoseHeaderNames.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.fmjsjx.libcommon.util.Base64Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class JoseHeaderTests {

    @AfterEach
    public void tearDown() {
        clearAllCaches();
    }

    @Test
    public void testParse() {
        mockStatic(DefaultJoseHeader.class);
        var result = mock(DefaultJoseHeader.class);
        when(DefaultJoseHeader.parse(any())).thenReturn(result);
        when(DefaultJoseHeader.parse(any(), any())).thenReturn(result);
        var rawJson = "{\"alg\":\"RS512\",\"typ\":\"JWT\",\"kid\":\"tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc\"}".getBytes(StandardCharsets.UTF_8);
        assertEquals(result, JoseHeader.parse(rawJson));
        assertEquals(result, JoseHeader.parse(rawJson, mock()));
    }

    @Test
    public void testParseLazy() {
        var rawJson = "{\"alg\":\"RS512\",\"typ\":\"JWT\",\"kid\":\"tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc\"}".getBytes(StandardCharsets.UTF_8);
        var base64String = Base64Util.encoder(true, true).encodeToString(rawJson);
        assertNotNull(JoseHeader.parseLazy(base64String));
        assertNotNull(JoseHeader.parseLazy(base64String, mock()));
    }

    static JoseHeader mockHeader() {
        return mock(JoseHeader.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    public void testGetAlgorithm() {
        var header = mockHeader();
        when(header.getString(ALGORITHM)).thenReturn(null);
        assertNull(header.getAlgorithm());
        when(header.getString(ALGORITHM)).thenReturn("RS512");
        assertEquals("RS512", header.getAlgorithm());
    }

    @Test
    public void testGetJwkSetUrl() {
        var header = mockHeader();
        when(header.getString(JWK_SET_URL)).thenReturn(null);
        assertNull(header.getJwkSetUrl());
        when(header.getString(JWK_SET_URL)).thenReturn("https://test.jwk.com/public-keys");
        assertEquals("https://test.jwk.com/public-keys", header.getJwkSetUrl());
    }

    @Test
    public void testGetJsonWebKey() {
        var header = mockHeader();
        when(header.getString(JSON_WEB_KEY)).thenReturn(null);
        assertNull(header.getJsonWebKey());
        when(header.getString(JSON_WEB_KEY)).thenReturn("JSON_WEB_KEY");
        assertEquals("JSON_WEB_KEY", header.getJsonWebKey());
    }

    @Test
    public void testGetKeyId() {
        var header = mockHeader();
        when(header.getString(KEY_ID)).thenReturn("tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc");
        assertEquals("tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc", header.getKeyId());
    }

    @Test
    public void testGetX509Url() {
        var header = mockHeader();
        when(header.getString(X509_URL)).thenReturn("https://test.x5u.com/public-keys");
        assertEquals("https://test.x5u.com/public-keys", header.getX509Url());
    }

    @Test
    public void testGetX509CertificateChain() {
        var header = mockHeader();
        when(header.getList(X509_CERTIFICATE_CHAIN, String.class)).thenReturn(List.of("AA", "BB", "CC"));
        assertArrayEquals(new String[]{"AA", "BB", "CC"}, header.getX509CertificateChain().toArray(String[]::new));
    }

    @Test
    public void testGetX509CertificateSha1Thumbprint() {
        var header = mockHeader();
        when(header.getString(X509_CERTIFICATE_SHA1_THUMBPRINT)).thenReturn("AA");
        assertEquals("AA", header.getX509CertificateSha1Thumbprint());
    }

    @Test
    public void testGetX509CertificateSha256Thumbprint() {
        var header = mockHeader();
        when(header.getString(X509_CERTIFICATE_SHA256_THUMBPRINT)).thenReturn("AA");
        assertEquals("AA", header.getX509CertificateSha256Thumbprint());
    }

    @Test
    public void testGetType() {
        var header = mockHeader();
        when(header.getString(TYPE)).thenReturn("JWT");
        assertEquals("JWT", header.getType());
    }

    @Test
    public void testGetContentType() {
        var header = mockHeader();
        when(header.getContentType()).thenReturn("application/json");
        assertEquals("application/json", header.getContentType());
    }

    @Test
    public void testGetCritical() {
        var header = mockHeader();
        when(header.getList(CRITICAL, String.class)).thenReturn(List.of("exp"));
        assertArrayEquals(new String[]{"exp"}, header.getCritical().toArray(String[]::new));
    }

}
