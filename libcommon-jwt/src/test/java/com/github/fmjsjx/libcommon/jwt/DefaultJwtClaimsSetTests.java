package com.github.fmjsjx.libcommon.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.github.fmjsjx.libcommon.json.Fastjson2Library;
import com.github.fmjsjx.libcommon.jwt.exception.IllegalJwtException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class DefaultJwtClaimsSetTests {

    static final byte[] EMPTY_JSON = "{}".getBytes();
    static final byte[] TEST_RAW_JSON = "{\"exp\":1731574512,\"iat\":1731488112,\"jti\":\"2b62d6eb-2c78-45eb-ace4-e9b7906baa44\",\"iss\":\"https://dev-keycloak.hihcpss.cn/auth/realms/hiscene-dev\",\"sub\":\"f:2bee04c9-8e6f-4e03-9293-35c3a2b2b290:70951\",\"typ\":\"Bearer\",\"azp\":\"hi-enterprise-plat\",\"session_state\":\"2f731bec-4b39-4111-8e22-31aa3169b9e9\",\"acr\":\"1\",\"scope\":\"userinfo\",\"sid\":\"2f731bec-4b39-4111-8e22-31aa3169b9e9\",\"user_id\":\"70951\",\"nickname\":\"方敏杰\",\"picture\":\"http://192.168.24.66:32355/1/Xj0wDa.jpg\"}".getBytes(StandardCharsets.UTF_8);

    @Test
    public void testParse() {
        assertNotNull(DefaultJwtClaimsSet.parse(TEST_RAW_JSON));
        try {
            DefaultJwtClaimsSet.parse(Arrays.copyOf(TEST_RAW_JSON, TEST_RAW_JSON.length - 1));
            fail("Should throw IllegalJwtException");
        } catch (IllegalJwtException e) {
            assertEquals(Fastjson2Library.Fastjson2Exception.class, e.getCause().getClass());
        }
        try {
            DefaultJwtClaimsSet.parse("[]".getBytes());
            fail("Should throw IllegalJwtException");
        } catch (IllegalJwtException e) {
            assertEquals(Fastjson2Library.Fastjson2Exception.class, e.getCause().getClass());
        }
    }

    @Test
    public void testToJson() {
        var claimsSet = DefaultJwtClaimsSet.parse(TEST_RAW_JSON);
        assertArrayEquals(TEST_RAW_JSON, Fastjson2Library.getInstance().dumpsToBytes(claimsSet));
    }

    static DefaultJwtClaimsSet createEmpty() {
        return DefaultJwtClaimsSet.parse(EMPTY_JSON);
    }

    static DefaultJwtClaimsSet createTest() {
        return DefaultJwtClaimsSet.parse(TEST_RAW_JSON);
    }

    static DefaultJwtClaimsSet createTest(String jsonString) {
        return DefaultJwtClaimsSet.parse(jsonString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetIssuer() {
        assertNull(createEmpty().getIssuer());
        assertEquals("https://dev-keycloak.hihcpss.cn/auth/realms/hiscene-dev", createTest().getIssuer());
    }

    @Test
    public void testGetSubject() {
        assertNull(createEmpty().getSubject());
        assertEquals("f:2bee04c9-8e6f-4e03-9293-35c3a2b2b290:70951", createTest().getSubject());
    }

    @Test
    public void testGetAudience() {
        assertNull(createTest().getAudience());
        assertEquals("audience", createTest("{\"aud\":\"audience\"}").getAudience());
    }

    @Test
    public void testGetExpirationTime() {
        assertFalse(createEmpty().getExpirationTime().isPresent());
        assertEquals(1731574512L, createTest().getExpirationTime().orElse(-1));
    }

    @Test
    public void testGetNotBefore() {
        assertFalse(createTest().getNotBefore().isPresent());
        assertEquals(1731488112L, createTest("{\"nbf\":1731488112}").getNotBefore().orElse(-1));
    }

    @Test
    public void testGetIssuedAt() {
        assertFalse(createEmpty().getIssuedAt().isPresent());
        assertEquals(1731488112L, createTest().getIssuedAt().orElse(-1));
    }

    @Test
    public void testGetJwtId() {
        assertNull(createEmpty().getJwtId());
        assertEquals("2b62d6eb-2c78-45eb-ace4-e9b7906baa44", createTest().getJwtId());
    }

    @Test
    public void testGet_String_Class() {
        assertNull(createEmpty().get("t", String.class));
        assertNull(createEmpty().get("t", Integer.class));
        assertNull(createEmpty().get("t", Long.class));
        assertNull(createEmpty().get("t", Double.class));
        assertNull(createEmpty().get("t", Boolean.class));
        assertNull(createEmpty().get("t", Map.class));
        assertEquals("70951", createTest().get("user_id", String.class));
        assertEquals(70951, createTest().get("user_id", Integer.class));
        assertEquals(1731488112L, createTest().get("iat", Long.class));
        assertEquals(3.14159, createTest("{\"a\":3.14159}").get("a", Double.class));
        assertEquals(true, createTest("{\"a\":1}").get("a", Boolean.class));
        assertEquals(false, createTest("{\"a\":0}").get("a", Boolean.class));
        assertEquals(true, createTest("{\"a\":true}").get("a", Boolean.class));
        assertEquals(Map.of("a", "a"), createTest("{\"a\":{\"a\":\"a\"}}").get("a", Map.class));
    }

    @Test
    public void testGet_String_Type() {
        assertNull(createEmpty().get("t", String.class));
        assertEquals("a", createTest("{\"a\":\"a\"}").get("a", String.class));
        var mapType = Fastjson2Library.getInstance().mapTypeReference(String.class, String.class).getType();
        assertEquals(Map.of("a", "a"), createTest("{\"a\":{\"a\":\"a\"}}").<Map<String, String>>get("a", mapType));
        var listType = Fastjson2Library.getInstance().listTypeReference(String.class).getType();
        assertArrayEquals(new String[]{"a", "b", "c"}, createTest("{\"a\":[\"a\",\"b\",\"c\"]}").<List<String>>get("a", listType).toArray(String[]::new));
    }

    @Test
    public void testGetString() {
        assertNull(createEmpty().getString("t"));
        assertEquals("a", createTest("{\"t\":\"a\"}").getString("t"));
    }


    @Test
    public void testGetInt() {
        var v = createEmpty().getInt("t");
        assertFalse(v.isPresent());
        v = createTest("{\"t\":1}").getInt("t");
        assertTrue(v.isPresent());
        assertEquals(1, v.getAsInt());
    }

    @Test
    public void testGetLong() {
        var v = createEmpty().getLong("t");
        assertFalse(v.isPresent());
        v = createTest("{\"t\":1234567890123}").getLong("t");
        assertTrue(v.isPresent());
        assertEquals(1234567890123L, v.getAsLong());
    }

    @Test
    public void testGetDouble() {
        var v = createEmpty().getDouble("t");
        assertFalse(v.isPresent());
        v = createTest("{\"t\":3.14159}").getDouble("t");
        assertTrue(v.isPresent());
        assertEquals(3.14159, v.getAsDouble());
    }

    @Test
    public void testGetBoolean() {
        assertNull(createEmpty().getBoolean("t"));
        assertTrue(createTest("{\"t\":true}").getBoolean("t"));
        assertTrue(createTest("{\"t\":1}").getBoolean("t"));
        assertFalse(createTest("{\"t\":false}").getBoolean("t"));
        assertFalse(createTest("{\"t\":0}").getBoolean("t"));
    }

    @Test
    public void testGetList() {
        assertNull(createEmpty().getList("x5c", String.class));
        var header = createTest("{\"x5c\":[\"AA\",\"BB\",\"CC\"]}");
        assertArrayEquals(new String[]{"AA", "BB", "CC"}, header.getList("x5c", String.class).toArray(String[]::new));
        assertArrayEquals(new int[]{1, 2, 3}, createTest("{\"i\":[1,2,3]}").getList("i", Integer.class).stream().mapToInt(Integer::intValue).toArray());
    }

}
