package com.github.fmjsjx.libcommon.r2dbc;

import static com.github.fmjsjx.libcommon.r2dbc.PersistentColumnInfo.builder;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PersistentEntityInfoTests {

    @Test
    public void testConstructor() {
        Function<TestEntity, Object> valueGetter0 = TestEntity::getTest0;
        Function<TestEntity, Object> valueGetter1 = TestEntity::getTest1;
        var builder0 = builder().columnName("test0").valueGetter(valueGetter0);
        var builder1 = builder().columnName("test1").valueGetter(valueGetter1).insertOnly(true);
        var columnBuilders = new ArrayList<>(List.of(builder0, builder1));
        var info = new PersistentEntityInfo<>(TestEntity.class, columnBuilders);
        assertNotNull(info);
        assertEquals(TestEntity.class, info.getEntityClass());
        assertNotNull(info.getColumns());
        assertEquals(2, info.getColumns().size());
        var testEntity = new TestEntity();
        testEntity.setTest0("value0");
        testEntity.setTest1("value1");
        var column0 = info.getColumns().get(0);
        assertNotNull(column0);
        assertEquals("test0", column0.getColumnName());
        assertEquals(info, column0.getEntityInfo());
        assertEquals(TestEntity.class, column0.getEntityClass());
        assertEquals(valueGetter0, column0.getValueGetter());
        assertFalse(column0.isInsertOnly());
        assertEquals("value0", column0.getValueGetter().apply(testEntity));
        var column1 = info.getColumns().get(1);
        assertNotNull(column1);
        assertEquals("test1", column1.getColumnName());
        assertEquals(info, column1.getEntityInfo());
        assertEquals(TestEntity.class, column1.getEntityClass());
        assertEquals(valueGetter1, column1.getValueGetter());
        assertTrue(column1.isInsertOnly());
        assertEquals("value1", column1.getValueGetter().apply(testEntity));
    }

    static class TestEntity {

        private String test0;
        private String test1;

        public String getTest0() {
            return test0;
        }

        public void setTest0(String test0) {
            this.test0 = test0;
        }

        public String getTest1() {
            return test1;
        }

        public void setTest1(String test1) {
            this.test1 = test1;
        }

    }

}
