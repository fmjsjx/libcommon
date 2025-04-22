package com.github.fmjsjx.libcommon.r2dbc

import com.github.fmjsjx.libcommon.r2dbc.SqlBuilder.getTableName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import org.springframework.data.annotation.Transient as SpringDataTransient

@Suppress("SpringDataJdbcAssociatedDbElementsInspection")
class SqlBuilderExtensionsTests {

    companion object {
        @Suppress("UNCHECKED_CAST")
        infix fun <R : Any> SqlBuilder.fieldValue(fieldName: String): R? =
            SqlBuilder::class.java.getDeclaredField(fieldName).apply { isAccessible = true }.get(this) as R?

        val SqlBuilder.sqlPartsValue: List<String>? get() = fieldValue("sqlParts")

        val SqlBuilder.selectColumns: List<String> ? get() = fieldValue("selectColumns")

        val SqlBuilder.valuesValue: List<Any>? get() = fieldValue("values")
    }

    @Test
    fun testGetTableName() {
        assertEquals("test_entity", getTableName(TestEntity::class.java))
        assertEquals("test2", getTableName(TestEntity2::class.java))
        assertEquals("test3", getTableName(TestEntity3::class.java))
        assertEquals("test.test4", getTableName(TestEntity4::class.java))
    }

    @Test
    fun testValue_Entity() {
        var sqlBuilder = SqlBuilder()
        val entity = TestEntity()
        entity.name = "name"
        val now = LocalDateTime.now()
        entity.createTime = now
        assertSame(sqlBuilder, sqlBuilder.value(entity))
        assertIterableEquals(listOf("VALUE", "(", "?, ?", ")"), sqlBuilder.sqlPartsValue)
        assertIterableEquals(listOf("name", now), sqlBuilder.valuesValue)

        val e1 = TestEntityE1("address")
        sqlBuilder = SqlBuilder()
        e1.name = "name"
        e1.createTime = now
        assertSame(sqlBuilder, sqlBuilder.value(e1))
        assertIterableEquals(listOf("VALUE", "(", "?, ?, ?", ")"), sqlBuilder.sqlPartsValue)
        assertIterableEquals(listOf("name", now, "address"), sqlBuilder.valuesValue)
    }

    open class TestEntity(
        @Id
        var id: Long? = null,
        var name: String? = null,
        @Column("create_time")
        @InsertOnlyProperty
        var createTime: LocalDateTime? = null,
        @Transient
        var other1: Any? = null,
        @SpringDataTransient
        var other2: Any? = null,
        @ReadOnlyProperty
        var updateTime: LocalDateTime? = null,
    )

    @Table("test2")
    data class TestEntity2(
        @Id
        var id: Long? = null,
    )

    @Table(name = "test3")
    data class TestEntity3(
        @Id
        var id: Long? = null,
    )


    @Table(name = "test4", schema = "test")
    data class TestEntity4(
        @Id
        var id: Long? = null,
    )

    data class TestEntityE1(
        var address: String? = null,
    ) : TestEntity()

    @Test
    fun testFrom_KClass() {
        var sqlBuilder = SqlBuilder()
        assertSame(sqlBuilder, sqlBuilder.from<TestEntity>())
        assertIterableEquals(listOf("FROM", "test_entity"), sqlBuilder.sqlPartsValue)

        sqlBuilder = SqlBuilder().selectAll()
        assertSame(sqlBuilder, sqlBuilder.from<TestEntity>())
        assertIterableEquals(listOf("SELECT", "*", "FROM", "test_entity"), sqlBuilder.sqlPartsValue)
        assertNull(sqlBuilder.selectColumns)
    }

    @Test
    fun testFrom_KClassArray() {
        var sqlBuilder = SqlBuilder()
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity::class, TestEntity2::class))
        assertIterableEquals(listOf("FROM", "test_entity, test2"), sqlBuilder.sqlPartsValue)

        sqlBuilder = SqlBuilder().selectAll()
        assertSame(sqlBuilder, sqlBuilder.from(TestEntity::class, TestEntity2::class))
        assertIterableEquals(listOf("SELECT", "*", "FROM", "test_entity, test2"), sqlBuilder.sqlPartsValue)
        assertNull(sqlBuilder.selectColumns)
    }

    @Test
    fun testInto_KClass() {
        val sqlBuilder = SqlBuilder()
        assertSame(sqlBuilder, sqlBuilder.into<TestEntity>())
        assertIterableEquals(listOf("INTO", "test_entity"), sqlBuilder.sqlPartsValue)
    }

    @Test
    fun testColumns_KClass() {
        val sqlBuilder = SqlBuilder()
        assertSame(sqlBuilder, sqlBuilder.columns<TestEntity>())
        assertIterableEquals(listOf("(", "name, create_time", ")"), sqlBuilder.sqlPartsValue)
    }

    @Test
    fun testUpdate_KClass() {
        val sqlBuilder = SqlBuilder()
        assertSame(sqlBuilder, sqlBuilder.update<TestEntity>())
        assertIterableEquals(listOf("UPDATE", "test_entity"), sqlBuilder.sqlPartsValue)
    }

}