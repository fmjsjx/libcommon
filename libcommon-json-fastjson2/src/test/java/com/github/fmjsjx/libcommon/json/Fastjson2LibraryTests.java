package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import com.alibaba.fastjson2.JSONObject;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import org.junit.jupiter.api.Test;

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

            // {"l1":2,"d1":3.0}
            json = "{\"l1\":2,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());
            assertNull(o.getAny());

            // {"i1":null,"l1":2,"d1":3.0}
            json = "{\"i1\":null,\"l1\":2,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertTrue(o.getI1().isEmpty());
            assertEquals(2, o.getL1().getAsLong());
            assertEquals(3.0, o.getD1().getAsDouble());
            assertNull(o.getAny());

            // {"i1":1,"d1":3.0}
            json = "{\"i1\":1,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());
            assertNull(o.getAny());

            // {"i1":1,"l1":null,"d1":3.0}
            json = "{\"i1\":1,\"l1\":null,\"d1\":3.0}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertTrue(o.getL1().isEmpty());
            assertEquals(3.0, o.getD1().getAsDouble());
            assertNull(o.getAny());

            // {"i1":1,"l1":2}
            json = "{\"i1\":1,\"l1\":2}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());
            assertNull(o.getAny());

            // {"i1":1,"l1":2,"d1":null}
            json = "{\"i1\":1,\"l1\":2,\"d1\":null}";
            o = Fastjson2Library.getInstance().loads(json, TestObj.class);
            assertNotNull(o);
            assertEquals(1, o.getI1().getAsInt());
            assertEquals(2, o.getL1().getAsLong());
            assertTrue(o.getD1().isEmpty());
            assertNull(o.getAny());

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
        } catch (Exception e) {
            fail(e);
        }
    }

    public static class TestObj {

        private OptionalInt i1 = OptionalInt.empty();
        private OptionalLong l1 = OptionalLong.empty();
        private OptionalDouble d1 = OptionalDouble.empty();

        private Any any;

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

}
