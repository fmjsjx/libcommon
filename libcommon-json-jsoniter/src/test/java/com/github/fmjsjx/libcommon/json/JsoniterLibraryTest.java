package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jsoniter.ValueType;
import org.junit.jupiter.api.Test;

public class JsoniterLibraryTest {

    @Test
    public void test() {
        try {
            // {"i1":1,"l1":2,"d1":3.0}
            String json = "{\"i1\":1,\"l1\":2,\"d1\":3.0}";
            TestObj o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"l1":2,"d1":3.0}
            json = "{\"l1\":2,\"d1\":3.0}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":null,"l1":2,"d1":3.0}
            json = "{\"i1\":null,\"l1\":2,\"d1\":3.0}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"d1":3.0}
            json = "{\"i1\":1,\"d1\":3.0}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"l1":null,"d1":3.0}
            json = "{\"i1\":1,\"l1\":null,\"d1\":3.0}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"l1":2}
            json = "{\"i1\":1,\"l1\":2}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());

            // {"i1":1,"l1":2,"d1":null}
            json = "{\"i1\":1,\"l1\":2,\"d1\":null}";
            o = JsoniterLibrary.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLoadAsType() {
        var json = """
                {"i1":1,"l1":2,"d1":null}""";
        var o = JsoniterLibrary.getInstance().loadsAsType(json, ValueType.OBJECT);
        assertEquals(json, o.toString());
        try {
            JsoniterLibrary.getInstance().loadsAsType(json, ValueType.ARRAY);
            fail("Should throw JsoniterException");
        } catch (JsoniterLibrary.JsoniterException e) {
            assertEquals("JSON value expected be ARRAY but was OBJECT", e.getMessage());
        }
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        o = JsoniterLibrary.getInstance().loadsAsType(bytes, ValueType.OBJECT);
        assertEquals(json, o.toString());
        try {
            JsoniterLibrary.getInstance().loadsAsType(bytes, ValueType.ARRAY);
            fail("Should throw JsoniterException");
        } catch (JsoniterLibrary.JsoniterException e) {
            assertEquals("JSON value expected be ARRAY but was OBJECT", e.getMessage());
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class TestObj {

        private OptionalInt i1 = OptionalInt.empty();
        private OptionalLong l1 = OptionalLong.empty();
        private OptionalDouble d1 = OptionalDouble.empty();

        public OptionalInt getI1() {
            return i1;
        }

        public void setI1(OptionalInt i1) {
            this.i1 = i1;
        }

        public OptionalLong getL1() {
            return l1;
        }

        public void setL1(OptionalLong l1) {
            this.l1 = l1;
        }

        public OptionalDouble getD1() {
            return d1;
        }

        public void setD1(OptionalDouble d1) {
            this.d1 = d1;
        }

    }

    @Test
    public void testDecodeJsonNode() {
        var test = JsoniterLibrary.getInstance().loads("""
                {"nod":1,"obj":{"name":"test"},"arr":[1,2,3]}""", TestJsonNode.class);
        assertNotNull(test);
        assertNotNull(test.nod);
        assertEquals(1, test.nod.intValue());
        assertNotNull(test.obj);
        assertTrue(test.obj.isObject());
        assertEquals(1, test.obj.size());
        assertEquals("test", test.obj.get("name").textValue());
        assertNotNull(test.arr);
        assertTrue(test.arr.isArray());
        assertEquals(3, test.arr.size());
        assertEquals(1, test.arr.get(0).intValue());
        assertEquals(2, test.arr.get(1).intValue());
        assertEquals(3, test.arr.get(2).intValue());

        test = JsoniterLibrary.getInstance().loads("{}", TestJsonNode.class);
        assertNotNull(test);
        assertNull(test.nod);
        assertNull(test.obj);
        assertNull(test.arr);
        test = JsoniterLibrary.getInstance().loads("""
                {"nod":null,"obj":null,"arr":null}""", TestJsonNode.class);
        assertNotNull(test);
        assertNotNull(test.nod);
        assertTrue(test.nod.isNull());
        assertNull(test.obj);
        assertNull(test.arr);
    }

    public static class TestJsonNode {

        private JsonNode nod;
        private ObjectNode obj;
        private ArrayNode arr;

        public JsonNode getNod() {
            return nod;
        }

        public void setNod(JsonNode nod) {
            this.nod = nod;
        }

        public ObjectNode getObj() {
            return obj;
        }

        public void setObj(ObjectNode obj) {
            this.obj = obj;
        }

        public ArrayNode getArr() {
            return arr;
        }

        public void setArr(ArrayNode arr) {
            this.arr = arr;
        }
    }

    @Test
    public void testEncodeJsonNode() {
        var object = Jackson2Library.getInstance().createObjectNode();
        object.put("int", 1)
                .put("long", 1234567890123L)
                .put("double", 1.2)
                .put("boolean", true)
                .put("string", "abc")
                .putNull("null")
                .put("bigInteger", new BigInteger("1234567890123456789012345"))
                .put("bigDecimal", new BigDecimal("0.1234567890123456789012345"));
        object.putObject("object").put("name", "test");
        object.putArray("array").add(1).add(2).add(3);
        assertEquals(
                Jackson2Library.getInstance().dumpsToString(object),
                JsoniterLibrary.getInstance().dumpsToString(object)
        );
        assertEquals("1", JsoniterLibrary.getInstance().dumpsToString(object.path("int")));
        assertEquals("1234567890123", JsoniterLibrary.getInstance().dumpsToString(object.path("long")));
        assertEquals("1.2", JsoniterLibrary.getInstance().dumpsToString(object.path("double")));
        assertEquals("true", JsoniterLibrary.getInstance().dumpsToString(object.path("boolean")));
        assertEquals("\"abc\"", JsoniterLibrary.getInstance().dumpsToString(object.path("string")));
        assertEquals("null", JsoniterLibrary.getInstance().dumpsToString(object.path("null")));
        assertEquals("null", JsoniterLibrary.getInstance().dumpsToString(object.path("missing")));
        assertEquals("1234567890123456789012345", JsoniterLibrary.getInstance().dumpsToString(object.path("bigInteger")));
        assertEquals("0.1234567890123456789012345", JsoniterLibrary.getInstance().dumpsToString(object.path("bigDecimal")));
        assertEquals("{\"name\":\"test\"}", JsoniterLibrary.getInstance().dumpsToString(object.path("object")));
        assertEquals("[1,2,3]", JsoniterLibrary.getInstance().dumpsToString(object.path("array")));
    }

    public static class TestFastjson2Objects {

        private JSONObject obj;
        private JSONArray arr;

        public JSONObject getObj() {
            return obj;
        }

        public void setObj(JSONObject obj) {
            this.obj = obj;
        }

        public JSONArray getArr() {
            return arr;
        }

        public void setArr(JSONArray arr) {
            this.arr = arr;
        }

    }

    @Test
    public void testDecodeFastjson2() {
        var test = JsoniterLibrary.getInstance().loads("""
                {"obj":{"name":"test"},"arr":[1,2,3]}""", TestFastjson2Objects.class);
        assertNotNull(test);
        assertNotNull(test.obj);
        assertEquals(1, test.obj.size());
        assertEquals("test", test.obj.get("name"));
        assertNotNull(test.arr);
        assertEquals(3, test.arr.size());
        assertEquals(1, test.arr.get(0));
        assertEquals(2, test.arr.get(1));
        assertEquals(3, test.arr.get(2));

        test = JsoniterLibrary.getInstance().loads("{}", TestFastjson2Objects.class);
        assertNotNull(test);
        assertNull(test.obj);
        assertNull(test.arr);
        test = JsoniterLibrary.getInstance().loads("""
                {"obj":null,"arr":null}""", TestFastjson2Objects.class);
        assertNotNull(test);
        assertNull(test.obj);
        assertNull(test.arr);
    }

    @Test
    public void testEncodeFastjson2() {
        var object = new JSONObject();
        object.fluentPut("int", 1)
                .fluentPut("long", 1234567890123L)
                .fluentPut("double", 1.2)
                .fluentPut("boolean", true)
                .fluentPut("string", "abc")
                .fluentPut("bigInteger", new BigInteger("1234567890123456789012345"))
                .fluentPut("bigDecimal", new BigDecimal("0.1234567890123456789012345"));
        object.putObject("object").fluentPut("name", "test");
        object.putArray("array").fluentAdd(1).fluentAdd(2).fluentAdd(3);
        assertEquals(
                Fastjson2Library.getInstance().dumpsToString(object),
                JsoniterLibrary.getInstance().dumpsToString(object)
        );
        assertEquals("{\"name\":\"test\"}", JsoniterLibrary.getInstance().dumpsToString(object.get("object")));
        assertEquals("[1,2,3]", JsoniterLibrary.getInstance().dumpsToString(object.get("array")));
    }

    @Test
    public void testDecodeJackson3JsonNode() {
        var test = JsoniterLibrary.getInstance().loads("""
                {"nod":1,"obj":{"name":"test"},"arr":[1,2,3]}""", TestJackson3JsonNode.class);
        assertNotNull(test);
        assertNotNull(test.nod);
        assertEquals(1, test.nod.intValue());
        assertNotNull(test.obj);
        assertTrue(test.obj.isObject());
        assertEquals(1, test.obj.size());
        assertEquals("test", test.obj.get("name").stringValue());
        assertNotNull(test.arr);
        assertTrue(test.arr.isArray());
        assertEquals(3, test.arr.size());
        assertEquals(1, test.arr.get(0).intValue());
        assertEquals(2, test.arr.get(1).intValue());
        assertEquals(3, test.arr.get(2).intValue());

        test = JsoniterLibrary.getInstance().loads("{}", TestJackson3JsonNode.class);
        assertNotNull(test);
        assertNull(test.nod);
        assertNull(test.obj);
        assertNull(test.arr);
        test = JsoniterLibrary.getInstance().loads("""
                {"nod":null,"obj":null,"arr":null}""", TestJackson3JsonNode.class);
        assertNotNull(test);
        assertNotNull(test.nod);
        assertTrue(test.nod.isNull());
        assertNull(test.obj);
        assertNull(test.arr);
    }

    public static class TestJackson3JsonNode {

        private tools.jackson.databind.JsonNode nod;
        private tools.jackson.databind.node.ObjectNode obj;
        private tools.jackson.databind.node.ArrayNode arr;

        public tools.jackson.databind.JsonNode getNod() {
            return nod;
        }

        public void setNod(tools.jackson.databind.JsonNode nod) {
            this.nod = nod;
        }

        public tools.jackson.databind.node.ObjectNode getObj() {
            return obj;
        }

        public void setObj(tools.jackson.databind.node.ObjectNode obj) {
            this.obj = obj;
        }

        public tools.jackson.databind.node.ArrayNode getArr() {
            return arr;
        }

        public void setArr(tools.jackson.databind.node.ArrayNode arr) {
            this.arr = arr;
        }
    }

    @Test
    public void testEncodeJackson3JsonNode() {
        var object = Jackson3Library.getInstance().createObjectNode();
        object.put("int", 1)
                .put("long", 1234567890123L)
                .put("double", 1.2)
                .put("boolean", true)
                .put("string", "abc")
                .putNull("null")
                .put("bigInteger", new BigInteger("1234567890123456789012345"))
                .put("bigDecimal", new BigDecimal("0.1234567890123456789012345"));
        object.putObject("object").put("name", "test");
        object.putArray("array").add(1).add(2).add(3);
        assertEquals(
                Jackson3Library.getInstance().dumpsToString(object),
                JsoniterLibrary.getInstance().dumpsToString(object)
        );
        assertEquals("1", JsoniterLibrary.getInstance().dumpsToString(object.path("int")));
        assertEquals("1234567890123", JsoniterLibrary.getInstance().dumpsToString(object.path("long")));
        assertEquals("1.2", JsoniterLibrary.getInstance().dumpsToString(object.path("double")));
        assertEquals("true", JsoniterLibrary.getInstance().dumpsToString(object.path("boolean")));
        assertEquals("\"abc\"", JsoniterLibrary.getInstance().dumpsToString(object.path("string")));
        assertEquals("null", JsoniterLibrary.getInstance().dumpsToString(object.path("null")));
        assertEquals("null", JsoniterLibrary.getInstance().dumpsToString(object.path("missing")));
        assertEquals("1234567890123456789012345", JsoniterLibrary.getInstance().dumpsToString(object.path("bigInteger")));
        assertEquals("0.1234567890123456789012345", JsoniterLibrary.getInstance().dumpsToString(object.path("bigDecimal")));
        assertEquals("{\"name\":\"test\"}", JsoniterLibrary.getInstance().dumpsToString(object.path("object")));
        assertEquals("[1,2,3]", JsoniterLibrary.getInstance().dumpsToString(object.path("array")));
    }

}
