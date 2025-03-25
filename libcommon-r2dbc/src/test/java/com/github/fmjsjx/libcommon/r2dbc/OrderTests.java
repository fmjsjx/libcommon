package com.github.fmjsjx.libcommon.r2dbc;

import org.junit.jupiter.api.Test;

import static com.github.fmjsjx.libcommon.r2dbc.Sort.ASC;
import static com.github.fmjsjx.libcommon.r2dbc.Sort.DESC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTests {

    @Test
    public void testToOrderString() {
        assertEquals("id", new Order("id", null).toOrderString());
        assertEquals("id ASC", new Order("id", ASC).toOrderString());
        assertEquals("id DESC", new Order("id", DESC).toOrderString());
        assertEquals("create_time DESC", new Order("create_time", DESC).toOrderString());
    }

}
