package com.github.fmjsjx.libcommon.r2dbc

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.mapping.Table
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable


@Suppress("ReactiveStreamsUnusedPublisher")
class CrudR2dbcSqlBuilderRepositoryTests {

    @Table("test_users")
    data class TestUser(
        @Id
        var id: Long? = null,
        var username: String? = null,
        var age: Int? = null,
    ) : Serializable

    @Table("test_orders")
    data class TestOrder(
        @Id
        var orderId: String? = null,
        var amount: Double? = null,
    ) : Serializable

    class TestUserRepository(
        override val r2dbcEntityOperations: R2dbcEntityOperations,
    ) : CrudR2dbcSqlBuilderRepository<TestUser, Long>

    class TestOrderRepository(
        override val r2dbcEntityOperations: R2dbcEntityOperations,
        override val parameterStyle: ParameterStyle,
    ) : CrudR2dbcSqlBuilderRepository<TestOrder, String>

    private lateinit var operations: R2dbcEntityOperations
    private lateinit var userRepository: TestUserRepository
    private lateinit var orderRepository: TestOrderRepository

    @BeforeEach
    fun setUp() {
        operations = mockk()
        userRepository = TestUserRepository(operations)
        orderRepository = TestOrderRepository(operations, ParameterStyle.POSTGRESQL)
        mockkStatic("com.github.fmjsjx.libcommon.r2dbc.R2dbcExtensionsKt")
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic("com.github.fmjsjx.libcommon.r2dbc.R2dbcExtensionsKt")
    }

    @Test
    fun testInsertOne() {
        val user = TestUser(username = "Alice", age = 30)
        val savedUser = TestUser(id = 1L, username = "Alice", age = 30)

        every { operations.insert(any<TestUser>()) } returns Mono.just(savedUser)

        val result = userRepository.insertOne(user).block()

        result.shouldNotBeNull()
        result shouldBe savedUser
        verify { operations.insert(user) }
    }

