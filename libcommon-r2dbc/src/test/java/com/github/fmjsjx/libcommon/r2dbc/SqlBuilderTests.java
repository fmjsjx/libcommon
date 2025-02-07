package com.github.fmjsjx.libcommon.r2dbc;

import org.junit.jupiter.api.Test;

import static com.github.fmjsjx.libcommon.r2dbc.ParameterStyle.MYSQL;
import static com.github.fmjsjx.libcommon.r2dbc.ParameterStyle.NONE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilderTests {

    @SuppressWarnings("unchecked")
    private static <R> R getFieldValue(Object object, String fieldName) {
        try {
            var field = SqlBuilder.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (R) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static List<String> sqlParts(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "sqlParts");
    }

    private static List<Object> values(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "values");
    }

    private static SqlBuilder parent(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "parent");
    }

    public static int mode(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "mode");
    }

    public static ParameterStyle parameterStyle(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "parameterStyle");
    }

    public static List<String> selectColumns(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "selectColumns");
    }

    public static boolean distinct(SqlBuilder sqlBuilder) {
        return getFieldValue(sqlBuilder, "distinct");
    }

    @Test
    public void testConstructors() {
        var sqlBuilder = new SqlBuilder();
        assertNotNull(sqlBuilder);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(0, sqlParts(sqlBuilder).size());
        assertNotNull(values(sqlBuilder));
        assertEquals(0, values(sqlBuilder).size());
        assertNull(parent(sqlBuilder));
        assertEquals(0, mode(sqlBuilder));
        assertEquals(NONE, parameterStyle(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
        assertFalse(distinct(sqlBuilder));

        var parent = sqlBuilder;
        sqlBuilder = new SqlBuilder(parent);
        assertNotNull(sqlBuilder);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(0, sqlParts(sqlBuilder).size());
        assertNotNull(values(sqlBuilder));
        assertEquals(0, values(sqlBuilder).size());
        assertEquals(parent, parent(sqlBuilder));
        assertEquals(1, mode(sqlBuilder));
        assertEquals(NONE, parameterStyle(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
        assertFalse(distinct(sqlBuilder));

        sqlBuilder = new SqlBuilder(parent, 2);
        assertNotNull(sqlBuilder);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(0, sqlParts(sqlBuilder).size());
        assertNotNull(values(sqlBuilder));
        assertEquals(0, values(sqlBuilder).size());
        assertEquals(parent, parent(sqlBuilder));
        assertEquals(2, mode(sqlBuilder));
        assertEquals(NONE, parameterStyle(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
        assertFalse(distinct(sqlBuilder));

        sqlBuilder = new SqlBuilder(new ArrayList<>(), new ArrayList<>(), parent, 3);
        assertNotNull(sqlBuilder);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(0, sqlParts(sqlBuilder).size());
        assertNotNull(values(sqlBuilder));
        assertEquals(0, values(sqlBuilder).size());
        assertEquals(parent, parent(sqlBuilder));
        assertEquals(3, mode(sqlBuilder));
        assertEquals(NONE, parameterStyle(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
        assertFalse(distinct(sqlBuilder));

        try {
            new SqlBuilder(null, 1);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in subquery mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, 2);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in group mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, 3);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in where clause mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, 4);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in set clause mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, 5);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in <5> mode", e.getMessage());
        }
    }

    @Test
    public void testParameterStyle() {
        var sqlBuilder = new SqlBuilder().parameterStyle(MYSQL);
        assertEquals(MYSQL, parameterStyle(sqlBuilder));
        assertEquals(MYSQL, sqlBuilder.getParameterStyle());
        try {
            new SqlBuilder().parameterStyle(null);
            fail("Should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("parameterStyle must not be null", e.getMessage());
        }
    }

    @Test
    public void testGetSqlParts() {
        var sqlBuilder = new SqlBuilder();
        assertNotNull(sqlBuilder.getSqlParts());
        assertEquals(0, sqlBuilder.getSqlParts().size());
        assertNotSame(sqlParts(sqlBuilder), sqlBuilder.getSqlParts());
    }

    @Test
    public void testGetValues() {
        var sqlBuilder = new SqlBuilder();
        assertNotNull(sqlBuilder.getValues());
        assertEquals(0, sqlBuilder.getValues().size());
        assertNotSame(values(sqlBuilder), sqlBuilder.getValues());
    }

    @Test
    public void testAppendSql() {
        var sqlBuilder = new SqlBuilder().appendSql("SELECT");
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        try {
            new SqlBuilder().appendSql((String) null);
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

}
