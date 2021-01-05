package com.github.fmjsjx.libcommon.collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class CollectorUtilTest {

    @Test
    public void testToLinkedHashMap() {
        var map = List.of("a", "b", "c").stream().collect(CollectorUtil.toLinkedHashMap(Function.identity()));
        assertEquals(LinkedHashMap.class, map.getClass());
        assertArrayEquals(new Object[] { "a", "b", "c" }, map.keySet().toArray());
        assertArrayEquals(new Object[] { "a", "b", "c" }, map.values().toArray());

        map = List.of("a", "b", "c").stream().collect(CollectorUtil.toLinkedHashMap("k:"::concat, "v:"::concat));
        assertEquals(LinkedHashMap.class, map.getClass());
        assertArrayEquals(new Object[] { "k:a", "k:b", "k:c" }, map.keySet().toArray());
        assertArrayEquals(new Object[] { "v:a", "v:b", "v:c" }, map.values().toArray());
    }

}
