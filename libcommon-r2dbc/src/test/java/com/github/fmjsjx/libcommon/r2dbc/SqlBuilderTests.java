package com.github.fmjsjx.libcommon.r2dbc;

import io.r2dbc.spi.Parameters;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import static com.github.fmjsjx.libcommon.r2dbc.Sort.ASC;
import static com.github.fmjsjx.libcommon.r2dbc.Sort.DESC;
import static com.github.fmjsjx.libcommon.r2dbc.SqlBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
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
        assertEquals(ParameterStyle.NONE, parameterStyle(sqlBuilder));
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
        assertEquals(ParameterStyle.NONE, parameterStyle(sqlBuilder));
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
        assertEquals(ParameterStyle.NONE, parameterStyle(sqlBuilder));
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
        assertEquals(ParameterStyle.NONE, parameterStyle(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
        assertFalse(distinct(sqlBuilder));

        try {
            new SqlBuilder(null, SUBQUERY);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in subquery mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, GROUP);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in group mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, WHERE_CLAUSE);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in where clause mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, SET_CLAUSE);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in set clause mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, HAVING_CLAUSE);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in having clause mode", e.getMessage());
        }
        try {
            new SqlBuilder(null, 999);
            fail("Should throw IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("parent must not be null in <999> mode", e.getMessage());
        }
    }

    @Test
    public void testParameterStyle() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.parameterStyle(ParameterStyle.MYSQL));
        assertEquals(ParameterStyle.MYSQL, parameterStyle(sqlBuilder));
        assertEquals(ParameterStyle.MYSQL, sqlBuilder.getParameterStyle());
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendSql("SELECT"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendSql("SELECT", "'x'"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendSql(List.of("SELECT", "'x'")));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.s("SELECT"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.s("SELECT", "'x'"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.s(List.of("SELECT", "'x'")));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.addValue("a"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.addValues("a", null, String.class));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAddValues_ObjectList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.addValues(Arrays.asList("a", null, String.class)));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }


    @Test
    public void testV_Object() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.v("a"));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.v("a", null, String.class));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testV_ObjectList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.v(Arrays.asList("a", null, String.class)));
        assertNotNull(values(sqlBuilder));
        assertEquals(3, values(sqlBuilder).size());
        assertEquals("a", values(sqlBuilder).get(0));
        assertEquals(Parameters.in(Object.class), values(sqlBuilder).get(1));
        assertEquals(Parameters.in(String.class), values(sqlBuilder).get(2));
    }

    @Test
    public void testAppendSqlValues_String_ObjectArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendSqlValues("WHERE a IN (?, ?, ?)", "a", null, String.class));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendSqlValues("WHERE a IN (?, ?, ?)", Arrays.asList("a", null, String.class)));
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
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.sv("WHERE a IN (?, ?, ?)", "a", null, String.class));
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
    public void testSV_String_ObjectList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.sv("WHERE a IN (?, ?, ?)", Arrays.asList("a", null, String.class)));
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
    public void testAppend() {
        var sqlBuilder = new SqlBuilder().s("SELECT", "*", "FROM", "tb_test");
        var other = new SqlBuilder().s("WHERE", "id = ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.append(other));
        assertEquals(6, sqlParts(sqlBuilder).size());
        assertIterableEquals(List.of("SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?"), sqlParts(sqlBuilder));
        assertEquals(1, values(sqlBuilder).size());
        assertEquals(1, values(sqlBuilder).get(0));

        sqlBuilder = new SqlBuilder().selectAll();
        other = new SqlBuilder().s("FROM", "tb_test", "WHERE", "id = ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.append(other));
        assertEquals(6, sqlParts(sqlBuilder).size());
        assertIterableEquals(List.of("SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?"), sqlParts(sqlBuilder));
        assertEquals(1, values(sqlBuilder).size());
        assertEquals(1, values(sqlBuilder).get(0));
    }

    @Test
    public void testFinishSelect() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.finishSelect());
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(0, sqlParts(sqlBuilder).size());

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.finishSelect());
        assertEquals(2, sqlParts(sqlBuilder).size());
        assertIterableEquals(List.of("SELECT", "*"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().select("`id`", "`name`").distinct();
        assertSame(sqlBuilder, sqlBuilder.finishSelect());
        assertEquals(3, sqlParts(sqlBuilder).size());
        assertIterableEquals(List.of("SELECT", "DISTINCT", "`id`, `name`"), sqlParts(sqlBuilder));
    }

    @Test
    public void testBuildSql() {
        var testSql = List.of("SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?", "AND", "state = ?");
        var sqlBuilder = new SqlBuilder(testSql, List.of(1, 1), null, 0);
        assertEquals("SELECT * FROM tb_test WHERE id = ? AND state = ?", sqlBuilder.buildSql());
        assertEquals("SELECT * FROM tb_test WHERE id = $1 AND state = $2", sqlBuilder.parameterStyle(ParameterStyle.POSTGRESQL).buildSql());
        assertEquals("SELECT * FROM tb_test WHERE id = @0 AND state = @1", sqlBuilder.buildSql("@", 0));
    }

    @Test
    public void testSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.subquery();
        assertNotSame(sqlBuilder, subquery);
        assertSame(sqlBuilder, parent(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertNotNull(sqlParts(sqlBuilder));
        assertEquals(1, sqlParts(sqlBuilder).size());
        assertEquals("(", sqlParts(sqlBuilder).get(0));
        assertNotNull(sqlParts(subquery));
        assertEquals(0, sqlParts(subquery).size());
    }

    @Test
    public void testEndSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.subquery()
                .s("SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?", "AND", "state = ?")
                .v(1, 1);
        assertSame(sqlBuilder, subquery.endSubquery());
        assertNotNull(sqlParts(sqlBuilder));
        assertIterableEquals(List.of("(", "SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?", "AND", "state = ?", ")"), sqlParts(sqlBuilder));
        assertNotNull(values(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        subquery = sqlBuilder.subquery()
                .s("SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?", "AND", "state = ?")
                .v(1, 1);
        assertSame(sqlBuilder, subquery.endSubquery("a"));
        assertNotNull(sqlParts(sqlBuilder));
        assertIterableEquals(List.of("(", "SELECT", "*", "FROM", "tb_test", "WHERE", "id = ?", "AND", "state = ?", ")", "a"), sqlParts(sqlBuilder));
        assertNotNull(values(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));
        try {
            sqlBuilder.endSubquery();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not in a subquery", e.getMessage());
        }
    }

    @Test
    public void testSelect() {
        var sqlBuilder = new SqlBuilder();
        assertNull(selectColumns(sqlBuilder));
        assertSame(sqlBuilder, sqlBuilder.select());
        assertNotNull(selectColumns(sqlBuilder));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
    }

    @Test
    public void testDistinct() {
        var sqlBuilder = new SqlBuilder();
        assertFalse(distinct(sqlBuilder));
        assertSame(sqlBuilder, sqlBuilder.distinct());
        assertTrue(distinct(sqlBuilder));
    }

    @Test
    public void testAppendColumns() {
        var sqlBuilder = new SqlBuilder().select();
        assertSame(sqlBuilder, sqlBuilder.appendColumns("id, name"));
        assertIterableEquals(List.of("id, name"), selectColumns(sqlBuilder));
        assertSame(sqlBuilder, sqlBuilder.appendColumns("cellphone", "address"));
        assertIterableEquals(List.of("id, name", "cellphone", "address"), selectColumns(sqlBuilder));
        assertSame(sqlBuilder, sqlBuilder.appendColumns(List.of("create_time")));
        assertIterableEquals(List.of("id, name", "cellphone", "address", "create_time"), selectColumns(sqlBuilder));

        try {
            new SqlBuilder().appendColumns("id");
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not at select step", e.getMessage());
        }
        try {
            new SqlBuilder().appendColumns("id", "name");
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not at select step", e.getMessage());
        }
        try {
            new SqlBuilder().appendColumns(List.of("id", "name"));
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not at select step", e.getMessage());
        }
    }

    @Test
    public void testSelect_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.select("*"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("*"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectAll() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectAll());
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("*"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelect_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.select("id", "name"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("id", "name"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelect_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.select(List.of("id", "name")));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("id", "name"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectCount() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectCount());
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("COUNT(*)"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectCount_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectCount("id"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("COUNT(id)"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectCount_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectCount("id", "name"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("COUNT(id, name)"), selectColumns(sqlBuilder));
    }


    @Test
    public void testSelectCount_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectCount(List.of("id", "name")));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("COUNT(id, name)"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectDistinct_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectDistinct("*"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertTrue(distinct(sqlBuilder));
        assertIterableEquals(List.of("*"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectDistinct_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectDistinct("id", "name"));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertTrue(distinct(sqlBuilder));
        assertIterableEquals(List.of("id", "name"), selectColumns(sqlBuilder));
    }

    @Test
    public void testSelectDistinct_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.selectDistinct(List.of("id", "name")));
        assertIterableEquals(List.of("SELECT"), sqlParts(sqlBuilder));
        assertTrue(distinct(sqlBuilder));
        assertIterableEquals(List.of("id", "name"), selectColumns(sqlBuilder));
    }

    @Test
    public void testFrom() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from());
        assertIterableEquals(List.of("FROM"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from());
        assertIterableEquals(List.of("SELECT", "*", "FROM"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testFrom_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from("tb_test"));
        assertIterableEquals(List.of("FROM", "tb_test"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from("tb_test"));
        assertIterableEquals(List.of("SELECT", "*", "FROM", "tb_test"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testFrom_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from("tb_test", "tb_test2"));
        assertIterableEquals(List.of("FROM", "tb_test, tb_test2"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from("tb_test", "tb_test2"));
        assertIterableEquals(List.of("SELECT", "*", "FROM", "tb_test, tb_test2"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testFrom_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from(List.of("tb_test", "tb_test2")));
        assertIterableEquals(List.of("FROM", "tb_test, tb_test2"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from(List.of("tb_test", "tb_test2")));
        assertIterableEquals(List.of("SELECT", "*", "FROM", "tb_test, tb_test2"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testGetTableName() {
        assertEquals("test_entity", getTableName(TestEntity.class));
        assertEquals("test2", getTableName(TestEntity2.class));
        assertEquals("test3", getTableName(TestEntity3.class));
        assertEquals("test.test4", getTableName(TestEntity4.class));
    }

    static class TestEntity {
        public static final int OK = 0;
        @Id
        private long id;
        private String name;
        @Column("create_time")
        @InsertOnlyProperty
        private LocalDateTime createTime;

        private transient Object other1;
        @Transient
        private Object other2;
        @ReadOnlyProperty
        private LocalDateTime updateTime;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public Object getOther1() {
            return other1;
        }

        public void setOther1(Object other1) {
            this.other1 = other1;
        }

        public Object getOther2() {
            return other2;
        }

        public void setOther2(Object other2) {
            this.other2 = other2;
        }

        public LocalDateTime getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
        }
    }

    @Table("test2")
    static class TestEntity2 {
    }

    @Table(name = "test3")
    static class TestEntity3 {
    }


    @Table(name = "test4", schema = "test")
    static class TestEntity4 {
    }

    @Test
    public void testFrom_Class() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity.class));
        assertIterableEquals(List.of("FROM", "test_entity"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity.class));
        assertIterableEquals(List.of("SELECT", "*", "FROM", "test_entity"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testFrom_ClassArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity.class, TestEntity2.class));
        assertIterableEquals(List.of("FROM", "test_entity, test2"), sqlParts(sqlBuilder));

        sqlBuilder = new SqlBuilder().selectAll();
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity.class, TestEntity2.class));
        assertIterableEquals(List.of("SELECT", "*", "FROM", "test_entity, test2"), sqlParts(sqlBuilder));
        assertNull(selectColumns(sqlBuilder));
    }

    @Test
    public void testInnerJoin_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.innerJoin("tb_test2 b"));
        assertIterableEquals(List.of("INNER", "JOIN", "tb_test2 b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInnerJoin_SqlBuilder_String() {
        var subquery = new SqlBuilder().selectAll().from("tb_test2");
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.innerJoin(subquery, "b"));
        assertIterableEquals(List.of("INNER", "JOIN", "(", "SELECT", "*", "FROM", "tb_test2", ")", "b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInnerJoinSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.innerJoinSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("INNER", "JOIN", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
    }

    @Test
    public void testLeftJoin_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.leftJoin("tb_test2 b"));
        assertIterableEquals(List.of("LEFT", "JOIN", "tb_test2 b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testLeftJoin_SqlBuilder_String() {
        var subquery = new SqlBuilder().selectAll().from("tb_test2");
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.leftJoin(subquery, "b"));
        assertIterableEquals(List.of("LEFT", "JOIN", "(", "SELECT", "*", "FROM", "tb_test2", ")", "b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testLeftJoinSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.leftJoinSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("LEFT", "JOIN", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
    }

    @Test
    public void testRightJoin_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.rightJoin("tb_test2 b"));
        assertIterableEquals(List.of("RIGHT", "JOIN", "tb_test2 b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testRightJoin_SqlBuilder_String() {
        var subquery = new SqlBuilder().selectAll().from("tb_test2");
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.rightJoin(subquery, "b"));
        assertIterableEquals(List.of("RIGHT", "JOIN", "(", "SELECT", "*", "FROM", "tb_test2", ")", "b"), sqlParts(sqlBuilder));
    }

    @Test
    public void testRightJoinSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.rightJoinSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("RIGHT", "JOIN", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertSame(sqlBuilder, parent(subquery));
    }

    @Test
    public void testOn() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.on());
        assertIterableEquals(List.of("ON"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOn_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.on("a.id = b.test_id"));
        assertIterableEquals(List.of("ON", "a.id = b.test_id"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUsing() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.using());
        assertIterableEquals(List.of("USING"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUsing_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.using("first_name", "last_name"));
        assertIterableEquals(List.of("USING", "(", "first_name, last_name", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUsing_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.using(List.of("first_name", "last_name")));
        assertIterableEquals(List.of("USING", "(", "first_name, last_name", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testWhere() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.where());
        assertIterableEquals(List.of("WHERE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testWhere_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.where("id = ? AND state = 1"));
        assertIterableEquals(List.of("WHERE", "id = ? AND state = 1"), sqlParts(sqlBuilder));
    }

    @Test
    public void testWhere_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.where("id = ?", "state = 1"));
        assertIterableEquals(List.of("WHERE", "id = ? AND state = 1"), sqlParts(sqlBuilder));
    }

    @Test
    public void testWhereClause() {
        var sqlBuilder = new SqlBuilder();
        var clause = sqlBuilder.whereClause();
        assertNotSame(sqlBuilder, clause);
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));
        assertEquals(WHERE_CLAUSE, mode(clause));
        assertSame(sqlBuilder, parent(clause));
    }

    @Test
    public void testEndWhereClause() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, WHERE_CLAUSE).endWhereClause());
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));

        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, WHERE_CLAUSE).sv("id = ? AND state = ?", 1, 1).endWhereClause());
        assertIterableEquals(List.of("WHERE", "id = ? AND state = ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, WHERE_CLAUSE).s("id", "= ?", "AND", "state", "= ?").v(1, 1).endWhereClause());
        assertIterableEquals(List.of("WHERE", "id", "= ?", "AND", "state", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));

        try {
            new SqlBuilder().endWhereClause();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not in a where clause", e.getMessage());
        }
    }

    @Test
    public void testAnd() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.and());
        assertIterableEquals(List.of("AND"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOr() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.or());
        assertIterableEquals(List.of("OR"), sqlParts(sqlBuilder));
    }

    @Test
    public void testColumn() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.column("id"));
        assertIterableEquals(List.of("id"), sqlParts(sqlBuilder));
    }

    @Test
    public void testAny() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.any());
        assertIterableEquals(List.of("ANY"), sqlParts(sqlBuilder));
    }

    @Test
    public void testSome() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.some());
        assertIterableEquals(List.of("SOME"), sqlParts(sqlBuilder));
    }

    @Test
    public void testAll() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.all());
        assertIterableEquals(List.of("ALL"), sqlParts(sqlBuilder));
    }

    @Test
    public void testNot() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.not());
        assertIterableEquals(List.of("NOT"), sqlParts(sqlBuilder));
    }

    @Test
    public void testExists() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.exists());
        assertIterableEquals(List.of("EXISTS"), sqlParts(sqlBuilder));
    }

    @Test
    public void testIsNull() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.isNull());
        assertIterableEquals(List.of("IS NULL"), sqlParts(sqlBuilder));
    }

    @Test
    public void testIsNotNull() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.isNotNull());
        assertIterableEquals(List.of("IS NOT NULL"), sqlParts(sqlBuilder));
    }

    @Test
    public void testEq() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.eq(1));
        assertIterableEquals(List.of("= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testNe() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.ne(1));
        assertIterableEquals(List.of("<> ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testGt() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.gt(1));
        assertIterableEquals(List.of("> ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testGe() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.ge(1));
        assertIterableEquals(List.of(">= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testLt() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.lt(1));
        assertIterableEquals(List.of("< ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testLe() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.le(1));
        assertIterableEquals(List.of("<= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testLike() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.like("%a%"));
        assertIterableEquals(List.of("LIKE ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("%a%"), values(sqlBuilder));
    }

    @Test
    public void testAnyLike() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.anyLike("a"));
        assertIterableEquals(List.of("LIKE ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("%a%"), values(sqlBuilder));
    }

    @Test
    public void testBeginLike() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.beginLike("a"));
        assertIterableEquals(List.of("LIKE ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("a%"), values(sqlBuilder));
    }

    @Test
    public void testEndLike() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.endLike("a"));
        assertIterableEquals(List.of("LIKE ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("%a"), values(sqlBuilder));
    }

    @Test
    public void testAnyLikeConcat() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.anyLikeConcat("a"));
        assertIterableEquals(List.of("LIKE CONCAT('%', ?, '%')"), sqlParts(sqlBuilder));
    }

    @Test
    public void testBeginLikeConcat() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.beginLikeConcat("a"));
        assertIterableEquals(List.of("LIKE CONCAT(?, '%')"), sqlParts(sqlBuilder));
    }

    @Test
    public void testEndLikeConcat() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.endLikeConcat("a"));
        assertIterableEquals(List.of("LIKE CONCAT('%', ?)"), sqlParts(sqlBuilder));
    }

    @Test
    public void testIn_Array() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.in(1, 2, 3));
        assertIterableEquals(List.of("IN", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 2, 3), values(sqlBuilder));
    }

    @Test
    public void testIn_List() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.in(List.of(1, 2, 3)));
        assertIterableEquals(List.of("IN", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 2, 3), values(sqlBuilder));
    }

    @Test
    public void testIn_SqlBuilder() {
        var sqlBuilder = new SqlBuilder();
        var subquery = new SqlBuilder().s("SELECT", "id", "FROM", "tb_test", "WHERE", "state", "= ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.in(subquery));
        assertIterableEquals(List.of("IN", "(", "SELECT", "id", "FROM", "tb_test", "WHERE", "state", "= ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testInSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.inSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("IN", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertSame(sqlBuilder, parent(subquery));
    }

    @Test
    public void testBetween() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.between(1, 2));
        assertIterableEquals(List.of("BETWEEN ? AND ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 2), values(sqlBuilder));
    }

    @Test
    public void testBeginGroup() {
        var sqlBuilder = new SqlBuilder();
        var group = sqlBuilder.beginGroup();
        assertNotSame(sqlBuilder, group);
        assertIterableEquals(List.of("("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), values(group));
        assertEquals(GROUP, mode(group));
        assertSame(sqlBuilder, parent(group));
    }

    @Test
    public void testEndGroup() {
        var sqlBuilder = new SqlBuilder();
        var group = new SqlBuilder(sqlBuilder.s("("), GROUP)
                .s("id", "= ?", "OR", "state", "= ?").v(1, 1);
        assertSame(sqlBuilder, group.endGroup());
        assertIterableEquals(List.of("(", "id", "= ?", "OR", "state", "= ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(group));

        try {
            new SqlBuilder().endGroup();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not in group", e.getMessage());
        }
    }

    @Test
    public void testEndAllGroups() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.endAllGroups());
        var g = sqlBuilder.beginGroup().s("id", "= ?", "AND").v(1)
                .beginGroup().s("state", "= ?", "OR", "type", "= ?", "OR").v(0, 2)
                .beginGroup().s("ip", "= ?", "AND", "port", "= ?").v("0.0.0.0", 12345);
        assertSame(sqlBuilder, g.endAllGroups());
        assertIterableEquals(List.of("(", "id", "= ?", "AND", "(", "state", "= ?", "OR", "type", "= ?", "OR", "(", "ip", "= ?", "AND", "port", "= ?", ")", ")", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 0, 2, "0.0.0.0", 12345), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        var whereClause = new SqlBuilder(sqlBuilder, WHERE_CLAUSE);
        g = whereClause.s("1 = 1", "AND")
                .beginGroup().s("id", "= ?", "AND").v(1)
                .beginGroup().s("state", "= ?", "OR", "type", "= ?", "OR").v(0, 2)
                .beginGroup().s("ip", "= ?", "AND", "port", "= ?").v("0.0.0.0", 12345);
        assertSame(whereClause, g.endAllGroups());
        assertIterableEquals(List.of("1 = 1", "AND", "(", "id", "= ?", "AND", "(", "state", "= ?", "OR", "type", "= ?", "OR", "(", "ip", "= ?", "AND", "port", "= ?", ")", ")", ")"), sqlParts(whereClause));
        assertIterableEquals(List.of(1, 0, 2, "0.0.0.0", 12345), values(whereClause));
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), values(sqlBuilder));
    }

    @Test
    public void testAs() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.as());
        assertIterableEquals(List.of("AS"), sqlParts(sqlBuilder));
    }

    @Test
    public void testWith() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.with());
        assertIterableEquals(List.of("WITH"), sqlParts(sqlBuilder));
    }

    @Test
    public void testGroupBy() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.groupBy());
        assertIterableEquals(List.of("GROUP BY"), sqlParts(sqlBuilder));
    }

    @Test
    public void testGroupBy_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.groupBy("type, sub_type"));
        assertIterableEquals(List.of("GROUP BY", "type, sub_type"), sqlParts(sqlBuilder));
    }

    @Test
    public void testGroupBy_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.groupBy("type", "sub_type"));
        assertIterableEquals(List.of("GROUP BY", "type, sub_type"), sqlParts(sqlBuilder));
    }

    @Test
    public void testGroupBy_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.groupBy(List.of("type", "sub_type")));
        assertIterableEquals(List.of("GROUP BY", "type, sub_type"), sqlParts(sqlBuilder));
    }

    @Test
    public void testHaving() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.having());
        assertIterableEquals(List.of("HAVING"), sqlParts(sqlBuilder));
    }

    @Test
    public void testHaving_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.having("id = ? AND state = 1"));
        assertIterableEquals(List.of("HAVING", "id = ? AND state = 1"), sqlParts(sqlBuilder));
    }

    @Test
    public void testHaving_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.having("id = ?", "state = 1"));
        assertIterableEquals(List.of("HAVING", "id = ? AND state = 1"), sqlParts(sqlBuilder));
    }

    @Test
    public void testHavingClause() {
        var sqlBuilder = new SqlBuilder();
        var clause = sqlBuilder.havingClause();
        assertNotSame(sqlBuilder, clause);
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));
        assertEquals(HAVING_CLAUSE, mode(clause));
        assertSame(sqlBuilder, parent(clause));
    }

    @Test
    public void testEndHavingClause() {
        var sqlBuilder = new SqlBuilder();
        new SqlBuilder(sqlBuilder, HAVING_CLAUSE).endHavingClause();
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));

        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, HAVING_CLAUSE).sv("id = ? AND state = ?", 1, 1).endHavingClause());
        assertIterableEquals(List.of("HAVING", "id = ? AND state = ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, HAVING_CLAUSE).s("id", "= ?", "AND", "state", "= ?").v(1, 1).endHavingClause());
        assertIterableEquals(List.of("HAVING", "id", "= ?", "AND", "state", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1, 1), values(sqlBuilder));

        try {
            new SqlBuilder().endHavingClause();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not in a having clause", e.getMessage());
        }
    }

    @Test
    public void testOrderBy() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.orderBy());
        assertIterableEquals(List.of("ORDER BY"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOrderBy_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.orderBy("state DESC, create_time DESC"));
        assertIterableEquals(List.of("ORDER BY", "state DESC, create_time DESC"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOrderBy_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.orderBy("state DESC", "create_time DESC"));
        assertIterableEquals(List.of("ORDER BY", "state DESC, create_time DESC"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOrderBy_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.orderBy(List.of("state DESC", "create_time DESC")));
        assertIterableEquals(List.of("ORDER BY", "state DESC, create_time DESC"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOrderBy_OrderArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.orderBy(new Order("id", null), new Order("state", ASC), new Order("create_time", DESC)));
        assertIterableEquals(List.of("ORDER BY", "id, state ASC, create_time DESC"), sqlParts(sqlBuilder));
    }

    @Test
    public void testOffset() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.offset(10));
        assertIterableEquals(List.of("OFFSET ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(10L), values(sqlBuilder));
    }

    @Test
    public void testLimit_Int() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.limit(10));
        assertIterableEquals(List.of("LIMIT ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(10), values(sqlBuilder));
    }

    @Test
    public void testLimit_Long_Int() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.limit(0, 10));
        assertIterableEquals(List.of("LIMIT ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(10), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.limit(20, 10));
        assertIterableEquals(List.of("LIMIT ?, ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(20L, 10), values(sqlBuilder));

        try {
            new SqlBuilder().limit(-1, 10);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("offset must be greater than or equal to 0", e.getMessage());
        }
        try {
            new SqlBuilder().limit(0, 0);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("limit must be greater than 0", e.getMessage());
        }
    }

    @Test
    public void testUnion() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.union());
        assertIterableEquals(List.of("UNION"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUnion_SqlBuilder() {
        var sqlBuilder = new SqlBuilder();
        var subquery = new SqlBuilder().s("SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.union(subquery));
        assertIterableEquals(List.of("UNION", "(", "SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testUnionSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.unionSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("UNION", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertSame(sqlBuilder, parent(subquery));
    }

    @Test
    public void testUnionDistinct() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.unionDistinct());
        assertIterableEquals(List.of("UNION", "DISTINCT"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUnionDistinct_SqlBuilder() {
        var sqlBuilder = new SqlBuilder();
        var subquery = new SqlBuilder().s("SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.unionDistinct(subquery));
        assertIterableEquals(List.of("UNION", "DISTINCT", "(", "SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testUnionDistinctSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.unionDistinctSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("UNION", "DISTINCT", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertSame(sqlBuilder, parent(subquery));
    }

    @Test
    public void testUnionAll() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.unionAll());
        assertIterableEquals(List.of("UNION", "ALL"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUnionAll_SqlBuilder() {
        var sqlBuilder = new SqlBuilder();
        var subquery = new SqlBuilder().s("SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?").v(1);
        assertSame(sqlBuilder, sqlBuilder.unionAll(subquery));
        assertIterableEquals(List.of("UNION", "ALL", "(", "SELECT", "*", "FROM", "tb_test", "WHERE", "state", "= ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(1), values(sqlBuilder));
    }

    @Test
    public void testUnionAllSubquery() {
        var sqlBuilder = new SqlBuilder();
        var subquery = sqlBuilder.unionAllSubquery();
        assertNotSame(sqlBuilder, subquery);
        assertIterableEquals(List.of("UNION", "ALL", "("), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(subquery));
        assertEquals(SUBQUERY, mode(subquery));
        assertSame(sqlBuilder, parent(subquery));
    }

    @Test
    public void testInsert() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.insert());
        assertIterableEquals(List.of("INSERT"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInsert_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.insert("IGNORE"));
        assertIterableEquals(List.of("INSERT", "IGNORE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testIgnore() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.ignore());
        assertIterableEquals(List.of("IGNORE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInto() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.into());
        assertIterableEquals(List.of("INTO"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInto_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.into("tb_test"));
        assertIterableEquals(List.of("INTO", "tb_test"), sqlParts(sqlBuilder));
    }

    @Test
    public void testInto_Class() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.into(TestEntity.class));
        assertIterableEquals(List.of("INTO", "test_entity"), sqlParts(sqlBuilder));
    }

    @Test
    public void testPartition() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.partition());
        assertIterableEquals(List.of("PARTITION"), sqlParts(sqlBuilder));
    }

    @Test
    public void testPartition_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.partition("p1"));
        assertIterableEquals(List.of("PARTITION", "(", "p1", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testPartition_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.partition("p1", "p2", "p3"));
        assertIterableEquals(List.of("PARTITION", "(", "p1, p2, p3", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testPartition_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.partition(List.of("p1", "p2", "p3")));
        assertIterableEquals(List.of("PARTITION", "(", "p1, p2, p3", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testColumns_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.columns("id, name"));
        assertIterableEquals(List.of("(", "id, name", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testColumns_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.columns("id", "name"));
        assertIterableEquals(List.of("(", "id, name", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testColumns_StringList() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.columns(List.of("id", "name")));
        assertIterableEquals(List.of("(", "id, name", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testColumns_Class() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.columns(TestEntity.class));
        assertIterableEquals(List.of("(", "name, create_time", ")"), sqlParts(sqlBuilder));
    }

    @Test
    public void testValue() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.value());
        assertIterableEquals(List.of("VALUE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testValue_Entity() {
        var sqlBuilder = new SqlBuilder();
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.value(entity));
        assertIterableEquals(List.of("VALUE", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now), values(sqlBuilder));

        var e1 = new TestEntityE1();
        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        e1.setAddress("address");
        assertSame(sqlBuilder, sqlBuilder.value(e1));
        assertIterableEquals(List.of("VALUE", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "address"), values(sqlBuilder));
    }

    static class TestEntityE1 extends TestEntity {
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    @Test
    public void testValue_Class_Entity() {
        var sqlBuilder = new SqlBuilder();
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.value(TestEntity.class, entity));
        assertIterableEquals(List.of("VALUE", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now), values(sqlBuilder));

        var e1 = new TestEntityE1();
        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.value(TestEntity.class, e1));
        assertIterableEquals(List.of("VALUE", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.value(TestEntityE1.class, e1));
        assertIterableEquals(List.of("VALUE", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, Parameters.in(Object.class)), values(sqlBuilder));

        e1.setAddress("address");
        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.value(TestEntityE1.class, e1));
        assertIterableEquals(List.of("VALUE", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "address"), values(sqlBuilder));
    }


    @Test
    public void testValues() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.values());
        assertIterableEquals(List.of("VALUES"), sqlParts(sqlBuilder));
    }

    @Test
    public void testValues_Entity() {
        var sqlBuilder = new SqlBuilder();
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.values(entity));
        assertIterableEquals(List.of("VALUES", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now), values(sqlBuilder));

        var e1 = new TestEntityE1();
        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        e1.setAddress("address");
        assertSame(sqlBuilder, sqlBuilder.values(e1));
        assertIterableEquals(List.of("VALUES", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "address"), values(sqlBuilder));
    }

    @Test
    public void testValues_EntityList() {
        var sqlBuilder = new SqlBuilder();
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var entity2 = new TestEntity();
        entity2.setName("name2");
        entity2.setCreateTime(now);
        assertSame(sqlBuilder, sqlBuilder.values(List.of(entity, entity2)));
        assertIterableEquals(List.of("VALUES", "(", "?, ?", ")", ",", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "name2", now), values(sqlBuilder));

        var e1 = new TestEntityE1();
        sqlBuilder = new SqlBuilder();
        e1.setName("name");
        e1.setCreateTime(now);
        e1.setAddress("address");
        var e2 = new TestEntityE1();
        e2.setName("name2");
        e2.setCreateTime(now);
        e2.setAddress("address2");
        assertSame(sqlBuilder, sqlBuilder.values(List.of(e1, e2)));
        assertIterableEquals(List.of("VALUES", "(", "?, ?, ?", ")", ",", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "address", "name2", now, "address2"), values(sqlBuilder));
    }

    @Test
    public void testValues_Class_EntityList() {
        var sqlBuilder = new SqlBuilder();
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var entity2 = new TestEntity();
        entity2.setName("name2");
        entity2.setCreateTime(now);
        var e1 = new TestEntityE1();
        e1.setName("name");
        e1.setCreateTime(now);
        e1.setAddress("address");
        var e2 = new TestEntityE1();
        e2.setName("name2");
        e2.setCreateTime(now);
        e2.setAddress("address2");

        assertSame(sqlBuilder, sqlBuilder.values(TestEntity.class, List.of(entity, entity2)));
        assertIterableEquals(List.of("VALUES", "(", "?, ?", ")", ",", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "name2", now), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.values(TestEntity.class, List.of(entity, e2)));
        assertIterableEquals(List.of("VALUES", "(", "?, ?", ")", ",", "(", "?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "name2", now), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.values(TestEntityE1.class, List.of(e1, e2)));
        assertIterableEquals(List.of("VALUES", "(", "?, ?, ?", ")", ",", "(", "?, ?, ?", ")"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", now, "address", "name2", now, "address2"), values(sqlBuilder));
    }

    @Test
    public void testDelete() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.delete());
        assertIterableEquals(List.of("DELETE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testDelete_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.delete("A", "B"));
        assertIterableEquals(List.of("DELETE", "A", "B"), sqlParts(sqlBuilder));
    }

    @Test
    public void testDeleteFrom_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.deleteFrom("tb_test"));
        assertIterableEquals(List.of("DELETE", "FROM", "tb_test"), sqlParts(sqlBuilder));
    }

    @Test
    public void testDeleteFrom_Class() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.deleteFrom(TestEntity.class));
        assertIterableEquals(List.of("DELETE", "FROM", "test_entity"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUpdate() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.update());
        assertIterableEquals(List.of("UPDATE"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUpdate_StringArray() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.update("A", "B"));
        assertIterableEquals(List.of("UPDATE", "A", "B"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUpdate_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.update("tb_test"));
        assertIterableEquals(List.of("UPDATE", "tb_test"), sqlParts(sqlBuilder));
    }

    @Test
    public void testUpdate_Class() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.update(TestEntity.class));
        assertIterableEquals(List.of("UPDATE", "test_entity"), sqlParts(sqlBuilder));
    }

    @Test
    public void testSet() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set());
        assertIterableEquals(List.of("SET"), sqlParts(sqlBuilder));
    }

    @Test
    public void testSetClause() {
        var sqlBuilder = new SqlBuilder();
        var clause = sqlBuilder.setClause();
        assertNotSame(sqlBuilder, clause);
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));
        assertIterableEquals(List.of(), sqlParts(clause));
        assertSame(sqlBuilder, parent(clause));
        assertEquals(SET_CLAUSE, mode(clause));
    }


    @Test
    public void testEndSetClause() {
        var sqlBuilder = new SqlBuilder();
        new SqlBuilder(sqlBuilder, SET_CLAUSE).endSetClause();
        assertIterableEquals(List.of(), sqlParts(sqlBuilder));

        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, SET_CLAUSE).sv("name = ?, state = ?", "name", 1).endSetClause());
        assertIterableEquals(List.of("SET", "name = ?, state = ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", 1), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, new SqlBuilder(sqlBuilder, SET_CLAUSE).s(",", "name", "= ?", ",", "state", "= ?").v("name", 1).endSetClause());
        assertIterableEquals(List.of("SET", "name", "= ?", ",", "state", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name", 1), values(sqlBuilder));

        try {
            new SqlBuilder().endSetClause();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("not in a set clause", e.getMessage());
        }
    }

    @Test
    public void testComma() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.comma());
        assertIterableEquals(List.of(","), sqlParts(sqlBuilder));
    }

    @Test
    public void testAppendAssignment_String() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignment("name = ?"));
        assertIterableEquals(List.of(",", "name = ?"), sqlParts(sqlBuilder));
    }

    @Test
    public void testAppendAssignment_String_Object() {
        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignment("name", "name"));
        assertIterableEquals(List.of(",", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name"), values(sqlBuilder));
    }

    @Test
    public void testAppendAssignments_Entity() {
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var e1 = new TestEntityE1();
        e1.setName("e1");
        e1.setCreateTime(now);
        e1.setAddress("address");

        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(entity));
        assertIterableEquals(List.of(",", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(e1));
        assertIterableEquals(List.of(",", "name", "= ?", ",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1", "address"), values(sqlBuilder));

        e1.setName(null);
        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(e1));
        assertIterableEquals(List.of(",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("address"), values(sqlBuilder));
    }

    @Test
    public void testAppendAssignments_Class_Entity() {
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var e1 = new TestEntityE1();
        e1.setName("e1");
        e1.setCreateTime(now);
        e1.setAddress("address");

        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(TestEntity.class, entity));
        assertIterableEquals(List.of(",", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(TestEntityE1.class, e1));
        assertIterableEquals(List.of(",", "name", "= ?", ",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1", "address"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(TestEntity.class, e1));
        assertIterableEquals(List.of(",", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1"), values(sqlBuilder));

        e1.setName(null);
        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.appendAssignments(TestEntityE1.class, e1));
        assertIterableEquals(List.of(",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("address"), values(sqlBuilder));
    }

    @Test
    public void testSet_Entity() {
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var e1 = new TestEntityE1();
        e1.setName("e1");
        e1.setCreateTime(now);
        e1.setAddress("address");

        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(entity));
        assertIterableEquals(List.of("SET", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(e1));
        assertIterableEquals(List.of("SET", "name", "= ?", ",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1", "address"), values(sqlBuilder));

        e1.setName(null);
        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(e1));
        assertIterableEquals(List.of("SET", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("address"), values(sqlBuilder));
    }

    @Test
    public void testSet_Class_Entity() {
        var entity = new TestEntity();
        entity.setName("name");
        var now = LocalDateTime.now();
        entity.setCreateTime(now);
        var e1 = new TestEntityE1();
        e1.setName("e1");
        e1.setCreateTime(now);
        e1.setAddress("address");

        var sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(TestEntity.class, entity));
        assertIterableEquals(List.of("SET", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("name"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(TestEntityE1.class, e1));
        assertIterableEquals(List.of("SET", "name", "= ?", ",", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1", "address"), values(sqlBuilder));

        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(TestEntity.class, e1));
        assertIterableEquals(List.of("SET", "name", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("e1"), values(sqlBuilder));

        e1.setName(null);
        sqlBuilder = new SqlBuilder();
        assertSame(sqlBuilder, sqlBuilder.set(TestEntityE1.class, e1));
        assertIterableEquals(List.of("SET", "address", "= ?"), sqlParts(sqlBuilder));
        assertIterableEquals(List.of("address"), values(sqlBuilder));
    }

}
