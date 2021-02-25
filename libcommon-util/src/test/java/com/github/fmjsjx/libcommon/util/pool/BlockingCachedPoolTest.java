package com.github.fmjsjx.libcommon.util.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BlockingCachedPoolTest {

    private BlockingCachedPool<String> pool;

    @BeforeEach
    public void setUp() {
        pool = new BlockingCachedPool<>(3);
        pool.deque.offerLast("a");
        pool.deque.offerLast("b");
    }

    @Test
    public void testTryTake() {
        try {
            var o = pool.tryTake();
            assertTrue(o.isPresent());
            assertEquals("b", o.get());
            o = pool.tryTake();
            assertTrue(o.isPresent());
            assertEquals("a", o.get());

            o = pool.tryTake();
            assertTrue(o.isEmpty());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testTryBack() {
        try {
            assertTrue(pool.tryBack("c"));
            assertFalse(pool.tryBack("d"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testTryRelease() {
        try {
            assertTrue(pool.tryRelease("b"));
            assertFalse(pool.tryRelease("b"));
            assertTrue(pool.tryRelease("a"));
            assertFalse(pool.tryRelease("a"));
        } catch (Exception e) {
            fail(e);
        }
    }

}
