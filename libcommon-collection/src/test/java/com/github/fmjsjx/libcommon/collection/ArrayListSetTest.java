package com.github.fmjsjx.libcommon.collection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ArrayListSetTest {

    @Test
    public void testContains() {
        var ls = new ArrayListSet<String>("a", "b", "c");
        assertTrue(ls.contains("a"));
        assertTrue(ls.contains("b"));
        assertTrue(ls.contains("c"));
        assertFalse(ls.contains("d"));
        assertFalse(ls.contains("aa"));
        assertFalse(ls.contains("bb"));
        assertFalse(ls.contains("cc"));
    }

    @Test
    public void testSize() {
        var ls = new ArrayListSet<String>("a", "b", "c");
        assertEquals(3, ls.size());

        ls = new ArrayListSet<String>("a", "b");
        assertEquals(2, ls.size());

        ls = new ArrayListSet<String>();
        assertEquals(0, ls.size());
    }

    @Test
    public void testAdd() {
        var ls = new ArrayListSet<>();
        assertEquals(0, ls.size());
        assertTrue(ls.add("a"));
        assertEquals(1, ls.size());
        assertTrue(ls.contains("a"));

        assertFalse(ls.add("a"));
        assertEquals(1, ls.size());
        assertTrue(ls.contains("a"));

        assertTrue(ls.add("b"));
        assertEquals(2, ls.size());
        assertTrue(ls.contains("b"));

        assertFalse(ls.add("b"));
        assertEquals(2, ls.size());
        assertTrue(ls.contains("a"));
    }

    @Test
    public void testRemove() {
        var ls = new ArrayListSet<String>("a", "b", "c");
        assertFalse(ls.remove("d"));
        assertEquals(3, ls.size());

        assertTrue(ls.remove("a"));
        assertEquals(2, ls.size());
        assertFalse(ls.remove("a"));
        assertEquals(2, ls.size());

        assertTrue(ls.remove("b"));
        assertEquals(1, ls.size());
        assertFalse(ls.remove("b"));
        assertEquals(1, ls.size());

        assertTrue(ls.remove("c"));
        assertEquals(0, ls.size());
        assertFalse(ls.remove("c"));
        assertEquals(0, ls.size());

        assertFalse(ls.remove("d"));
    }

    @Test
    public void testClear() {
        var ls = new ArrayListSet<String>("a", "b", "c");
        assertEquals(3, ls.size());

        ls.clear();
        assertEquals(0, ls.size());
        ls.clear();
        assertEquals(0, ls.size());
    }

}
