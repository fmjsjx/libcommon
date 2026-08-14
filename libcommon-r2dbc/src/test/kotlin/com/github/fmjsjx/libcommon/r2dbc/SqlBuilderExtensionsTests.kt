@file:Suppress("unused")

package com.github.fmjsjx.libcommon.r2dbc

import com.github.fmjsjx.libcommon.r2dbc.SqlBuilder.getTableName
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import org.springframework.data.annotation.Transient as SpringDataTransient

class SqlBuilderExtensionsTests {

    companion object {
        @Suppress("UNCHECKED_CAST")
        infix fun <R : Any> SqlBuilder.fieldValue(fieldName: String): R? =
            SqlBuilder::class.java.getDeclaredField(fieldName).apply { isAccessible = true }.get(this) as R?

        val SqlBuilder.sqlPartsValue: List<String>? get() = fieldValue("sqlParts")

        val SqlBuilder.selectColumns: List<String>? get() = fieldValue("selectColumns")

        val SqlBuilder.valuesValue: List<Any>? get() = fieldValue("values")
    }

    @Test
    fun testGetTableName() {
        getTableName(TestEntity::class.java) shouldBe "test_entity"
        getTableName(TestEntity2::class.java) shouldBe "test2"
        getTableName(TestEntity3::class.java) shouldBe "test3"
        getTableName(TestEntity4::class.java) shouldBe "test.test4"
    }

    @Test
    fun testValue_Entity() {
        var sqlBuilder = SqlBuilder()
        val entity = TestEntity()
        entity.name = "name"
        val now = LocalDateTime.now()
        entity.createTime = now
        sqlBuilder.value(entity) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("VALUE", "(", "?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf("name", now)

        val e1 = TestEntityE1("address")
        sqlBuilder = SqlBuilder()
        e1.name = "name"
        e1.createTime = now
        sqlBuilder.value(e1) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("VALUE", "(", "?, ?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf("name", now, "address")
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
    fun testAppendColumns_KClass_NString_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(tableAlias = null) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(tableAlias = " ") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(tableAlias = "a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("a.id, a.name, a.create_time, a.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(columnsAliasPrefix = null) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(columnsAliasPrefix = " ") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>(columnsAliasPrefix = "a_") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id a_id, test_entity.name a_name, test_entity.create_time a_create_time, test_entity.update_time a_update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select().appendColumns<TestEntity>("a", "a_") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("a.id a_id, a.name a_name, a.create_time a_create_time, a.update_time a_update_time")
    }

    @Test
    fun testSelect_KClass_NString_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(tableAlias = null) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(tableAlias = " ") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(tableAlias = "a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("a.id, a.name, a.create_time, a.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(columnsAliasPrefix = null) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(columnsAliasPrefix = " ") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id, test_entity.name, test_entity.create_time, test_entity.update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>(columnsAliasPrefix = "a_") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("test_entity.id a_id, test_entity.name a_name, test_entity.create_time a_create_time, test_entity.update_time a_update_time")
        sqlBuilder = SqlBuilder()
        sqlBuilder.select<TestEntity>("a", "a_") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("a.id a_id, a.name a_name, a.create_time a_create_time, a.update_time a_update_time")
    }

    @Test
    fun testFrom_KClass_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.from<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("FROM", "test_entity")

        sqlBuilder = SqlBuilder().selectAll()
        sqlBuilder.from<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT", "*", "FROM", "test_entity")
        sqlBuilder.selectColumns.shouldBeNull()

        sqlBuilder = SqlBuilder()
        sqlBuilder.from<TestEntity>("a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("FROM", "test_entity", "a")

        sqlBuilder = SqlBuilder().selectAll()
        sqlBuilder.from<TestEntity>("a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT", "*", "FROM", "test_entity", "a")
        sqlBuilder.selectColumns.shouldBeNull()
    }

    @Test
    fun testFrom_KClassArray() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.from(TestEntity::class, TestEntity2::class) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("FROM", "test_entity, test2")

        sqlBuilder = SqlBuilder().selectAll()
        sqlBuilder.from(TestEntity::class, TestEntity2::class) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT", "*", "FROM", "test_entity, test2")
        sqlBuilder.selectColumns.shouldBeNull()
    }

    @Test
    fun testInnerJoin_KClass_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.innerJoin<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("INNER", "JOIN", "test_entity")
        sqlBuilder = SqlBuilder()
        sqlBuilder.innerJoin<TestEntity>("a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("INNER", "JOIN", "test_entity", "a")
    }

    @Test
    fun testLeftJoin_KClass_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.leftJoin<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("LEFT", "JOIN", "test_entity")
        sqlBuilder = SqlBuilder()
        sqlBuilder.leftJoin<TestEntity>("a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("LEFT", "JOIN", "test_entity", "a")
    }

    @Test
    fun testRightJoin_KClass_NString() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.rightJoin<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("RIGHT", "JOIN", "test_entity")
        sqlBuilder = SqlBuilder()
        sqlBuilder.rightJoin<TestEntity>("a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("RIGHT", "JOIN", "test_entity", "a")
    }

    @Test
    fun testInto_KClass() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.into<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("INTO", "test_entity")
    }

    @Test
    fun testColumns_KClass() {
        var sqlBuilder = SqlBuilder()
        sqlBuilder.columns<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("(", "name, create_time", ")")

        sqlBuilder = SqlBuilder()
        sqlBuilder.columns<TestEntity>(true) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("(", "name, create_time", ")")

        sqlBuilder = SqlBuilder()
        sqlBuilder.columns<TestEntity>(false) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("(", "id, name, create_time", ")")
    }

    @Test
    fun testUpdate_KClass() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.update<TestEntity>() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("UPDATE", "test_entity")
    }

