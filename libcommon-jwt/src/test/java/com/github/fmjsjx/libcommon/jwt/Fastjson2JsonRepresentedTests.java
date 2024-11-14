package com.github.fmjsjx.libcommon.jwt;

import static com.github.fmjsjx.libcommon.jwt.DefaultJwtClaimsSetTests.EMPTY_JSON;
import static com.github.fmjsjx.libcommon.jwt.DefaultJwtClaimsSetTests.TEST_RAW_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.alibaba.fastjson2.JSONObject;
import com.github.fmjsjx.libcommon.json.Fastjson2Library;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


public class Fastjson2JsonRepresentedTests {

    static Fastjson2JsonRepresented createEmpty() {
        return createTest(Fastjson2Library.getInstance().loads(EMPTY_JSON));
    }

    static Fastjson2JsonRepresented createTest() {
        return createTest(Fastjson2Library.getInstance().loads(TEST_RAW_JSON));
    }

    static Fastjson2JsonRepresented createTest(String jsonString) {
        return createTest(Fastjson2Library.getInstance().loads(jsonString));
    }

    static Fastjson2JsonRepresented createTest(JSONObject json) {
        var constructor = Fastjson2JsonRepresented.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            return (Fastjson2JsonRepresented) constructor.newInstance(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
