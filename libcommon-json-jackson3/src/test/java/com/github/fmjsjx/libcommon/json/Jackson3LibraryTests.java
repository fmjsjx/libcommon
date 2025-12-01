package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.*;

import com.jsoniter.JsonIterator;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.type.TypeFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit tests for {@link Jackson3Library}.
 */
public class Jackson3LibraryTests {

    private static final TypeFactory typeFactory = TypeFactory.createDefaultInstance();

    @Test
    public void testToJavaType() {
        var jt = Jackson3Library.getInstance().toJavaType(String.class);
        assertEquals(typeFactory.constructType(String.class), jt);
        var stringListTypeRef = new TypeReference<List<String>>() {
        };
        var jt2 = Jackson3Library.getInstance().toJavaType(stringListTypeRef.getType());
        assertEquals(typeFactory.constructCollectionType(List.class, String.class), jt2);
    }

    @Test
    public void testJavaTimeModule() {
        var json = """
                {"time":"2023-06-09T11:22:33"}""";
        var data = Jackson3Library.getInstance().loads(json, TestJavaTimeModule.class);
        assertEquals(LocalDateTime.of(2023, 6, 9, 11, 22, 33), data.getTime());
    }

    public static class TestJavaTimeModule {

        private LocalDateTime time;

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "TestJavaTimeModule{time=" + time + "}";
        }
    }

    @Test
    public void testJsoniterModule() {
        var value = JsonIterator.deserialize("""
                {"id":1,"o":[666,777,888]}""");
        var obj = Jackson3Library.getInstance().createObjectNode()
                .put("name", "testJsoniterModule")
                .putPOJO("value", value);
        assertEquals(
                "{\"name\":\"testJsoniterModule\",\"value\":{\"id\":1,\"o\":[666,777,888]}}",
                Jackson3Library.getInstance().dumpsToString(obj)
        );
    }

}