    @Test
    fun testDeleteOne() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.deleteOne(user).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "DELETE FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteOneWithZeroRowsAffected() {
        val user = TestUser(id = 99L, username = "unknown", age = 0)

        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.deleteOne(user).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testUpdateOne() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.updateOne(user).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "UPDATE test_users SET username = ? , age = ? WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("Alice", 30, 1L)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testUpdateOneWithZeroRowsAffected() {
        val user = TestUser(id = 99L, username = "unknown", age = 0)

        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.updateOne(user).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testFindById() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOne(TestUser::class, capture(sqlBuilderSlot)) } returns Mono.just(user)

        val result = userRepository.findOneById(TestUser::class, 1L).block()

        result.shouldNotBeNull()
        result shouldBe user
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT * FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.selectOne(TestUser::class, any()) }
    }

    @Test
    fun testFindByIdInlineExtension() {
        val user = TestUser(id = 2L, username = "bob", age = 25)

        every { operations.selectOne(TestUser::class, any()) } returns Mono.just(user)

        val result = userRepository.findOneById(2L).block()

        result.shouldNotBeNull()
        result shouldBe user
        verify { operations.selectOne(TestUser::class, any()) }
    }

    @Test
    fun testFindByIdReturnsEmpty() {
        every { operations.selectOne(TestUser::class, any()) } returns Mono.empty()

        val result = userRepository.findOneById(TestUser::class, 999L).block()

        result.shouldBeNull()
    }

    @Test
    fun testGenericTypeParametersWithDifferentEntity() {
        val order = TestOrder(orderId = "O001", amount = 199.99)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOne(TestOrder::class, capture(sqlBuilderSlot)) } returns Mono.just(order)

        val result = orderRepository.findOneById(TestOrder::class, "O001").block()

        result.shouldNotBeNull()
        result shouldBe order
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"SELECT * FROM test_orders WHERE order_id = $1"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001")
    }

    @Test
    fun testParameterStyleIsPassedToSqlBuilder() {
        val order = TestOrder(orderId = "O002", amount = 99.99)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        orderRepository.deleteOne(order).block()

        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"DELETE FROM test_orders WHERE order_id = $1"
        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testDefaultParameterStyleIsNone() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        userRepository.deleteOne(user).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testInsertOnePropagatesEntityToOperations() {
        val user = TestUser(username = "charlie", age = 35)
        val savedUser = TestUser(id = 3L, username = "charlie", age = 35)

        every { operations.insert(any<TestUser>()) } returns Mono.just(savedUser)

        val mono = userRepository.insertOne(user)

        mono.block() shouldBeSameInstanceAs savedUser
        verify(exactly = 1) { operations.insert(user) }
    }

    @Test
    fun testFindAllByIds() {
        val users = listOf(
            TestUser(id = 1L, username = "Alice", age = 30),
            TestUser(id = 2L, username = "Bob", age = 25),
            TestUser(id = 3L, username = "Charlie", age = 35)
        )
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(users)

        val result = userRepository.findAllByIds(TestUser::class, listOf(1L, 2L, 3L)).collectList().block()

        result.shouldNotBeNull()
        result shouldBe users
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT * FROM test_users WHERE id IN ( ?, ?, ? )"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L, 2L, 3L)
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllByIdsSingleId() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.just(user)

        val result = userRepository.findAllByIds(TestUser::class, listOf(1L)).collectList().block()

        result.shouldNotBeNull()
        result shouldBe listOf(user)
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT * FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllByIdsReturnsEmpty() {
        every { operations.select(TestUser::class, any()) } returns Flux.empty()

        val result = userRepository.findAllByIds(TestUser::class, listOf(998L, 999L)).collectList().block()

        result.shouldNotBeNull()
        result shouldBe emptyList<TestUser>()
    }

    @Test
    fun testFindAllByIdsWithDifferentEntity() {
        val orders = listOf(
            TestOrder(orderId = "O001", amount = 199.99),
            TestOrder(orderId = "O002", amount = 299.99),
            TestOrder(orderId = "O003", amount = 399.99)
        )
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestOrder::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(orders)

        val result = orderRepository.findAllByIds(TestOrder::class, listOf("O001", "O002", "O003")).collectList().block()

        result.shouldNotBeNull()
        result shouldBe orders
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"SELECT * FROM test_orders WHERE order_id IN ( $1, $2, $3 )"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001", "O002", "O003")
    }

    @Test
    fun testFindAllByIdsInlineExtension() {
        val users = listOf(
            TestUser(id = 1L, username = "Alice", age = 30),
            TestUser(id = 2L, username = "Bob", age = 25)
        )

        every { operations.select(TestUser::class, any()) } returns Flux.fromIterable(users)

        val result = userRepository.findAllByIds(listOf(1L, 2L)).collectList().block()

        result.shouldNotBeNull()
        result shouldBe users
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllByIdsParameterStyleIsPassed() {
        val orders = listOf(
            TestOrder(orderId = "O001", amount = 199.99),
            TestOrder(orderId = "O002", amount = 299.99)
        )
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestOrder::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(orders)

        orderRepository.findAllByIds(TestOrder::class, listOf("O001", "O002")).collectList().block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testFindAllByIdsDefaultParameterStyleIsNone() {
        val users = listOf(TestUser(id = 1L, username = "Alice", age = 30))
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(users)

        userRepository.findAllByIds(TestUser::class, listOf(1L)).collectList().block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testDeleteOneById() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.deleteOneById(TestUser::class, 1L).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "DELETE FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteOneByIdWithZeroRowsAffected() {
        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.deleteOneById(TestUser::class, 999L).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testDeleteOneByIdWithDifferentEntity() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = orderRepository.deleteOneById(TestOrder::class, "O001").block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"DELETE FROM test_orders WHERE order_id = $1"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001")
    }

    @Test
    fun testDeleteOneByIdInlineExtension() {
        every { operations.executeUpdate(any()) } returns Mono.just(1L)

        val result = userRepository.deleteOneById(1L).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteOneByIdParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        orderRepository.deleteOneById(TestOrder::class, "O002").block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testDeleteOneByIdDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        userRepository.deleteOneById(TestUser::class, 1L).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testDeleteManyByIds() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(3L)

        val result = userRepository.deleteManyByIds(TestUser::class, listOf(1L, 2L, 3L)).block()

        result.shouldNotBeNull()
        result shouldBe 3L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "DELETE FROM test_users WHERE id IN ( ?, ?, ? )"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L, 2L, 3L)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteManyByIdsSingleId() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.deleteManyByIds(TestUser::class, listOf(1L)).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "DELETE FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteManyByIdsWithZeroRowsAffected() {
        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.deleteManyByIds(TestUser::class, listOf(998L, 999L)).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testDeleteManyByIdsWithDifferentEntity() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(3L)

        val result = orderRepository.deleteManyByIds(TestOrder::class, listOf("O001", "O002", "O003")).block()

        result.shouldNotBeNull()
        result shouldBe 3L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"DELETE FROM test_orders WHERE order_id IN ( $1, $2, $3 )"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001", "O002", "O003")
    }

    @Test
    fun testDeleteManyByIdsInlineExtension() {
        every { operations.executeUpdate(any()) } returns Mono.just(2L)

        val result = userRepository.deleteManyByIds(listOf(1L, 2L)).block()

        result.shouldNotBeNull()
        result shouldBe 2L
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteManyByIdsParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(2L)

        orderRepository.deleteManyByIds(TestOrder::class, listOf("O001", "O002")).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testDeleteManyByIdsDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        userRepository.deleteManyByIds(TestUser::class, listOf(1L)).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    // ========== findAll tests ==========

    @Test
    fun testFindAll() {
        val users = listOf(
            TestUser(id = 1L, username = "Alice", age = 30),
            TestUser(id = 2L, username = "Bob", age = 25),
            TestUser(id = 3L, username = "Charlie", age = 35)
        )
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(users)

        val result = userRepository.findAll(TestUser::class).collectList().block()

        result.shouldNotBeNull()
        result shouldBe users
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT * FROM test_users"
        sqlBuilderSlot.captured.values shouldContainExactly emptyList<Any>()
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllReturnsEmpty() {
        every { operations.select(TestUser::class, any()) } returns Flux.empty()

        val result = userRepository.findAll(TestUser::class).collectList().block()

        result.shouldNotBeNull()
        result shouldBe emptyList<TestUser>()
    }

    @Test
    fun testFindAllWithDifferentEntity() {
        val orders = listOf(
            TestOrder(orderId = "O001", amount = 199.99),
            TestOrder(orderId = "O002", amount = 299.99)
        )
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestOrder::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(orders)

        val result = orderRepository.findAll(TestOrder::class).collectList().block()

        result.shouldNotBeNull()
        result shouldBe orders
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT * FROM test_orders"
        sqlBuilderSlot.captured.values shouldContainExactly emptyList<Any>()
    }

    @Test
    fun testFindAllParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestOrder::class, capture(sqlBuilderSlot)) } returns Flux.empty()

        orderRepository.findAll(TestOrder::class).collectList().block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testFindAllDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.empty()

        userRepository.findAll(TestUser::class).collectList().block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testFindAllInlineExtension() {
        val users = listOf(
            TestUser(id = 1L, username = "Alice", age = 30),
            TestUser(id = 2L, username = "Bob", age = 25)
        )

        every { operations.select(TestUser::class, any()) } returns Flux.fromIterable(users)

        val result = userRepository.findAll().collectList().block()

        result.shouldNotBeNull()
        result shouldBe users
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllInlineExtensionReturnsEmpty() {
        every { operations.select(TestUser::class, any()) } returns Flux.empty()

        val result = userRepository.findAll().collectList().block()

        result.shouldNotBeNull()
        result shouldBe emptyList<TestUser>()
    }

    // ========== countAll tests ==========

    @Test
    fun testCountAll() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(5L)

        val result = userRepository.countAll(TestUser::class).block()

        result.shouldNotBeNull()
        result shouldBe 5L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT COUNT(*) FROM test_users"
        sqlBuilderSlot.captured.values shouldContainExactly emptyList<Any>()
        verify { operations.selectLong(any()) }
    }

    @Test
    fun testCountAllReturnsZero() {
        every { operations.selectLong(any()) } returns Mono.just(0L)

        val result = userRepository.countAll(TestUser::class).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testCountAllWithDifferentEntity() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(10L)

        val result = orderRepository.countAll(TestOrder::class).block()

        result.shouldNotBeNull()
        result shouldBe 10L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT COUNT(*) FROM test_orders"
        sqlBuilderSlot.captured.values shouldContainExactly emptyList<Any>()
    }

    @Test
    fun testCountAllParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(0L)

        orderRepository.countAll(TestOrder::class).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testCountAllDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(0L)

        userRepository.countAll(TestUser::class).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testCountAllInlineExtension() {
        every { operations.selectLong(any()) } returns Mono.just(3L)

        val result = userRepository.countAll().block()

        result.shouldNotBeNull()
        result shouldBe 3L
        verify { operations.selectLong(any()) }
    }

    @Test
    fun testCountAllInlineExtensionReturnsZero() {
        every { operations.selectLong(any()) } returns Mono.just(0L)

        val result = userRepository.countAll().block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    // ========== countById tests ==========

    @Test
    fun testCountById() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.countById(TestUser::class, 1L).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT COUNT(*) FROM test_users WHERE id = ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf(1L)
        verify { operations.selectLong(any()) }
    }

    @Test
    fun testCountByIdReturnsZero() {
        every { operations.selectLong(any()) } returns Mono.just(0L)

        val result = userRepository.countById(TestUser::class, 999L).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    @Test
    fun testCountByIdWithDifferentEntity() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = orderRepository.countById(TestOrder::class, "O001").block()

        result.shouldNotBeNull()
        result shouldBe 1L
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"SELECT COUNT(*) FROM test_orders WHERE order_id = $1"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001")
    }

    @Test
    fun testCountByIdParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        orderRepository.countById(TestOrder::class, "O001").block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testCountByIdDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectLong(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        userRepository.countById(TestUser::class, 1L).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testCountByIdInlineExtension() {
        every { operations.selectLong(any()) } returns Mono.just(1L)

        val result = userRepository.countById(1L).block()

        result.shouldNotBeNull()
        result shouldBe 1L
        verify { operations.selectLong(any()) }
    }

    @Test
    fun testCountByIdInlineExtensionReturnsZero() {
        every { operations.selectLong(any()) } returns Mono.just(0L)

        val result = userRepository.countById(999L).block()

        result.shouldNotBeNull()
        result shouldBe 0L
    }

    // ========== existsById tests ==========

    @Test
    fun testExistsByIdReturnsTrue() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOneValue<Int>(capture(sqlBuilderSlot)) } returns Mono.just(1)

        val result = userRepository.existsById(TestUser::class, 1L).block()

        result.shouldNotBeNull()
        result shouldBe true
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe "SELECT 1 FROM test_users WHERE id = ? LIMIT ?"
        sqlBuilderSlot.captured.values shouldContainExactly listOf<Any>(1L, 1)
        verify { operations.selectOneValue<Int>(any()) }
    }

    @Test
    fun testExistsByIdReturnsFalse() {
        every { operations.selectOneValue<Int>(any()) } returns Mono.empty()

        val result = userRepository.existsById(TestUser::class, 999L).block()

        result.shouldNotBeNull()
        result shouldBe false
    }

    @Test
    fun testExistsByIdWithDifferentEntity() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOneValue<Int>(capture(sqlBuilderSlot)) } returns Mono.just(1)

        val result = orderRepository.existsById(TestOrder::class, "O001").block()

        result.shouldNotBeNull()
        result shouldBe true
        val sql = sqlBuilderSlot.captured.buildSql()
        sql shouldBe $$"SELECT 1 FROM test_orders WHERE order_id = $1 LIMIT $2"
        sqlBuilderSlot.captured.values shouldContainExactly listOf("O001", 1)
    }

    @Test
    fun testExistsByIdParameterStyleIsPassed() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOneValue<Int>(capture(sqlBuilderSlot)) } returns Mono.empty()

        orderRepository.existsById(TestOrder::class, "O999").block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.POSTGRESQL
    }

    @Test
    fun testExistsByIdDefaultParameterStyleIsNone() {
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOneValue<Int>(capture(sqlBuilderSlot)) } returns Mono.empty()

        userRepository.existsById(TestUser::class, 1L).block()

        sqlBuilderSlot.captured.parameterStyle shouldBe ParameterStyle.NONE
    }

    @Test
    fun testExistsByIdInlineExtensionReturnsTrue() {
        every { operations.selectOneValue<Int>(any()) } returns Mono.just(1)

        val result = userRepository.existsById(1L).block()

        result.shouldNotBeNull()
        result shouldBe true
        verify { operations.selectOneValue<Int>(any()) }
    }

    @Test
    fun testExistsByIdInlineExtensionReturnsFalse() {
        every { operations.selectOneValue<Int>(any()) } returns Mono.empty()

        val result = userRepository.existsById(999L).block()

        result.shouldNotBeNull()
        result shouldBe false
    }

    @Test
    fun testExistsByIdWithCountGreaterThanOne() {
        every { operations.selectOneValue<Int>(any()) } returns Mono.just(1)

        val result = userRepository.existsById(TestUser::class, 1L).block()

        result.shouldNotBeNull()
        result shouldBe true
    }

}
