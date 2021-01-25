package com.github.fmjsjx.libcommon.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class Jackson2YamlLibraryTest {

    @Test
    public void testToJavaType() {
        Type type = String.class;
        var jt = Jackson2YamlLibrary.toJavaType(type);
        assertEquals(TypeFactory.defaultInstance().constructType(String.class), jt);
        TypeReference<List<String>> stringListTypeRef = new TypeReference<List<String>>() {
        };
        jt = Jackson2YamlLibrary.toJavaType(stringListTypeRef.getType());
        assertEquals(TypeFactory.defaultInstance().constructCollectionLikeType(List.class, String.class), jt);
    }

}
