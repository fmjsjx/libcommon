package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
        int value = RandomUtil.randomOne(10);
        assertEquals(10, value);
        for (int i = 0; i < TRY_COUNT; i++) {
            int v = RandomUtil.randomOne(10, 20, 30, 40, 50, 60, 70, 80, 90);
            if (!(v >= 10 && v <= 90 && v % 10 == 0)) {
                fail("Unexpected value " + v);
            }
        }
    }

    @Test
    public void testRandomOne_Llong() {
        long value = RandomUtil.randomOne(10L);
        assertEquals(10L, value);
        for (int i = 0; i < TRY_COUNT; i++) {
            long v = RandomUtil.randomOne(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L);
            if (!(v >= 10 && v <= 90 && v % 10 == 0)) {
                fail("Unexpected value " + v);
            }
        }
    }

    @Test
    public void testRandomOne_LObject() {
        assertEquals("a", RandomUtil.randomOne("a"));
        for (int i = 0; i < TRY_COUNT; i++) {
            var v = RandomUtil.randomOne("a", "b", "c", "d", "e", "f", "g");
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

}
