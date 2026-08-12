package com.github.fmjsjx.libcommon.collection;

import com.github.fmjsjx.libcommon.collection.ImmutableCollections.ListSetN;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListSetTest {

    @Test
    public void testOf() {
        var ls = ListSet.of();
        assertNotNull(ls);
        assertEquals(ls, ImmutableCollections.emptyListSet());
        assertTrue(ls.isEmpty());
        assertArrayEquals(new Object[0], ls.internalList().toArray());

        ls = ListSet.of("a", "b", "c");
        assertNotNull(ls);
        assertInstanceOf(ListSetN.class, ls);
        assertEquals(3, ls.size());
        assertArrayEquals(new Object[] { "a", "b", "c" }, ls.internalList().toArray());

        ls = ListSet.of("a", "b", "a");
        assertNotNull(ls);
        assertInstanceOf(ListSetN.class, ls);
        assertEquals(2, ls.size());
        assertArrayEquals(new Object[] { "a", "b" }, ls.internalList().toArray());
    }

    @Test
    public void testCopyOf() {
        var ls = ListSet.copyOf(List.of());
        assertNotNull(ls);
        assertEquals(ls, ImmutableCollections.emptyListSet());
        assertTrue(ls.isEmpty());
        assertArrayEquals(new Object[0], ls.internalList().toArray());

        ls = ListSet.copyOf(List.of("a", "b", "c"));
        assertNotNull(ls);
        assertInstanceOf(ListSetN.class, ls);
        assertEquals(3, ls.size());
        assertArrayEquals(new Object[] { "a", "b", "c" }, ls.internalList().toArray());

        ls = ListSet.copyOf(List.of("a", "b", "a"));
        assertNotNull(ls);
        assertInstanceOf(ListSetN.class, ls);
        assertEquals(2, ls.size());
        assertArrayEquals(new Object[] { "a", "b" }, ls.internalList().toArray());

        var ls2 = ListSet.copyOf(ls);
        assertNotNull(ls2);
        assertEquals(ls, ls2);
    }

}
