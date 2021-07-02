package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class RandomUtilTest {

    private static final int TRY_COUNT = 100_000;

    @Test
    public void testRandomInt() {
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomInt(123);
            assertTrue(v < 123 && v >= 0);
        }
    }

    @Test
    public void testRandomInRange() {
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomInRange(100, 200);
            assertTrue(v >= 100 && v <= 200);
        }
    }

    @Test
    public void testRandomOne_Lint() {
        assertEquals(10, RandomUtil.randomOne(new int[] { 10 }));
        for (int i = 0; i < TRY_COUNT; i++) {
            int v = RandomUtil.randomOne(new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90 });
            if (!(v >= 10 && v <= 90 && v % 10 == 0)) {
                fail("Unexpected value " + v);
            }
        }
    }

    @Test
    public void testRandomOne_Llong() {
        assertEquals(10L, RandomUtil.randomOne(new long[] { 10L }));
        for (int i = 0; i < TRY_COUNT; i++) {
            long v = RandomUtil.randomOne(new long[] { 10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L });
            if (!(v >= 10 && v <= 90 && v % 10 == 0)) {
                fail("Unexpected value " + v);
            }
        }
    }

    @Test
    public void testRandomOne_LObject() {
        assertEquals("a", RandomUtil.randomOne(new String[] { "a" }));
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomOne(new String[] { "a", "b", "c", "d", "e", "f", "g" });
            switch (v) {
            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
                break;
            default:
                fail("Unexpected value " + v);
                break;
            }
        }
    }

    @Test
    public void testRandomOne_List() {
        assertEquals("a", RandomUtil.randomOne(List.of("a")));
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomOne(List.of("a", "b", "c", "d", "e", "f", "g"));
            switch (v) {
            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
                break;
            default:
                fail("Unexpected value " + v);
                break;
            }
        }
    }

    @Test
    public void testRandomOne_Collection() {
        assertEquals("a", RandomUtil.randomOne(Set.of("a")));
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomOne(Set.of("a", "b", "c", "d", "e", "f", "g"));
            switch (v) {
            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
                break;
            default:
                fail("Unexpected value " + v);
                break;
            }
        }
    }

    @Test
    public void testRandomIndex_Lint() {
        var weights = new int[] { 10, 10, 10, 10, 10 };
        for (int i = 0; i < TRY_COUNT; i++) {
            var index = RandomUtil.randomIndex(weights);
            assertTrue(index >= 0 && index < 5);
        }
    }

    @Test
    public void testRandomN_List_int() {
        var list = List.of("a", "b", "c", "d", "e", "f", "g");
        var out = RandomUtil.randomN(list, 3);
        assertNotNull(out);
        assertEquals(3, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(3, Set.copyOf(out).size());

        out = RandomUtil.randomN(list, 5);
        assertNotNull(out);
        assertEquals(5, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(5, Set.copyOf(out).size());
    }

    @Test
    public void testRandomN_List_int_List() {
        var list = List.of("a", "b", "c", "d", "e", "f", "g");
        var out = new ArrayList<String>();
        RandomUtil.randomN(list, 3, out);
        assertNotNull(out);
        assertEquals(3, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(3, Set.copyOf(out).size());

        out = new ArrayList<String>();
        RandomUtil.randomN(list, 5, out);
        assertNotNull(out);
        assertEquals(5, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(5, Set.copyOf(out).size());
    }

    @Test
    public void testRandomN_List_int_List_boolean() {
        var list = List.of("a", "b", "c", "d", "e", "f", "g");
        var out = new ArrayList<String>();
        RandomUtil.randomN(list, 3, out, true);
        assertNotNull(out);
        assertEquals(3, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(3, Set.copyOf(out).size());

        out = new ArrayList<String>();
        RandomUtil.randomN(list, 5, out, false);
        assertNotNull(out);
        assertEquals(5, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(5, Set.copyOf(out).size());

        var list2 = new ArrayList<>(list);
        out = new ArrayList<String>();
        RandomUtil.randomN(list2, 3, out, false);
        assertNotNull(out);
        assertEquals(3, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(3, Set.copyOf(out).size());
        assertTrue(list2.size() < 7);
        assertEquals(4, list2.size());

        list2 = new ArrayList<>(list);
        out = new ArrayList<String>();
        RandomUtil.randomN(list2, 5, out, false);
        assertNotNull(out);
        assertEquals(5, out.size());
        assertAll(out.stream().map(v -> () -> assertTrue(list.contains(v))));
        assertEquals(5, Set.copyOf(out).size());
        assertTrue(list2.size() < 7);
        assertEquals(5, list2.size());
    }

}
