package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.runtime.TypeDefinition;

public class DsljsonLibraryTest {

    private static final TypeDefinition<List<TestObj>> typeDefinition = new TypeDefinition<>() {
    };

    @Test
    @SuppressWarnings("unchecked")
    public void testLoads() {
        var json = "{\"id\":123,\"name\":\"HaHa\",\"mails\":[\"test@test.test\"]}";
        var obj = DsljsonLibrary.getInstance().loads(json, TestObj.class);
        assertNotNull(obj);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        obj = DsljsonLibrary.getInstance().loads(new ByteArrayInputStream(json.getBytes()), TestObj.class);
        assertNotNull(obj);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        var jsonList = "[{\"id\":123,\"name\":\"HaHa\",\"mails\":[\"test@test.test\"]}]";
        List<TestObj> list = DsljsonLibrary.getInstance().loads(jsonList, typeDefinition.type);
        assertNotNull(list);
        assertEquals(1, list.size());
        obj = list.get(0);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        list = DsljsonLibrary.getInstance().loads(new ByteArrayInputStream(jsonList.getBytes()), typeDefinition.type);
        assertNotNull(list);
        assertEquals(1, list.size());
        obj = list.get(0);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        list = DsljsonLibrary.getInstance().loads(jsonList, typeDefinition);
        assertNotNull(list);
        assertEquals(1, list.size());
        obj = list.get(0);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        list = DsljsonLibrary.getInstance().loads(new ByteArrayInputStream(jsonList.getBytes()), typeDefinition);
        assertNotNull(list);
        assertEquals(1, list.size());
        obj = list.get(0);
        assertEquals(123, obj.id);
        assertEquals("HaHa", obj.name);
        assertNotNull(obj.mails);
        assertEquals(1, obj.mails.size());
        assertEquals("test@test.test", obj.mails.get(0));

        var map = DsljsonLibrary.getInstance().loads(json);
        assertNotNull(map);
        assertEquals(123L, map.get("id"));
        assertEquals("HaHa", map.get("name"));

        List<String> mails = (List<String>) map.get("mails");
        assertEquals(1, mails.size());
        assertEquals("test@test.test", mails.get(0));

        map = DsljsonLibrary.getInstance().loads(new ByteArrayInputStream(json.getBytes()));
        assertNotNull(map);
        assertEquals(123L, map.get("id"));
        assertEquals("HaHa", map.get("name"));

        mails = (List<String>) map.get("mails");
        assertEquals(1, mails.size());
        assertEquals("test@test.test", mails.get(0));
    }

    @Test
    public void testDumpsToString() {
        var obj = new TestObj();
        obj.setId(123);
        obj.setName("test");
        obj.setMails(Collections.emptyList());

        var json = DsljsonLibrary.getInstance().dumpsToString(obj);
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"id\":123"));
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"mails\":[]"));

        obj.setMails(List.of("t@t.t", "o@o.o"));
        json = DsljsonLibrary.getInstance().dumpsToString(obj);
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"id\":123"));
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"mails\":[\"t@t.t\",\"o@o.o\"]"));

        var list = List.of(obj);
        json = DsljsonLibrary.getInstance().dumpsToString(list);
        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        json = json.substring(1, json.length() - 1);
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));
        assertTrue(json.contains("\"id\":123"));
        assertTrue(json.contains("\"name\":\"test\""));
        assertTrue(json.contains("\"mails\":[\"t@t.t\",\"o@o.o\"]"));

    }

    public static final class TestObj {

        @JsonAttribute
        private int id;
        @JsonAttribute
        private String name;
        @JsonAttribute
        private List<String> mails;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getMails() {
            return mails;
        }

        public void setMails(List<String> mails) {
            this.mails = mails;
        }

        @Override
        public String toString() {
            return "TestObj(id" + id + ", name=" + name + ", mails=" + mails + ")";
        }

    }

}
