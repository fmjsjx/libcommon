package com.github.fmjsjx.libcommon.bson;

import org.bson.*;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.github.fmjsjx.libcommon.bson.BsonValueUtil.*;

/**
 * A factory for expression operators.
 *
 * @author MJ Fang
 * @since 3.1
 */
public class ExpressionOperators {


    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $abs arithmetic expression operator
     */
    public static final Bson abs(Object expression) {
        return operator("$abs", expression);
    }

    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $abs arithmetic expression operator
     */
    public static final Bson add(Iterable<?> expressions) {
        var array = new BsonArray();
        for (var expression : expressions) {
            array.add(BsonValueUtil.encode(expression));
        }
        return operator("$add", array);
    }

    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $abs arithmetic expression operator
     */
    public static final Bson add(Object... expressions) {
        var array = new BsonArray();
        for (var expression : expressions) {
            array.add(BsonValueUtil.encode(expression));
        }
        return operator("$add", array);
    }

    /**
     * Creates a $ceil arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $ceil arithmetic expression operator
     */
    public static final Bson ceil(Object expression) {
        return operator("$ceil", expression);
    }

    /**
     * Creates a $divide arithmetic expression operator.
     *
     * @param dividend can be any valid expression as long as it resolves to a number
     * @param divisor  can be any valid expression as long as it resolves to a number
     * @return the $divide arithmetic expression operator
     */
    public static final Bson divide(Object dividend, Object divisor) {
        return operator("$divide", BsonValueUtil.encodeList(dividend, divisor));
    }

    /**
     * Creates an $exp arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $exp arithmetic expression operator
     */
    public static final Bson exp(Object expression) {
        return operator("$exp", expression);
    }

    /**
     * Creates a $floor arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $floor arithmetic expression operator
     */
    public static final Bson floor(Object expression) {
        return operator("$floor", expression);
    }

    /**
     * Creates a $ln arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a non-negative number
     * @return the $ln arithmetic expression operator
     */
    public static final Bson ln(Object expression) {
        return operator("$ln", expression);
    }

    /**
     * Creates a $log arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a non-negative number
     * @param base   can be any valid expression as long as it resolves to a positive number greater than {@code 1}
     * @return the $log arithmetic expression operator
     */
    public static final Bson log(Object number, Object base) {
        return operator("$log", BsonValueUtil.encodeList(number, base));
    }

    /**
     * Creates a $log10 arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a non-negative number
     * @return the $log10 arithmetic expression operator
     */
    public static final Bson log10(Object expression) {
        return operator("$log10", expression);
    }

    /**
     * Creates a $mod arithmetic expression operator.
     *
     * @param dividend can be any valid expression as long as it resolves to a number
     * @param divisor  can be any valid expression as long as it resolves to a number
     * @return the $mod arithmetic expression operator
     */
    public static final Bson mod(Object dividend, Object divisor) {
        return operator("$mod", BsonValueUtil.encodeList(dividend, divisor));
    }

    /**
     * Creates a $multiply arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $multiply arithmetic expression operator
     */
    public static final Bson multiply(Object... expressions) {
        var array = new BsonArray();
        for (var expression : expressions) {
            array.add(BsonValueUtil.encode(expression));
        }
        return operator("$multiply", array);
    }

    /**
     * Creates a $round arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @param place  can be any valid expression as long as it resolves to a number
     * @return the $round arithmetic expression operator
     */
    public static final Bson round(Object number, Object place) {
        return operator("$round", BsonValueUtil.encodeList(number, place));
    }

    /**
     * Creates a $round arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @return the $round arithmetic expression operator
     */
    public static final Bson round(Object number) {
        return round(number, 0);
    }

    /**
     * Creates a $sqrt arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a non-negative number
     * @return the $sqrt arithmetic expression operator
     */
    public static final Bson sqrt(Object expression) {
        return operator("$sqrt", expression);
    }

    /**
     * Creates a $subtract arithmetic expression operator.
     *
     * @param expression1 can be any valid expression as long as they resolve to numbers and/or dates
     * @param expression2 can be any valid expression as long as they resolve to numbers and/or dates
     * @return the $subtract arithmetic expression operator
     */
    public static final Bson subtract(Object expression1, Object expression2) {
        return operator("$subtract", BsonValueUtil.encodeList(expression1, expression2));
    }

    /**
     * Creates a $trunc arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @param place  can be any valid expression as long as it resolves to a number
     * @return the $trunc arithmetic expression operator
     */
    public static final Bson trunc(Object number, Object place) {
        return operator("$trunc", BsonValueUtil.encodeList(number, place));
    }

    /**
     * Creates a $trunc arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @return the $trunc arithmetic expression operator
     */
    public static final Bson trunc(Object number) {
        return trunc(number, 0);
    }


    /**
     * Creates an $arrayElemAt array expression operator.
     *
     * @param array can be any valid expression that resolves to an array
     * @param idx   can be any valid expression that resolves to an integer
     * @return the $arrayElemAt array expression operator
     */
    public static final Bson arrayElemAt(Object array, Object idx) {
        return operator("$arrayElemAt", BsonValueUtil.encodeList(array, idx));
    }

    /**
     * Creates an $arrayElemAt array expression operator.
     *
     * @param array the array
     * @param idx   can be any valid expression that resolves to an integer
     * @return the $arrayElemAt array expression operator
     */
    public static final Bson arrayElemAt(Iterable<?> array, Object idx) {
        return arrayElemAt((BsonValue) BsonExtensionKt.toBsonArray(array, BsonValueUtil::encode), idx);
    }

