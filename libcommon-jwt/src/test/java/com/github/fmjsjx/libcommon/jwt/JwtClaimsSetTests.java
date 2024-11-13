package com.github.fmjsjx.libcommon.jwt;

import static com.github.fmjsjx.libcommon.jwt.DefaultJwtClaimsSetTests.TEST_RAW_JSON;
import static com.github.fmjsjx.libcommon.jwt.JwtClaimNames.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.OptionalLong;


public class JwtClaimsSetTests {

    @AfterEach
    public void tearDown() {
        clearAllCaches();
    }

    @Test
    public void testParse() {
        mockStatic(DefaultJwtClaimsSet.class);
        var result = mock(DefaultJwtClaimsSet.class);
        when(DefaultJwtClaimsSet.parse(any())).thenReturn(result);
        assertEquals(result, JwtClaimsSet.parse(TEST_RAW_JSON));
    }

    static JwtClaimsSet mockClaimsSet() {
        return mock(JwtClaimsSet.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    public void testGetIssuer() {
        var claimsSet = mockClaimsSet();
        when(claimsSet.getString(ISSUER)).thenReturn("issuer");
        assertEquals("issuer", claimsSet.getIssuer());
    }

    @Test
    public void testGetSubject() {
        var claimsSet = mockClaimsSet();
        when(claimsSet.getString(SUBJECT)).thenReturn("subject");
        assertEquals("subject", claimsSet.getSubject());
    }

    @Test
    public void testGetAudience() {
        var claimsSet = mockClaimsSet();
        when(claimsSet.getString(AUDIENCE)).thenReturn("audience");
        assertEquals("audience", claimsSet.getAudience());
    }

    @Test
    public void testGetExpiration() {
        var claimsSet = mockClaimsSet();
        var unixTime = DateTimeUtil.unixTime() + 7200;
        when(claimsSet.getLong(EXPIRATION_TIME)).thenReturn(OptionalLong.empty(), OptionalLong.of(unixTime));
        assertFalse(claimsSet.getExpirationTime().isPresent());
        assertEquals(unixTime, claimsSet.getExpirationTime().getAsLong());
    }

    @Test
    public void testGetNotBefore() {
        var claimsSet = mockClaimsSet();
        var unixTime = DateTimeUtil.unixTime();
        when(claimsSet.getLong(NOT_BEFORE)).thenReturn(OptionalLong.empty(), OptionalLong.of(unixTime));
        assertFalse(claimsSet.getNotBefore().isPresent());
        assertEquals(unixTime, claimsSet.getNotBefore().getAsLong());
    }

    @Test
    public void testGetIssuedAt() {
        var claimsSet = mockClaimsSet();
        var unixTime = DateTimeUtil.unixTime();
        when(claimsSet.getLong(ISSUED_AT)).thenReturn(OptionalLong.empty(), OptionalLong.of(unixTime));
        assertFalse(claimsSet.getIssuedAt().isPresent());
        assertEquals(unixTime, claimsSet.getIssuedAt().getAsLong());
    }

    @Test
    public void testGetJwtId() {
        var claimsSet = mockClaimsSet();
        when(claimsSet.getString(JWT_ID)).thenReturn("jwtId");
        assertEquals("jwtId", claimsSet.getJwtId());
    }

}
