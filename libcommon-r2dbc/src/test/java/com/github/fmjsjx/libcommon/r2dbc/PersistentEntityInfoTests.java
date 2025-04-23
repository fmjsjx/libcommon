package com.github.fmjsjx.libcommon.r2dbc;

import static com.github.fmjsjx.libcommon.r2dbc.PersistentColumnInfo.builder;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PersistentEntityInfoTests {

    @Test
    public void testConstructor() {
        Function<TestEntity, Object> idGetter = TestEntity::getId;
        Function<TestEntity, Object> valueGetter0 = TestEntity::getTest0;
        Function<TestEntity, Object> valueGetter1 = TestEntity::getTest1;
        var builderId = builder().columnName("id").valueGetter(idGetter).id(true);
        var builder0 = builder().columnName("test0").valueGetter(valueGetter0).readOnly(true);
        var builder1 = builder().columnName("test1").valueGetter(valueGetter1).insertOnly(true);
        var columnBuilders = new ArrayList<>(List.of(builderId, builder0, builder1));
        var info = new PersistentEntityInfo<>(TestEntity.class, columnBuilders);
        assertNotNull(info);
        assertEquals(TestEntity.class, info.getEntityClass());
        assertNotNull(info.getColumns());
        assertEquals(3, info.getColumns().size());
        assertEquals(2, info.getInsertColumns());
        assertEquals(1, info.getInsertColumnsWithoutId());
        var testEntity = new TestEntity();
        testEntity.setTest0("value0");
        testEntity.setTest1("value1");
        var columnId = info.getColumns().get(0);
        assertNotNull(columnId);
        assertEquals("id", columnId.getColumnName());
        assertEquals(info, columnId.getEntityInfo());
        assertEquals(TestEntity.class, columnId.getEntityClass());
        assertEquals(idGetter, columnId.getValueGetter());
        assertTrue(columnId.isId());
        assertFalse(columnId.isReadOnly());
        assertFalse(columnId.isInsertOnly());
        assertNull(columnId.getValueGetter().apply(testEntity));
        var column0 = info.getColumns().get(1);
        assertNotNull(column0);
        assertEquals("test0", column0.getColumnName());
        assertEquals(info, column0.getEntityInfo());
        assertEquals(TestEntity.class, column0.getEntityClass());
        assertEquals(valueGetter0, column0.getValueGetter());
        assertFalse(column0.isId());
        assertTrue(column0.isReadOnly());
        assertFalse(column0.isInsertOnly());
        assertEquals("value0", column0.getValueGetter().apply(testEntity));
        var column1 = info.getColumns().get(2);
        assertNotNull(column1);
        assertEquals("test1", column1.getColumnName());
        assertEquals(info, column1.getEntityInfo());
        assertEquals(TestEntity.class, column1.getEntityClass());
        assertEquals(valueGetter1, column1.getValueGetter());
        assertFalse(column1.isId());
        assertFalse(column1.isReadOnly());
        assertTrue(column1.isInsertOnly());
        assertEquals("value1", column1.getValueGetter().apply(testEntity));
    }

    static class TestEntity {

        @Id
        private Long id;
        @ReadOnlyProperty
        private String test0;
        @InsertOnlyProperty
        private String test1;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

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
