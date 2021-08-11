package com.github.fmjsjx.libcommon.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntHashSetTest {

    IntHashSet set;

    @BeforeEach
    public void setUp() {
        set = new IntHashSet(1, 2, 3);
    }

    @Test
    public void testAddInteger() {
        assertFalse(set.add(Integer.valueOf(1)));
        assertFalse(set.add(Integer.valueOf(2)));
        assertFalse(set.add(Integer.valueOf(3)));
        assertTrue(set.add(Integer.valueOf(4)));
        assertTrue(set.add(Integer.valueOf(5)));
        assertTrue(set.add(Integer.valueOf(6)));

        assertEquals(6, set.size());

        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));
        assertTrue(set.contains(6));
        assertFalse(set.contains(0));

    }

    @Test
    public void testContains() {
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));
        assertFalse(set.contains(6));
        assertFalse(set.contains(0));
    }

    @Test
    public void testSize() {
        assertEquals(3, set.size());
    }

}