    /**
     * Creates an $arrayElemAt array expression operator.
     *
     * @param idx   can be any valid expression that resolves to an integer
     * @param array the array
     * @return the $arrayElemAt array expression operator
     */
    public static final Bson arrayElemAt(Object idx, Object... array) {
        return arrayElemAt((BsonValue) BsonExtensionKt.toBsonArray(array, BsonValueUtil::encode), idx);
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param expression can be any valid expression that resolves to an array
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObject(Object expression) {
        return operator("$arrayToObject", expression);
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param entries the entries
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObject(Map.Entry<?, ?>... entries) {
        var expression = new BsonArray(1);
        var values = new BsonArray(entries.length);
        expression.add(values);
        for (var entry : entries) {
            var value = new BsonArray(2);
            value.add(encode(entry.getKey()));
            value.add(encode(entry.getValue()));
            values.add(value);
        }
        return arrayToObject(expression);
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param entries the entries
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObjectLiteral(Map.Entry<?, ?>... entries) {
        var values = new BsonArray(entries.length);
        for (var entry : entries) {
            var value = new BsonArray(2);
            value.add(encode(entry.getKey()));
            value.add(encode(entry.getValue()));
            values.add(value);
        }
        return arrayToObject(literal(values));
    }

    /**
     * Creates an $concatArrays array expression operator.
     *
     * @param arrays the arrays
     * @return the $concatArrays array expression operator
     */
    public static final Bson concatArrays(Object... arrays) {
        return operator("$concatArrays", encodeList(arrays));
    }

    /**
     * Creates a $filter array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param cond  an expression that resolves to a boolean value used to determine if an element should be included in the output array
     * @param as    a name for the variable that represents each individual element of the input array
     * @param limit an umber expression that restricts the number of matching array elements that $filter returns
     * @return the $filter array expression operator
     */
    public static final Bson filter(Object input, Object cond, String as, Object limit) {
        var doc = new BsonDocument(8).append("input", encode(input)).append("cond", encode(cond));
        if (as != null) {
            doc.append("as", new BsonString(as));
        }
        if (limit != null) {
            doc.append("limit", encode(limit));
        }
        return operator("$filter", doc);
    }

    /**
     * Creates a $filter array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param cond  an expression that resolves to a boolean value used to determine if an element should be included in the output array
     * @return the $filter array expression operator
     */
    public static final Bson filter(Object input, Object cond) {
        return filter(input, cond, null, null);
    }

    /**
     * Creates a $filter array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param cond  an expression that resolves to a boolean value used to determine if an element should be included in the output array
     * @param as    a name for the variable that represents each individual element of the input array
     * @return the $filter array expression operator
     */
    public static final Bson filter(Object input, Object cond, String as) {
        return filter(input, cond, as, null);
    }

    /**
     * Creates a $filter array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param cond  an expression that resolves to a boolean value used to determine if an element should be included in the output array
     * @param limit an umber expression that restricts the number of matching array elements that $filter returns
     * @return the $filter array expression operator
     */
    public static final Bson filter(Object input, Object cond, Object limit) {
        return filter(input, cond, null, limit);
    }

    /**
     * Creates a $first array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array, null or missing
     * @return the $first array expression operator
     */
    public static final Bson first(Object expression) {
        return operator("$first", expression);
    }

    /**
     * Creates a $firstN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return {@code n} elements
     * @return the $firstN array expression operator
     */
    public static final Bson firstN(Object n, Object input) {
        return operator("$firstN", new BsonDocument(4).append("n", encode(n)).append("input", encode(input)));
    }

    /**
     * Creates an $in array expression operator.
     *
     * @param expression      any valid expression expression
     * @param arrayExpression any valid expression that resolves to an array
     * @return the $in array expression operator
     */
    public static final Bson in(Object expression, Object arrayExpression) {
        return operator("$in", encodeList(expression, arrayExpression));
    }

    /**
     * Creates a $indexOfArray array expression operator.
     *
     * @param array  can be any valid expression as long as it resolves to an array
     * @param search can be any valid expression
     * @param start  the starting index position for the search, can be any valid expression that resolves to a
     *               non-negative integral number
     * @param end    the ending index position for the search, can be any valid expression that resolves to a
     *               non-negative integral number
     * @return the $indexOfArray array expression operator
     */
    public static final Bson indexOfArray(Object array, Object search, Object start, Object end) {
        var len = end != null ? 4 : (start != null ? 3 : 2);
        var params = new BsonArray(len);
        params.add(encode(array));
        params.add(encode(search));
        if (start != null) {
            params.add(encode(start));
        }
        if (end != null) {
            if (start == null) {
                params.add(new BsonInt32(0));
            }
            params.add(encode(end));
        }
        return operator("$indexOfArray", params);
    }

    /**
     * Creates a $indexOfArray array expression operator.
     *
     * @param array  can be any valid expression as long as it resolves to an array
     * @param search can be any valid expression
     * @return the $indexOfArray array expression operator
     */
    public static final Bson indexOfArray(Object array, Object search) {
        return indexOfArray(array, search, null);
    }

    /**
     * Creates a $indexOfArray array expression operator.
     *
     * @param array  can be any valid expression as long as it resolves to an array
     * @param search can be any valid expression
     * @param start  the starting index position for the search, can be any valid expression that resolves to a
     *               non-negative integral number
     * @return the $indexOfArray array expression operator
     */
    public static final Bson indexOfArray(Object array, Object search, Object start) {
        return indexOfArray(array, search, start, null);
    }

    /**
     * Creates a $isArray array expression operator.
     *
     * @param expression can be any valid expression
     * @return the $isArray array expression operator
     */
    public static final Bson isArray(Object expression) {
        return operator("$isArray", expression);
    }

    /**
     * Creates a $last array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array, null, or missing
     * @return the $last array expression operator
     */
    public static final Bson last(Object expression) {
        return operator("$last", expression);
    }

    /**
     * Creates a $lastN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return {@code n} elements
     * @return the $lastN array expression operator
     */
    public static final Bson lastN(Object n, Object input) {
        return operator("$lastN", new BsonDocument(4).append("n", encode(n)).append("input", encode(input)));
    }

    /**
     * Creates a $map array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param as    a name for the variable that represents each individual element of the input array
     * @param in    an expression that is applied to each element of the input array
     * @return the $map array expression operator
     */
    public static final Bson map(Object input, String as, Object in) {
        var doc = new BsonDocument(4).append("input", encode(input)).append("in", encode(in));
        if (as != null) {
            doc.append("as", new BsonString(as));
        }
        return operator("$map", doc);
    }

    /**
     * Creates a $map array expression operator.
     *
     * @param input an expression that resolves to an array
     * @param in    an expression that is applied to each element of the input array
     * @return the $map array expression operator
     */
    public static final Bson map(Object input, Object in) {
        return map(input, null, in);
    }

    /**
     * Creates a $maxN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return the maximal {@code n} elements
     * @return the $maxN array expression operator
     */
    public static final Bson maxN(Object n, Object input) {
        return operator("$maxN", new BsonDocument(4).append("n", encode(n)).append("input", encode(input)));
    }

    /**
     * Creates a $minN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return the minimal {@code n} elements
     * @return the $minN array expression operator
     */
    public static final Bson minN(Object n, Object input) {
        return operator("$minN", new BsonDocument(4).append("n", encode(n)).append("input", encode(input)));
    }

    /**
     * Creates a $objectToArray array expression operator.
     *
     * @param object can be any valid expression as long as it resolves to a document object
     * @return the $objectToArray array expression operator
     */
    public static final Bson objectToArray(Object object) {
        return operator("$objectToArray", object);
    }

    /**
     * Creates a $range array expression operator.
     *
     * @param start an integer that specifies the start of the sequence, can be any valid expression that resolves to an
     *              integer
     * @param end   an integer that specifies the exclusive upper limit of the sequence, can be any valid expression
     *              that resolves to an integer
     * @param step  optional, an integer that specifies the increment value. Can be any valid expression that resolves
     *              to a non-zero integer, defaults to {@code 1}.
     * @return the $range array expression operator
     */
    public static final Bson range(Object start, Object end, Object step) {
        var params = new BsonArray(step == null ? 2 : 3);
        params.add(encode(start));
        params.add(encode(end));
        if (step != null) {
            params.add(encode(step));
        }
        return operator("$range", params);
    }

    /**
     * Creates a $reduce array expression operator.
     *
     * @param input        can be any valid expression that resolves to an array
     * @param initialValue the initial cumulative value set before in is applied to the first element of the input array
     * @param in           a valid expression that $reduce applies to each element in the input {@code array}
     * @return the $reduce array expression operator
     */
    public static final Bson reduce(Object input, Object initialValue, Object in) {
        var doc = new BsonDocument(4).append("input", encode(input)).append("initialValue", encode(initialValue)).append("in", encode(in));
        return operator("$reduce", doc);
    }

    /**
     * Creates a $reverseArray array expression operator.
     *
     * @param array can be any valid expression as long as it resolves to an array
     * @return the $reverseArray array expression operator
     */
    public static final Bson reverseArray(Object array) {
        return operator("$reverseArray", array);
    }

    /**
     * Creates a $size array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array
     * @return the $size array expression operator
     */
    public static final Bson size(Object expression) {
        return operator("$size", expression);
    }

    /**
     * Creates a $slice array expression operator.
     *
     * @param array    any valid expression as long as it resolves to an array.
     * @param position optional, any valid expression as long as it resolves to an integer
     * @param n        any valid expression as long as it resolves to an integer, if {@code position} is specified,
     *                 {@code n} must resolve to a positive integer
     * @return the $slice array expression operator
     */
    public static final Bson slice(Object array, Object position, Object n) {
        var params = new BsonArray(position == null ? 2 : 3);
        params.add(encode(array));
        if (position != null) {
            params.add(encode(position));
        }
        params.add(encode(n));
        return operator("$range", params);
    }

    /**
     * Creates a $sortArray array expression operator.
     *
     * @param input  the array to be sorted
     * @param sortBy the document specifies a sort ordering
     * @return the $sortArray array expression operator
     */
    public static final Bson sortArray(Object input, Object sortBy) {
        var doc = new BsonDocument(4).append("input", encode(input)).append("sortBy", encode(sortBy));
        return operator("$sortArray", doc);
    }

    /**
     * Creates a $zip array expression operator.
     *
     * @param inputs           an array of expressions that resolve to arrays
     * @param useLongestLength a boolean which specifies whether the length of the longest array determines the number
     *                         of arrays in the output array
     * @param defaults         an array of default element values to use if the input arrays have different lengths
     * @return the $zip array expression operator
     */
    public static final Bson zip(Iterable<?> inputs, boolean useLongestLength, Object defaults) {
        var doc = new BsonDocument(4).append("inputs", encode(inputs));
        if (useLongestLength) {
            doc.append("useLongestLength", BsonBoolean.TRUE);
            if (defaults != null) {
                doc.append("defaults", encode(defaults));
            }
        }
        return operator("$zip", doc);
    }

    /**
     * Creates a $zip array expression operator.
     *
     * @param inputs an array of expressions that resolve to arrays
     * @return the $zip array expression operator
     */
    public static final Bson zip(Iterable<?> inputs) {
        return zip(inputs, false, null);
    }

    /**
     * Creates a $zip array expression operator.
     *
     * @param useLongestLength a boolean which specifies whether the length of the longest array determines the number
     *                         of arrays in the output array
     * @param defaults         an array of default element values to use if the input arrays have different lengths
     * @param inputs           an array of expressions that resolve to arrays
     * @return the $zip array expression operator
     */
    public static final Bson zip(boolean useLongestLength, Object defaults, Object... inputs) {
        var arrays = new BsonArray(inputs.length);
        for (var input : inputs) {
            arrays.add(encode(input));
        }
        var doc = new BsonDocument(4).append("inputs", arrays);
        if (useLongestLength) {
            doc.append("useLongestLength", BsonBoolean.TRUE);
            if (defaults != null) {
                doc.append("defaults", encode(defaults));
            }
        }
        return operator("$zip", doc);
    }

    /**
     * Creates a $zip array expression operator.
     *
     * @param inputs an array of expressions that resolve to arrays
     * @return the $zip array expression operator
     */
    public static final Bson zip(Objects... inputs) {
        return zip(false, null, (Object[]) inputs);
    }

    /**
     * Creates an $and boolean expression operator.
     *
     * @param expressions an array of expressions
     * @return the $and boolean expression operator
     */
    public static final Bson and(Object... expressions) {
        return operator("$and", encodeList(expressions));
    }

    /**
     * Creates a $not boolean expression operator.
     *
     * @param expression an array of expressions
     * @return the $not boolean expression operator
     */
    public static final Bson not(Object expression) {
        return operator("$not", encodeList(expression));
    }

    /**
     * Creates an $or boolean expression operator.
     *
     * @param expressions an array of expressions
     * @return the $or boolean expression operator
     */
    public static final Bson or(Object... expressions) {
        return operator("$or", encodeList(expressions));
    }

    /**
     * Creates a comparison expression operator.
     *
     * @param name        the operator name
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the comparison expression operator
     */
    public static final Bson comparison(String name, Object expression1, Object expression2) {
        return operator(name, encodeList(expression1, expression2));
    }

    /**
     * Creates a $cmp comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $cmp comparison expression operator
     */
    public static final Bson cmp(Object expression1, Object expression2) {
        return comparison("$cmp", expression1, expression2);
    }

    /**
     * Creates an $eq comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $eq comparison expression operator
     */
    public static final Bson eq(Object expression1, Object expression2) {
        return comparison("$eq", expression1, expression2);
    }

    /**
     * Creates a $gt comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $gt comparison expression operator
     */
    public static final Bson gt(Object expression1, Object expression2) {
        return comparison("$gt", expression1, expression2);
    }

    /**
     * Creates a $gte comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $gte comparison expression operator
     */
    public static final Bson gte(Object expression1, Object expression2) {
        return comparison("$gte", expression1, expression2);
    }

    /**
     * Creates a $lt comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $lt comparison expression operator
     */
    public static final Bson lt(Object expression1, Object expression2) {
        return comparison("$lt", expression1, expression2);
    }

    /**
     * Creates a $lte comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $lte comparison expression operator
     */
    public static final Bson lte(Object expression1, Object expression2) {
        return comparison("$lte", expression1, expression2);
    }

    /**
     * Creates a $ne comparison expression operator.
     *
     * @param expression1 the first value expression
     * @param expression2 the second value expressions
     * @return the $ne comparison expression operator
     */
    public static final Bson ne(Object expression1, Object expression2) {
        return comparison("$ne", expression1, expression2);
    }

    /**
     * Creates a $cond conditional expression operator.
     *
     * @param ifExpression   the if expression
     * @param thenExpression the then expressions
     * @param elseExpression the else expressions
     * @return the $cond conditional expression operator
     */
    public static final Bson cond(Object ifExpression, Object thenExpression, Object elseExpression) {
        var doc = new BsonDocument(4).append("if", encode(ifExpression))
                .append("then", encode(thenExpression))
                .append("else", encode(elseExpression));
        return operator("$cond", doc);
    }

    /**
     * Creates a new {@link CondThenBuilder} instance.
     *
     * @param expression the if expression
     * @return the {@code CondThenBuilder} instance.
     */
    public static final CondThenBuilder condIf(Object expression) {
        return new CondThenBuilder(expression);
    }

    /**
     * Builder for $cond conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.1
     */
    public static final class CondThenBuilder {

        private final Object ifExpression;

        private CondThenBuilder(Object ifExpression) {
            this.ifExpression = ifExpression;
        }

        /**
         * Creates a new {@link CondElseBuilder} instance.
         *
         * @param expression the else expression
         * @return the {@code CondElseBuilder} instance
         */
        public CondElseBuilder then(Object expression) {
            return new CondElseBuilder(ifExpression, expression);
        }

    }

    /**
     * Builder for $cond conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.1
     */
    public static final class CondElseBuilder {

        private final Object ifExpression;
        private final Object thenExpression;

        private CondElseBuilder(Object ifExpression, Object thenExpression) {
            this.ifExpression = ifExpression;
            this.thenExpression = thenExpression;
        }

        /**
         * Creates a $cond conditional expression operator.
         *
         * @param expression the else expression
         * @return the $cond conditional expression operator
         */
        public Bson onElse(Object expression) {
            return cond(ifExpression, thenExpression, expression);
        }

    }

    /**
     * Creates a $ifNull conditional expression operator.
     *
     * @param inputExpressions      the input expressions
     * @param replacementExpression the replacement expression
     * @return the $ifNull conditional expression operator
     */
    public static final Bson ifNull(Iterable<?> inputExpressions, Object replacementExpression) {
        var params = new BsonArray(inputExpressions instanceof Collection<?> collection ? collection.size() + 1 : 10);
        for (var inputExpression : inputExpressions) {
            params.add(encode(inputExpression));
        }
        params.add(encode(replacementExpression));
        return operator("$ifNull", params);
    }

    /**
     * Creates a $ifNull conditional expression operator.
     *
     * @param inputExpression       the first input expressions
     * @param replacementExpression the replacement expression
     * @param otherInputExpressions the other input expressions
     * @return the $ifNull conditional expression operator
     */
    public static final Bson ifNull(Object inputExpression, Object replacementExpression, Object... otherInputExpressions) {
        var params = new BsonArray(otherInputExpressions.length + 2);
        params.add(encode(inputExpression));
        for (var otherInputExpression : otherInputExpressions) {
            params.add(encode(otherInputExpression));
        }
        params.add(encode(replacementExpression));
        return operator("$ifNull", params);
    }

    /**
     * Creates a new {@link IfNullBuilder} instance.
     *
     * @return the {@code IfNullBuilder} instance
     */
    public static final IfNullBuilder ifNull() {
        return new IfNullBuilder();
    }

    /**
     * Creates a new {@link IfNullBuilder} instance with one input expression.
     *
     * @param inputExpression the input expression
     * @return the {@code IfNullBuilder} instance
     */
    public static final IfNullBuilder ifNull(Object inputExpression) {
        return ifNull().input(inputExpression);
    }

    /**
     * Builder for $ifNull conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.1
     */
    public static final class IfNullBuilder {

        private final ArrayList<Object> inputExpressions = new ArrayList<>();

        private IfNullBuilder() {
        }

        /**
         * Append an input expression and returns this builder.
         *
         * @param expression the input expression
         * @return this builder
         */
        public IfNullBuilder input(Object expression) {
            inputExpressions.add(expression);
            return this;
        }

        /**
         * Creates a $ifNull conditional expression operator.
         *
         * @param expression the replacement expression if null
         * @return the $ifNull conditional expression operator
         */
        public Bson replacement(Object expression) {
            return ifNull(inputExpressions, expression);
        }

    }

    /**
     * Creates a $switch conditional expression operator.
     *
     * @param branches          an array of control branch documents
     * @param defaultExpression the default value expression
     * @return the $switch conditional expression operator
     */
    public static final Bson switchN(Iterable<?> branches, Object defaultExpression) {
        var doc = new BsonDocument(4);
        doc.append("branches", encode(branches));
        if (defaultExpression != null) {
            doc.append("default", encode(defaultExpression));
        }
        return operator("$switch", doc);
    }

    /**
     * Creates a $switch conditional expression operator.
     *
     * @param branches          an array of control branch documents
     * @param defaultExpression the default value expression
     * @return the $switch conditional expression operator
     */
    public static final Bson switchN(BsonArray branches, Object defaultExpression) {
        var doc = new BsonDocument(4);
        doc.append("branches", branches);
        if (defaultExpression != null) {
            doc.append("default", encode(defaultExpression));
        }
        return operator("$switch", doc);
    }

    /**
     * Creates a new {@link SwitchBuilder} instance.
     *
     * @return the {@code SwitchBuilder} instance
     */
    public static final SwitchBuilder switch0() {
        return new SwitchBuilder();
    }

    /**
     * Creates a new {@link SwitchBuilder.BranchBuilder} instance.
     *
     * @param expression the case expression
     * @return the {@code BranchBuilder} instance
     */
    public static final SwitchBuilder.BranchBuilder switchCase(Object expression) {
        return switch0().onCase(expression);
    }

    /**
     * Builder for $switch conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.1
     */
    public static final class SwitchBuilder {

        private final BsonArray branches = new BsonArray();

        private SwitchBuilder() {
        }

        /**
         * Creates a new {@link BranchBuilder} instance.
         *
         * @param expression the case expression
         * @return the {@code  BranchBuilder} instance
         */
        public BranchBuilder onCase(Object expression) {
            return new BranchBuilder(expression);
        }

        /**
         * Creates a $switch conditional expression operator.
         *
         * @return the $switch conditional expression operator
         */
        public Bson build() {
            return build(null);
        }

        /**
         * Creates a $switch conditional expression operator.
         *
         * @param defaultExpression the default expression
         * @return the $switch conditional expression operator
         */
        public Bson build(Object defaultExpression) {
            return switchN(branches, defaultExpression);
        }

        /**
         * Builder for $switch conditional expression operator.
         *
         * @author MJ Fang
         * @since 3.1
         */
        public final class BranchBuilder {

            private final Object caseExpression;

            private BranchBuilder(Object caseExpression) {
                this.caseExpression = caseExpression;
            }

            /**
             * Append branch document and returns self switch builder.
             *
             * @param expression the thenExpression
             * @return the {@code SwitchBuilder}
             */
            public SwitchBuilder then(Object expression) {
                var doc = new BsonDocument(4)
                        .append("case", encode(caseExpression))
                        .append("then", encode(expression));
                branches.add(doc);
                return SwitchBuilder.this;
            }

        }

    }

    /**
     * Creates a $binarySize data size operator.
     *
     * @param expression can be any valid expression as long as it resolves to either a string or binary data value
     * @return the $binarySize data size operator
     */
    public static final Bson binarySize(Object expression) {
        return operator("$binarySize", encode(expression));
    }

    /**
     * Creates a $bsonSize data size operator.
     *
     * @param expression can be any valid expression as long as it resolves to either an object or {@code null}
     * @return the $bsonSize data size operator
     */
    public static final Bson bsonSize(Object expression) {
        return operator("$bsonSize", encode(expression));
    }

    /**
     * Creates a $dateAdd date expression operator.
     *
     * @param startDate the beginning date, in UTC, for the addition operation, can be any expression that resolves to
     *                  a Date, a Timestamp, or an ObjectID
     * @param unit      the unit used to measure the amount of time added to the startDate
     * @param amount    the number of units added to the startDate, can be an expression that resolves to an integer or
     *                  long
     * @param timezone  optional, the timezone to carry out the operation
     * @return the $dateAdd date expression operator
     */
    public static final Bson dateAdd(Object startDate, Object unit, Object amount, Object timezone) {
        var doc = new BsonDocument(8)
                .append("startDate", encode(startDate))
                .append("unit", encode(unit))
                .append("amount", encode(amount));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        return operator("$dateAdd", doc);
    }

    /**
     * Creates a $dateAdd date expression operator.
     *
     * @param startDate the beginning date, in UTC, for the addition operation, can be any expression that resolves to
     *                  a Date, a Timestamp, or an ObjectID
     * @param unit      the unit used to measure the amount of time added to the startDate
     * @param amount    the number of units added to the startDate, can be an expression that resolves to an integer or
     *                  long
     * @return the $dateAdd date expression operator
     */
    public static final Bson dateAdd(Object startDate, Object unit, Object amount) {
        return dateAdd(startDate, unit, amount, null);
    }

    /**
     * Creates a $dateDiff date expression operator.
     *
     * @param startDate the start of the time period, can be any expression that resolves to a Date, a Timestamp, or an
     *                  ObjectID
     * @param endDate   the end of the time period, can be any expression that resolves to a Date, a Timestamp, or an
     *                  ObjectID
     * @param unit      the time measurement unit between the startDate and endDate
     * @return the $dateDiff date expression operator
     */
    public static final Bson dateDiff(Object startDate, Object endDate, Object unit) {
        return dateDiff(startDate, endDate, unit, null, null);
    }

    /**
     * Creates a $dateDiff date expression operator.
     *
     * @param startDate   the start of the time period, can be any expression that resolves to a Date, a Timestamp, or
     *                    an ObjectID
     * @param endDate     the end of the time period, can be any expression that resolves to a Date, a Timestamp, or an
     *                    ObjectID
     * @param unit        the time measurement unit between the startDate and endDate
     * @param timezone    optional, the timezone to carry out the operation
     * @param startOfWeek optional, used when the unit is equal to week, defaults to {@code Sunday}
     * @return the $dateDiff date expression operator
     */
    public static final Bson dateDiff(Object startDate, Object endDate, Object unit, Object timezone, Object startOfWeek) {
        var doc = new BsonDocument(8)
                .append("startDate", encode(startDate))
                .append("endDate", encode(endDate))
                .append("unit", encode(unit));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        if (startOfWeek != null) {
            doc.append("startOfWeek", encode(startOfWeek));
        }
        return operator("$dateDiff", doc);
    }

    /**
     * Creates a $dateFromParts date expression operator.
     *
     * @param year        calendar year, can be any expression that evaluates to a number
     * @param month       optional, month, can be any expression that evaluates to a number
     * @param day         optional, day of month, can be any expression that evaluates to a number
     * @param hour        optional, hour, can be any expression that evaluates to a number
     * @param minute      optional, minute, can be any expression that evaluates to a number
     * @param second      optional, second, can be any expression that evaluates to a number
     * @param millisecond optional, millisecond, can be any expression that evaluates to a number
     * @param timezone    optional, the timezone
     * @return the $dateFromParts date expression operator
     */
    public static final Bson dateFromParts(Object year, Object month, Object day, Object hour, Object minute,
                                           Object second, Object millisecond, Object timezone) {
        var doc = new BsonDocument("year", encode(year));
        if (month != null) {
            doc.append("month", encode(month));
        }
        if (day != null) {
            doc.append("day", encode(day));
        }
        appendTime(doc, minute, hour, millisecond, timezone, second);
        return operator("$dateFromParts", doc);
    }

    private static final void appendTime(BsonDocument doc, Object minute, Object hour, Object millisecond,
                                         Object timezone, Object second) {
        if (hour != null) {
            doc.append("hour", encode(hour));
        }
        if (hour != null) {
            doc.append("minute", encode(minute));
        }
        if (hour != null) {
            doc.append("second", encode(second));
        }
        if (hour != null) {
            doc.append("millisecond", encode(millisecond));
        }
        if (hour != null) {
            doc.append("timezone", encode(timezone));
        }
    }

    /**
     * Creates a $dateFromParts date expression operator.
     *
     * @param isoWeekYear  ISO week date year, can be any expression that evaluates to a number
     * @param isoWeek      week of year, can be any expression that evaluates to a number
     * @param isoDayOfWeek day of week, can be any expression that evaluates to a number
     * @param hour         optional, hour, can be any expression that evaluates to a number
     * @param minute       optional, minute, can be any expression that evaluates to a number
     * @param second       optional, second, can be any expression that evaluates to a number
     * @param millisecond  optional, millisecond, can be any expression that evaluates to a number
     * @param timezone     optional, the timezone
     * @return the $dateFromParts date expression operator
     */
    public static final Bson dateFromIsoParts(Object isoWeekYear, Object isoWeek, Object isoDayOfWeek, Object hour,
                                              Object minute, Object second, Object millisecond, Object timezone) {
        var doc = new BsonDocument("isoWeekYear", encode(isoWeekYear));
        if (isoWeek != null) {
            doc.append("isoWeek", encode(isoWeek));
        }
        if (isoDayOfWeek != null) {
            doc.append("isoDayOfWeek", encode(isoDayOfWeek));
        }
        appendTime(doc, minute, hour, millisecond, timezone, second);
        return operator("$dateFromParts", doc);
    }

    /**
     * Creates a $dateFromString date expression operator.
     *
     * @param dateString the date/time string to convert to a date object
     * @param format     optional, the date format specification of the dateString, can be any expression that evaluates
     *                   to a string literal, containing 0 or more format specifiers
     * @param timezone   optional, the time zone to use to format the date
     * @param onError    optional, if an error occurs while parsing the given dateString, it outputs the result value of
     *                   the provided expression
     * @param onNull     optional, if the dateString is null or missing, it outputs the result value of the provided
     *                   expression
     * @return the $dateFromString date expression operator
     */
    public static final Bson dateFromString(Object dateString, Object format, Object timezone, Object onError,
                                            Object onNull) {
        var doc = new BsonDocument(8).append("dateString", encode(dateString));
        if (format != null) {
            doc.append("format", encode(format));
        }
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        if (onError != null) {
            doc.append("onError", encode(onError));
        }
        if (onNull != null) {
            doc.append("onNull", encode(onNull));
        }
        return operator("$dateFromString", doc);
    }

    /**
     * Creates a $dateSubtract date expression operator.
     *
     * @param startDate the beginning date, in UTC, for the addition operation, can be any expression that resolves to
     *                  a Date, a Timestamp, or an ObjectID
     * @param unit      the unit used to measure the amount of time added to the startDate
     * @param amount    the number of units added to the startDate, can be an expression that resolves to an integer or
     *                  long
     * @param timezone  optional, the timezone to carry out the operation
     * @return the $dateSubtract date expression operator
     */
    public static final Bson dateSubtract(Object startDate, Object unit, Object amount, Object timezone) {
        var doc = new BsonDocument(8)
                .append("startDate", encode(startDate))
                .append("unit", encode(unit))
                .append("amount", encode(amount));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        return operator("$dateSubtract", doc);
    }

    /**
     * Creates a $dateToParts date expression operator.
     *
     * @param date     the input date for which to return parts,  can be any expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone to use to format the date
     * @param iso8601  optional, can be any expression that resolves to a Boolean
     * @return the $dateToParts date expression operator
     */
    public static final Bson dateToParts(Object date, Object timezone, Object iso8601) {
        var doc = new BsonDocument(8).append("date", encode(date));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        if (iso8601 != null) {
            doc.append("iso8601", encode(iso8601));
        }
        return operator("$dateToParts", doc);
    }

    /**
     * Creates a $dateToParts date expression operator.
     *
     * @param date     the input date for which to return parts,  can be any expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone to use to format the date
     * @param iso8601  if set to true, modifies the output document to use ISO week date fields
     * @return the $dateToParts date expression operator
     */
    public static final Bson dateToParts(Object date, Object timezone, boolean iso8601) {
        var doc = new BsonDocument(iso8601 ? 8 : 4).append("date", encode(date));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        if (iso8601) {
            doc.append("iso8601", BsonBoolean.TRUE);
        }
        return operator("$dateToParts", doc);
    }

    /**
     * Creates a $dateToString date expression operator.
     *
     * @param date     the date to convert to string, must be a valid expression that resolves to a Date, a Timestamp,
     *                 or an ObjectID
     * @param format   optional, the date format specification, can be any string literal, containing 0 or more format
     *                 specifiers
     * @param timezone optional, the timezone of the operation result
     * @return the $dateToString date expression operator
     */
    public static final Bson dateToString(Object date, Object format, Object timezone) {
        var doc = new BsonDocument(8)
                .append("date", encode(date));
        if (format != null) {
            doc.append("format", encode(format));
        }
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        return operator("$dateToString", doc);
    }

    /**
     * Creates a $dateTrunc date expression operator.
     *
     * @param date        the date to truncate, specified in UTC, can be any expression that resolves to a Date, a
     *                    Timestamp, or an ObjectID
     * @param unit        the unit of time
     * @param binSize     optional, the numeric time value, specified as an expression that must resolve to a positive
     *                    non-zero number
     * @param timezone    optional, the timezone
     * @param startOfWeek optional, the start of the week, used when unit is {@code week}.
     * @return the $dateTrunc date expression operator
     */
    public static final Bson dateTrunc(Object date, Object unit, Object binSize, Object timezone, Object startOfWeek) {
        var doc = new BsonDocument(8)
                .append("date", encode(date))
                .append("unit", encode(unit));
        if (binSize != null) {
            doc.append("binSize", encode(binSize));
        }
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        return operator("$dateTrunc", doc);
    }

    private static final Bson datePortion(String name, Object date) {
        return operator(name, encode(date));
    }

    private static final Bson datePortion(String name, Object date, Object timezone) {
        var doc = new BsonDocument(4)
                .append("date", encode(date));
        if (timezone != null) {
            doc.append("timezone", encode(timezone));
        }
        return operator(name, doc);
    }

    /**
     * Creates a $dayOfMonth date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dayOfMonth date expression operator
     */
    public static final Bson dayOfMonth(Object date) {
        return datePortion("$dayOfMonth", date);
    }

    /**
     * Creates a $dayOfMonth date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $dayOfMonth date expression operator
     */
    public static final Bson dayOfMonth(Object date, Object timezone) {
        return datePortion("$dayOfMonth", date, timezone);
    }

    /**
     * Creates a $dayOfWeek date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dayOfWeek date expression operator
     */
    public static final Bson dayOfWeek(Object date) {
        return datePortion("$dayOfWeek", date);
    }

    /**
     * Creates a $dayOfWeek date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $dayOfWeek date expression operator
     */
    public static final Bson dayOfWeek(Object date, Object timezone) {
        return datePortion("$dayOfWeek", date, timezone);
    }

    /**
     * Creates a $dayOfYear date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dayOfYear date expression operator
     */
    public static final Bson dayOfYear(Object date) {
        return datePortion("$dayOfYear", date);
    }

    /**
     * Creates a $dayOfYear date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $dayOfYear date expression operator
     */
    public static final Bson dayOfYear(Object date, Object timezone) {
        return datePortion("$dayOfYear", date, timezone);
    }

    /**
     * Creates an $hour date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $hour date expression operator
     */
    public static final Bson hour(Object date) {
        return datePortion("$hour", date);
    }

    /**
     * Creates an $hour date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $hour date expression operator
     */
    public static final Bson hour(Object date, Object timezone) {
        return datePortion("$hour", date, timezone);
    }

    /**
     * Creates an $isoDayOfWeek date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $isoDayOfWeek date expression operator
     */
    public static final Bson isoDayOfWeek(Object date) {
        return datePortion("$isoDayOfWeek", date);
    }

    /**
     * Creates an $isoDayOfWeek date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $isoDayOfWeek date expression operator
     */
    public static final Bson isoDayOfWeek(Object date, Object timezone) {
        return datePortion("$isoDayOfWeek", date, timezone);
    }

    /**
     * Creates an $isoWeek date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $isoWeek date expression operator
     */
    public static final Bson isoWeek(Object date) {
        return datePortion("$isoWeek", date);
    }

    /**
     * Creates an $isoWeek date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $isoWeek date expression operator
     */
    public static final Bson isoWeek(Object date, Object timezone) {
        return datePortion("$isoWeek", date, timezone);
    }

    /**
     * Creates an $isoWeekYear date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $isoWeekYear date expression operator
     */
    public static final Bson isoWeekYear(Object date) {
        return datePortion("$isoWeekYear", date);
    }

    /**
     * Creates an $isoWeekYear date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $isoWeekYear date expression operator
     */
    public static final Bson isoWeekYear(Object date, Object timezone) {
        return datePortion("$isoWeekYear", date, timezone);
    }

    /**
     * Creates a $millisecond date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $millisecond date expression operator
     */
    public static final Bson millisecond(Object date) {
        return datePortion("$millisecond", date);
    }

    /**
     * Creates a $millisecond date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $millisecond date expression operator
     */
    public static final Bson millisecond(Object date, Object timezone) {
        return datePortion("$millisecond", date, timezone);
    }

    /**
     * Creates a $minute date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $minute date expression operator
     */
    public static final Bson minute(Object date) {
        return datePortion("$minute", date);
    }

    /**
     * Creates a $minute date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $minute date expression operator
     */
    public static final Bson minute(Object date, Object timezone) {
        return datePortion("$minute", date, timezone);
    }

    /**
     * Creates a $month date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $month date expression operator
     */
    public static final Bson month(Object date) {
        return datePortion("$month", date);
    }

    /**
     * Creates a $month date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $month date expression operator
     */
    public static final Bson month(Object date, Object timezone) {
        return datePortion("$month", date, timezone);
    }

    /**
     * Creates a $second date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $second date expression operator
     */
    public static final Bson second(Object date) {
        return datePortion("$second", date);
    }

    /**
     * Creates a $second date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $second date expression operator
     */
    public static final Bson second(Object date, Object timezone) {
        return datePortion("$second", date, timezone);
    }

    /**
     * Creates a $toDate date expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toDate date expression operator
     */
    public static final Bson toDate(Object expression) {
        return datePortion("$toDate", expression);
    }

    /**
     * Creates a $week date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $week date expression operator
     */
    public static final Bson week(Object date) {
        return datePortion("$week", date);
    }

    /**
     * Creates a $week date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $week date expression operator
     */
    public static final Bson week(Object date, Object timezone) {
        return datePortion("$week", date, timezone);
    }

    /**
     * Creates a $year date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $year date expression operator
     */
    public static final Bson year(Object date) {
        return datePortion("$year", date);
    }

    /**
     * Creates a $year date expression operator.
     *
     * @param date     the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *                 Timestamp, or an ObjectID
     * @param timezone optional, the timezone
     * @return the $year date expression operator
     */
    public static final Bson year(Object date, Object timezone) {
        return datePortion("$year", date, timezone);
    }

    /**
     * Creates a $literal literal expression operator.
     *
     * @param expression can be any valid expression
     * @return the $literal literal expression operator
     */
    public static final Bson literal(Object expression) {
        return operator("$literal", expression);
    }

    /**
     * Creates a $getField miscellaneous operator.
     *
     * @param field the field in the input object for which you want to return a value, can be any valid expression that
     *              resolves to a string constant
     * @param input optional, a valid expression that contains the field for which you want to return a value, must
     *              resolve to an object, missing, null, or undefined
     * @return the $getField miscellaneous operator
     */
    public static final Bson getField(Object field, Object input) {
        var doc = new BsonDocument(4).append("field", encode(field));
        if (input != null) {
            doc.append("input", encode(input));
        }
        return operator("$getField", doc);
    }

    /**
     * Creates a $getField miscellaneous operator.
     *
     * @param field the field in the input object for which you want to return a value, can be any valid expression that
     *              resolves to a string constant
     * @return the $getField miscellaneous operator
     */
    public static final Bson getField(Object field) {
        return operator("$getField", encode(field));
    }

    /**
     * Creates a $rand miscellaneous operator.
     *
     * @return the $rand miscellaneous operator
     */
    public static final Bson rand() {
        return operator("$rand", new BsonDocument());
    }

    /**
     * Creates a $sampleRate miscellaneous operator.
     *
     * @param expression can be any valid expression that resolves to a non-negative double value
     * @return the $sampleRate miscellaneous operator
     */
    public static final Bson sampleRate(Object expression) {
        return operator("$sampleRate", encode(expression));
    }

    /**
     * Creates a $mergeObjects object expression operator.
     *
     * @param expression can be any valid expression that resolves to a document
     * @return the $mergeObjects object expression operator
     */
    public static final Bson mergeObjects(Object expression) {
        return operator("$mergeObjects", encode(expression));
    }

    /**
     * Creates a $mergeObjects object expression operator.
     *
     * @param expressions can be any valid expression that resolves to documents
     * @return the $mergeObjects object expression operator
     */
    public static final Bson mergeObjects(Iterable<?> expressions) {
        return operator("$mergeObjects", encode(expressions));
    }

    /**
     * Creates a $mergeObjects object expression operator.
     *
     * @param expressions can be any valid expression that resolves to documents
     * @return the $mergeObjects object expression operator
     */
    public static final Bson mergeObjects(Object... expressions) {
        return operator("$mergeObjects", encodeList(expressions));
    }

    /**
     * Creates a $setField object expression operator.
     *
     * @param field the field in the input object that you want to add, update, or remove, can be any valid expression
     *              that resolves to a string constant
     * @param input the document that contains the field that you want to add or update, must resolve to an object,
     *              missing, null, or undefined
     * @param value the value that you want to assign to field, can be any valid expression
     * @return the $setField object expression operator
     */
    public static final Bson setField(Object field, Object input, Object value) {
        var doc = new BsonDocument(8)
                .append("field", encode(field))
                .append("input", encode(input))
                .append("value", encode(value));
        return operator("$setField", doc);
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param expression must resolve to an array
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Object expression) {
        return operator("$allElementsTrue", encodeList(expression));
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Iterable<?> elements) {
        return allElementsTrue(encode(elements));
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Object... elements) {
        return allElementsTrue(encodeList(elements));
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param expression must resolve to an array
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Object expression) {
        return operator("$anyElementTrue", encodeList(expression));
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Iterable<?> elements) {
        return anyElementTrue(encode(elements));
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Object... elements) {
        return anyElementTrue(encodeList(elements));
    }

    private static final Bson setCompare(String name, Object expression1, Object expression2) {
        return operator(name, encodeList(expression1, expression2));
    }

    /**
     * Creates a $setDifference set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setDifference set expression operator
     */
    public static final Bson setDifference(Object expression1, Object expression2) {
        return setCompare("$setDifference", expression1, expression2);
    }

    /**
     * Creates a $setEquals set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setEquals set expression operator
     */
    public static final Bson setEquals(Object expression1, Object expression2) {
        return setCompare("$setEquals", expression1, expression2);
    }

    /**
     * Creates a $setIntersection set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setIntersection set expression operator
     */
    public static final Bson setIntersection(Object... expressions) {
        return operator("$setIntersection", encodeList(expressions));
    }

    /**
     * Creates a $setIntersection set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setIntersection set expression operator
     */
    public static final Bson setIntersection(Iterable<?> expressions) {
        return operator("$setIntersection", encode(expressions));
    }

    /**
     * Creates a $setIsSubset set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setIsSubset set expression operator
     */
    public static final Bson setIsSubset(Object expression1, Object expression2) {
        return setCompare("$setIsSubset", expression1, expression2);
    }

    /**
     * Creates a $setUnion set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setUnion set expression operator
     */
    public static final Bson setUnion(Object... expressions) {
        return operator("$setUnion", encodeList(expressions));
    }

    /**
     * Creates a $setUnion set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setUnion set expression operator
     */
    public static final Bson setUnion(Iterable<?> expressions) {
        return operator("$setUnion", encode(expressions));
    }

    /**
     * Creates a $concat string expression operator.
     *
     * @param expressions can be any valid expression as long as they resolve to strings
     * @return the $concat string expression operator
     */
    public static final Bson concat(Object... expressions) {
        return operator("$concat", encodeList(expressions));
    }

    /**
     * Creates a $concat string expression operator.
     *
     * @param expressions can be any valid expression as long as they resolve to strings
     * @return the $concat string expression operator
     */
    public static final Bson concat(Iterable<?> expressions) {
        return operator("$concat", encode(expressions));
    }

    private static final Bson indexOf(String name, Object string, Object subString, Object start, Object end) {
        var params = new BsonArray(4);
        params.add(encode(string));
        params.add(encode(subString));
        if (start != null) {
            params.add(encode(start));
            if (end != null) {
                params.add(encode(end));
            }
        }
        return operator(name, params);
    }

    /**
     * Creates a $indexOfBytes string expression operator.
     *
     * @param string    can be any valid expression as long as it resolves to a string
     * @param subString can be any valid expression as long as it resolves to a string
     * @param start     optional, an integral number that specifies the starting index position for the search, can be
     *                  any valid expression that resolves to a non-negative integral number
     * @param end       optional, n integral number that specifies the ending index position for the search, can be any
     *                  valid expression that resolves to a non-negative integral number
     * @return the $indexOfBytes string expression operator
     */
    public static final Bson indexOfBytes(Object string, Object subString, Object start, Object end) {
        return indexOf("$indexOfBytes", string, subString, start, end);
    }

    /**
     * Creates a $indexOfCP string expression operator.
     *
     * @param string    can be any valid expression as long as it resolves to a string
     * @param subString can be any valid expression as long as it resolves to a string
     * @param start     optional, an integral number that specifies the starting index position for the search, can be
     *                  any valid expression that resolves to a non-negative integral number
     * @param end       optional, n integral number that specifies the ending index position for the search, can be any
     *                  valid expression that resolves to a non-negative integral number
     * @return the $indexOfCP string expression operator
     */
    public static final Bson indexOfCP(Object string, Object subString, Object start, Object end) {
        return indexOf("$indexOfCP", string, subString, start, end);
    }

    private static final Bson trimOperator(String name, Object input, Object chars) {
        var doc = new BsonDocument(4).append("input", encode(input));
        if (chars != null) {
            doc.append("chars", encode(chars));
        }
        return operator(name, doc);
    }

    /**
     * Creates a $ltrim string expression operator.
     *
     * @param input the string to trim, can be any valid expression that resolves to a string
     * @param chars optional, the character(s) to trim from the beginning of the input,  can be any valid expression
     *              that resolves to a string
     * @return the $ltrim string expression operator
     */
    public static final Bson ltrim(Object input, Object chars) {
        return trimOperator("$ltrim", input, chars);
    }

    private static final Bson regexOperator(String name, Object input, Object regex, Object options) {
        var doc = new BsonDocument(8).append("input", encode(input)).append("regex", encode(regex));
        if (options != null) {
            doc.append("options", encode(options));
        }
        return operator(name, doc);
    }

    /**
     * Creates a $regexFind string expression operator.
     *
     * @param input   the string on which you wish to apply the regex pattern, can be a string or any valid expression
     *                that resolves to a string
     * @param regex   the regex pattern to apply, can be any valid expression that resolves to a string ot regex pattern
     * @param options optional, the regex options, can be any valid expression that resolves to a string
     * @return the $regexFind string expression operator
     */
    public static final Bson regexFind(Object input, Object regex, Object options) {
        return regexOperator("$regexFind", input, regex, options);
    }

    /**
     * Creates a $regexFindAll string expression operator.
     *
     * @param input   the string on which you wish to apply the regex pattern, can be a string or any valid expression
     *                that resolves to a string
     * @param regex   the regex pattern to apply, can be any valid expression that resolves to a string ot regex pattern
     * @param options optional, the regex options, can be any valid expression that resolves to a string
     * @return the $regexFindAll string expression operator
     */
    public static final Bson regexFindAll(Object input, Object regex, Object options) {
        return regexOperator("$regexFindAll", input, regex, options);
    }

    /**
     * Creates a $regexMatch string expression operator.
     *
     * @param input   the string on which you wish to apply the regex pattern, can be a string or any valid expression
     *                that resolves to a string
     * @param regex   the regex pattern to apply, can be any valid expression that resolves to a string ot regex pattern
     * @param options optional, the regex options, can be any valid expression that resolves to a string
     * @return the $regexMatch string expression operator
     */
    public static final Bson regexMatch(Object input, Object regex, Object options) {
        return regexOperator("$regexMatch", input, regex, options);
    }

    private static final Bson replaceOperator(String name, Object input, Object find, Object replacement) {
        var doc = new BsonDocument(8)
                .append("input", encode(input))
                .append("find", encode(find))
                .append("replacement", encode(replacement));
        return operator(name, doc);
    }

    /**
     * Creates a $replaceOne string expression operator.
     *
     * @param input       the string on which you wish to apply the {@code find}, can be any valid expression that
     *                    resolves to a {@code string} or a {@code null}
     * @param find        the string to search for within the given {@code input}, can be any valid expression that
     *                    resolves to a {@code string} or a {@code null}
     * @param replacement the string to use to replace the first matched instance of {@code find} in {@code input}, can
     *                    be any valid expression that resolves to a {@code string} or a {@code null}
     * @return the $replaceOne string expression operator
     */
    public static final Bson replaceOne(Object input, Object find, Object replacement) {
        return replaceOperator("$replaceOne", input, find, replacement);
    }

    /**
     * Creates a $replaceAll string expression operator.
     *
     * @param input       the string on which you wish to apply the {@code find}, can be any valid expression that
     *                    resolves to a {@code string} or a {@code null}
     * @param find        the string to search for within the given {@code input}, can be any valid expression that
     *                    resolves to a {@code string} or a {@code null}
     * @param replacement the string to use to replace the first matched instance of {@code find} in {@code input}, can
     *                    be any valid expression that resolves to a {@code string} or a {@code null}
     * @return the $replaceAll string expression operator
     */
    public static final Bson replaceAll(Object input, Object find, Object replacement) {
        return replaceOperator("$replaceAll", input, find, replacement);
    }

    /**
     * Creates a $rtrim string expression operator.
     *
     * @param input the string to trim, can be any valid expression that resolves to a string
     * @param chars optional, the character(s) to trim from the beginning of the input,  can be any valid expression
     *              that resolves to a string
     * @return the $rtrim string expression operator
     */
    public static final Bson rtrim(Object input, Object chars) {
        return trimOperator("$rtrim", input, chars);
    }

    /**
     * Creates a $split string expression operator.
     *
     * @param string    the string to be split, can be any valid expression as long as it resolves to a string
     * @param delimiter the delimiter to use when splitting the string expression, can be any valid expression as long
     *                  as it resolves to a string
     * @return the $split string expression operator
     */
    public static final Bson split(Object string, Object delimiter) {
        return operator("$split", encodeList(string, delimiter));
    }

    /**
     * Creates a $strLenBytes string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $strLenBytes string expression operator
     */
    public static final Bson strLenBytes(Object string) {
        return operator("$strLenBytes", encode(string));
    }

    /**
     * Creates a $strLenCP string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $strLenCP string expression operator
     */
    public static final Bson strLenCP(Object string) {
        return operator("$strLenCP", encode(string));
    }

    /**
     * Creates a $strcasecmp string expression operator.
     *
     * @param expression1 the first string, can be any valid expression as long as it resolves to a string
     * @param expression2 the second string, can be any valid expression as long as it resolves to a string
     * @return the $strcasecmp string expression operator
     */
    public static final Bson strcasecmp(Object expression1, Object expression2) {
        return operator("$strcasecmp", encodeList(expression1, expression2));
    }

    /**
     * Creates a $substr string expression operator.
     *
     * @param string the string, can be any valid expression as long as it resolves to a string
     * @param start  the starting index, can be any valid expression as long as it resolves to an integer
     * @param length the char length, can be any valid expression as long as it resolves to an integer
     * @return the $substr string expression operator
     */
    public static final Bson substr(Object string, Object start, Object length) {
        return operator("$substr", encodeList(string, start, length));
    }

    /**
     * Creates a $substrBytes string expression operator.
     *
     * @param string the string, can be any valid expression as long as it resolves to a string
     * @param index  the byte index, can be any valid expression as long as it resolves to an integer
     * @param count  the byte count, can be any valid expression as long as it resolves to an integer
     * @return the $substrBytes string expression operator
     */
    public static final Bson substrBytes(Object string, Object index, Object count) {
        return operator("$substrBytes", encodeList(string, index, count));
    }

    /**
     * Creates a $substrCP string expression operator.
     *
     * @param string the string, can be any valid expression as long as it resolves to a string
     * @param index  the code point index, can be any valid expression as long as it resolves to an integer
     * @param count  the code point count, can be any valid expression as long as it resolves to an integer
     * @return the $substrCP string expression operator
     */
    public static final Bson substrCP(Object string, Object index, Object count) {
        return operator("$substrCP", encodeList(string, index, count));
    }

    /**
     * Creates a $toLower string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $toLower string expression operator
     */
    public static final Bson toLower(Object string) {
        return operator("$toLower", encode(string));
    }

    /**
     * Creates a $toString string expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a string
     * @return the $toString string expression operator
     */
    public static final Bson toString(Object expression) {
        return operator("$toString", encode(expression));
    }

    /**
     * Creates a $trim string expression operator.
     *
     * @param input the string to trim, can be any valid expression that resolves to a string
     * @param chars optional, the character(s) to trim from the beginning of the input,  can be any valid expression
     *              that resolves to a string
     * @return the $trim string expression operator
     */
    public static final Bson trim(Object input, Object chars) {
        return trimOperator("$trim", input, chars);
    }

    /**
     * Creates a $toUpper string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $toUpper string expression operator
     */
    public static final Bson toUpper(Object string) {
        return operator("$toUpper", encode(string));
    }

    /**
     * Creates a $meta text expression operator.
     *
     * @param metaDataKeyword the metadata keyword
     * @return the $meta text expression operator
     */
    public static final Bson meta(Object metaDataKeyword) {
        return operator("$meta", encode(metaDataKeyword));
    }

    /**
     * Creates a $tsIncrement timestamp expression operator.
     *
     * @param expression must resolve to a timestamp
     * @return the $tsIncrement timestamp expression operator
     */
    public static final Bson tsIncrement(Object expression) {
        return operator("$tsIncrement", encode(expression));
    }

    /**
     * Creates a $tsSecond timestamp expression operator.
     *
     * @param expression must resolve to a timestamp
     * @return the $tsSecond timestamp expression operator
     */
    public static final Bson tsSecond(Object expression) {
        return operator("$tsSecond", encode(expression));
    }

    private ExpressionOperators() {
    }

}
