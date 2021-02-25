package com.github.fmjsjx.libcommon.util.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutoGenerationCachedPoolTest {

    private AutoGenerationCachedPool<String> pool;

    @BeforeEach
    public void setUp() {
        pool = new AutoGenerationCachedPool<>(new ConcurrentCachedPool<>(2), () -> "g");
    }

    @Test
    public void testTake() {
        try {
            for (int i = 0; i < 100; i++) {
                var o = pool.take();
                assertEquals("g", o);
            }
        } catch (Exception e) {
            fail(e);
        }
    }

}