    @Test
    fun testIsIn_List() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.isIn(listOf<Any>("a", "b")) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("IN", "(", "?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf("a", "b")
    }

    @Test
    fun testIsIn_VarargAny() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.isIn(1 as Any, "a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("IN", "(", "?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1, "a")
    }

    @Test
    fun testIsIn_VarargInt() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.isIn(1, 2, 3) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("IN", "(", "?, ?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1, 2, 3)
    }

    @Test
    fun testIsIn_VarargLong() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.isIn(1L, 2L, 3L) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("IN", "(", "?, ?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1L, 2L, 3L)
    }

    @Test
    fun testNotIn_List() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.notIn(listOf<Any>("a", "b")) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("NOT", "IN", "(", "?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf("a", "b")
    }

    @Test
    fun testNotIn_VarargAny() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.notIn(1 as Any, "a") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("NOT", "IN", "(", "?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1, "a")
    }

    @Test
    fun testNotIn_VarargInt() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.notIn(1, 2, 3) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("NOT", "IN", "(", "?, ?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1, 2, 3)
    }

    @Test
    fun testNotIn_VarargLong() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.notIn(1L, 2L, 3L) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("NOT", "IN", "(", "?, ?, ?", ")")
        sqlBuilder.valuesValue shouldContainExactly listOf(1L, 2L, 3L)
    }

    @Test
    fun testToColumn() {
        TestEntity::name.toColumn() shouldBe "name"
        TestEntity::createTime.toColumn() shouldBe "create_time"
        TestEntity::updateTime.toColumn() shouldBe "update_time"
        TestEntity::id.toColumn() shouldBe "id"
    }

    @Test
    fun testToOrder() {
        TestEntity::createTime.toOrder() shouldBe Order("create_time", null)
        (TestEntity::createTime toOrder Sort.ASC) shouldBe Order("create_time", Sort.ASC)
        (TestEntity::name toOrder Sort.DESC) shouldBe Order("name", Sort.DESC)
    }

    @Test
    fun testAscDesc() {
        TestEntity::createTime.asc() shouldBe Order("create_time", Sort.ASC)
        TestEntity::createTime.desc() shouldBe Order("create_time", Sort.DESC)
        TestEntity::name.asc() shouldBe Order("name", Sort.ASC)
        TestEntity::name.desc() shouldBe Order("name", Sort.DESC)
    }

    @Test
    fun testSelect_KProperty1Array() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.select(TestEntity::name, TestEntity::createTime) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("name", "create_time")
    }

    @Test
    fun testSelectDistinct_KProperty1Array() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.selectDistinct(TestEntity::name, TestEntity::createTime) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("SELECT")
        sqlBuilder.selectColumns shouldContainExactly listOf("name", "create_time")
    }

    @Test
    fun testWhere_KProperty1() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.where(TestEntity::name) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("WHERE", "name")
    }

    @Test
    fun testAnd_KProperty1() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.and(TestEntity::createTime) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("AND", "create_time")
    }

    @Test
    fun testOr_KProperty1() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.or(TestEntity::updateTime) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("OR", "update_time")
    }

    @Test
    fun testAssign_KProperty1_Any() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.assign(TestEntity::name, "test") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("name", "= ?")
        sqlBuilder.valuesValue shouldContainExactly listOf("test")
    }

    @Test
    fun testColumn_KProperty1() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.column(TestEntity::name) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("name")
    }

    @Test
    fun testBeginGroup_KProperty1() {
        val sqlBuilder = SqlBuilder()
        val groupBuilder = sqlBuilder.beginGroup(TestEntity::name)
        groupBuilder shouldNotBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("(")
        groupBuilder.sqlPartsValue shouldContainExactly listOf("name")
        groupBuilder.endGroup() shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("(", "name", ")")
    }

    @Test
    fun testAppendAssignment_KProperty1_Any() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.appendAssignment(TestEntity::name, "test") shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf(",", "name", "= ?")
        sqlBuilder.valuesValue shouldContainExactly listOf("test")
    }

    @Test
    fun testGroupBy_KProperty1Array() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.groupBy(TestEntity::name, TestEntity::createTime) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("GROUP BY", "name, create_time")
    }

    @Test
    fun testSubquery() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.subquery("test") {
            selectAll()
            from<TestEntity>()
        } shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly
                listOf("(", "SELECT", "*", "FROM", "test_entity", ")", "test")
    }

    @Test
    fun testIsIn_Subquery() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.isIn {
            selectAll()
            from<TestEntity>()
        } shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly
                listOf("IN", "(", "SELECT", "*", "FROM", "test_entity", ")")
    }

    @Test
    fun testInnerJoin_String_Subquery() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.innerJoin("t") {
            selectAll()
            from<TestEntity>()
        } shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly
                listOf("INNER", "JOIN", "(", "SELECT", "*", "FROM", "test_entity", ")", "t")
    }

    @Test
    fun testLeftJoin_String_Subquery() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.leftJoin("t") {
            selectAll()
            from<TestEntity>()
        } shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly
                listOf("LEFT", "JOIN", "(", "SELECT", "*", "FROM", "test_entity", ")", "t")
    }

    @Test
    fun testRightJoin_String_Subquery() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.rightJoin("t") {
            selectAll()
            from<TestEntity>()
        } shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly
                listOf("RIGHT", "JOIN", "(", "SELECT", "*", "FROM", "test_entity", ")", "t")
    }

    @Test
    fun testUsing_KProperty1Array() {
        val sqlBuilder = SqlBuilder()
        sqlBuilder.using(TestEntity::id, TestEntity::name) shouldBeSameInstanceAs sqlBuilder
        sqlBuilder.sqlPartsValue shouldContainExactly listOf("USING", "(", "id, name", ")")
    }

}