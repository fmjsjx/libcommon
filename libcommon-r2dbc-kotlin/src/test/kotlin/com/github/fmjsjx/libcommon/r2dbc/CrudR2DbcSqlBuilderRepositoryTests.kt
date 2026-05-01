package com.github.fmjsjx.libcommon.r2dbc

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.mapping.Table
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable


@Suppress("ReactiveStreamsUnusedPublisher")
class CrudR2DbcSqlBuilderRepositoryTests {

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

        assertNotNull(result)
        assertEquals(savedUser, result)
        verify { operations.insert(user) }
    }

    @Test
    fun testDeleteOne() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.deleteOne(user).block()

        assertNotNull(result)
        assertEquals(1L, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals("DELETE FROM test_users WHERE id = ?", sql)
        assertIterableEquals(listOf(1L), sqlBuilderSlot.captured.values)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testDeleteOneWithZeroRowsAffected() {
        val user = TestUser(id = 99L, username = "unknown", age = 0)

        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.deleteOne(user).block()

        assertNotNull(result)
        assertEquals(0L, result)
    }

    @Test
    fun testUpdateOne() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        val result = userRepository.updateOne(user).block()

        assertNotNull(result)
        assertEquals(1L, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals("UPDATE test_users SET username = ? , age = ? WHERE id = ?", sql)
        assertIterableEquals(listOf("Alice", 30, 1L), sqlBuilderSlot.captured.values)
        verify { operations.executeUpdate(any()) }
    }

    @Test
    fun testUpdateOneWithZeroRowsAffected() {
        val user = TestUser(id = 99L, username = "unknown", age = 0)

        every { operations.executeUpdate(any()) } returns Mono.just(0L)

        val result = userRepository.updateOne(user).block()

        assertNotNull(result)
        assertEquals(0L, result)
    }

    @Test
    fun testFindById() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOne(TestUser::class, capture(sqlBuilderSlot)) } returns Mono.just(user)

        val result = userRepository.findOneById(TestUser::class, 1L).block()

        assertNotNull(result)
        assertEquals(user, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals("SELECT * FROM test_users WHERE id = ?", sql)
        assertIterableEquals(listOf(1L), sqlBuilderSlot.captured.values)
        verify { operations.selectOne(TestUser::class, any()) }
    }

    @Test
    fun testFindByIdInlineExtension() {
        val user = TestUser(id = 2L, username = "bob", age = 25)

        every { operations.selectOne(TestUser::class, any()) } returns Mono.just(user)

        val result = userRepository.findOneById(2L).block()

        assertNotNull(result)
        assertEquals(user, result)
        verify { operations.selectOne(TestUser::class, any()) }
    }

    @Test
    fun testFindByIdReturnsEmpty() {
        every { operations.selectOne(TestUser::class, any()) } returns Mono.empty()

        val result = userRepository.findOneById(TestUser::class, 999L).block()

        assertEquals(null, result)
    }

    @Test
    fun testGenericTypeParametersWithDifferentEntity() {
        val order = TestOrder(orderId = "O001", amount = 199.99)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.selectOne(TestOrder::class, capture(sqlBuilderSlot)) } returns Mono.just(order)

        val result = orderRepository.findOneById(TestOrder::class, "O001").block()

        assertNotNull(result)
        assertEquals(order, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals($$"SELECT * FROM test_orders WHERE order_id = $1", sql)
        assertIterableEquals(listOf("O001"), sqlBuilderSlot.captured.values)
    }

    @Test
    fun testParameterStyleIsPassedToSqlBuilder() {
        val order = TestOrder(orderId = "O002", amount = 99.99)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        orderRepository.deleteOne(order).block()

        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals($$"DELETE FROM test_orders WHERE order_id = $1", sql)
        assertEquals(ParameterStyle.POSTGRESQL, sqlBuilderSlot.captured.parameterStyle)
    }

    @Test
    fun testDefaultParameterStyleIsNone() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.executeUpdate(capture(sqlBuilderSlot)) } returns Mono.just(1L)

        userRepository.deleteOne(user).block()

        assertEquals(ParameterStyle.NONE, sqlBuilderSlot.captured.parameterStyle)
    }

    @Test
    fun testInsertOnePropagatesEntityToOperations() {
        val user = TestUser(username = "charlie", age = 35)
        val savedUser = TestUser(id = 3L, username = "charlie", age = 35)

        every { operations.insert(any<TestUser>()) } returns Mono.just(savedUser)

        val mono = userRepository.insertOne(user)

        assertSame(savedUser, mono.block())
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

        assertNotNull(result)
        assertEquals(users, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals("SELECT * FROM test_users WHERE id IN ( ?, ?, ? )", sql)
        assertIterableEquals(listOf(1L, 2L, 3L), sqlBuilderSlot.captured.values)
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllByIdsSingleId() {
        val user = TestUser(id = 1L, username = "Alice", age = 30)
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.just(user)

        val result = userRepository.findAllByIds(TestUser::class, listOf(1L)).collectList().block()

        assertNotNull(result)
        assertEquals(listOf(user), result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals("SELECT * FROM test_users WHERE id = ?", sql)
        assertIterableEquals(listOf(1L), sqlBuilderSlot.captured.values)
        verify { operations.select(TestUser::class, any()) }
    }

    @Test
    fun testFindAllByIdsReturnsEmpty() {
        every { operations.select(TestUser::class, any()) } returns Flux.empty()

        val result = userRepository.findAllByIds(TestUser::class, listOf(998L, 999L)).collectList().block()

        assertNotNull(result)
        assertEquals(emptyList<TestUser>(), result)
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

        assertNotNull(result)
        assertEquals(orders, result)
        val sql = sqlBuilderSlot.captured.buildSql()
        assertEquals($$"SELECT * FROM test_orders WHERE order_id IN ( $1, $2, $3 )", sql)
        assertIterableEquals(listOf("O001", "O002", "O003"), sqlBuilderSlot.captured.values)
    }

    @Test
    fun testFindAllByIdsInlineExtension() {
        val users = listOf(
            TestUser(id = 1L, username = "Alice", age = 30),
            TestUser(id = 2L, username = "Bob", age = 25)
        )

        every { operations.select(TestUser::class, any()) } returns Flux.fromIterable(users)

        val result = userRepository.findAllByIds(listOf(1L, 2L)).collectList().block()

        assertNotNull(result)
        assertEquals(users, result)
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

        assertEquals(ParameterStyle.POSTGRESQL, sqlBuilderSlot.captured.parameterStyle)
    }

    @Test
    fun testFindAllByIdsDefaultParameterStyleIsNone() {
        val users = listOf(TestUser(id = 1L, username = "Alice", age = 30))
        val sqlBuilderSlot = slot<SqlBuilder>()

        every { operations.select(TestUser::class, capture(sqlBuilderSlot)) } returns Flux.fromIterable(users)

        userRepository.findAllByIds(TestUser::class, listOf(1L)).collectList().block()

        assertEquals(ParameterStyle.NONE, sqlBuilderSlot.captured.parameterStyle)
    }

}
