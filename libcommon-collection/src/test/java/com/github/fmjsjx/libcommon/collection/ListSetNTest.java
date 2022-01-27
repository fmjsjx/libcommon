package com.github.fmjsjx.libcommon.collection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ListSetNTest {

    @Test
    public void testContains() {
        var ls = new ImmutableCollections.ListSetN<>("a", "b", "c");
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
        var ls = new ImmutableCollections.ListSetN<>("a", "b", "c");
        assertEquals(3, ls.size());

        ls = new ImmutableCollections.ListSetN<>("a", "b");
        assertEquals(2, ls.size());

        ls = new ImmutableCollections.ListSetN<>();
        assertEquals(0, ls.size());
    }

    @Test
    public void testImmutable() {
        var ls = new ImmutableCollections.ListSetN<>("a", "b", "c");
        try {
            ls.add("a");
            fail();
        } catch (Exception e) {
            // OK
        }
        try {
            ls.remove("a");
            fail();
        } catch (Exception e) {
            // OK
        }
        try {
            ls.clear();
            fail();
        } catch (Exception e) {
            // OK
        }
        try {
            ls.internalList().remove(0);
            fail();
        } catch (Exception e) {
            // OK
        }
        try {
            ls.internalList().add("d");
            fail();
        } catch (Exception e) {
            // OK
        }
    }

}
