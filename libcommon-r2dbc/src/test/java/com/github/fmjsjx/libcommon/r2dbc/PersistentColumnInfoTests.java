package com.github.fmjsjx.libcommon.r2dbc;

import static com.github.fmjsjx.libcommon.r2dbc.PersistentColumnInfo.builder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class PersistentColumnInfoTests {

    @Test
    public void testBuilder() {
        var builder = builder();
        assertNotNull(builder);
        try {
            builder.build();
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("entityInfo must not be null", e.getMessage());
        }
        PersistentEntityInfo<?> mockEntityInfo = mock();
        builder.entityInfo(mockEntityInfo);
        try {
            builder.build();
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("columnName must not be null", e.getMessage());
        }
        builder.columnName("test");
        try {
            builder.build();
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("valueGetter must not be null", e.getMessage());
        }
        Function<?, Object> mockValueGetter = mock();
        builder.valueGetter(mockValueGetter);
        var info = builder.build();
        assertNotNull(info);
        assertEquals(mockEntityInfo, info.getEntityInfo());
        assertEquals("test", info.getColumnName());
        assertEquals(mockValueGetter, info.getValueGetter());
        assertFalse(info.isId());
        assertFalse(info.isReadOnly());
        assertFalse(info.isInsertOnly());
        info = builder.id(true).readOnly(true).insertOnly(true).build();
        assertNotNull(info);
        assertEquals(mockEntityInfo, info.getEntityInfo());
        assertEquals("test", info.getColumnName());
        assertEquals(mockValueGetter, info.getValueGetter());
        assertTrue(info.isId());
        assertTrue(info.isReadOnly());
        assertTrue(info.isInsertOnly());
    }

}
