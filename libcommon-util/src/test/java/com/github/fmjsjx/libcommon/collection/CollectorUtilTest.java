package com.github.fmjsjx.libcommon.collection;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorUtilTest {

    @Test
    public void testToLinkedHashMap() {
        var map = Stream.of("a", "b", "c").collect(CollectorUtil.toLinkedHashMap(Function.identity()));
        assertEquals(LinkedHashMap.class, map.getClass());
        assertArrayEquals(new Object[] { "a", "b", "c" }, map.keySet().toArray());
        assertArrayEquals(new Object[] { "a", "b", "c" }, map.values().toArray());

        map = Stream.of("a", "b", "c").collect(CollectorUtil.toLinkedHashMap("k:"::concat, "v:"::concat));
        assertEquals(LinkedHashMap.class, map.getClass());
        assertArrayEquals(new Object[] { "k:a", "k:b", "k:c" }, map.keySet().toArray());
        assertArrayEquals(new Object[] { "v:a", "v:b", "v:c" }, map.values().toArray());
    }

}
