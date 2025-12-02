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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            // {"jn3":null,"on3":null,"an3":null}
            json = "{\"jn3\":null,\"on3\":null,\"an3\":null}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNull(o.getJn3());
            assertNull(o.getOn3());
            assertNull(o.getAn3());
            // {"jn3":{"key":"value"},"on3":{"key":"value"},"an3":[1,2,3]}
            json = "{\"jn3\":{\"key\":\"value\"},\"on3\":{\"key\":\"value\"},\"an3\":[1,2,3]}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.OBJECT, o.getJn3().getNodeType());
            assertEquals("{\"key\":\"value\"}", o.getJn3().toString());
            assertNotNull(o.getOn3());
            assertEquals("{\"key\":\"value\"}", o.getOn3().toString());
            assertNotNull(o.getAn3());
            assertEquals("[1,2,3]", o.getAn3().toString());
            // {"jn3":[1,2,3]}
            json = "{\"jn3\":[1,2,3]}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.ARRAY, o.getJn3().getNodeType());
            assertEquals("[1,2,3]", o.getJn3().toString());
            // {"jn3":"This is a string"}
            json = "{\"jn3\":\"This is a string\"}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.STRING, o.getJn3().getNodeType());
            assertEquals("This is a string", o.getJn3().stringValue());
            // {"jn3":123}
            json = "{\"jn3\":123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.NUMBER, o.getJn3().getNodeType());
            assertEquals(123, o.getJn3().intValue());
            // {"jn3":123.4567890123}
            json = "{\"jn3\":123.4567890123}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.NUMBER, o.getJn3().getNodeType());
            assertEquals(123.4567890123, o.getJn3().doubleValue());
            // {"jn3":true}
            json = "{\"jn3\":true}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.BOOLEAN, o.getJn3().getNodeType());
            assertTrue(o.getJn3().booleanValue());
            // {"jn3":false}
            json = "{\"jn3\":false}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertNotNull(o.getJn3());
            assertEquals(tools.jackson.databind.node.JsonNodeType.BOOLEAN, o.getJn3().getNodeType());
            assertFalse(o.getJn3().booleanValue());
        } catch (Exception e) {
            fail(e);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class TestObj {

        private OptionalInt i1 = OptionalInt.empty();
        private OptionalLong l1 = OptionalLong.empty();
        private OptionalDouble d1 = OptionalDouble.empty();

        private Any any;

        private JsonNode jn;
        private ObjectNode on;
        private ArrayNode an;

        private tools.jackson.databind.JsonNode jn3;
        private tools.jackson.databind.node.ObjectNode on3;
        private tools.jackson.databind.node.ArrayNode an3;

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

        public tools.jackson.databind.JsonNode getJn3() {
            return jn3;
        }

        public void setJn3(tools.jackson.databind.JsonNode jn3) {
            this.jn3 = jn3;
        }

        public tools.jackson.databind.node.ObjectNode getOn3() {
            return on3;
        }

        public void setOn3(tools.jackson.databind.node.ObjectNode on3) {
            this.on3 = on3;
        }

        public tools.jackson.databind.node.ArrayNode getAn3() {
            return an3;
        }

        public void setAn3(tools.jackson.databind.node.ArrayNode an3) {
            this.an3 = an3;
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
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":true,"false":false,"object":{"key":"value"},"array":[0]}""",
                Fastjson2Library.getInstance().dumpsToString(object)
        );
        assertArrayEquals(
                """
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":true,"false":false,"object":{"key":"value"},"array":[0]}""".getBytes(StandardCharsets.UTF_8),
                Fastjson2Library.getInstance().dumpsToBytes(object)
        );
    }

    @Test
    public void testWriteJackson3JsonNode() {
        var object = new JSONObject();
        object.put("null", tools.jackson.databind.node.NullNode.getInstance());
        object.put("string", tools.jackson.databind.node.StringNode.valueOf("abc"));
        object.put("short", tools.jackson.databind.node.ShortNode.valueOf((short) 123));
        object.put("int", tools.jackson.databind.node.IntNode.valueOf(123456));
        object.put("long", tools.jackson.databind.node.LongNode.valueOf(1234567890123L));
        object.put("double", tools.jackson.databind.node.DoubleNode.valueOf(123.456));
        object.put("true", tools.jackson.databind.node.BooleanNode.TRUE);
        object.put("false", tools.jackson.databind.node.BooleanNode.FALSE);
        object.put("object", tools.jackson.databind.node.JsonNodeFactory.instance.objectNode().put("key", "value"));
        object.put("array", tools.jackson.databind.node.JsonNodeFactory.instance.arrayNode().add(0));

        assertEquals(
                """
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":true,"false":false,"object":{"key":"value"},"array":[0]}""",
                Fastjson2Library.getInstance().dumpsToString(object)
        );
        assertArrayEquals(
                """
                        {"null":null,"string":"abc","short":123,"int":123456,"long":1234567890123,"double":123.456,"true":true,"false":false,"object":{"key":"value"},"array":[0]}""".getBytes(StandardCharsets.UTF_8),
                Fastjson2Library.getInstance().dumpsToBytes(object)
        );
    }

    public static class FieldLocalDate {
        private LocalDate field;

        public LocalDate getField() {
            return field;
        }

        public void setField(LocalDate field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "FieldLocalDate{" +
                    "field=" + getField() +
                    '}';
        }
    }

    @Test
    public void testLocalDate() {
        var obj = new FieldLocalDate();
        obj.setField(LocalDate.now());
        var json = "{\"field\":\"" + obj.getField().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\"}";
        assertNull(Fastjson2Library.getInstance().loads("{}", FieldLocalDate.class).field);
        assertNull(Fastjson2Library.getInstance().loads("{\"field\":null}", FieldLocalDate.class).field);
        assertEquals(obj.getField(), Fastjson2Library.getInstance().loads(json, FieldLocalDate.class).field);
        assertEquals(json, Fastjson2Library.getInstance().dumpsToString(obj));
        assertEquals("{}", Fastjson2Library.getInstance().dumpsToString(new FieldLocalDate()));
        assertEquals(obj.getField(), JsoniterLibrary.getInstance().loads(json, FieldLocalDate.class).field);
    }

    public static class FieldLocalDateTime {
        private LocalDateTime field;

        public LocalDateTime getField() {
            return field;
        }

        public void setField(LocalDateTime field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "FieldLocalDateTime{" +
                    "field=" + getField() +
                    '}';
        }
    }

    @Test
    public void testLocalDateTime() {
        var obj = new FieldLocalDateTime();
        obj.setField(LocalDateTime.now());
        var json = "{\"field\":\"" + obj.getField().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"}";
        assertNull(Fastjson2Library.getInstance().loads("{}", FieldLocalDateTime.class).field);
        assertNull(Fastjson2Library.getInstance().loads("{\"field\":null}", FieldLocalDateTime.class).field);
        assertEquals(obj.getField(), Fastjson2Library.getInstance().loads(json, FieldLocalDateTime.class).field);
        assertEquals(json, Fastjson2Library.getInstance().dumpsToString(obj));
        assertEquals("{}", Fastjson2Library.getInstance().dumpsToString(new FieldLocalDateTime()));
        assertEquals(obj.getField(), JsoniterLibrary.getInstance().loads(json, FieldLocalDateTime.class).field);
    }

    public static class FieldOffsetDateTime {
        private OffsetDateTime field;

        public OffsetDateTime getField() {
            return field;
        }

        public void setField(OffsetDateTime field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "FieldOffsetDateTime{" +
                    "field=" + getField() +
                    '}';
        }
    }

    @Test
    public void testOffsetDateTime() {
        var obj = new FieldOffsetDateTime();
        obj.setField(OffsetDateTime.now());
        var json = "{\"field\":\"" + obj.getField().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "\"}";
        assertNull(Fastjson2Library.getInstance().loads("{}", FieldOffsetDateTime.class).field);
        assertNull(Fastjson2Library.getInstance().loads("{\"field\":null}", FieldOffsetDateTime.class).field);
        assertEquals(obj.getField(), Fastjson2Library.getInstance().loads(json, FieldOffsetDateTime.class).field);
        assertEquals(json, Fastjson2Library.getInstance().dumpsToString(obj));
        assertEquals("{}", Fastjson2Library.getInstance().dumpsToString(new FieldOffsetDateTime()));
        assertEquals(obj.getField(), JsoniterLibrary.getInstance().loads(json, FieldOffsetDateTime.class).field);
    }

    public static class FieldZonedDateTime {
        private ZonedDateTime field;

        public ZonedDateTime getField() {
            return field;
        }

        public void setField(ZonedDateTime field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return "FieldZonedDateTime{" +
                    "field=" + getField() +
                    '}';
        }
    }

    @Test
    public void testZonedDateTime() {
        var obj = new FieldZonedDateTime();
        obj.setField(ZonedDateTime.now());
        var json = "{\"field\":\"" + obj.getField().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + "\"}";
        assertNull(Fastjson2Library.getInstance().loads("{}", FieldZonedDateTime.class).field);
        assertNull(Fastjson2Library.getInstance().loads("{\"field\":null}", FieldZonedDateTime.class).field);
        assertEquals(obj.getField(), Fastjson2Library.getInstance().loads(json, FieldZonedDateTime.class).field);
        assertEquals(json, Fastjson2Library.getInstance().dumpsToString(obj));
        assertEquals("{}", Fastjson2Library.getInstance().dumpsToString(new FieldZonedDateTime()));
        assertEquals(obj.getField(), JsoniterLibrary.getInstance().loads(json, FieldZonedDateTime.class).field);
    }

}
