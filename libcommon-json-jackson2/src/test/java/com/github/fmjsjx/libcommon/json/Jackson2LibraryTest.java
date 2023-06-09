package com.github.fmjsjx.libcommon.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class Jackson2LibraryTest {

    @Test
    public void testToJavaType() {
        Type type = String.class;
        var jt = Jackson2Library.toJavaType(type);
        assertEquals(TypeFactory.defaultInstance().constructType(String.class), jt);
        TypeReference<List<String>> stringListTypeRef = new TypeReference<List<String>>() {
        };
        jt = Jackson2Library.toJavaType(stringListTypeRef.getType());
        assertEquals(TypeFactory.defaultInstance().constructCollectionLikeType(List.class, String.class), jt);
    }

    @Test
    public void testJavaTimeModule() {
        var json = """
                {"time":"2023-06-09T11:22:33"}""";
        var data = Jackson2Library.getInstance().loads(json, TestJavaTimeModule.class);
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

}
