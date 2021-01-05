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
