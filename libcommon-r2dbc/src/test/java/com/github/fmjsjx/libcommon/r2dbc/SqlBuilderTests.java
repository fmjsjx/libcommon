package com.github.fmjsjx.libcommon.r2dbc;

import io.r2dbc.spi.Parameters;
import org.junit.jupiter.api.Test;

import static com.github.fmjsjx.libcommon.r2dbc.ParameterStyle.MYSQL;
import static com.github.fmjsjx.libcommon.r2dbc.ParameterStyle.NONE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void testAppendSql_String() {
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

    @Test
    public void testAppendSql_StringArray() {
        var sqlBuilder = new SqlBuilder().appendSql("SELECT", "'x'");
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(2, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        assertEquals("'x'", sqlParts(sqlBuilder).get(1));
        try {
            new SqlBuilder().appendSql("SELECT", null);
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

    @Test
    public void testAppendSql_StringList() {
        var sqlBuilder = new SqlBuilder().appendSql(List.of("SELECT", "'x'"));
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(2, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        assertEquals("'x'", sqlParts(sqlBuilder).get(1));
        try {
            new SqlBuilder().appendSql(Arrays.asList("SELECT", null));
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

    @Test
    public void testS_String() {
        var sqlBuilder = new SqlBuilder().s("SELECT");
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        try {
            new SqlBuilder().s((String) null);
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

    @Test
    public void testS_StringArray() {
        var sqlBuilder = new SqlBuilder().s("SELECT", "'x'");
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(2, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        assertEquals("'x'", sqlParts(sqlBuilder).get(1));
        try {
            new SqlBuilder().s("SELECT", null);
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

    @Test
    public void testS_StringList() {
        var sqlBuilder = new SqlBuilder().s(List.of("SELECT", "'x'"));
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(2, sqlParts(sqlBuilder).size());
        assertEquals("SELECT", sqlParts(sqlBuilder).get(0));
        assertEquals("'x'", sqlParts(sqlBuilder).get(1));
        try {
            new SqlBuilder().s(Arrays.asList("SELECT", null));
            fail("should throw NullPointerException");
        } catch (Exception e) {
            assertEquals("sqlPart must not be null", e.getMessage());
        }
    }

    @Test
    public void testAddValue() {
        var sqlBuilder = new SqlBuilder().addValue("a");
        assertNotNull(values(sqlBuilder));
        assertEquals(1, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        sqlBuilder.addValue(null);
        assertNotNull(values(sqlBuilder));
        assertEquals(2, values(sqlBuilder).size());
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        sqlBuilder.addValues(String.class);
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAddValues_ObjectArray() {
        var sqlBuilder = new SqlBuilder().addValues("a", null, String.class);
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAddValues_ObjectList() {
        var sqlBuilder = new SqlBuilder().addValues(Arrays.asList("a", null, String.class));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }


    @Test
    public void testV_Object() {
        var sqlBuilder = new SqlBuilder().v("a");
        assertNotNull(values(sqlBuilder));
        assertEquals(1, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        sqlBuilder.v((Object) null);
        assertNotNull(values(sqlBuilder));
        assertEquals(2, values(sqlBuilder).size());
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        sqlBuilder.v(String.class);
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testV_ObjectArray() {
        var sqlBuilder = new SqlBuilder().v("a", null, String.class);
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testV_ObjectList() {
        var sqlBuilder = new SqlBuilder().v(Arrays.asList("a", null, String.class));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAppendSqlValues_String_ObjectArray() {
        var sqlBuilder = new SqlBuilder().appendSqlValues("WHERE a IN (?, ?, ?)", "a", null, String.class);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("WHERE a IN (?, ?, ?)", sqlParts(sqlBuilder).get(0));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAppendSqlValues_String_ObjectList() {
        var sqlBuilder = new SqlBuilder().appendSqlValues("WHERE a IN (?, ?, ?)", Arrays.asList("a", null, String.class));
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("WHERE a IN (?, ?, ?)", sqlParts(sqlBuilder).get(0));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testSV_String_ObjectArray() {
        var sqlBuilder = new SqlBuilder().sv("WHERE a IN (?, ?, ?)", "a", null, String.class);
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("WHERE a IN (?, ?, ?)", sqlParts(sqlBuilder).get(0));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testSV_ObjectList() {
        var sqlBuilder = new SqlBuilder().sv("WHERE a IN (?, ?, ?)", Arrays.asList("a", null, String.class));
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("WHERE a IN (?, ?, ?)", sqlParts(sqlBuilder).get(0));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

}
