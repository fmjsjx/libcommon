package com.github.fmjsjx.libcommon.bson

import com.github.fmjsjx.libcommon.bson.ExpressionOperators.*
import com.mongodb.client.model.Sorts
import org.bson.BsonArray
import org.bson.BsonBoolean
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Map.entry

class ExpressionOperatorsTests {

    @Test
    fun testAbs() {
        assertEquals(simple("\$abs"), abs("\$test"))
    }

    @Test
    fun testAdd() {
        assertEquals(
            BsonDocument(
                "\$add", BsonArray(
                    listOf(
                        BsonString("\$n1"),
                        BsonString("\$n2"),
                        BsonInt32(3),
                    )
                )
            ),
            add(listOf("\$n1", "\$n2", 3))
        )
        assertEquals(
            BsonDocument(
                "\$add", BsonArray(
                    listOf(
                        BsonString("\$n1"),
                        BsonString("\$n2"),
                        BsonInt32(3),
                    )
                )
            ),
            add("\$n1", "\$n2", 3)
        )
    }

    @Test
    fun testCeil() {
        assertEquals(simple("\$ceil"), ceil("\$test"))
    }

    @Test
    fun testDivide() {
        assertEquals(
            BsonDocument(
                "\$divide", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            divide("\$test", 2)
        )
    }

    private fun simple(name: String) = BsonDocument(name, BsonString("\$test"))

    @Test
    fun testExp() {
        assertEquals(simple("\$exp"), exp("\$test"))
    }

    @Test
    fun testFloor() {
        assertEquals(simple("\$floor"), floor("\$test"))
    }

    @Test
    fun testLn() {
        assertEquals(simple("\$ln"), ln("\$test"))
    }

    @Test
    fun testLog() {
        assertEquals(
            BsonDocument(
                "\$log", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            log("\$test", 2)
        )
    }

    @Test
    fun testLog10() {
        assertEquals(
            simple("\$log10"),
            log10("\$test")
        )
    }

    @Test
    fun testMod() {
        assertEquals(
            BsonDocument(
                "\$mod", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            mod("\$test", 2)
        )
    }

    @Test
    fun testMultiply() {
        assertEquals(
            BsonDocument(
                "\$multiply", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            multiply("\$test", 2)
        )
    }

    @Test
    fun testRound() {
        assertEquals(
            BsonDocument(
                "\$round", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            round("\$test", 2)
        )
        assertEquals(
            BsonDocument(
                "\$round", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(0),
                    )
                )
            ),
            round("\$test")
        )
    }

    @Test
    fun testSqrt() {
        assertEquals(
            simple("\$sqrt"),
            sqrt("\$test")
        )
    }

    @Test
    fun testSubtract() {
        assertEquals(
            BsonDocument(
                "\$subtract", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            subtract("\$test", 2)
        )
    }

    @Test
    fun testTrunc() {
        assertEquals(
            BsonDocument(
                "\$trunc", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            trunc("\$test", 2)
        )
        assertEquals(
            BsonDocument(
                "\$trunc", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(0),
                    )
                )
            ),
            trunc("\$test")
        )
    }

    @Test
    fun testArrayElemAt() {
        assertEquals(
            BsonDocument(
                "\$arrayElemAt", BsonArray(
                    listOf(
                        BsonString("\$test"),
                        BsonInt32(2),
                    )
                )
            ),
            arrayElemAt("\$test", 2)
        )
        assertEquals(
            BsonDocument(
                "\$arrayElemAt", BsonArray(
                    listOf(
                        BsonArray(
                            listOf(
                                BsonString("a"),
                                BsonString("b"),
                                BsonString("c"),
                            )
                        ),
                        BsonInt32(2),
                    )
                )
            ),
            arrayElemAt(listOf("a", "b", "c"), 2)
        )
        assertEquals(
            BsonDocument(
                "\$arrayElemAt", BsonArray(
                    listOf(
                        BsonArray(
                            listOf(
                                BsonString("a"),
                                BsonString("b"),
                                BsonString("c"),
                            )
                        ),
                        BsonInt32(2),
                    )
                )
            ),
            arrayElemAt(2, "a", "b", "c")
        )
    }

    @Test
    fun testArrayToObject() {
        assertEquals(
            simple("\$arrayToObject"),
            arrayToObject("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$arrayToObject",
                BsonArray(
                    listOf(
                        BsonArray(
                            listOf(
                                BsonArray(
                                    listOf(
                                        BsonString("id"),
                                        BsonString("\$id"),
                                    )
                                ),
                                BsonArray(
                                    listOf(
                                        BsonString("name"),
                                        BsonString("\$name"),
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            arrayToObject(entry("id", "\$id"), entry("name", "\$name"))
        )
    }

    @Test
    fun testArrayToObjectLiteral() {
        assertEquals(
            BsonDocument(
                "\$arrayToObject",
                BsonDocument(
                    "\$literal",
                    BsonArray(
                        listOf(
                            BsonArray(
                                listOf(
                                    BsonString("id"),
                                    BsonString("\$id"),
                                )
                            ),
                            BsonArray(
                                listOf(
                                    BsonString("name"),
                                    BsonString("\$name"),
                                )
                            )
                        )
                    )
                )
            ),
            arrayToObjectLiteral(entry("id", "\$id"), entry("name", "\$name"))
        )
    }

    @Test
    fun testConcatArrays() {
        assertEquals(
            BsonDocument(
                "\$concatArrays",
                BsonArray(
                    listOf(
                        BsonString("\$array1"),
                        BsonString("\$array2"),
                    )
                )
            ),
            concatArrays("\$array1", "\$array2")
        )
    }

    @Test
    fun testFilter() {
        assertEquals(
            BsonDocument(
                "\$filter",
                BsonDocument("input", BsonString("\$input"))
                    .append("cond", BsonString("\$cond"))
                    .append("as", BsonString("as"))
                    .append("limit", BsonString("\$limit"))
            ),
            filter("\$input", "\$cond", "as", "\$limit")
        )
        assertEquals(
            BsonDocument(
                "\$filter",
                BsonDocument("input", BsonString("\$input"))
                    .append("cond", BsonString("\$cond"))
                    .append("as", BsonString("as"))
            ),
            filter("\$input", "\$cond", "as", null)
        )
        assertEquals(
            BsonDocument(
                "\$filter",
                BsonDocument("input", BsonString("\$input"))
                    .append("cond", BsonString("\$cond"))
                    .append("limit", BsonString("\$limit"))
            ),
            filter("\$input", "\$cond", null, "\$limit")
        )
        assertEquals(
            BsonDocument(
                "\$filter",
                BsonDocument("input", BsonString("\$input"))
                    .append("cond", BsonString("\$cond"))
            ),
            filter("\$input", "\$cond")
        )
    }

    @Test
    fun testFirst() {
        assertEquals(
            simple("\$first"),
            first("\$test")
        )
    }

    @Test
    fun testFirstN() {
        assertEquals(
            BsonDocument(
                "\$firstN",
                BsonDocument("n", BsonString("\$n")).append("input", BsonString("\$input"))
            ),
            firstN("\$n", "\$input")
        )
    }

    @Test
    fun testIn() {
        assertEquals(
            BsonDocument(
                "\$in",
                BsonArray(
                    listOf(
                        BsonString("abc"),
                        BsonString("\$array")
                    )
                )
            ),
            `in`("abc", "\$array")
        )
    }

    @Test
    fun testIndexOfArray() {
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                        BsonString("\$start"),
                        BsonString("\$end"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search", "\$start", "\$end")
        )
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                        BsonInt32(0),
                        BsonString("\$end"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search", null, "\$end")
        )
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                        BsonString("\$start"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search", "\$start", null)
        )
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search", null, null)
        )
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                        BsonString("\$start"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search", "\$start")
        )
        assertEquals(
            BsonDocument(
                "\$indexOfArray",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$search"),
                    )
                )
            ),
            indexOfArray("\$array", "\$search")
        )
    }

    @Test
    fun testIsArray() {
        assertEquals(
            simple("\$isArray"),
            isArray("\$test")
        )
    }

    @Test
    fun testLast() {
        assertEquals(
            simple("\$last"),
            last("\$test")
        )
    }

    @Test
    fun testLastN() {
        assertEquals(
            BsonDocument(
                "\$lastN",
                BsonDocument("n", BsonString("\$n"))
                    .append("input", BsonString("\$input"))
            ),
            lastN("\$n", "\$input")
        )
    }

    @Test
    fun testMap() {
        assertEquals(
            BsonDocument(
                "\$map",
                BsonDocument("input", BsonString("\$input"))
                    .append("as", BsonString("as"))
                    .append("in", BsonString("\$in"))
            ),
            map("\$input", "as", "\$in")
        )
        assertEquals(
            BsonDocument(
                "\$map",
                BsonDocument("input", BsonString("\$input"))
                    .append("in", BsonString("\$in"))
            ),
            map("\$input", "\$in")
        )
    }

    @Test
    fun testMaxN() {
        assertEquals(
            BsonDocument(
                "\$maxN",
                BsonDocument("n", BsonString("\$n"))
                    .append("input", BsonString("\$input"))
            ),
            maxN("\$n", "\$input")
        )
    }

    @Test
    fun testMinN() {
        assertEquals(
            BsonDocument(
                "\$minN",
                BsonDocument("n", BsonString("\$n"))
                    .append("input", BsonString("\$input"))
            ),
            minN("\$n", "\$input")
        )
    }

    @Test
    fun testObjectToArray() {
        assertEquals(
            simple("\$objectToArray"),
            objectToArray("\$test")
        )
    }

    @Test
    fun testRange() {
        assertEquals(
            BsonDocument(
                "\$range",
                BsonArray(
                    listOf(
                        BsonString("\$start"),
                        BsonString("\$end"),
                        BsonString("\$step"),
                    )
                )
            ),
            range("\$start", "\$end", "\$step")
        )
        assertEquals(
            BsonDocument(
                "\$range",
                BsonArray(
                    listOf(
                        BsonInt32(2),
                        BsonInt32(6),
                        BsonInt32(2),
                    )
                )
            ),
            range(2, 6, 2)
        )
        assertEquals(
            BsonDocument(
                "\$range",
                BsonArray(
                    listOf(
                        BsonString("\$start"),
                        BsonString("\$end"),
                    )
                )
            ),
            range("\$start", "\$end")
        )
    }

    @Test
    fun testReduce() {
        assertEquals(
            BsonDocument(
                "\$reduce",
                BsonDocument("input", BsonString("\$input"))
                    .append("initialValue", BsonString("\$initialValue"))
                    .append("in", BsonString("\$in"))
            ),
            reduce("\$input", "\$initialValue", "\$in")
        )
    }

    @Test
    fun testReverseArray() {
        assertEquals(
            simple("\$reverseArray"),
            reverseArray("\$test")
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            simple("\$size"),
            size("\$test")
        )
    }

    @Test
    fun slice() {
        assertEquals(
            BsonDocument(
                "\$slice",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$position"),
                        BsonString("\$n"),
                    )
                )
            ),
            slice("\$array", "\$position", "\$n")
        )
        assertEquals(
            BsonDocument(
                "\$slice",
                BsonArray(
                    listOf(
                        BsonString("\$array"),
                        BsonString("\$n"),
                    )
                )
            ),
            slice("\$array", "\$n")
        )
    }

    @Test
    fun testSortArray() {
        assertEquals(
            BsonDocument(
                "\$sortArray",
                BsonDocument("input", BsonString("\$input"))
                    .append("sortBy", BsonDocument("_id", BsonInt32(1)))
            ),
            sortArray("\$input", Sorts.ascending("_id"))
        )
    }

    @Test
    fun testZip() {
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
                    .append("useLongestLength", BsonBoolean.TRUE)
                    .append("defaults", BsonString("\$defaults"))
            ),
            zip(listOf("\$array1", "\$array2"), true, "\$defaults")
        )
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
            ),
            zip(listOf("\$array1", "\$array2"), false, "\$defaults")
        )
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
            ),
            zip(listOf("\$array1", "\$array2"))
        )
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
            ),
            zip("\$array1", "\$array2")
        )
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
                    .append("useLongestLength", BsonBoolean.TRUE)
                    .append("defaults", BsonString("\$defaults"))
            ),
            zip(true, "\$defaults", "\$array1", "\$array2")
        )
        assertEquals(
            BsonDocument(
                "\$zip",
                BsonDocument(
                    "inputs",
                    BsonArray(
                        listOf(
                            BsonString("\$array1"),
                            BsonString("\$array2"),
                        )
                    )
                )
            ),
            zip(false, "\$defaults", "\$array1", "\$array2")
        )
    }

    @Test
    fun testAnd() {
        assertEquals(
            BsonDocument(
                "\$and",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            and("\$e1", "\$e2")
        )
    }

    @Test
    fun testNot() {
        assertEquals(
            BsonDocument(
                "\$not",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                    )
                )
            ),
            not("\$e1")
        )
    }

    @Test
    fun testOr() {
        assertEquals(
            BsonDocument(
                "\$or",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            or("\$e1", "\$e2")
        )
    }

    @Test
    fun testExpr() {
        assertEquals(
            simple("\$expr"),
            expr("\$test")
        )
    }

    @Test
    fun testCmp() {
        assertEquals(
            BsonDocument(
                "\$cmp",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            cmp("\$e1", "\$e2")
        )
    }

    @Test
    fun testEq() {
        assertEquals(
            BsonDocument(
                "\$eq",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            eq("\$e1", "\$e2")
        )
    }

    @Test
    fun testGt() {
        assertEquals(
            BsonDocument(
                "\$gt",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            gt("\$e1", "\$e2")
        )
    }

    @Test
    fun testGte() {
        assertEquals(
            BsonDocument(
                "\$gte",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            gte("\$e1", "\$e2")
        )
    }

    @Test
    fun testLt() {
        assertEquals(
            BsonDocument(
                "\$lt",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            lt("\$e1", "\$e2")
        )
    }

    @Test
    fun testLte() {
        assertEquals(
            BsonDocument(
                "\$lte",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            lte("\$e1", "\$e2")
        )
    }

    @Test
    fun testNe() {
        assertEquals(
            BsonDocument(
                "\$ne",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            ne("\$e1", "\$e2")
        )
    }

    @Test
    fun testCond() {
        assertEquals(
            BsonDocument(
                "\$cond",
                BsonArray(
                    listOf(
                        BsonString("\$if"),
                        BsonString("\$then"),
                        BsonString("\$else"),
                    )
                )
            ),
            cond("\$if", "\$then", "\$else")
        )
        assertEquals(
            BsonDocument(
                "\$cond",
                BsonArray(
                    listOf(
                        BsonString("\$if"),
                        BsonString("\$then"),
                        BsonString("\$else"),
                    )
                )
            ),
            condIf("\$if").then("\$then").build("\$else")
        )
    }

    @Test
    fun testIfNull() {
        assertEquals(
            BsonDocument(
                "\$ifNull",
                BsonArray(
                    listOf(
                        BsonString("\$e0"),
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$replacement"),
                    )
                )
            ),
            ifNull(listOf("\$e0", "\$e1", "\$e2"), "\$replacement")
        )
        assertEquals(
            BsonDocument(
                "\$ifNull",
                BsonArray(
                    listOf(
                        BsonString("\$e0"),
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$replacement"),
                    )
                )
            ),
            ifNull("\$e0", "\$replacement", "\$e1", "\$e2")
        )
        assertEquals(
            BsonDocument(
                "\$ifNull",
                BsonArray(
                    listOf(
                        BsonString("\$e0"),
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$replacement"),
                    )
                )
            ),
            ifNull()
                .input("\$e0")
                .input("\$e1")
                .input("\$e2")
                .replacement("\$replacement")
        )
        assertEquals(
            BsonDocument(
                "\$ifNull",
                BsonArray(
                    listOf(
                        BsonString("\$e0"),
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$replacement"),
                    )
                )
            ),
            ifNull("\$e0")
                .input("\$e1")
                .input("\$e2")
                .replacement("\$replacement")
        )
    }

    @Test
    fun testSwitch() {
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                ).append("default", BsonString("\$default"))
            ),
            switchN(
                BsonArray(
                    listOf(
                        BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                        BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                        BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                    )
                ),
                "\$default"
            )
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                )
            ),
            switchN(
                BsonArray(
                    listOf(
                        BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                        BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                        BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                    )
                ),
                null
            )
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                ).append("default", BsonString("\$default"))
            ),
            switchN(
                listOf(
                    BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                    BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                    BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                ),
                "\$default"
            )
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                )
            ),
            switchN(
                listOf(
                    BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                    BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                    BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                ),
                null
            )
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                ).append("default", BsonString("\$default"))
            ),
            switchN(
                listOf(
                    linkedMapOf(
                        "case" to "\$case1",
                        "then" to "\$then1",
                    ),
                    linkedMapOf(
                        "case" to "\$case2",
                        "then" to "\$then2",
                    ),
                    linkedMapOf(
                        "case" to "\$case3",
                        "then" to "\$then3",
                    ),
                ),
                "\$default"
            )
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                ).append("default", BsonString("\$default"))
            ),
            switch0()
                .onCase("\$case1").then("\$then1")
                .onCase("\$case2").then("\$then2")
                .onCase("\$case3").then("\$then3")
                .build("\$default")
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                )
            ),
            switch0()
                .onCase("\$case1").then("\$then1")
                .onCase("\$case2").then("\$then2")
                .onCase("\$case3").then("\$then3")
                .build()
        )
        assertEquals(
            BsonDocument(
                "\$switch",
                BsonDocument(
                    "branches",
                    BsonArray(
                        listOf(
                            BsonDocument("case", BsonString("\$case1")).append("then", BsonString("\$then1")),
                            BsonDocument("case", BsonString("\$case2")).append("then", BsonString("\$then2")),
                            BsonDocument("case", BsonString("\$case3")).append("then", BsonString("\$then3")),
                        )
                    )
                ).append("default", BsonString("\$default"))
            ),
            switchCase("\$case1").then("\$then1")
                .onCase("\$case2").then("\$then2")
                .onCase("\$case3").then("\$then3")
                .build("\$default")
        )
    }

    @Test
    fun testBinarySize() {
        assertEquals(
            simple("\$binarySize"),
            binarySize("\$test")
        )
    }

    @Test
    fun testBsonSize() {
        assertEquals(
            simple("\$bsonSize"),
            bsonSize("\$test")
        )
    }

    @Test
    fun testDateAdd() {
        assertEquals(
            BsonDocument(
                "\$dateAdd",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("amount", BsonString("\$amount"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dateAdd("\$startDate", "\$unit", "\$amount", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$dateAdd",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("amount", BsonString("\$amount"))
            ),
            dateAdd("\$startDate", "\$unit", "\$amount")
        )
    }

    @Test
    fun testDateDiff() {
        assertEquals(
            BsonDocument(
                "\$dateDiff",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("endDate", BsonString("\$endDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("startOfWeek", BsonString("\$startOfWeek"))
            ),
            dateDiff("\$startDate", "\$endDate", "\$unit", "\$timezone", "\$startOfWeek")
        )
        assertEquals(
            BsonDocument(
                "\$dateDiff",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("endDate", BsonString("\$endDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dateDiff("\$startDate", "\$endDate", "\$unit", "\$timezone", null)
        )
        assertEquals(
            BsonDocument(
                "\$dateDiff",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("endDate", BsonString("\$endDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("startOfWeek", BsonString("\$startOfWeek"))
            ),
            dateDiff("\$startDate", "\$endDate", "\$unit", null, "\$startOfWeek")
        )
        assertEquals(
            BsonDocument(
                "\$dateDiff",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("endDate", BsonString("\$endDate"))
                    .append("unit", BsonString("\$unit"))
            ),
            dateDiff("\$startDate", "\$endDate", "\$unit")
        )
    }

    @Test
    fun testDateFromParts() {
        assertEquals(
            BsonDocument(
                "\$dateFromParts",
                BsonDocument("year", BsonString("\$year"))
                    .append("month", BsonString("\$month"))
                    .append("day", BsonString("\$day"))
                    .append("hour", BsonString("\$hour"))
                    .append("minute", BsonString("\$minute"))
                    .append("second", BsonString("\$second"))
                    .append("millisecond", BsonString("\$millisecond"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dateFromParts("\$year", "\$month", "\$day", "\$hour", "\$minute", "\$second", "\$millisecond", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$dateFromParts",
                BsonDocument("year", BsonString("\$year"))
            ),
            dateFromParts("\$year", null, null, null, null, null, null, null)
        )
    }

    @Test
    fun testDateFromIsoParts() {
        assertEquals(
            BsonDocument(
                "\$dateFromParts",
                BsonDocument("isoWeekYear", BsonString("\$year"))
                    .append("isoWeek", BsonString("\$week"))
                    .append("isoDayOfWeek", BsonString("\$day"))
                    .append("hour", BsonString("\$hour"))
                    .append("minute", BsonString("\$minute"))
                    .append("second", BsonString("\$second"))
                    .append("millisecond", BsonString("\$millisecond"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dateFromIsoParts(
                "\$year",
                "\$week",
                "\$day",
                "\$hour",
                "\$minute",
                "\$second",
                "\$millisecond",
                "\$timezone"
            )
        )
        assertEquals(
            BsonDocument(
                "\$dateFromParts",
                BsonDocument("isoWeekYear", BsonString("\$year"))
            ),
            dateFromIsoParts("\$year", null, null, null, null, null, null, null)
        )
    }

    @Test
    fun testDateFromString() {
        assertEquals(
            BsonDocument(
                "\$dateFromString",
                BsonDocument("dateString", BsonString("\$dateString"))
                    .append("format", BsonString("\$format"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("onError", BsonString("\$onError"))
                    .append("onNull", BsonString("\$onNull"))
            ),
            dateFromString("\$dateString", "\$format", "\$timezone", "\$onError", "\$onNull")
        )
        assertEquals(
            BsonDocument(
                "\$dateFromString",
                BsonDocument("dateString", BsonString("\$dateString"))
            ),
            dateFromString("\$dateString", null, null, null, null)
        )
    }

    @Test
    fun testDateSubtract() {
        assertEquals(
            BsonDocument(
                "\$dateSubtract",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("amount", BsonString("\$amount"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dateSubtract("\$startDate", "\$unit", "\$amount", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$dateSubtract",
                BsonDocument("startDate", BsonString("\$startDate"))
                    .append("unit", BsonString("\$unit"))
                    .append("amount", BsonString("\$amount"))
            ),
            dateSubtract("\$startDate", "\$unit", "\$amount")
        )
    }

    @Test
    fun testDateToParts() {
        assertEquals(
            BsonDocument(
                "\$dateToParts",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("iso8601", BsonString("\$iso8601"))
            ),
            dateToParts("\$date", "\$timezone", "\$iso8601")
        )
        assertEquals(
            BsonDocument(
                "\$dateToParts",
                BsonDocument("date", BsonString("\$date"))
            ),
            dateToParts("\$date")
        )
        assertEquals(
            BsonDocument(
                "\$dateToParts",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("iso8601", BsonBoolean.TRUE)
            ),
            dateToParts("\$date", "\$timezone", true)
        )
    }

    @Test
    fun testDateToString() {
        assertEquals(
            BsonDocument(
                "\$dateToString",
                BsonDocument("date", BsonString("\$date"))
                    .append("format", BsonString("\$format"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("onError", BsonString("\$onError"))
            ),
            dateToString("\$date", "\$format", "\$timezone", "\$onError")
        )
        assertEquals(
            BsonDocument(
                "\$dateToString",
                BsonDocument("date", BsonString("\$date"))
            ),
            dateToString("\$date", null, null, null)
        )
    }

    @Test
    fun testDateTrunc() {
        assertEquals(
            BsonDocument(
                "\$dateTrunc",
                BsonDocument("date", BsonString("\$date"))
                    .append("unit", BsonString("\$unit"))
                    .append("binSize", BsonString("\$binSize"))
                    .append("timezone", BsonString("\$timezone"))
                    .append("startOfWeek", BsonString("\$startOfWeek"))
            ),
            dateTrunc("\$date", "\$unit", "\$binSize", "\$timezone", "\$startOfWeek")
        )
        assertEquals(
            BsonDocument(
                "\$dateTrunc",
                BsonDocument("date", BsonString("\$date"))
                    .append("unit", BsonString("\$unit"))
            ),
            dateTrunc("\$date", "\$unit")
        )
    }

    @Test
    fun testDayOfWeek() {
        assertEquals(
            simple("\$dayOfWeek"),
            dayOfWeek("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$dayOfWeek",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dayOfWeek("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$dayOfWeek",
                BsonDocument("date", BsonString("\$date"))
            ),
            dayOfWeek("\$date", null)
        )
    }

    @Test
    fun testDayOfYear() {
        assertEquals(
            simple("\$dayOfYear"),
            dayOfYear("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$dayOfYear",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            dayOfYear("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$dayOfYear",
                BsonDocument("date", BsonString("\$date"))
            ),
            dayOfYear("\$date", null)
        )
    }

    @Test
    fun testHour() {
        assertEquals(
            simple("\$hour"),
            hour("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$hour",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            hour("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$hour",
                BsonDocument("date", BsonString("\$date"))
            ),
            hour("\$date", null)
        )
    }

    @Test
    fun testIsoDayOfWeek() {
        assertEquals(
            simple("\$isoDayOfWeek"),
            isoDayOfWeek("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$isoDayOfWeek",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            isoDayOfWeek("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$isoDayOfWeek",
                BsonDocument("date", BsonString("\$date"))
            ),
            isoDayOfWeek("\$date", null)
        )
    }

    @Test
    fun testIsoWeek() {
        assertEquals(
            simple("\$isoWeek"),
            isoWeek("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$isoWeek",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            isoWeek("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$isoWeek",
                BsonDocument("date", BsonString("\$date"))
            ),
            isoWeek("\$date", null)
        )
    }

    @Test
    fun testIsoWeekYear() {
        assertEquals(
            simple("\$isoWeekYear"),
            isoWeekYear("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$isoWeekYear",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            isoWeekYear("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$isoWeekYear",
                BsonDocument("date", BsonString("\$date"))
            ),
            isoWeekYear("\$date", null)
        )
    }

    @Test
    fun testMillisecond() {
        assertEquals(
            simple("\$millisecond"),
            millisecond("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$millisecond",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            millisecond("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$millisecond",
                BsonDocument("date", BsonString("\$date"))
            ),
            millisecond("\$date", null)
        )
    }

    @Test
    fun testMinute() {
        assertEquals(
            simple("\$minute"),
            minute("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$minute",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            minute("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$minute",
                BsonDocument("date", BsonString("\$date"))
            ),
            minute("\$date", null)
        )
    }

    @Test
    fun testMonth() {
        assertEquals(
            simple("\$month"),
            month("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$month",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            month("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$month",
                BsonDocument("date", BsonString("\$date"))
            ),
            month("\$date", null)
        )
    }

    @Test
    fun testSecond() {
        assertEquals(
            simple("\$second"),
            second("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$second",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            second("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$second",
                BsonDocument("date", BsonString("\$date"))
            ),
            second("\$date", null)
        )
    }

    @Test
    fun testToDate() {
        assertEquals(
            simple("\$toDate"),
            toDate("\$test")
        )
    }

    @Test
    fun testWeek() {
        assertEquals(
            simple("\$week"),
            week("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$week",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            week("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$week",
                BsonDocument("date", BsonString("\$date"))
            ),
            week("\$date", null)
        )
    }

    @Test
    fun testYear() {
        assertEquals(
            simple("\$year"),
            year("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$year",
                BsonDocument("date", BsonString("\$date"))
                    .append("timezone", BsonString("\$timezone"))
            ),
            year("\$date", "\$timezone")
        )
        assertEquals(
            BsonDocument(
                "\$year",
                BsonDocument("date", BsonString("\$date"))
            ),
            year("\$date", null)
        )
    }

    @Test
    fun testLiteral() {
        assertEquals(
            simple("\$literal"),
            literal("\$test")
        )
    }

    @Test
    fun testGetField() {
        assertEquals(
            BsonDocument(
                "\$getField",
                BsonDocument("field", BsonString("\$field"))
                    .append("input", BsonString("\$input"))
            ),
            getField("\$field", "\$input")
        )
        assertEquals(
            BsonDocument(
                "\$getField",
                BsonDocument("field", BsonString("\$field"))
            ),
            getField("\$field", null)
        )
        assertEquals(
            BsonDocument(
                "\$getField",
                BsonString("\$field")
            ),
            getField("\$field")
        )
    }

    @Test
    fun testRand() {
        assertEquals(
            BsonDocument(
                "\$rand",
                BsonDocument()
            ),
            rand()
        )
    }

    @Test
    fun testSampleRate() {
        assertEquals(
            simple("\$sampleRate"),
            sampleRate("\$test")
        )
    }

    @Test
    fun testMergeObjects() {
        assertEquals(
            BsonDocument(
                "\$mergeObjects",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            mergeObjects(listOf("\$e1", "\$e2"))
        )
        assertEquals(
            BsonDocument(
                "\$mergeObjects",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            mergeObjects("\$e1", "\$e2")
        )
    }

    @Test
    fun testSetField() {
        assertEquals(
            BsonDocument(
                "\$setField",
                BsonDocument("field", BsonString("\$field"))
                    .append("input", BsonString("\$input"))
                    .append("value", BsonString("\$value"))
            ),
            setField("\$field", "\$input", "\$value")
        )
    }

    @Test
    fun testAllElementsTrue() {
        assertEquals(
            BsonDocument(
                "\$allElementsTrue",
                BsonArray(
                    listOf(
                        BsonArray(
                            listOf(
                                BsonString("\$e1"),
                                BsonString("\$e2"),
                                BsonString("\$e3"),
                            )
                        )
                    )
                )
            ),
            allElementsTrue("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testAnyElementTrue() {
        assertEquals(
            BsonDocument(
                "\$anyElementTrue",
                BsonArray(
                    listOf(
                        BsonArray(
                            listOf(
                                BsonString("\$e1"),
                                BsonString("\$e2"),
                                BsonString("\$e3"),
                            )
                        )
                    )
                )
            ),
            anyElementTrue("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testSetDifference() {
        assertEquals(
            BsonDocument(
                "\$setDifference",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            setDifference("\$e1", "\$e2")
        )
    }

    @Test
    fun testSetEquals() {
        assertEquals(
            BsonDocument(
                "\$setEquals",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            setEquals("\$e1", "\$e2")
        )
    }

    @Test
    fun testSetIntersection() {
        assertEquals(
            BsonDocument(
                "\$setIntersection",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            setIntersection("\$e1", "\$e2")
        )
    }

    @Test
    fun testSetIsSubset() {
        assertEquals(
            BsonDocument(
                "\$setIsSubset",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            setIsSubset("\$e1", "\$e2")
        )
    }

    @Test
    fun testSetUnion() {
        assertEquals(
            BsonDocument(
                "\$setUnion",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            setUnion("\$e1", "\$e2")
        )
    }

    @Test
    fun testConcat() {
        assertEquals(
            BsonDocument(
                "\$concat",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            concat("\$e1", "\$e2")
        )
    }

    @Test
    fun testIndexOfBytes() {
        assertEquals(
            BsonDocument(
                "\$indexOfBytes",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$start"),
                        BsonString("\$end"),
                    )
                )
            ),
            indexOfBytes("\$e1", "\$e2", "\$start", "\$end")
        )
        assertEquals(
            BsonDocument(
                "\$indexOfBytes",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            indexOfBytes("\$e1", "\$e2", null, null)
        )
    }

    @Test
    fun testIndexOfCP() {
        assertEquals(
            BsonDocument(
                "\$indexOfCP",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$start"),
                        BsonString("\$end"),
                    )
                )
            ),
            indexOfCP("\$e1", "\$e2", "\$start", "\$end")
        )
        assertEquals(
            BsonDocument(
                "\$indexOfCP",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            indexOfCP("\$e1", "\$e2", null, null)
        )
    }

    @Test
    fun testLtrim() {
        assertEquals(
            BsonDocument(
                "\$ltrim",
                BsonDocument("input", BsonString("\$input"))
                    .append("chars", BsonString("\$chars"))
            ),
            ltrim("\$input", "\$chars")
        )
        assertEquals(
            BsonDocument(
                "\$ltrim",
                BsonDocument("input", BsonString("\$input"))
            ),
            ltrim("\$input")
        )
    }

    @Test
    fun testRegexFind() {
        assertEquals(
            BsonDocument(
                "\$regexFind",
                BsonDocument("input", BsonString("\$input"))
                    .append("regex", BsonString("\$regex"))
                    .append("options", BsonString("\$options"))
            ),
            regexFind("\$input", "\$regex", "\$options")
        )
    }

    @Test
    fun testRegexFindAll() {
        assertEquals(
            BsonDocument(
                "\$regexFindAll",
                BsonDocument("input", BsonString("\$input"))
                    .append("regex", BsonString("\$regex"))
                    .append("options", BsonString("\$options"))
            ),
            regexFindAll("\$input", "\$regex", "\$options")
        )
    }

    @Test
    fun testRegexMatch() {
        assertEquals(
            BsonDocument(
                "\$regexMatch",
                BsonDocument("input", BsonString("\$input"))
                    .append("regex", BsonString("\$regex"))
                    .append("options", BsonString("\$options"))
            ),
            regexMatch("\$input", "\$regex", "\$options")
        )
    }

    @Test
    fun testReplaceOne() {
        assertEquals(
            BsonDocument(
                "\$replaceOne",
                BsonDocument("input", BsonString("\$input"))
                    .append("find", BsonString("\$find"))
                    .append("replacement", BsonString("\$replacement"))
            ),
            replaceOne("\$input", "\$find", "\$replacement")
        )
    }

    @Test
    fun testReplaceAll() {
        assertEquals(
            BsonDocument(
                "\$replaceAll",
                BsonDocument("input", BsonString("\$input"))
                    .append("find", BsonString("\$find"))
                    .append("replacement", BsonString("\$replacement"))
            ),
            replaceAll("\$input", "\$find", "\$replacement")
        )
    }

    @Test
    fun testRtrim() {
        assertEquals(
            BsonDocument(
                "\$rtrim",
                BsonDocument("input", BsonString("\$input"))
                    .append("chars", BsonString("\$chars"))
            ),
            rtrim("\$input", "\$chars")
        )
        assertEquals(
            BsonDocument(
                "\$rtrim",
                BsonDocument("input", BsonString("\$input"))
            ),
            rtrim("\$input")
        )
    }

    @Test
    fun testSplit() {
        assertEquals(
            BsonDocument(
                "\$split",
                BsonArray(
                    listOf(
                        BsonString("\$string"),
                        BsonString("\$delimiter"),
                    )
                )
            ),
            split("\$string", "\$delimiter")
        )
    }

    @Test
    fun testStrLenBytes() {
        assertEquals(
            simple("\$strLenBytes"),
            strLenBytes("\$test")
        )
    }

    @Test
    fun testStrLenCP() {
        assertEquals(
            simple("\$strLenCP"),
            strLenCP("\$test")
        )
    }

    @Test
    fun testStrcasecmp() {
        assertEquals(
            BsonDocument(
                "\$strcasecmp",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            strcasecmp("\$e1", "\$e2")
        )
    }

    @Test
    fun testSubstr() {
        assertEquals(
            BsonDocument(
                "\$substr",
                BsonArray(
                    listOf(
                        BsonString("\$string"),
                        BsonString("\$start"),
                        BsonString("\$length"),
                    )
                )
            ),
            substr("\$string", "\$start", "\$length")
        )
    }

    @Test
    fun testSubstrBytes() {
        assertEquals(
            BsonDocument(
                "\$substrBytes",
                BsonArray(
                    listOf(
                        BsonString("\$string"),
                        BsonString("\$start"),
                        BsonString("\$length"),
                    )
                )
            ),
            substrBytes("\$string", "\$start", "\$length")
        )
    }

    @Test
    fun testSubstrCP() {
        assertEquals(
            BsonDocument(
                "\$substrCP",
                BsonArray(
                    listOf(
                        BsonString("\$string"),
                        BsonString("\$start"),
                        BsonString("\$length"),
                    )
                )
            ),
            substrCP("\$string", "\$start", "\$length")
        )
    }

    @Test
    fun testToLower() {
        assertEquals(
            simple("\$toLower"),
            toLower("\$test")
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            simple("\$toString"),
            toString("\$test")
        )
    }

    @Test
    fun testTrim() {
        assertEquals(
            BsonDocument(
                "\$trim",
                BsonDocument("input", BsonString("\$input"))
                    .append("chars", BsonString("\$chars"))
            ),
            trim("\$input", "\$chars")
        )
        assertEquals(
            BsonDocument(
                "\$trim",
                BsonDocument("input", BsonString("\$input"))
            ),
            trim("\$input", null)
        )
    }

    @Test
    fun testToUpper() {
        assertEquals(
            simple("\$toUpper"),
            toUpper("\$test")
        )
    }

    @Test
    fun testMeta() {
        assertEquals(
            simple("\$meta"),
            meta("\$test")
        )
    }

    @Test
    fun testTsIncrement() {
        assertEquals(
            simple("\$tsIncrement"),
            tsIncrement("\$test")
        )
    }

    @Test
    fun testTsSecond() {
        assertEquals(
            simple("\$tsSecond"),
            tsSecond("\$test")
        )
    }

    @Test
    fun testSin() {
        assertEquals(
            simple("\$sin"),
            sin("\$test")
        )
    }

    @Test
    fun testCos() {
        assertEquals(
            simple("\$cos"),
            cos("\$test")
        )
    }

    @Test
    fun testTan() {
        assertEquals(
            simple("\$tan"),
            tan("\$test")
        )
    }

    @Test
    fun testAsin() {
        assertEquals(
            simple("\$asin"),
            asin("\$test")
        )
    }

    @Test
    fun testAcos() {
        assertEquals(
            simple("\$acos"),
            acos("\$test")
        )
    }

    @Test
    fun testAtan() {
        assertEquals(
            simple("\$atan"),
            atan("\$test")
        )
    }

    @Test
    fun testAtan2() {
        assertEquals(
            BsonDocument(
                "\$atan2",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                    )
                )
            ),
            atan2("\$e1", "\$e2")
        )
    }

    @Test
    fun testAsinh() {
        assertEquals(
            simple("\$asinh"),
            asinh("\$test")
        )
    }

    @Test
    fun testAcosh() {
        assertEquals(
            simple("\$acosh"),
            acosh("\$test")
        )
    }

    @Test
    fun testAtanh() {
        assertEquals(
            simple("\$atanh"),
            atanh("\$test")
        )
    }

    @Test
    fun testSinh() {
        assertEquals(
            simple("\$sinh"),
            sinh("\$test")
        )
    }

    @Test
    fun testCosh() {
        assertEquals(
            simple("\$cosh"),
            cosh("\$test")
        )
    }

    @Test
    fun testTanh() {
        assertEquals(
            simple("\$tanh"),
            tanh("\$test")
        )
    }

    @Test
    fun testDegreesToRadians() {
        assertEquals(
            simple("\$degreesToRadians"),
            degreesToRadians("\$test")
        )
    }

    @Test
    fun testRadiansToDegrees() {
        assertEquals(
            simple("\$radiansToDegrees"),
            radiansToDegrees("\$test")
        )
    }

    @Test
    fun testConvert() {
        assertEquals(
            BsonDocument(
                "\$convert",
                BsonDocument("input", BsonString("\$input"))
                    .append("to", BsonString("\$to"))
                    .append("onError", BsonString("\$onError"))
                    .append("onNull", BsonString("\$onNull"))
            ),
            convert("\$input", "\$to", "\$onError", "\$onNull")
        )
        assertEquals(
            BsonDocument(
                "\$convert",
                BsonDocument("input", BsonString("\$input"))
                    .append("to", BsonString("\$to"))
            ),
            convert("\$input", "\$to")
        )
    }

    @Test
    fun testIsNumber() {
        assertEquals(
            simple("\$isNumber"),
            isNumber("\$test")
        )
    }

    @Test
    fun testToBool() {
        assertEquals(
            simple("\$toBool"),
            toBool("\$test")
        )
    }

    @Test
    fun testToDecimal() {
        assertEquals(
            simple("\$toDecimal"),
            toDecimal("\$test")
        )
    }

    @Test
    fun testToDouble() {
        assertEquals(
            simple("\$toDouble"),
            toDouble("\$test")
        )
    }

    @Test
    fun testToInt() {
        assertEquals(
            simple("\$toInt"),
            toInt("\$test")
        )
    }

    @Test
    fun testToLong() {
        assertEquals(
            simple("\$toLong"),
            toLong("\$test")
        )
    }

    @Test
    fun testToObjectId() {
        assertEquals(
            simple("\$toObjectId"),
            toObjectId("\$test")
        )
    }

    @Test
    fun testType() {
        assertEquals(
            simple("\$type"),
            type("\$test")
        )
    }

    @Test
    fun testAvg() {
        assertEquals(
            simple("\$avg"),
            avg("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$avg",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            avg("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testMax() {
        assertEquals(
            simple("\$max"),
            max("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$max",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            max("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testMin() {
        assertEquals(
            simple("\$min"),
            min("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$min",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            min("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testStdDevPop() {
        assertEquals(
            simple("\$stdDevPop"),
            stdDevPop("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$stdDevPop",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            stdDevPop("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testStdDevSamp() {
        assertEquals(
            simple("\$stdDevSamp"),
            stdDevSamp("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$stdDevSamp",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            stdDevSamp("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testSum() {
        assertEquals(
            simple("\$sum"),
            sum("\$test")
        )
        assertEquals(
            BsonDocument(
                "\$sum",
                BsonArray(
                    listOf(
                        BsonString("\$e1"),
                        BsonString("\$e2"),
                        BsonString("\$e3"),
                    )
                )
            ),
            sum("\$e1", "\$e2", "\$e3")
        )
    }

    @Test
    fun testLet() {
        assertEquals(
            BsonDocument(
                "\$let",
                BsonDocument("vars", BsonString("\$vars"))
                    .append("in", BsonString("\$in"))
            ),
            let("\$vars", "\$in")
        )
        assertEquals(
            BsonDocument(
                "\$let",
                BsonDocument(
                    "vars",
                    BsonDocument("id", BsonString("\$id"))
                        .append("name", BsonString("\$name"))
                ).append("in", BsonString("\$in"))
            ),
            let("\$in", entry("id", "\$id"), entry("name", "\$name"))
        )
    }

}
