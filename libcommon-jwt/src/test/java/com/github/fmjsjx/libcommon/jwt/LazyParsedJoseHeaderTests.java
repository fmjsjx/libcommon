package com.github.fmjsjx.libcommon.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.github.fmjsjx.libcommon.json.Fastjson2Library;
import com.github.fmjsjx.libcommon.util.Base64Util;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class LazyParsedJoseHeaderTests {

    @Test
    public void testToJson() {
        var rawJson = "{\"alg\":\"RS512\",\"typ\":\"JWT\",\"kid\":\"tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc\"}".getBytes(StandardCharsets.UTF_8);
        var base64String = Base64Util.encoder(true, true).encodeToString(rawJson);
        var header = new LazyParsedJoseHeader(base64String, JsoniterJsonRepresented.getFactory());
        assertArrayEquals(rawJson, Fastjson2Library.getInstance().dumpsToBytes(header));
    }

    static LazyParsedJoseHeader create(String rawJsonString) {
        var rawJson = rawJsonString.getBytes(StandardCharsets.UTF_8);
        var base64String = Base64Util.encoder(true, true).encodeToString(rawJson);
        return new LazyParsedJoseHeader(base64String, Fastjson2JsonRepresented.getFactory());
    }

    @Test
    public void testGetAlgorithm() {
        assertNull(create("{}").getAlgorithm());
        var header = create("{\"alg\":\"RS512\"}");
        assertEquals("RS512", header.getAlgorithm());
    }

    @Test
    public void testGetJwkSetUrl() {
        assertNull(create("{}").getJwkSetUrl());
        var header = create("{\"jku\":\"https://test.jwk.com/public-keys\"}");
        assertEquals("https://test.jwk.com/public-keys", header.getJwkSetUrl());
    }

    @Test
    public void testGetJsonWebKey() {
        assertNull(create("{}").getJwkSetUrl());
        var header = create("{\"jwk\":\"JSON_WEB_KEY\"}");
        assertEquals("JSON_WEB_KEY", header.getJsonWebKey());
    }

    @Test
    public void testGetKeyId() {
        assertNull(create("{}").getKeyId());
        assertEquals("tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc", create("{\"kid\":\"tY9fuGA5DXjEUObRLfngQ6eeZgeHr5_-9CKy7QBjEjc\"}").getKeyId());
    }

    @Test
    public void testGetX509Url() {
        assertNull(create("{}").getX509Url());
        assertEquals("https://test.x5u.com/public-keys", create("{\"x5u\":\"https://test.x5u.com/public-keys\"}").getX509Url());
    }

    @Test
    public void testGetX509CertificateChain() {
        assertNull(create("{}").getX509CertificateChain());
        var header = create("{\"x5c\":[\"AA\",\"BB\",\"CC\"]}");
        assertArrayEquals(new String[]{"AA", "BB", "CC"}, header.getX509CertificateChain().toArray(String[]::new));
    }

    @Test
    public void testGetX509CertificateSha1Thumbprint() {
        assertNull(create("{}").getX509CertificateSha1Thumbprint());
        assertEquals("AA", create("{\"x5t\":\"AA\"}").getX509CertificateSha1Thumbprint());
    }

    @Test
    public void testGetX509CertificateSha256Thumbprint() {
        assertNull(create("{}").getX509CertificateSha256Thumbprint());
        assertEquals("AA", create("{\"x5t#S256\":\"AA\"}").getX509CertificateSha256Thumbprint());
    }

    @Test
    public void testGetType() {
        assertNull(create("{}").getType());
        assertEquals("JWT", create("{\"typ\":\"JWT\"}").getType());
    }

    @Test
    public void testGetContentType() {
        assertNull(create("{}").getContentType());
        assertEquals("application/json", create("{\"cty\":\"application/json\"}").getContentType());
    }

    @Test
    public void testGetCritical() {
        assertNull(create("{}").getCritical());
        assertArrayEquals(new String[]{"exp"}, create("{\"crit\":[\"exp\"]}").getCritical().toArray(String[]::new));
    }

    @Test
    public void testGet_String_Class() {
        assertNull(create("{}").get("t", String.class));
        assertNull(create("{}").get("t", Integer.class));
        assertNull(create("{}").get("t", Long.class));
        assertNull(create("{}").get("t", Double.class));
        assertNull(create("{}").get("t", Boolean.class));
        assertNull(create("{}").get("t", Map.class));
        assertEquals("a", create("{\"a\":\"a\"}").get("a", String.class));
        assertEquals(1, create("{\"a\":1}").get("a", Integer.class));
        assertEquals(1234567890123L, create("{\"a\":1234567890123}").get("a", Long.class));
        assertEquals(3.14159, create("{\"a\":3.14159}").get("a", Double.class));
        assertEquals(true, create("{\"a\":1}").get("a", Boolean.class));
        assertEquals(false, create("{\"a\":0}").get("a", Boolean.class));
        assertEquals(true, create("{\"a\":true}").get("a", Boolean.class));
        assertEquals(Map.of("a", "a"), create("{\"a\":{\"a\":\"a\"}}").get("a", Map.class));
    }

    @Test
    public void testGet_String_Type() {
        assertNull(create("{}").get("t", String.class));
        assertEquals("a", create("{\"a\":\"a\"}").get("a", String.class));
        var mapType = Fastjson2Library.getInstance().mapTypeReference(String.class, String.class).getType();
        assertEquals(Map.of("a", "a"), create("{\"a\":{\"a\":\"a\"}}").<Map<String, String>>get("a", mapType));
        var listType = Fastjson2Library.getInstance().listTypeReference(String.class).getType();
        assertArrayEquals(new String[]{"a", "b", "c"}, create("{\"a\":[\"a\",\"b\",\"c\"]}").<List<String>>get("a", listType).toArray(String[]::new));
    }

    @Test
    public void testGetString() {
        assertNull(create("{}").getString("t"));
        assertEquals("a", create("{\"t\":\"a\"}").getString("t"));
    }

    @Test
    public void testGetInt() {
        var v = create("{}").getInt("t");
        assertFalse(v.isPresent());
        v = create("{\"t\":1}").getInt("t");
        assertTrue(v.isPresent());
        assertEquals(1, v.getAsInt());
    }

    @Test
    public void testGetLong() {
        var v = create("{}").getLong("t");
        assertFalse(v.isPresent());
        v = create("{\"t\":1234567890123}").getLong("t");
        assertTrue(v.isPresent());
        assertEquals(1234567890123L, v.getAsLong());
    }

    @Test
    public void testGetDouble() {
        var v = create("{}").getDouble("t");
        assertFalse(v.isPresent());
        v = create("{\"t\":3.14159}").getDouble("t");
        assertTrue(v.isPresent());
        assertEquals(3.14159, v.getAsDouble());
    }

    @Test
    public void testGetBoolean() {
        assertNull(create("{}").getBoolean("t"));
        assertTrue(create("{\"t\":true}").getBoolean("t"));
        assertTrue(create("{\"t\":1}").getBoolean("t"));
        assertFalse(create("{\"t\":false}").getBoolean("t"));
        assertFalse(create("{\"t\":0}").getBoolean("t"));
    }

    @Test
    public void testGetList() {
        assertNull(create("{}").getList("x5c", String.class));
        var header = create("{\"x5c\":[\"AA\",\"BB\",\"CC\"]}");
        assertArrayEquals(new String[]{"AA", "BB", "CC"}, header.getList("x5c", String.class).toArray(String[]::new));
        assertArrayEquals(new int[]{1, 2, 3}, create("{\"i\":[1,2,3]}").getList("i", Integer.class).stream().mapToInt(Integer::intValue).toArray());
    }

}
