package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Fastjson2LibraryTests {

    @Test
    public void testLoadsJSONObject() {
        try {
            var json = """
                    {"id":1,"name":"test","array":[1,2,3],"obj":{"o":0}}""";
            var obj = Fastjson2Library.defaultInstance().loads(json);
            assertEquals(1, obj.getIntValue("id"));
            assertEquals("test", obj.getString("name"));
            assertEquals(3, obj.getJSONArray("array").size());
            assertArrayEquals(new int[]{1, 2, 3}, obj.getJSONArray("array").toJavaList(Integer.class).stream().mapToInt(Number::intValue).toArray());
            assertEquals(1, obj.getJSONObject("obj").size());
            assertEquals(0, obj.getJSONObject("obj").getIntValue("o", -1));

            obj = Fastjson2Library.defaultInstance().loads(json.getBytes(StandardCharsets.UTF_8));
            assertEquals(1, obj.getIntValue("id"));
            assertEquals("test", obj.getString("name"));
            assertEquals(3, obj.getJSONArray("array").size());
            assertArrayEquals(new int[]{1, 2, 3}, obj.getJSONArray("array").toJavaList(Integer.class).stream().mapToInt(Number::intValue).toArray());
            assertEquals(1, obj.getJSONObject("obj").size());
            assertEquals(0, obj.getJSONObject("obj").getIntValue("o", -1));

            obj = Fastjson2Library.defaultInstance().loads(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            assertEquals(1, obj.getIntValue("id"));
            assertEquals("test", obj.getString("name"));
            assertEquals(3, obj.getJSONArray("array").size());
            assertArrayEquals(new int[]{1, 2, 3}, obj.getJSONArray("array").toJavaList(Integer.class).stream().mapToInt(Number::intValue).toArray());
            assertEquals(1, obj.getJSONObject("obj").size());
            assertEquals(0, obj.getJSONObject("obj").getIntValue("o", -1));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testLoads() {
        try {
            // {"i1":1,"l1":2,"d1":3.0}
            String json = "{\"i1\":1,\"l1\":2,\"d1\":3.0}";
            TestObj o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());
            assertNull(o.getAny());
            assertNull(o.getJn());
            assertNull(o.getOn());
            assertNull(o.getAn());

            // {"l1":2,"d1":3.0}
            json = "{\"l1\":2,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":null,"l1":2,"d1":3.0}
            json = "{\"i1\":null,\"l1\":2,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"d1":3.0}
            json = "{\"i1\":1,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"l1":null,"d1":3.0}
            json = "{\"i1\":1,\"l1\":null,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());

            // {"i1":1,"l1":2}
            json = "{\"i1\":1,\"l1\":2}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());

            // {"i1":1,"l1":2,"d1":null}
            json = "{\"i1\":1,\"l1\":2,\"d1\":null}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());

            // {"any":null}
            json = "{\"any\":null}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNull(o.getAny());
            // {"any":{"key":"value"}}
            json = "{\"any\":{\"key\":\"value\"}}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.OBJECT, o.getAny().valueType());
            assertEquals("{\"key\":\"value\"}", o.getAny().toString());
            // {"any":123}
            json = "{\"any\":123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.NUMBER, o.getAny().valueType());
            assertEquals(123, o.getAny().toInt());
            // {"any":123.4567890123}
            json = "{\"any\":123.4567890123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.NUMBER, o.getAny().valueType());
            assertEquals(123.4567890123, o.getAny().toDouble());
            // {"any":"this is a string"}
            json = "{\"any\":\"this is a string\"}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.STRING, o.getAny().valueType());
            assertEquals("this is a string", o.getAny().toString());
            // {"any":true}
            json = "{\"any\":true}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.BOOLEAN, o.getAny().valueType());
            assertTrue(o.getAny().toBoolean());
            // {"any":false}
            json = "{\"any\":false}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getAny());
            assertEquals(ValueType.BOOLEAN, o.getAny().valueType());
            assertFalse(o.getAny().toBoolean());
            // {"jn":null,"on":null,"an":null}
            json = "{\"jn\":null,\"on\":null,\"an\":null}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNull(o.getJn());
            assertNull(o.getOn());
            assertNull(o.getAn());
            // {"jn":{"key":"value"},"on":{"key":"value"},"an":[1,2,3]}
            json = "{\"jn\":{\"key\":\"value\"},\"on\":{\"key\":\"value\"},\"an\":[1,2,3]}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.OBJECT, o.getJn().getNodeType());
            assertEquals("{\"key\":\"value\"}", o.getJn().toString());
            assertNotNull(o.getOn());
            assertEquals("{\"key\":\"value\"}", o.getOn().toString());
            assertNotNull(o.getAn());
            assertEquals("[1,2,3]", o.getAn().toString());
            // {"jn":[1,2,3]}
            json = "{\"jn\":[1,2,3]}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.ARRAY, o.getJn().getNodeType());
            assertEquals("[1,2,3]", o.getJn().toString());
            // {"jn":"This is a string"}
            json = "{\"jn\":\"This is a string\"}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.STRING, o.getJn().getNodeType());
            assertEquals("This is a string", o.getJn().textValue());
            // {"jn":123}
            json = "{\"jn\":123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.NUMBER, o.getJn().getNodeType());
            assertEquals(123, o.getJn().intValue());
            // {"jn":123.4567890123}
            json = "{\"jn\":123.4567890123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.NUMBER, o.getJn().getNodeType());
            assertEquals(123.4567890123, o.getJn().doubleValue());
            // {"jn":true}
            json = "{\"jn\":true}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.BOOLEAN, o.getJn().getNodeType());
            assertTrue(o.getJn().booleanValue());
            // {"jn":false}
            json = "{\"jn\":false}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn());
            assertEquals(JsonNodeType.BOOLEAN, o.getJn().getNodeType());
            assertFalse(o.getJn().booleanValue());
        } catch (Exception e) {
            fail(e);
        }
    }

    public static class TestObj {

        private OptionalInt i1 = OptionalInt.empty();
        private OptionalLong l1 = OptionalLong.empty();
        private OptionalDouble d1 = OptionalDouble.empty();

        private Any any;

        private JsonNode jn;
        private ObjectNode on;
        private ArrayNode an;

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

        public Any getAny() {
            return any;
        }

        public void setAny(Any any) {
            this.any = any;
        }

        public JsonNode getJn() {
            return jn;
        }

        public void setJn(JsonNode jn) {
            this.jn = jn;
        }

        public ObjectNode getOn() {
            return on;
        }

        public void setOn(ObjectNode on) {
            this.on = on;
        }

        public ArrayNode getAn() {
            return an;
        }

        public void setAn(ArrayNode an) {
            this.an = an;
        }

    }

    @Test
    public void testNonStringKey() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        assertEquals("{\"1\":1,\"2\":2,\"3\":3}", Fastjson2Library.getInstance().dumpsToString(map));
    }

    @Test
    public void testWriteJsoniterAny() {
        var object = new JSONObject();
        object.put("name", "testWriteJsoniterAny");
        var value = JsonIterator.deserialize("{\"id\":1,\"o\":[666,777,888]}");
        object.put("value", value);
        object.put("null", Any.wrapNull());
        assertEquals(
                "{\"name\":\"testWriteJsoniterAny\",\"value\":{\"id\":1,\"o\":[666,777,888]},\"null\":null}",
                Fastjson2Library.getInstance().dumpsToString(object)
        );
        assertArrayEquals(
                "{\"name\":\"testWriteJsoniterAny\",\"value\":{\"id\":1,\"o\":[666,777,888]},\"null\":null}".getBytes(StandardCharsets.UTF_8),
                Fastjson2Library.getInstance().dumpsToBytes(object)
        );
    }

    @Test
    public void testWriteJsonNode() {
        var object = new JSONObject();
        object.put("null", NullNode.getInstance());
        object.put("string", TextNode.valueOf("abc"));
        object.put("short", ShortNode.valueOf((short) 123));
        object.put("int", IntNode.valueOf(123456));
        object.put("long", LongNode.valueOf(1234567890123L));
        object.put("double", DoubleNode.valueOf(123.456));
        object.put("true", BooleanNode.TRUE);
        object.put("false", BooleanNode.FALSE);
        object.put("object", JsonNodeFactory.instance.objectNode().put("key", "value"));
        object.put("array", JsonNodeFactory.instance.arrayNode().add(0));

        assertEquals(
                """
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":true,"containerNode":false,"double":false,"empty":true,"float":false,"floatingPointNumber":false,"int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"BOOLEAN","null":false,"number":false,"object":false,"pojo":false,"short":false,"textual":false,"valueNode":true},"false":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":true,"containerNode":false,"double":false,"empty":true,"float":false,"floatingPointNumber":false,"int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"BOOLEAN","null":false,"number":false,"object":false,"pojo":false,"short":false,"textual":false,"valueNode":true},"object":{"key":"value"},"array":[0]}""",
                Fastjson2Library.getInstance().dumpsToString(object)
        );
        assertArrayEquals(
                """
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":true,"containerNode":false,"double":false,"empty":true,"float":false,"floatingPointNumber":false,"int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"BOOLEAN","null":false,"number":false,"object":false,"pojo":false,"short":false,"textual":false,"valueNode":true},"false":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":true,"containerNode":false,"double":false,"empty":true,"float":false,"floatingPointNumber":false,"int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"BOOLEAN","null":false,"number":false,"object":false,"pojo":false,"short":false,"textual":false,"valueNode":true},"object":{"key":"value"},"array":[0]}""".getBytes(StandardCharsets.UTF_8),
                Fastjson2Library.getInstance().dumpsToBytes(object)
        );
    }

}
