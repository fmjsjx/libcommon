package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

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

}
