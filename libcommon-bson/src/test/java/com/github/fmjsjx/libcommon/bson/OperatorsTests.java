package com.github.fmjsjx.libcommon.bson;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.fmjsjx.libcommon.bson.Operators.*;

import com.mongodb.client.model.Sorts;
import org.bson.*;
import org.junit.jupiter.api.Test;

import java.util.List;


public class OperatorsTests {

    @Test
    public void testAbs() {
        assertEquals(simple("$abs"), abs("$test").toBsonDocument());
    }

    private BsonDocument simple(String operator) {
        return new BsonDocument(operator, new BsonString("$test"));
    }

    @Test
    public void testAdd() {
        assertEquals(
                new BsonDocument(
                        "$add",
                        new BsonArray(
                                List.of(
                                        new BsonString("$n1"),
                                        new BsonString("$n2"),
                                        new BsonInt32(3)
                                )
                        )
                ),
                add(List.of("$n1", "$n2", 3)).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$add",
                        new BsonArray(
                                List.of(
                                        new BsonString("$n1"),
                                        new BsonString("$n2"),
                                        new BsonInt32(3)
                                )
                        )
                ),
                add("$n1", "$n2", 3).toBsonDocument()
        );
    }

    @Test
    public void testCeil() {
        assertEquals(simple("$ceil"), ceil("$test").toBsonDocument());
    }

    @Test
    public void testDivide() {
        assertEquals(
                new BsonDocument(
                        "$divide",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                divide("$test", 2).toBsonDocument()
        );
    }

    @Test
    public void testExp() {
        assertEquals(simple("$exp"), exp("$test").toBsonDocument());
    }

    @Test
    public void testFloor() {
        assertEquals(simple("$floor"), floor("$test").toBsonDocument());
    }

    @Test
    public void testLn() {
        assertEquals(simple("$ln"), ln("$test").toBsonDocument());
    }

    @Test
    public void testLog() {
        assertEquals(
                new BsonDocument(
                        "$log",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                log("$test", 2).toBsonDocument()
        );
    }

    @Test
    public void testLog10() {
        assertEquals(simple("$log10"), log10("$test").toBsonDocument());
    }

    @Test
    public void testMod() {
        assertEquals(
                new BsonDocument(
                        "$mod",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                mod("$test", 2).toBsonDocument()
        );
    }

    @Test
    public void testMultiply() {
        assertEquals(
                new BsonDocument(
                        "$multiply",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                multiply("$test", 2).toBsonDocument()
        );
    }

    @Test
    public void testRound() {
        assertEquals(
                new BsonDocument(
                        "$round",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                round("$test", 2).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$round",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(0)
                                )
                        )
                ),
                round("$test").toBsonDocument()
        );
    }

    @Test
    public void testSqrt() {
        assertEquals(simple("$sqrt"), sqrt("$test").toBsonDocument());
    }

    @Test
    public void testSubtract() {
        assertEquals(
                new BsonDocument(
                        "$subtract",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                subtract("$test", 2).toBsonDocument()
        );
    }

    @Test
    public void testTrunc() {
        assertEquals(
                new BsonDocument(
                        "$trunc",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                trunc("$test", 2).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$trunc",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(0)
                                )
                        )
                ),
                trunc("$test").toBsonDocument()
        );
    }

    @Test
    public void testArrayElemAt() {
        assertEquals(
                new BsonDocument(
                        "$arrayElemAt",
                        new BsonArray(
                                List.of(
                                        new BsonString("$test"),
                                        new BsonInt32(2)
                                )
                        )
                ),
                arrayElemAt("$test", 2).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$arrayElemAt",
                        new BsonArray(
                                List.of(
                                        new BsonArray(
                                                List.of(
                                                        new BsonString("a"),
                                                        new BsonString("b"),
                                                        new BsonString("c")
                                                )
                                        ),
                                        new BsonInt32(2)
                                )
                        )
                ),
                arrayElemAt(List.of("a", "b", "c"), 2).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$arrayElemAt",
                        new BsonArray(
                                List.of(
                                        new BsonArray(
                                                List.of(
                                                        new BsonString("a"),
                                                        new BsonString("b"),
                                                        new BsonString("c")
                                                )
                                        ),
                                        new BsonInt32(2)
                                )
                        )
                ),
                arrayElemAt(2, "a", "b", "c").toBsonDocument()
        );
    }

    @Test
    public void testArrayToObject() {
        assertEquals(simple("$arrayToObject"), arrayToObject("$test").toBsonDocument());
        assertEquals(
                new BsonDocument(
                        "$arrayToObject",
                        new BsonArray(
                                List.of(
                                        new BsonArray(
                                                List.of(
                                                        new BsonArray(
                                                                List.of(
                                                                        new BsonString("id"),
                                                                        new BsonString("$id")
                                                                )
                                                        ),
                                                        new BsonArray(
                                                                List.of(
                                                                        new BsonString("name"),
                                                                        new BsonString("$name")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                arrayToObject(entry("id", "$id"), entry("name", "$name")).toBsonDocument()
        );
    }

    @Test
    public void testArrayToObjectLiteral() {
        assertEquals(
                new BsonDocument(
                        "$arrayToObject",
                        new BsonDocument(
                                "$literal",
                                new BsonArray(
                                        List.of(
                                                new BsonArray(
                                                        List.of(
                                                                new BsonString("id"),
                                                                new BsonString("$id")
                                                        )
                                                ),
                                                new BsonArray(
                                                        List.of(
                                                                new BsonString("name"),
                                                                new BsonString("$name")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                arrayToObjectLiteral(entry("id", "$id"), entry("name", "$name")).toBsonDocument()
        );
    }

    @Test
    public void testConcatArrays() {
        assertEquals(
                new BsonDocument(
                        "$concatArrays",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array1"),
                                        new BsonString("$array2")
                                )
                        )
                ),
                concatArrays("$array1", "$array2").toBsonDocument()
        );
    }


    @Test
    public void testFilter() {
        assertEquals(
                new BsonDocument(
                        "$filter",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("cond", new BsonString("$cond"))
                                .append("as", new BsonString("as"))
                                .append("limit", new BsonString("$limit"))
                ),
                filter("$input", "$cond", "as", "$limit").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$filter",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("cond", new BsonString("$cond"))
                                .append("as", new BsonString("as"))
                ),
                filter("$input", "$cond", "as", null).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$filter",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("cond", new BsonString("$cond"))
                                .append("limit", new BsonString("$limit"))
                ),
                filter("$input", "$cond", null, "$limit").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$filter",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("cond", new BsonString("$cond"))
                ),
                filter("$input", "$cond").toBsonDocument()
        );
    }

    @Test
    public void testFirst() {
        assertEquals(
                simple("$first"),
                first("$test").toBsonDocument()
        );
    }

    @Test
    public void testFirstN() {
        assertEquals(
                new BsonDocument(
                        "$firstN",
                        new BsonDocument("n", new BsonString("$n")).append("input", new BsonString("$input"))
                ),
                firstN("$n", "$input").toBsonDocument()
        );
    }


    @Test
    public void testIn() {
        assertEquals(
                new BsonDocument(
                        "$in",
                        new BsonArray(
                                List.of(
                                        new BsonString("abc"),
                                        new BsonString("$array")
                                )
                        )
                ),
                in("abc", "$array").toBsonDocument()
        );
    }

    @Test
    public void testIndexOfArray() {
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search"),
                                        new BsonString("$start"),
                                        new BsonString("$end")
                                )
                        )
                ),
                indexOfArray("$array", "$search", "$start", "$end").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search"),
                                        new BsonInt32(0),
                                        new BsonString("$end")
                                )
                        )
                ),
                indexOfArray("$array", "$search", null, "$end").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search"),
                                        new BsonString("$start")
                                )
                        )
                ),
                indexOfArray("$array", "$search", "$start", null).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search")
                                )
                        )
                ),
                indexOfArray("$array", "$search", null, null).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search"),
                                        new BsonString("$start")
                                )
                        )
                ),
                indexOfArray("$array", "$search", "$start").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfArray",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$search")
                                )
                        )
                ),
                indexOfArray("$array", "$search").toBsonDocument()
        );
    }

    @Test
    public void testIsArray() {
        assertEquals(
                simple("$isArray"),
                isArray("$test").toBsonDocument()
        );
    }

    @Test
    public void testLast() {
        assertEquals(
                simple("$last"),
                last("$test").toBsonDocument()
        );
    }

    @Test
    public void testLastN() {
        assertEquals(
                new BsonDocument(
                        "$lastN",
                        new BsonDocument("n", new BsonString("$n"))
                                .append("input", new BsonString("$input"))
                ),
                lastN("$n", "$input").toBsonDocument()
        );
    }

    @Test
    public void testMap() {
        assertEquals(
                new BsonDocument(
                        "$map",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("as", new BsonString("as"))
                                .append("in", new BsonString("$in"))
                ),
                map("$input", "as", "$in").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$map",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("in", new BsonString("$in"))
                ),
                map("$input", "$in").toBsonDocument()
        );
    }

    @Test
    public void testMaxN() {
        assertEquals(
                new BsonDocument(
                        "$maxN",
                        new BsonDocument("n", new BsonString("$n"))
                                .append("input", new BsonString("$input"))
                ),
                maxN("$n", "$input").toBsonDocument()
        );
    }

    @Test
    public void testMinN() {
        assertEquals(
                new BsonDocument(
                        "$minN",
                        new BsonDocument("n", new BsonString("$n"))
                                .append("input", new BsonString("$input"))
                ),
                minN("$n", "$input").toBsonDocument()
        );
    }

    @Test
    public void testObjectToArray() {
        assertEquals(
                simple("$objectToArray"),
                objectToArray("$test").toBsonDocument()
        );
    }

    @Test
    public void testRange() {
        assertEquals(
                new BsonDocument(
                        "$range",
                        new BsonArray(
                                List.of(
                                        new BsonString("$start"),
                                        new BsonString("$end"),
                                        new BsonString("$step")
                                )
                        )
                ),
                range("$start", "$end", "$step").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$range",
                        new BsonArray(
                                List.of(
                                        new BsonInt32(2),
                                        new BsonInt32(6),
                                        new BsonInt32(2)
                                )
                        )
                ),
                range(2, 6, 2).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$range",
                        new BsonArray(
                                List.of(
                                        new BsonString("$start"),
                                        new BsonString("$end")
                                )
                        )
                ),
                range("$start", "$end").toBsonDocument()
        );
    }

    @Test
    public void testReduce() {
        assertEquals(
                new BsonDocument(
                        "$reduce",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("initialValue", new BsonString("$initialValue"))
                                .append("in", new BsonString("$in"))
                ),
                reduce("$input", "$initialValue", "$in").toBsonDocument()
        );
    }

    @Test
    public void testReverseArray() {
        assertEquals(
                simple("$reverseArray"),
                reverseArray("$test").toBsonDocument()
        );
    }

    @Test
    public void testSize() {
        assertEquals(
                simple("$size"),
                size("$test").toBsonDocument()
        );
    }

    @Test
    public void testSlice() {
        assertEquals(
                new BsonDocument(
                        "$slice",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$position"),
                                        new BsonString("$n")
                                )
                        )
                ),
                slice("$array", "$position", "$n").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$slice",
                        new BsonArray(
                                List.of(
                                        new BsonString("$array"),
                                        new BsonString("$n")
                                )
                        )
                ),
                slice("$array", "$n").toBsonDocument()
        );
    }

    @Test
    public void testSortArray() {
        assertEquals(
                new BsonDocument(
                        "$sortArray",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("sortBy", new BsonDocument("_id", new BsonInt32(1)))
                ),
                sortArray("$input", Sorts.ascending("_id")).toBsonDocument()
        );
    }

    @Test
    public void testZip() {
        assertEquals(
                new BsonDocument(
                        "$zip",
                        new BsonDocument(
                                "inputs",
                                new BsonArray(
                                        List.of(
                                                new BsonString("$array1"),
                                                new BsonString("$array2")
                                        )
                                )
                        )
                                .append("useLongestLength", BsonBoolean.TRUE)
                                .append("defaults", new BsonString("$defaults"))
                ),
                zip(List.of("$array1", "$array2"), true, "$defaults").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$zip",
                        new BsonDocument(
                                "inputs",
                                new BsonArray(
                                        List.of(
                                                new BsonString("$array1"),
                                                new BsonString("$array2")
                                        )
                                )
                        )
                ),
                zip(List.of("$array1", "$array2"), false, "$defaults").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$zip",
                        new BsonDocument(
                                "inputs",
                                new BsonArray(
                                        List.of(
                                                new BsonString("$array1"),
                                                new BsonString("$array2")
                                        )
                                )
                        )
                ),
                zip(List.of("$array1", "$array2")).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$zip",
                        new BsonDocument(
                                "inputs",
                                new BsonArray(
                                        List.of(
                                                new BsonString("$array1"),
                                                new BsonString("$array2")
                                        )
                                )
                        )
                                .append("useLongestLength", BsonBoolean.TRUE)
                                .append("defaults", new BsonString("$defaults"))
                ),
                zip(true, "$defaults", "$array1", "$array2").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$zip",
                        new BsonDocument(
                                "inputs",
                                new BsonArray(
                                        List.of(
                                                new BsonString("$array1"),
                                                new BsonString("$array2")
                                        )
                                )
                        )
                ),
                zip(false, "$defaults", "$array1", "$array2").toBsonDocument()
        );
    }

    @Test
    public void testAnd() {
        assertEquals(
                new BsonDocument(
                        "$and",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                and("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testNot() {
        assertEquals(
                new BsonDocument(
                        "$not",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1")
                                )
                        )
                ),
                not("$e1").toBsonDocument()
        );
    }

    @Test
    public void testOr() {
        assertEquals(
                new BsonDocument(
                        "$or",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                or("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testExpr() {
        assertEquals(
                simple("$expr"),
                expr("$test").toBsonDocument()
        );
    }

    @Test
    public void testCmp() {
        assertEquals(
                new BsonDocument(
                        "$cmp",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                cmp("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testEq() {
        assertEquals(
                new BsonDocument(
                        "$eq",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                eq("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testGt() {
        assertEquals(
                new BsonDocument(
                        "$gt",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                gt("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testGte() {
        assertEquals(
                new BsonDocument(
                        "$gte",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                gte("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testLt() {
        assertEquals(
                new BsonDocument(
                        "$lt",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                lt("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testLte() {
        assertEquals(
                new BsonDocument(
                        "$lte",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                lte("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testNe() {
        assertEquals(
                new BsonDocument(
                        "$ne",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                ne("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testCond() {
        assertEquals(
                new BsonDocument(
                        "$cond",
                        new BsonArray(
                                List.of(
                                        new BsonString("$if"),
                                        new BsonString("$then"),
                                        new BsonString("$else")
                                )
                        )
                ),
                cond("$if", "$then", "$else").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$cond",
                        new BsonArray(
                                List.of(
                                        new BsonString("$if"),
                                        new BsonString("$then"),
                                        new BsonString("$else")
                                )
                        )
                ),
                condIf("$if").then("$then").build("$else").toBsonDocument()
        );
    }

    @Test
    public void testIfNull() {
        assertEquals(
                new BsonDocument(
                        "$ifNull",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e0"),
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$replacement")
                                )
                        )
                ),
                ifNull(List.of("$e0", "$e1", "$e2"), "$replacement").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$ifNull",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e0"),
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$replacement")
                                )
                        )
                ),
                ifNull("$e0", "$replacement", "$e1", "$e2").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$ifNull",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e0"),
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$replacement")
                                )
                        )
                ),
                ifNull()
                        .input("$e0")
                        .input("$e1")
                        .input("$e2")
                        .replacement("$replacement").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$ifNull",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e0"),
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$replacement")
                                )
                        )
                ),
                ifNull("$e0")
                        .input("$e1")
                        .input("$e2")
                        .replacement("$replacement").toBsonDocument()
        );
    }

    @Test
    public void testSwitch() {
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        ).append("default", new BsonString("$default"))
                ),
                switchN(
                        List.of(
                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                        ),
                        "$default"
                ).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        )
                ),
                switchN(
                        List.of(
                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                        ),
                        null
                ).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        ).append("default", new BsonString("$default"))
                ),
                switchN(
                        List.of(
                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                        ),
                        "$default"
                ).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        )
                ),
                switchN(
                        List.of(
                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                        ),
                        null
                ).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        ).append("default", new BsonString("$default"))
                ),
                switch0()
                        .onCase("$case1").then("$then1")
                        .onCase("$case2").then("$then2")
                        .onCase("$case3").then("$then3")
                        .build("$default").toBsonDocument()
        );
        assertEquals(
                3,
                switch0()
                        .onCase("$case1").then("$then1")
                        .onCase("$case2").then("$then2")
                        .onCase("$case3").then("$then3").branches().size()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        )
                ),
                switch0()
                        .onCase("$case1").then("$then1")
                        .onCase("$case2").then("$then2")
                        .onCase("$case3").then("$then3")
                        .build().toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        ).append("default", new BsonString("$default"))
                ),
                switchCase("$case1").then("$then1")
                        .onCase("$case2").then("$then2")
                        .onCase("$case3").then("$then3")
                        .build("$default").toBsonDocument()
        );
    }

    @Test
    public void testBinarySize() {
        assertEquals(
                simple("$binarySize"),
                binarySize("$test").toBsonDocument()
        );
    }

    @Test
    public void testBsonSize() {
        assertEquals(
                simple("$bsonSize"),
                bsonSize("$test").toBsonDocument()
        );
    }

    @Test
    public void testDateAdd() {
        assertEquals(
                new BsonDocument(
                        "$dateAdd",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("amount", new BsonString("$amount"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dateAdd("$startDate", "$unit", "$amount", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateAdd",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("amount", new BsonString("$amount"))
                ),
                dateAdd("$startDate", "$unit", "$amount").toBsonDocument()
        );
    }

    @Test
    public void testDateDiff() {
        assertEquals(
                new BsonDocument(
                        "$dateDiff",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("endDate", new BsonString("$endDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("startOfWeek", new BsonString("$startOfWeek"))
                ),
                dateDiff("$startDate", "$endDate", "$unit", "$timezone", "$startOfWeek").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateDiff",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("endDate", new BsonString("$endDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dateDiff("$startDate", "$endDate", "$unit", "$timezone", null).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateDiff",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("endDate", new BsonString("$endDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("startOfWeek", new BsonString("$startOfWeek"))
                ),
                dateDiff("$startDate", "$endDate", "$unit", null, "$startOfWeek").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateDiff",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("endDate", new BsonString("$endDate"))
                                .append("unit", new BsonString("$unit"))
                ),
                dateDiff("$startDate", "$endDate", "$unit").toBsonDocument()
        );
    }

    @Test
    public void testDateFromParts() {
        assertEquals(
                new BsonDocument(
                        "$dateFromParts",
                        new BsonDocument("year", new BsonString("$year"))
                                .append("month", new BsonString("$month"))
                                .append("day", new BsonString("$day"))
                                .append("hour", new BsonString("$hour"))
                                .append("minute", new BsonString("$minute"))
                                .append("second", new BsonString("$second"))
                                .append("millisecond", new BsonString("$millisecond"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dateFromParts("$year", "$month", "$day", "$hour", "$minute", "$second", "$millisecond", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateFromParts",
                        new BsonDocument("year", new BsonString("$year"))
                ),
                dateFromParts("$year", null, null, null, null, null, null, null).toBsonDocument()
        );
    }

    @Test
    public void testDateFromIsoParts() {
        assertEquals(
                new BsonDocument(
                        "$dateFromParts",
                        new BsonDocument("isoWeekYear", new BsonString("$year"))
                                .append("isoWeek", new BsonString("$week"))
                                .append("isoDayOfWeek", new BsonString("$day"))
                                .append("hour", new BsonString("$hour"))
                                .append("minute", new BsonString("$minute"))
                                .append("second", new BsonString("$second"))
                                .append("millisecond", new BsonString("$millisecond"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dateFromIsoParts(
                        "$year",
                        "$week",
                        "$day",
                        "$hour",
                        "$minute",
                        "$second",
                        "$millisecond",
                        "$timezone"
                ).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateFromParts",
                        new BsonDocument("isoWeekYear", new BsonString("$year"))
                ),
                dateFromIsoParts("$year", null, null, null, null, null, null, null).toBsonDocument()
        );
    }

    @Test
    public void testDateFromString() {
        assertEquals(
                new BsonDocument(
                        "$dateFromString",
                        new BsonDocument("dateString", new BsonString("$dateString"))
                                .append("format", new BsonString("$format"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("onError", new BsonString("$onError"))
                                .append("onNull", new BsonString("$onNull"))
                ),
                dateFromString("$dateString", "$format", "$timezone", "$onError", "$onNull").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateFromString",
                        new BsonDocument("dateString", new BsonString("$dateString"))
                ),
                dateFromString("$dateString", null, null, null, null).toBsonDocument()
        );
    }

    @Test
    public void testDateSubtract() {
        assertEquals(
                new BsonDocument(
                        "$dateSubtract",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("amount", new BsonString("$amount"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dateSubtract("$startDate", "$unit", "$amount", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateSubtract",
                        new BsonDocument("startDate", new BsonString("$startDate"))
                                .append("unit", new BsonString("$unit"))
                                .append("amount", new BsonString("$amount"))
                ),
                dateSubtract("$startDate", "$unit", "$amount").toBsonDocument()
        );
    }

    @Test
    public void testDateToParts() {
        assertEquals(
                new BsonDocument(
                        "$dateToParts",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("iso8601", new BsonString("$iso8601"))
                ),
                dateToParts("$date", "$timezone", "$iso8601").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateToParts",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                dateToParts("$date").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateToParts",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("iso8601", BsonBoolean.TRUE)
                ),
                dateToParts("$date", "$timezone", true).toBsonDocument()
        );
    }

    @Test
    public void testDateToString() {
        assertEquals(
                new BsonDocument(
                        "$dateToString",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("format", new BsonString("$format"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("onError", new BsonString("$onError"))
                ),
                dateToString("$date", "$format", "$timezone", "$onError").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateToString",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                dateToString("$date", null, null, null).toBsonDocument()
        );
    }

    @Test
    public void testDateTrunc() {
        assertEquals(
                new BsonDocument(
                        "$dateTrunc",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("unit", new BsonString("$unit"))
                                .append("binSize", new BsonString("$binSize"))
                                .append("timezone", new BsonString("$timezone"))
                                .append("startOfWeek", new BsonString("$startOfWeek"))
                ),
                dateTrunc("$date", "$unit", "$binSize", "$timezone", "$startOfWeek").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dateTrunc",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("unit", new BsonString("$unit"))
                ),
                dateTrunc("$date", "$unit").toBsonDocument()
        );
    }

    @Test
    public void testDayOfWeek() {
        assertEquals(
                simple("$dayOfWeek"),
                dayOfWeek("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dayOfWeek",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dayOfWeek("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dayOfWeek",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                dayOfWeek("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testDayOfYear() {
        assertEquals(
                simple("$dayOfYear"),
                dayOfYear("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dayOfYear",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                dayOfYear("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$dayOfYear",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                dayOfYear("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testHour() {
        assertEquals(
                simple("$hour"),
                hour("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$hour",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                hour("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$hour",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                hour("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testIsoDayOfWeek() {
        assertEquals(
                simple("$isoDayOfWeek"),
                isoDayOfWeek("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoDayOfWeek",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                isoDayOfWeek("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoDayOfWeek",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                isoDayOfWeek("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testIsoWeek() {
        assertEquals(
                simple("$isoWeek"),
                isoWeek("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoWeek",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                isoWeek("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoWeek",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                isoWeek("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testIsoWeekYear() {
        assertEquals(
                simple("$isoWeekYear"),
                isoWeekYear("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoWeekYear",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                isoWeekYear("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$isoWeekYear",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                isoWeekYear("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testMillisecond() {
        assertEquals(
                simple("$millisecond"),
                millisecond("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$millisecond",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                millisecond("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$millisecond",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                millisecond("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testMinute() {
        assertEquals(
                simple("$minute"),
                minute("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$minute",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                minute("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$minute",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                minute("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testMonth() {
        assertEquals(
                simple("$month"),
                month("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$month",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                month("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$month",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                month("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testSecond() {
        assertEquals(
                simple("$second"),
                second("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$second",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                second("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$second",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                second("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testToDate() {
        assertEquals(
                simple("$toDate"),
                toDate("$test").toBsonDocument()
        );
    }

    @Test
    public void testWeek() {
        assertEquals(
                simple("$week"),
                week("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$week",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                week("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$week",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                week("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testYear() {
        assertEquals(
                simple("$year"),
                year("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$year",
                        new BsonDocument("date", new BsonString("$date"))
                                .append("timezone", new BsonString("$timezone"))
                ),
                year("$date", "$timezone").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$year",
                        new BsonDocument("date", new BsonString("$date"))
                ),
                year("$date", null).toBsonDocument()
        );
    }

    @Test
    public void testLiteral() {
        assertEquals(
                simple("$literal"),
                literal("$test").toBsonDocument()
        );
    }

    @Test
    public void testGetField() {
        assertEquals(
                new BsonDocument(
                        "$getField",
                        new BsonDocument("field", new BsonString("$field"))
                                .append("input", new BsonString("$input"))
                ),
                getField("$field", "$input").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$getField",
                        new BsonDocument("field", new BsonString("$field"))
                ),
                getField("$field", null).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$getField",
                        new BsonString("$field")
                ),
                getField("$field").toBsonDocument()
        );
    }

    @Test
    public void testRand() {
        assertEquals(
                new BsonDocument(
                        "$rand",
                        new BsonDocument()
                ),
                rand().toBsonDocument()
        );
    }

    @Test
    public void testSampleRate() {
        assertEquals(
                simple("$sampleRate"),
                sampleRate("$test").toBsonDocument()
        );
    }

    @Test
    public void testMergeObjects() {
        assertEquals(
                new BsonDocument(
                        "$mergeObjects",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                mergeObjects(List.of("$e1", "$e2")).toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$mergeObjects",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                mergeObjects("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSetField() {
        assertEquals(
                new BsonDocument(
                        "$setField",
                        new BsonDocument("field", new BsonString("$field"))
                                .append("input", new BsonString("$input"))
                                .append("value", new BsonString("$value"))
                ),
                setField("$field", "$input", "$value").toBsonDocument()
        );
    }

    @Test
    public void testAllElementsTrue() {
        assertEquals(
                new BsonDocument(
                        "$allElementsTrue",
                        new BsonArray(
                                List.of(
                                        new BsonArray(
                                                List.of(
                                                        new BsonString("$e1"),
                                                        new BsonString("$e2"),
                                                        new BsonString("$e3")
                                                )
                                        )
                                )
                        )
                ),
                allElementsTrue("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testAnyElementTrue() {
        assertEquals(
                new BsonDocument(
                        "$anyElementTrue",
                        new BsonArray(
                                List.of(
                                        new BsonArray(
                                                List.of(
                                                        new BsonString("$e1"),
                                                        new BsonString("$e2"),
                                                        new BsonString("$e3")
                                                )
                                        )
                                )
                        )
                ),
                anyElementTrue("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testSetDifference() {
        assertEquals(
                new BsonDocument(
                        "$setDifference",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                setDifference("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSetEquals() {
        assertEquals(
                new BsonDocument(
                        "$setEquals",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                setEquals("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSetIntersection() {
        assertEquals(
                new BsonDocument(
                        "$setIntersection",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                setIntersection("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSetIsSubset() {
        assertEquals(
                new BsonDocument(
                        "$setIsSubset",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                setIsSubset("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSetUnion() {
        assertEquals(
                new BsonDocument(
                        "$setUnion",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                setUnion("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testConcat() {
        assertEquals(
                new BsonDocument(
                        "$concat",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                concat("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testIndexOfBytes() {
        assertEquals(
                new BsonDocument(
                        "$indexOfBytes",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$start"),
                                        new BsonString("$end")
                                )
                        )
                ),
                indexOfBytes("$e1", "$e2", "$start", "$end").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfBytes",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                indexOfBytes("$e1", "$e2", null, null).toBsonDocument()
        );
    }

    @Test
    public void testIndexOfCP() {
        assertEquals(
                new BsonDocument(
                        "$indexOfCP",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$start"),
                                        new BsonString("$end")
                                )
                        )
                ),
                indexOfCP("$e1", "$e2", "$start", "$end").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$indexOfCP",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                indexOfCP("$e1", "$e2", null, null).toBsonDocument()
        );
    }

    @Test
    public void testLtrim() {
        assertEquals(
                new BsonDocument(
                        "$ltrim",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("chars", new BsonString("$chars"))
                ),
                ltrim("$input", "$chars").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$ltrim",
                        new BsonDocument("input", new BsonString("$input"))
                ),
                ltrim("$input").toBsonDocument()
        );
    }

    @Test
    public void testRegexFind() {
        assertEquals(
                new BsonDocument(
                        "$regexFind",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("regex", new BsonString("$regex"))
                                .append("options", new BsonString("$options"))
                ),
                regexFind("$input", "$regex", "$options").toBsonDocument()
        );
    }

    @Test
    public void testRegexFindAll() {
        assertEquals(
                new BsonDocument(
                        "$regexFindAll",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("regex", new BsonString("$regex"))
                                .append("options", new BsonString("$options"))
                ),
                regexFindAll("$input", "$regex", "$options").toBsonDocument()
        );
    }

    @Test
    public void testRegexMatch() {
        assertEquals(
                new BsonDocument(
                        "$regexMatch",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("regex", new BsonString("$regex"))
                                .append("options", new BsonString("$options"))
                ),
                regexMatch("$input", "$regex", "$options").toBsonDocument()
        );
    }

    @Test
    public void testReplaceOne() {
        assertEquals(
                new BsonDocument(
                        "$replaceOne",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("find", new BsonString("$find"))
                                .append("replacement", new BsonString("$replacement"))
                ),
                replaceOne("$input", "$find", "$replacement").toBsonDocument()
        );
    }

    @Test
    public void testReplaceAll() {
        assertEquals(
                new BsonDocument(
                        "$replaceAll",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("find", new BsonString("$find"))
                                .append("replacement", new BsonString("$replacement"))
                ),
                replaceAll("$input", "$find", "$replacement").toBsonDocument()
        );
    }

    @Test
    public void testRtrim() {
        assertEquals(
                new BsonDocument(
                        "$rtrim",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("chars", new BsonString("$chars"))
                ),
                rtrim("$input", "$chars").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$rtrim",
                        new BsonDocument("input", new BsonString("$input"))
                ),
                rtrim("$input").toBsonDocument()
        );
    }

    @Test
    public void testSplit() {
        assertEquals(
                new BsonDocument(
                        "$split",
                        new BsonArray(
                                List.of(
                                        new BsonString("$string"),
                                        new BsonString("$delimiter")
                                )
                        )
                ),
                split("$string", "$delimiter").toBsonDocument()
        );
    }

    @Test
    public void testStrLenBytes() {
        assertEquals(
                simple("$strLenBytes"),
                strLenBytes("$test").toBsonDocument()
        );
    }

    @Test
    public void testStrLenCP() {
        assertEquals(
                simple("$strLenCP"),
                strLenCP("$test").toBsonDocument()
        );
    }

    @Test
    public void testStrcasecmp() {
        assertEquals(
                new BsonDocument(
                        "$strcasecmp",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                strcasecmp("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testSubstr() {
        assertEquals(
                new BsonDocument(
                        "$substr",
                        new BsonArray(
                                List.of(
                                        new BsonString("$string"),
                                        new BsonString("$start"),
                                        new BsonString("$length")
                                )
                        )
                ),
                substr("$string", "$start", "$length").toBsonDocument()
        );
    }

    @Test
    public void testSubstrBytes() {
        assertEquals(
                new BsonDocument(
                        "$substrBytes",
                        new BsonArray(
                                List.of(
                                        new BsonString("$string"),
                                        new BsonString("$start"),
                                        new BsonString("$length")
                                )
                        )
                ),
                substrBytes("$string", "$start", "$length").toBsonDocument()
        );
    }

    @Test
    public void testSubstrCP() {
        assertEquals(
                new BsonDocument(
                        "$substrCP",
                        new BsonArray(
                                List.of(
                                        new BsonString("$string"),
                                        new BsonString("$start"),
                                        new BsonString("$length")
                                )
                        )
                ),
                substrCP("$string", "$start", "$length").toBsonDocument()
        );
    }

    @Test
    public void testToLower() {
        assertEquals(
                simple("$toLower"),
                toLower("$test").toBsonDocument()
        );
    }

    @Test
    public void testToString() {
        assertEquals(
                simple("$toString"),
                Operators.toString("$test").toBsonDocument()
        );
    }

    @Test
    public void testTrim() {
        assertEquals(
                new BsonDocument(
                        "$trim",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("chars", new BsonString("$chars"))
                ),
                trim("$input", "$chars").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$trim",
                        new BsonDocument("input", new BsonString("$input"))
                ),
                trim("$input", null).toBsonDocument()
        );
    }

    @Test
    public void testToUpper() {
        assertEquals(
                simple("$toUpper"),
                toUpper("$test").toBsonDocument()
        );
    }

    @Test
    public void testMeta() {
        assertEquals(
                simple("$meta"),
                meta("$test").toBsonDocument()
        );
    }

    @Test
    public void testTsIncrement() {
        assertEquals(
                simple("$tsIncrement"),
                tsIncrement("$test").toBsonDocument()
        );
    }

    @Test
    public void testTsSecond() {
        assertEquals(
                simple("$tsSecond"),
                tsSecond("$test").toBsonDocument()
        );
    }

    @Test
    public void testSin() {
        assertEquals(
                simple("$sin"),
                sin("$test").toBsonDocument()
        );
    }

    @Test
    public void testCos() {
        assertEquals(
                simple("$cos"),
                cos("$test").toBsonDocument()
        );
    }

    @Test
    public void testTan() {
        assertEquals(
                simple("$tan"),
                tan("$test").toBsonDocument()
        );
    }

    @Test
    public void testAsin() {
        assertEquals(
                simple("$asin"),
                asin("$test").toBsonDocument()
        );
    }

    @Test
    public void testAcos() {
        assertEquals(
                simple("$acos"),
                acos("$test").toBsonDocument()
        );
    }

    @Test
    public void testAtan() {
        assertEquals(
                simple("$atan"),
                atan("$test").toBsonDocument()
        );
    }

    @Test
    public void testAtan2() {
        assertEquals(
                new BsonDocument(
                        "$atan2",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2")
                                )
                        )
                ),
                atan2("$e1", "$e2").toBsonDocument()
        );
    }

    @Test
    public void testAsinh() {
        assertEquals(
                simple("$asinh"),
                asinh("$test").toBsonDocument()
        );
    }

    @Test
    public void testAcosh() {
        assertEquals(
                simple("$acosh"),
                acosh("$test").toBsonDocument()
        );
    }

    @Test
    public void testAtanh() {
        assertEquals(
                simple("$atanh"),
                atanh("$test").toBsonDocument()
        );
    }

    @Test
    public void testSinh() {
        assertEquals(
                simple("$sinh"),
                sinh("$test").toBsonDocument()
        );
    }

    @Test
    public void testCosh() {
        assertEquals(
                simple("$cosh"),
                cosh("$test").toBsonDocument()
        );
    }

    @Test
    public void testTanh() {
        assertEquals(
                simple("$tanh"),
                tanh("$test").toBsonDocument()
        );
    }

    @Test
    public void testDegreesToRadians() {
        assertEquals(
                simple("$degreesToRadians"),
                degreesToRadians("$test").toBsonDocument()
        );
    }

    @Test
    public void testRadiansToDegrees() {
        assertEquals(
                simple("$radiansToDegrees"),
                radiansToDegrees("$test").toBsonDocument()
        );
    }

    @Test
    public void testConvert() {
        assertEquals(
                new BsonDocument(
                        "$convert",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("to", new BsonString("$to"))
                                .append("onError", new BsonString("$onError"))
                                .append("onNull", new BsonString("$onNull"))
                ),
                convert("$input", "$to", "$onError", "$onNull").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$convert",
                        new BsonDocument("input", new BsonString("$input"))
                                .append("to", new BsonString("$to"))
                ),
                convert("$input", "$to").toBsonDocument()
        );
    }

    @Test
    public void testIsNumber() {
        assertEquals(
                simple("$isNumber"),
                isNumber("$test").toBsonDocument()
        );
    }

    @Test
    public void testToBool() {
        assertEquals(
                simple("$toBool"),
                toBool("$test").toBsonDocument()
        );
    }

    @Test
    public void testToDecimal() {
        assertEquals(
                simple("$toDecimal"),
                toDecimal("$test").toBsonDocument()
        );
    }

    @Test
    public void testToDouble() {
        assertEquals(
                simple("$toDouble"),
                toDouble("$test").toBsonDocument()
        );
    }

    @Test
    public void testToInt() {
        assertEquals(
                simple("$toInt"),
                toInt("$test").toBsonDocument()
        );
    }

    @Test
    public void testToLong() {
        assertEquals(
                simple("$toLong"),
                toLong("$test").toBsonDocument()
        );
    }

    @Test
    public void testToObjectId() {
        assertEquals(
                simple("$toObjectId"),
                toObjectId("$test").toBsonDocument()
        );
    }

    @Test
    public void testType() {
        assertEquals(
                simple("$type"),
                type("$test").toBsonDocument()
        );
    }


    @Test
    public void testAvg() {
        assertEquals(
                simple("$avg"),
                avg("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$avg",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                avg("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testMax() {
        assertEquals(
                simple("$max"),
                max("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$max",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                max("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testMin() {
        assertEquals(
                simple("$min"),
                min("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$min",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                min("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testPush() {
        assertEquals(
                simple("$push"),
                push("$test").toBsonDocument()
        );
    }

    @Test
    public void testStdDevPop() {
        assertEquals(
                simple("$stdDevPop"),
                stdDevPop("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$stdDevPop",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                stdDevPop("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testStdDevSamp() {
        assertEquals(
                simple("$stdDevSamp"),
                stdDevSamp("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$stdDevSamp",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                stdDevSamp("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testSum() {
        assertEquals(
                simple("$sum"),
                sum("$test").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$sum",
                        new BsonArray(
                                List.of(
                                        new BsonString("$e1"),
                                        new BsonString("$e2"),
                                        new BsonString("$e3")
                                )
                        )
                ),
                sum("$e1", "$e2", "$e3").toBsonDocument()
        );
    }

    @Test
    public void testLet() {
        assertEquals(
                new BsonDocument(
                        "$let",
                        new BsonDocument("vars", new BsonString("$vars"))
                                .append("in", new BsonString("$in"))
                ),
                let("$vars", "$in").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$let",
                        new BsonDocument(
                                "vars",
                                new BsonDocument("id", new BsonString("$id"))
                                        .append("name", new BsonString("$name"))
                        ).append("in", new BsonString("$in"))
                ),
                let("$in", entry("id", "$id"), entry("name", "$name")).toBsonDocument()
        );
    }

    @Test
    public void testSwitchV2() {
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        ).append("default", new BsonString("$default"))
                ),
                switchV2()
                        .branch("$case1", "$then1")
                        .branch("$case2", "$then2")
                        .branch("$case3", "$then3")
                        .build("$default").toBsonDocument()
        );
        assertEquals(
                new BsonDocument(
                        "$switch",
                        new BsonDocument(
                                "branches",
                                new BsonArray(
                                        List.of(
                                                new BsonDocument("case", new BsonString("$case1")).append("then", new BsonString("$then1")),
                                                new BsonDocument("case", new BsonString("$case2")).append("then", new BsonString("$then2")),
                                                new BsonDocument("case", new BsonString("$case3")).append("then", new BsonString("$then3"))
                                        )
                                )
                        )
                ),
                switchV2()
                        .branch("$case1", "$then1")
                        .branch("$case2", "$then2")
                        .branch("$case3", "$then3")
                        .build().toBsonDocument()
        );
        assertEquals(
                3,
                switchV2()
                        .branch("$case1", "$then1")
                        .branch("$case2", "$then2")
                        .branch("$case3", "$then3").branchesCount()
        );
    }

}
