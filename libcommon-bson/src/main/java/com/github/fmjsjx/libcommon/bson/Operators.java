package com.github.fmjsjx.libcommon.bson;

import com.github.fmjsjx.libcommon.util.ObjectUtil;
import org.bson.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.stream.Stream;

import static com.github.fmjsjx.libcommon.bson.BsonValueUtil.*;


/**
 * A factory for MongoDB BSON operators.
 *
 * @author MJ Fang
 * @since 3.2
 */
public final class Operators {

    private interface Operator extends Bson {

        String operator();

        <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry);

        @Override
        default <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            var writer = new BsonDocumentWriter(new BsonDocument(1));
            writer.writeStartDocument();
            writer.writeName(operator());
            writeOperatorValue(writer, documentClass, codecRegistry);
            return writer.getDocument();
        }

    }

    private record SimpleOperator(String operator, Object expression) implements Operator {

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            encodeValue(writer, expression, codecRegistry);
        }

        @Override
        public String toString() {
            return "SimpleOperator(operator=" + operator + ", expression=" + ObjectUtil.toString(expression) + ")";
        }

    }

    private record SimpleArrayOperator(String operator, Object... expressions) implements Operator {

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            for (var expression : expressions) {
                encodeValue(writer, expression, codecRegistry);
            }
            writer.writeEndArray();
        }

        @Override
        public String toString() {
            return "SimpleOperator(operator=" + operator + ", expressions=" + Arrays.toString(expressions) + ")";
        }

    }

    private record SimpleIterableOperator(String operator, Iterable<?> expressions) implements Operator {
        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            for (var expression : expressions) {
                encodeValue(writer, expression, codecRegistry);
            }
            writer.writeEndArray();
        }
    }

    private record SimpleDocumentOperator1(String operator, String name, Object expression) implements Operator {

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName(name);
            encodeValue(writer, expression, codecRegistry);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "SimpleDocumentOperator1(operator=" + operator +
                    ", name=" + name + ", expression=" + ObjectUtil.toString(expression) + ")";
        }
    }

    private record SimpleDocumentOperator2(String operator, String name1, Object expression1, String name2,
                                           Object expression2) implements Operator {

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName(name1);
            encodeValue(writer, expression1, codecRegistry);
            writer.writeName(name2);
            encodeValue(writer, expression2, codecRegistry);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "SimpleDocumentOperator2(operator=" + operator +
                    ", name1=" + name1 + ", expression1=" + ObjectUtil.toString(expression1) +
                    ", name2=" + name2 + ", expression2=" + ObjectUtil.toString(expression2) + ")";
        }

    }

    private record SimpleDocumentOperator3(String operator, String name1, Object expression1, String name2,
                                           Object expression2, String name3, Object expression3) implements Operator {
        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName(name1);
            encodeValue(writer, expression1, codecRegistry);
            writer.writeName(name2);
            encodeValue(writer, expression2, codecRegistry);
            writer.writeName(name3);
            encodeValue(writer, expression3, codecRegistry);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "SimpleDocumentOperator3(operator=" + operator +
                    ", name1=" + name1 + ", expression1=" + ObjectUtil.toString(expression1) +
                    ", name2=" + name2 + ", expression2=" + ObjectUtil.toString(expression2) +
                    ", name3=" + name3 + ", expression3=" + ObjectUtil.toString(expression3) + ")";
        }

    }

    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $abs arithmetic expression operator
     */
    public static final Bson abs(Object expression) {
        return new SimpleOperator("$abs", expression);
    }

    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $abs arithmetic expression operator
     */
    public static final Bson add(Iterable<?> expressions) {
        return new SimpleIterableOperator("$add", expressions);
    }

    /**
     * Creates an $abs arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $abs arithmetic expression operator
     */
    public static final Bson add(Object... expressions) {
        return new SimpleArrayOperator("$add", expressions);
    }

    /**
     * Creates a $ceil arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $ceil arithmetic expression operator
     */
    public static final Bson ceil(Object expression) {
        return new SimpleOperator("$ceil", expression);
    }

    /**
     * Creates a $divide arithmetic expression operator.
     *
     * @param dividend can be any valid expression as long as it resolves to a number
     * @param divisor  can be any valid expression as long as it resolves to a number
     * @return the $divide arithmetic expression operator
     */
    public static final Bson divide(Object dividend, Object divisor) {
        return new SimpleArrayOperator("$divide", dividend, divisor);
    }

    /**
     * Creates an $exp arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $exp arithmetic expression operator
     */
    public static final Bson exp(Object expression) {
        return new SimpleOperator("$exp", expression);
    }

    /**
     * Creates a $floor arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a number
     * @return the $floor arithmetic expression operator
     */
    public static final Bson floor(Object expression) {
        return new SimpleOperator("$floor", expression);
    }

    /**
     * Creates a $ln arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a non-negative number
     * @return the $ln arithmetic expression operator
     */
    public static final Bson ln(Object expression) {
        return new SimpleOperator("$ln", expression);
    }

    /**
     * Creates a $log arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a non-negative number
     * @param base   can be any valid expression as long as it resolves to a positive number greater than {@code 1}
     * @return the $log arithmetic expression operator
     */
    public static final Bson log(Object number, Object base) {
        return new SimpleArrayOperator("$log", number, base);
    }

    /**
     * Creates a $log10 arithmetic expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a non-negative number
     * @return the $log10 arithmetic expression operator
     */
    public static final Bson log10(Object expression) {
        return new SimpleOperator("$log10", expression);
    }

    /**
     * Creates a $mod arithmetic expression operator.
     *
     * @param dividend can be any valid expression as long as it resolves to a number
     * @param divisor  can be any valid expression as long as it resolves to a number
     * @return the $mod arithmetic expression operator
     */
    public static final Bson mod(Object dividend, Object divisor) {
        return new SimpleArrayOperator("$mod", dividend, divisor);
    }

    /**
     * Creates a $multiply arithmetic expression operator.
     *
     * @param expressions can be any valid expression as long as it resolves to numbers
     * @return the $multiply arithmetic expression operator
     */
    public static final Bson multiply(Object... expressions) {
        return new SimpleArrayOperator("$multiply", expressions);
    }

    /**
     * Creates a $round arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @param place  can be any valid expression as long as it resolves to a number
     * @return the $round arithmetic expression operator
     */
    public static final Bson round(Object number, Object place) {
        return new SimpleArrayOperator("$round", number, place);
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
        return new SimpleOperator("$sqrt", expression);
    }

    /**
     * Creates a $subtract arithmetic expression operator.
     *
     * @param expression1 can be any valid expression as long as they resolve to numbers and/or dates
     * @param expression2 can be any valid expression as long as they resolve to numbers and/or dates
     * @return the $subtract arithmetic expression operator
     */
    public static final Bson subtract(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$subtract", expression1, expression2);
    }

    /**
     * Creates a $trunc arithmetic expression operator.
     *
     * @param number can be any valid expression as long as it resolves to a number
     * @param place  can be any valid expression as long as it resolves to a number
     * @return the $trunc arithmetic expression operator
     */
    public static final Bson trunc(Object number, Object place) {
        return new SimpleArrayOperator("$trunc", number, place);
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
        return new SimpleArrayOperator("$arrayElemAt", array, idx);
    }

    private record ArrayElemAtOperator(Iterable<?> array, Object idx) implements Operator {

        @Override
        public String operator() {
            return "$arrayElemAt";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            writer.writeStartArray();
            for (var v : array) {
                encodeValue(writer, v, codecRegistry);
            }
            writer.writeEndArray();
            encodeValue(writer, idx, codecRegistry);
            writer.writeEndArray();
        }

        @Override
        public String toString() {
            return "ArrayElemAtOperator(array=" + array + ", idx=" + ObjectUtil.toString(idx) + ")";
        }

    }

    /**
     * Creates an $arrayElemAt array expression operator.
     *
     * @param array the array
     * @param idx   can be any valid expression that resolves to an integer
     * @return the $arrayElemAt array expression operator
     */
    public static final Bson arrayElemAt(Iterable<?> array, Object idx) {
        return new ArrayElemAtOperator(array, idx);
    }

    /**
     * Creates an $arrayElemAt array expression operator.
     *
     * @param idx   can be any valid expression that resolves to an integer
     * @param array the array
     * @return the $arrayElemAt array expression operator
     */
    public static final Bson arrayElemAt(Object idx, Object... array) {
        return arrayElemAt(Arrays.asList(array), idx);
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param expression can be any valid expression that resolves to an array
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObject(Object expression) {
        return new SimpleOperator("$arrayToObject", expression);
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param entries the entries
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObject(Map.Entry<?, ?>... entries) {
        return new ArrayToObjectOperator(false, entries);
    }

    private record ArrayToObjectOperator(boolean literal, Map.Entry<?, ?>... entries) implements Operator {

        @Override
        public String operator() {
            return "$arrayToObject";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            if (literal) {
                writer.writeStartDocument();
                writer.writeName("$literal");
            } else {
                writer.writeStartArray();
            }
            writer.writeStartArray();
            for (var v : entries) {
                writer.writeStartArray();
                encodeValue(writer, v.getKey(), codecRegistry);
                encodeValue(writer, v.getValue(), codecRegistry);
                writer.writeEndArray();
            }
            writer.writeEndArray();
            if (literal) {
                writer.writeEndDocument();
            } else {
                writer.writeEndArray();
            }
        }

        @Override
        public String toString() {
            return "ArrayToObjectOperator(literal=" + literal + ", entries=" + Arrays.toString(entries) + ")";
        }
    }

    /**
     * Creates an $arrayToObject array expression operator.
     *
     * @param entries the entries
     * @return the $arrayToObject array expression operator
     */
    public static final Bson arrayToObjectLiteral(Map.Entry<?, ?>... entries) {
        return new ArrayToObjectOperator(true, entries);
    }

    /**
     * Creates an $concatArrays array expression operator.
     *
     * @param arrays the arrays
     * @return the $concatArrays array expression operator
     */
    public static final Bson concatArrays(Object... arrays) {
        return new SimpleArrayOperator("$concatArrays", arrays);
    }

    /**
     * Creates an $concatArrays array expression operator.
     *
     * @param arrays the arrays
     * @return the $concatArrays array expression operator
     */
    public static final Bson concatArrays(Iterable<?> arrays) {
        return new SimpleIterableOperator("$concatArrays", arrays);
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
        return new FilterOperator(input, cond, as, limit);
    }

    private record FilterOperator(Object input, Object cond, String as, Object limit) implements Operator {

        @Override
        public String operator() {
            return "$filter";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("input");
            encodeValue(writer, input, codecRegistry);
            writer.writeName("cond");
            encodeValue(writer, cond, codecRegistry);
            if (as != null) {
                writer.writeName("as");
                encodeValue(writer, as, codecRegistry);
            }
            if (limit != null) {
                writer.writeName("limit");
                encodeValue(writer, limit, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "FilterOperator(input=" + ObjectUtil.toString(input) +
                    ", cond=" + ObjectUtil.toString(cond) +
                    ", as=" + ObjectUtil.toString(as) +
                    ", limit=" + ObjectUtil.toString(limit) + ")";
        }

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
     * Creates a $first array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array, null or missing
     * @return the $first array expression operator
     */
    public static final Bson first(Object expression) {
        return new SimpleOperator("$first", expression);
    }

    /**
     * Creates a $firstN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return {@code n} elements
     * @return the $firstN array expression operator
     */
    public static final Bson firstN(Object n, Object input) {
        return new SimpleDocumentOperator2("$firstN", "n", n, "input", input);
    }

    /**
     * Creates an $in array expression operator.
     *
     * @param expression      any valid expression expression
     * @param arrayExpression any valid expression that resolves to an array
     * @return the $in array expression operator
     */
    public static final Bson in(Object expression, Object arrayExpression) {
        return new SimpleArrayOperator("$in", expression, arrayExpression);
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
        var expressions = new ArrayList<>(len);
        expressions.add(array);
        expressions.add(search);
        if (start != null) {
            expressions.add(start);
        }
        if (end != null) {
            if (start == null) {
                expressions.add(0);
            }
            expressions.add(end);
        }
        return new SimpleIterableOperator("$indexOfArray", expressions);
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
        return new SimpleOperator("$isArray", expression);
    }

    /**
     * Creates a $last array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array, null, or missing
     * @return the $last array expression operator
     */
    public static final Bson last(Object expression) {
        return new SimpleOperator("$last", expression);
    }

    /**
     * Creates a $lastN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return {@code n} elements
     * @return the $lastN array expression operator
     */
    public static final Bson lastN(Object n, Object input) {
        return new SimpleDocumentOperator2("$lastN", "n", n, "input", input);
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
        return new MapOperator(input, as, in);
    }

    private record MapOperator(Object input, String as, Object in) implements Operator {

        @Override
        public String operator() {
            return "$map";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("input");
            encodeValue(writer, input, codecRegistry);
            if (as != null) {
                writer.writeName("as");
                encodeValue(writer, as, codecRegistry);
            }
            writer.writeName("in");
            encodeValue(writer, in, codecRegistry);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "MapOperator(input=" + ObjectUtil.toString(input) +
                    ", as=" + ObjectUtil.toString(as) +
                    ", in=" + ObjectUtil.toString(in) + ")";
        }
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
        return new SimpleDocumentOperator2("$maxN", "n", n, "input", input);
    }

    /**
     * Creates a $minN array expression operator.
     *
     * @param n     an expression that resolves to a positive integer
     * @param input an expression that resolves to the array from which to return the minimal {@code n} elements
     * @return the $minN array expression operator
     */
    public static final Bson minN(Object n, Object input) {
        return new SimpleDocumentOperator2("$minN", "n", n, "input", input);
    }

    /**
     * Creates a $objectToArray array expression operator.
     *
     * @param object can be any valid expression as long as it resolves to a document object
     * @return the $objectToArray array expression operator
     */
    public static final Bson objectToArray(Object object) {
        return new SimpleOperator("$objectToArray", object);
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
        var params = new ArrayList<>(step == null ? 2 : 3);
        params.add(start);
        params.add(end);
        if (step != null) {
            params.add(step);
        }
        return new SimpleIterableOperator("$range", params);
    }

    /**
     * Creates a $range array expression operator.
     *
     * @param start an integer that specifies the start of the sequence, can be any valid expression that resolves to an
     *              integer
     * @param end   an integer that specifies the exclusive upper limit of the sequence, can be any valid expression
     *              that resolves to an integer
     * @return the $range array expression operator
     */
    public static final Bson range(Object start, Object end) {
        return range(start, end, null);
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
        return new SimpleDocumentOperator3("$reduce", "input", input, "initialValue", initialValue, "in", in);
    }

    /**
     * Creates a $reverseArray array expression operator.
     *
     * @param array can be any valid expression as long as it resolves to an array
     * @return the $reverseArray array expression operator
     */
    public static final Bson reverseArray(Object array) {
        return new SimpleOperator("$reverseArray", array);
    }

    /**
     * Creates a $size array expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to an array
     * @return the $size array expression operator
     */
    public static final Bson size(Object expression) {
        return new SimpleOperator("$size", expression);
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
        var params = new ArrayList<>(position == null ? 2 : 3);
        params.add(array);
        if (position != null) {
            params.add(position);
        }
        params.add(n);
        return new SimpleIterableOperator("$slice", params);
    }

    /**
     * Creates a $slice array expression operator.
     *
     * @param array any valid expression as long as it resolves to an array.
     * @param n     any valid expression as long as it resolves to an integer, if {@code position} is specified,
     *              {@code n} must resolve to a positive integer
     * @return the $slice array expression operator
     */
    public static final Bson slice(Object array, Object n) {
        return slice(array, null, n);
    }

    /**
     * Creates a $sortArray array expression operator.
     *
     * @param input  the array to be sorted
     * @param sortBy the document specifies a sort ordering
     * @return the $sortArray array expression operator
     */
    public static final Bson sortArray(Object input, Object sortBy) {
        return new SimpleDocumentOperator2("$sortArray", "input", input, "sortBy", sortBy);
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
        return new ZipOperator(inputs, useLongestLength, defaults);
    }

    private record ZipOperator(Iterable<?> inputs, boolean useLongestLength, Object defaults) implements Operator {

        @Override
        public String operator() {
            return "$zip";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeStartArray("inputs");
            for (var input : inputs) {
                encodeValue(writer, input, codecRegistry);
            }
            writer.writeEndArray();
            if (useLongestLength) {
                writer.writeBoolean("useLongestLength", true);
                if (defaults != null) {
                    writer.writeName("defaults");
                    encodeValue(writer, defaults, codecRegistry);
                }
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "ZipOperator(inputs=" + inputs +
                    ", useLongestLength=" + useLongestLength +
                    ", defaults=" + ObjectUtil.toString(defaults);
        }
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
        return zip(Arrays.asList(inputs), useLongestLength, defaults);
    }

    /**
     * Creates an $and boolean expression operator.
     *
     * @param expressions an array of expressions
     * @return the $and boolean expression operator
     */
    public static final Bson and(Object... expressions) {
        return new SimpleArrayOperator("$and", expressions);
    }

    /**
     * Creates a $not boolean expression operator.
     *
     * @param expression an array of expressions
     * @return the $not boolean expression operator
     */
    public static final Bson not(Object expression) {
        return new SimpleArrayOperator("$not", expression);
    }

    /**
     * Creates an $or boolean expression operator.
     *
     * @param expressions an array of expressions
     * @return the $or boolean expression operator
     */
    public static final Bson or(Object... expressions) {
        return new SimpleArrayOperator("$or", expressions);
    }

    /**
     * Creates an $expr operator.
     *
     * @param expression can be any valid aggregation expression
     * @return the $expr operator
     */
    public static final Bson expr(Object expression) {
        return new SimpleOperator("$expr", expression);
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
        return new SimpleArrayOperator(name, expression1, expression2);
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
        return new SimpleArrayOperator("$cond", ifExpression, thenExpression, elseExpression);
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
     * @since 3.2
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
     * @since 3.2
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
         * @param elseExpression the else expression
         * @return the $cond conditional expression operator
         */
        public Bson build(Object elseExpression) {
            return cond(ifExpression, thenExpression, elseExpression);
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
        return new IfNullOperator(inputExpressions, replacementExpression);
    }

    private record IfNullOperator(Iterable<?> inputExpressions, Object replacementExpression) implements Operator {

        @Override
        public String operator() {
            return "$ifNull";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            for (var inputExpression : inputExpressions) {
                encodeValue(writer, inputExpression, codecRegistry);
            }
            encodeValue(writer, replacementExpression, codecRegistry);
            writer.writeEndArray();
        }

        @Override
        public String toString() {
            return "IfNullOperator(inputExpressions=" + inputExpressions +
                    ", replacementExpression=" + ObjectUtil.toString(replacementExpression) + ")";
        }
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
        var list = new ArrayList<>(otherInputExpressions.length + 1);
        list.add(inputExpression);
        Collections.addAll(list, otherInputExpressions);
        return ifNull(list, replacementExpression);
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
     * @since 3.2
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
    public static final Bson switchN(Iterable<Bson> branches, Object defaultExpression) {
        return new SwitchOperator(branches, defaultExpression);
    }

    private record SwitchOperator(Iterable<Bson> branches, Object defaultExpression) implements Operator {

        @Override
        public String operator() {
            return "$switch";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeStartArray("branches");
            for (var branch : branches) {
                encodeValue(writer, branch, codecRegistry);
            }
            writer.writeEndArray();
            if (defaultExpression != null) {
                writer.writeName("default");
                encodeValue(writer, defaultExpression, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "SwitchOperator(branches=" + branches +
                    ", defaultExpression=" + ObjectUtil.toString(defaultExpression) + ")";
        }

    }

    /**
     * Creates a new {@link SwitchBuilder} instance.
     *
     * @return the {@code SwitchBuilder} instance
     */
    public static final SwitchBuilder switch0() {
        return new SwitchBuilder(List.of());
    }

    /**
     * Creates a new {@link BranchBuilder} instance.
     *
     * @param expression the case expression
     * @return the {@code BranchBuilder} instance
     */
    public static final BranchBuilder switchCase(Object expression) {
        return new BranchBuilder(List.of(), expression);
    }

    /**
     * Builder for $switch conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.2
     */
    public static final class SwitchBuilder {

        private final List<Bson> branches;

        private SwitchBuilder(List<Bson> branches) {
            this.branches = List.copyOf(branches);
        }

        /**
         * Returns the branches.
         *
         * @return the branches
         */
        public List<Bson> branches() {
            return branches;
        }

        /**
         * Creates a new {@link BranchBuilder} instance.
         *
         * @param expression the case expression
         * @return the {@code  BranchBuilder} instance
         */
        public BranchBuilder onCase(Object expression) {
            return new BranchBuilder(branches, expression);
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

    }

    /**
     * Builder for $switch conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.2
     */
    public static final class BranchBuilder {

        private final List<Bson> branches;
        private final Object caseExpression;

        private BranchBuilder(List<Bson> branches, Object caseExpression) {
            this.branches = List.copyOf(branches);
            this.caseExpression = caseExpression;
        }

        /**
         * Append branch document and returns self switch builder.
         *
         * @param expression the thenExpression
         * @return the {@code SwitchBuilder}
         */
        public SwitchBuilder then(Object expression) {
            var branch = Expressions.branch(caseExpression, expression);
            return new SwitchBuilder(Stream.concat(branches.stream(), Stream.of(branch)).toList());
        }

    }

    /**
     * Creates a $binarySize data size operator.
     *
     * @param expression can be any valid expression as long as it resolves to either a string or binary data value
     * @return the $binarySize data size operator
     */
    public static final Bson binarySize(Object expression) {
        return new SimpleOperator("$binarySize", expression);
    }

    /**
     * Creates a $bsonSize data size operator.
     *
     * @param expression can be any valid expression as long as it resolves to either an object or {@code null}
     * @return the $bsonSize data size operator
     */
    public static final Bson bsonSize(Object expression) {
        return new SimpleOperator("$bsonSize", expression);
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
        return new DateAddOperator(startDate, unit, amount, timezone);
    }

    private record DateAddOperator(Object startDate, Object unit, Object amount, Object timezone) implements Operator {

        @Override
        public String operator() {
            return "$dateAdd";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("startDate");
            encodeValue(writer, startDate, codecRegistry);
            writer.writeName("unit");
            encodeValue(writer, unit, codecRegistry);
            writer.writeName("amount");
            encodeValue(writer, amount, codecRegistry);
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateAddOperator(startDate=" + ObjectUtil.toString(startDate) +
                    ", unit=" + ObjectUtil.toString(unit) +
                    ", amount=" + ObjectUtil.toString(amount) +
                    ", timezone=" + ObjectUtil.toString(timezone) + ")";
        }
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
        return new DateDiff(startDate, endDate, unit, timezone, startOfWeek);
    }

    private record DateDiff(Object startDate, Object endDate, Object unit, Object timezone,
                            Object startOfWeek) implements Operator {
        @Override
        public String operator() {
            return "$dateDiff";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("startDate");
            encodeValue(writer, startDate, codecRegistry);
            writer.writeName("endDate");
            encodeValue(writer, endDate, codecRegistry);
            writer.writeName("unit");
            encodeValue(writer, unit, codecRegistry);
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            if (startOfWeek != null) {
                writer.writeName("startOfWeek");
                encodeValue(writer, startOfWeek, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateDiff(startDate=" + ObjectUtil.toString(startDate) +
                    ", endDate=" + ObjectUtil.toString(endDate) +
                    ", unit=" + ObjectUtil.toString(unit) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    ", startOfWeek=" + ObjectUtil.toString(startOfWeek);

        }
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
        return new DateFromPartsOperator(false, year, month, day, hour, minute, second, millisecond, timezone);
    }

    private record DateFromPartsOperator(boolean iso, Object year, Object monthOrWeek, Object day, Object hour,
                                         Object minute, Object second, Object millisecond,
                                         Object timezone) implements Operator {
        @Override
        public String operator() {
            return "$dateFromParts";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName(iso ? "isoWeekYear" : "year");
            encodeValue(writer, year, codecRegistry);
            if (monthOrWeek != null) {
                writer.writeName(iso ? "isoWeek" : "month");
                encodeValue(writer, monthOrWeek, codecRegistry);
            }
            if (day != null) {
                writer.writeName(iso ? "isoDayOfWeek" : "day");
                encodeValue(writer, day, codecRegistry);
            }
            appendTime(writer, codecRegistry, hour, minute, second, millisecond, timezone);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateFromPartsOperator(iso=" + iso +
                    ", year=" + ObjectUtil.toString(year) +
                    ", monthOrWeek=" + ObjectUtil.toString(monthOrWeek) +
                    ", day=" + ObjectUtil.toString(day) +
                    ", hour=" + ObjectUtil.toString(hour) +
                    ", minute=" + ObjectUtil.toString(minute) +
                    ", second=" + ObjectUtil.toString(second) +
                    ", millisecond=" + ObjectUtil.toString(millisecond) +
                    ", timezone=" + ObjectUtil.toString(timezone) + ")";
        }
    }

    private static final void appendTime(BsonDocumentWriter writer, CodecRegistry codecRegistry, Object hour,
                                         Object minute, Object second, Object millisecond, Object timezone) {
        if (hour != null) {
            writer.writeName("hour");
            encodeValue(writer, hour, codecRegistry);
        }
        if (minute != null) {
            writer.writeName("minute");
            encodeValue(writer, minute, codecRegistry);
        }
        if (second != null) {
            writer.writeName("second");
            encodeValue(writer, second, codecRegistry);
        }
        if (millisecond != null) {
            writer.writeName("millisecond");
            encodeValue(writer, millisecond, codecRegistry);
        }
        if (timezone != null) {
            writer.writeName("timezone");
            encodeValue(writer, timezone, codecRegistry);
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
        return new DateFromPartsOperator(true, isoWeekYear, isoWeek, isoDayOfWeek, hour, minute, second, millisecond,
                timezone);
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
    public static final Bson dateFromString(Object dateString, Object format, Object timezone,
                                            Object onError, Object onNull) {
        return new DateFromStringOperator(dateString, format, timezone, onError, onNull);
    }

    private record DateFromStringOperator(Object dateString, Object format, Object timezone, Object onError,
                                          Object onNull) implements Operator {

        @Override
        public String operator() {
            return "$dateFromString";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("dateString");
            encodeValue(writer, dateString, codecRegistry);
            if (format != null) {
                writer.writeName("format");
                encodeValue(writer, format, codecRegistry);
            }
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            if (onError != null) {
                writer.writeName("onError");
                encodeValue(writer, onError, codecRegistry);
            }
            if (onNull != null) {
                writer.writeName("onNull");
                encodeValue(writer, onNull, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateFromStringOperator{" +
                    "dateString=" + ObjectUtil.toString(dateString) +
                    ", format=" + ObjectUtil.toString(format) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    ", onError=" + ObjectUtil.toString(onError) +
                    ", onNull=" + ObjectUtil.toString(onNull) +
                    '}';
        }
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
        return new DateSubtract(startDate, unit, amount, timezone);
    }

    private record DateSubtract(Object startDate, Object unit, Object amount, Object timezone) implements Operator {

        @Override
        public String operator() {
            return "$dateSubtract";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("startDate");
            encodeValue(writer, startDate, codecRegistry);
            writer.writeName("unit");
            encodeValue(writer, unit, codecRegistry);
            writer.writeName("amount");
            encodeValue(writer, amount, codecRegistry);
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateSubtract(startDate=" + ObjectUtil.toString(startDate) +
                    ", unit=" + ObjectUtil.toString(unit) +
                    ", amount=" + ObjectUtil.toString(amount) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    '}';
        }
    }

    /**
     * Creates a $dateSubtract date expression operator.
     *
     * @param startDate the beginning date, in UTC, for the addition operation, can be any expression that resolves to
     *                  a Date, a Timestamp, or an ObjectID
     * @param unit      the unit used to measure the amount of time added to the startDate
     * @param amount    the number of units added to the startDate, can be an expression that resolves to an integer or
     *                  long
     * @return the $dateSubtract date expression operator
     */
    public static final Bson dateSubtract(Object startDate, Object unit, Object amount) {
        return dateSubtract(startDate, unit, amount, null);
    }


    /**
     * Creates a $dateToParts date expression operator.
     *
     * @param date the input date for which to return parts,  can be any expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dateToParts date expression operator
     */
    public static final Bson dateToParts(Object date) {
        return dateToParts(date, null, null);
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
        return new DateToPartsOperator(date, timezone, iso8601);
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
        return dateToParts(date, timezone, BsonBoolean.valueOf(iso8601));
    }

    private record DateToPartsOperator(Object date, Object timezone, Object iso8601) implements Operator {

        @Override
        public String operator() {
            return "$dateToParts";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("date");
            encodeValue(writer, date, codecRegistry);
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            if (iso8601 != null) {
                writer.writeName("iso8601");
                encodeValue(writer, iso8601, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateToPartsOperator(" +
                    "date=" + ObjectUtil.toString(date) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    ", iso8601=" + ObjectUtil.toString(iso8601) + ")";
        }
    }

    /**
     * Creates a $dateToString date expression operator.
     *
     * @param date     the date to convert to string, must be a valid expression that resolves to a Date, a Timestamp,
     *                 or an ObjectID
     * @param format   optional, the date format specification, can be any string literal, containing 0 or more format
     *                 specifiers
     * @param timezone optional, the timezone of the operation result
     * @param onError  optional, the value to return if the date is null or missing
     * @return the $dateToString date expression operator
     */
    public static final Bson dateToString(Object date, Object format, Object timezone, Object onError) {
        return new DateToStringOperator(date, format, timezone, onError);
    }

    private record DateToStringOperator(Object dateString, Object format, Object timezone,
                                        Object onError) implements Operator {

        @Override
        public String operator() {
            return "$dateToString";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("date");
            encodeValue(writer, dateString, codecRegistry);
            if (format != null) {
                writer.writeName("format");
                encodeValue(writer, format, codecRegistry);
            }
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            if (onError != null) {
                writer.writeName("onError");
                encodeValue(writer, onError, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateToStringOperator(dateString=" + ObjectUtil.toString(dateString) +
                    ", format=" + ObjectUtil.toString(format) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    ", onError=" + ObjectUtil.toString(onError) + ")";
        }
    }

    /**
     * Creates a $dateTrunc date expression operator.
     *
     * @param date the date to truncate, specified in UTC, can be any expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @param unit the unit of time
     * @return the $dateTrunc date expression operator
     */
    public static final Bson dateTrunc(Object date, Object unit) {
        return dateTrunc(date, unit, null, null, null);
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
        return new DateTruncOperator(date, unit, binSize, timezone, startOfWeek);
    }

    private record DateTruncOperator(Object date, Object unit, Object binSize, Object timezone,
                                     Object startOfWeek) implements Operator {

        @Override
        public String operator() {
            return "$dateTrunc";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("date");
            encodeValue(writer, date, codecRegistry);
            writer.writeName("unit");
            encodeValue(writer, unit, codecRegistry);
            if (binSize != null) {
                writer.writeName("binSize");
                encodeValue(writer, binSize, codecRegistry);
            }
            if (timezone != null) {
                writer.writeName("timezone");
                encodeValue(writer, timezone, codecRegistry);
            }
            if (startOfWeek != null) {
                writer.writeName("startOfWeek");
                encodeValue(writer, startOfWeek, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "DateTruncOperator{date=" + ObjectUtil.toString(date) +
                    ", unit=" + ObjectUtil.toString(unit) +
                    ", binSize=" + ObjectUtil.toString(binSize) +
                    ", timezone=" + ObjectUtil.toString(timezone) +
                    ", startOfWeek=" + ObjectUtil.toString(startOfWeek) + "\n";
        }
    }

    /**
     * Creates a $dayOfMonth date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dayOfMonth date expression operator
     */
    public static final Bson dayOfMonth(Object date) {
        return new SimpleOperator("$dayOfMonth", date);
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

    private static final Bson datePortion(String name, Object date, Object timezone) {
        return timezone == null ? new SimpleDocumentOperator1(name, "date", date)
                : new SimpleDocumentOperator2(name, "date", date, "timezone", timezone);
    }

    /**
     * Creates a $dayOfWeek date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $dayOfWeek date expression operator
     */
    public static final Bson dayOfWeek(Object date) {
        return new SimpleOperator("$dayOfWeek", date);
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
        return new SimpleOperator("$dayOfYear", date);
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
        return new SimpleOperator("$hour", date);
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
        return new SimpleOperator("$isoDayOfWeek", date);
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
        return new SimpleOperator("$isoWeek", date);
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
        return new SimpleOperator("$isoWeekYear", date);
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
        return new SimpleOperator("$millisecond", date);
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
        return new SimpleOperator("$minute", date);
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
        return new SimpleOperator("$month", date);
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
        return new SimpleOperator("$second", date);
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
        return new SimpleOperator("$toDate", expression);
    }

    /**
     * Creates a $week date expression operator.
     *
     * @param date the date to which the operator is applied, must be a valid expression that resolves to a Date, a
     *             Timestamp, or an ObjectID
     * @return the $week date expression operator
     */
    public static final Bson week(Object date) {
        return new SimpleOperator("$week", date);
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
        return new SimpleOperator("$year", date);
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
        return new SimpleOperator("$literal", expression);
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
        return input == null ? new SimpleDocumentOperator1("$getField", "field", field)
                : new SimpleDocumentOperator2("$getField", "field", field, "input", input);
    }

    /**
     * Creates a $getField miscellaneous operator.
     *
     * @param field the field in the input object for which you want to return a value, can be any valid expression that
     *              resolves to a string constant
     * @return the $getField miscellaneous operator
     */
    public static final Bson getField(Object field) {
        return new SimpleOperator("$getField", field);
    }

    /**
     * Creates a $rand miscellaneous operator.
     *
     * @return the $rand miscellaneous operator
     */
    public static final Bson rand() {
        return RandOperator.INSTANCE;
    }

    private record RandOperator() implements Operator {

        private static final RandOperator INSTANCE = new RandOperator();

        @Override
        public String operator() {
            return "$rand";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeEndDocument();
        }

    }

    /**
     * Creates a $sampleRate miscellaneous operator.
     *
     * @param expression can be any valid expression that resolves to a non-negative double value
     * @return the $sampleRate miscellaneous operator
     */
    public static final Bson sampleRate(Object expression) {
        return new SimpleOperator("$sampleRate", expression);
    }

    /**
     * Creates a $mergeObjects object expression operator.
     *
     * @param expressions can be any valid expression that resolves to documents
     * @return the $mergeObjects object expression operator
     */
    public static final Bson mergeObjects(Iterable<?> expressions) {
        return new SimpleIterableOperator("$mergeObjects", expressions);
    }

    /**
     * Creates a $mergeObjects object expression operator.
     *
     * @param expressions can be any valid expression that resolves to documents
     * @return the $mergeObjects object expression operator
     */
    public static final Bson mergeObjects(Object... expressions) {
        return new SimpleArrayOperator("$mergeObjects", expressions);
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
        return new SimpleDocumentOperator3("$setField", "field", field, "input", input, "value", value);
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param expression must resolve to an array
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Object expression) {
        return new SimpleArrayOperator("$allElementsTrue", expression);
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Iterable<?> elements) {
        return new ElementsTrueOperator(true, elements);
    }

    private record ElementsTrueOperator(boolean all, Iterable<?> elements) implements Operator {

        @Override
        public String operator() {
            return all ? "$allElementsTrue" : "$anyElementTrue";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            writer.writeStartArray();
            for (var element : elements) {
                encodeValue(writer, element, codecRegistry);
            }
            writer.writeEndArray();
            writer.writeEndArray();
        }

        @Override
        public String toString() {
            return "ElementsTrueOperator(all=" + all + ", elements=" + elements + ")";
        }
    }

    /**
     * Creates a $allElementsTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $allElementsTrue set expression operator
     */
    public static final Bson allElementsTrue(Object... elements) {
        return allElementsTrue(Arrays.asList(elements));
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param expression must resolve to an array
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Object expression) {
        return new SimpleArrayOperator("$anyElementTrue", expression);
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Iterable<?> elements) {
        return new ElementsTrueOperator(false, elements);
    }

    /**
     * Creates a $anyElementTrue set expression operator.
     *
     * @param elements each element in elements can be any valid expression
     * @return the $anyElementTrue set expression operator
     */
    public static final Bson anyElementTrue(Object... elements) {
        return anyElementTrue(Arrays.asList(elements));
    }

    /**
     * Creates a $setDifference set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setDifference set expression operator
     */
    public static final Bson setDifference(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$setDifference", expression1, expression2);
    }

    /**
     * Creates a $setEquals set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setEquals set expression operator
     */
    public static final Bson setEquals(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$setEquals", expression1, expression2);
    }

    /**
     * Creates a $setIntersection set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setIntersection set expression operator
     */
    public static final Bson setIntersection(Object... expressions) {
        return new SimpleArrayOperator("$setIntersection", expressions);
    }

    /**
     * Creates a $setIntersection set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setIntersection set expression operator
     */
    public static final Bson setIntersection(Iterable<?> expressions) {
        return new SimpleIterableOperator("$setIntersection", expressions);
    }

    /**
     * Creates a $setIsSubset set expression operator.
     *
     * @param expression1 must resolve to an array
     * @param expression2 must resolve to an array
     * @return the $setIsSubset set expression operator
     */
    public static final Bson setIsSubset(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$setIsSubset", expression1, expression2);
    }

    /**
     * Creates a $setUnion set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setUnion set expression operator
     */
    public static final Bson setUnion(Object... expressions) {
        return new SimpleArrayOperator("$setUnion", expressions);
    }

    /**
     * Creates a $setUnion set expression operator.
     *
     * @param expressions can be any valid expression as long as they each resolve to an array
     * @return the $setUnion set expression operator
     */
    public static final Bson setUnion(Iterable<?> expressions) {
        return new SimpleIterableOperator("$setUnion", expressions);
    }

    /**
     * Creates a $concat string expression operator.
     *
     * @param expressions can be any valid expression as long as they resolve to strings
     * @return the $concat string expression operator
     */
    public static final Bson concat(Object... expressions) {
        return new SimpleArrayOperator("$concat", expressions);
    }

    /**
     * Creates a $concat string expression operator.
     *
     * @param expressions can be any valid expression as long as they resolve to strings
     * @return the $concat string expression operator
     */
    public static final Bson concat(Iterable<?> expressions) {
        return new SimpleIterableOperator("$concat", expressions);
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
        return new IndexOfOperator("$indexOfBytes", string, subString, start, end);
    }

    private record IndexOfOperator(String operator, Object string, Object subString, Object start,
                                   Object end) implements Operator {
        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartArray();
            encodeValue(writer, string, codecRegistry);
            encodeValue(writer, subString, codecRegistry);
            if (start != null) {
                encodeValue(writer, start, codecRegistry);
                if (end != null) {
                    encodeValue(writer, end, codecRegistry);
                }
            }
            writer.writeEndArray();
        }

        @Override
        public String toString() {
            return "IndexOfOperator(operator=" + ObjectUtil.toString(operator) +
                    ", string=" + ObjectUtil.toString(string) +
                    ", subString=" + ObjectUtil.toString(subString) +
                    ", start=" + ObjectUtil.toString(start) +
                    ", end=" + ObjectUtil.toString(end) + ")";
        }
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
        return new IndexOfOperator("$indexOfCP", string, subString, start, end);
    }

    /**
     * Creates a $ltrim string expression operator.
     *
     * @param input the string to trim, can be any valid expression that resolves to a string
     * @return the $ltrim string expression operator
     */
    public static final Bson ltrim(Object input) {
        return ltrim(input, null);
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

    private static final Bson trimOperator(String operator, Object input, Object chars) {
        return chars == null ? new SimpleDocumentOperator1(operator, "input", input)
                : new SimpleDocumentOperator2(operator, "input", input, "chars", chars);
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

    private static final Bson regexOperator(String operator, Object input, Object regex, Object options) {
        return options == null ? new SimpleDocumentOperator2(operator, "input", input, "regex", regex)
                : new SimpleDocumentOperator3(operator, "input", input, "regex", regex, "options", options);
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

    private static final Bson replaceOperator(String operator, Object input, Object find, Object replacement) {
        return new SimpleDocumentOperator3(operator, "input", input, "find", find, "replacement", replacement);
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
     * @return the $rtrim string expression operator
     */
    public static final Bson rtrim(Object input) {
        return rtrim(input, null);
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
        return new SimpleArrayOperator("$split", string, delimiter);
    }

    /**
     * Creates a $strLenBytes string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $strLenBytes string expression operator
     */
    public static final Bson strLenBytes(Object string) {
        return new SimpleOperator("$strLenBytes", string);
    }

    /**
     * Creates a $strLenCP string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $strLenCP string expression operator
     */
    public static final Bson strLenCP(Object string) {
        return new SimpleOperator("$strLenCP", string);
    }

    /**
     * Creates a $strcasecmp string expression operator.
     *
     * @param expression1 the first string, can be any valid expression as long as it resolves to a string
     * @param expression2 the second string, can be any valid expression as long as it resolves to a string
     * @return the $strcasecmp string expression operator
     */
    public static final Bson strcasecmp(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$strcasecmp", expression1, expression2);
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
        return new SimpleArrayOperator("$substr", string, start, length);
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
        return new SimpleArrayOperator("$substrBytes", string, index, count);
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
        return new SimpleArrayOperator("$substrCP", string, index, count);
    }

    /**
     * Creates a $toLower string expression operator.
     *
     * @param string can be any valid expression as long as it resolves to a string
     * @return the $toLower string expression operator
     */
    public static final Bson toLower(Object string) {
        return new SimpleOperator("$toLower", string);
    }

    /**
     * Creates a $toString string expression operator.
     *
     * @param expression can be any valid expression as long as it resolves to a string
     * @return the $toString string expression operator
     */
    public static final Bson toString(Object expression) {
        return new SimpleOperator("$toString", expression);
    }

    /**
     * Creates a $trim string expression operator.
     *
     * @param input the string to trim, can be any valid expression that resolves to a string
     * @return the $trim string expression operator
     */
    public static final Bson trim(Object input) {
        return trim(input, null);
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
        return new SimpleOperator("$toUpper", string);
    }

    /**
     * Creates a $meta text expression operator.
     *
     * @param metaDataKeyword the metadata keyword
     * @return the $meta text expression operator
     */
    public static final Bson meta(Object metaDataKeyword) {
        return new SimpleOperator("$meta", metaDataKeyword);
    }

    /**
     * Creates a $tsIncrement timestamp expression operator.
     *
     * @param expression must resolve to a timestamp
     * @return the $tsIncrement timestamp expression operator
     */
    public static final Bson tsIncrement(Object expression) {
        return new SimpleOperator("$tsIncrement", expression);
    }

    /**
     * Creates a $tsSecond timestamp expression operator.
     *
     * @param expression must resolve to a timestamp
     * @return the $tsSecond timestamp expression operator
     */
    public static final Bson tsSecond(Object expression) {
        return new SimpleOperator("$tsSecond", expression);
    }

    /**
     * Creates a $sin trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $sin trigonometry expression operator
     */
    public static final Bson sin(Object expression) {
        return new SimpleOperator("$sin", expression);
    }

    /**
     * Creates a $cos trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $cos trigonometry expression operator
     */
    public static final Bson cos(Object expression) {
        return new SimpleOperator("$cos", expression);
    }

    /**
     * Creates a $tan trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $tan trigonometry expression operator
     */
    public static final Bson tan(Object expression) {
        return new SimpleOperator("$tan", expression);
    }

    /**
     * Creates an $asin trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $asin trigonometry expression operator
     */
    public static final Bson asin(Object expression) {
        return new SimpleOperator("$asin", expression);
    }

    /**
     * Creates an $acos trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $acos trigonometry expression operator
     */
    public static final Bson acos(Object expression) {
        return new SimpleOperator("$acos", expression);
    }

    /**
     * Creates an $atan trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $atan trigonometry expression operator
     */
    public static final Bson atan(Object expression) {
        return new SimpleOperator("$atan", expression);
    }

    /**
     * Creates an $atan2 trigonometry expression operator.
     *
     * @param expression1 must resolve to a number
     * @param expression2 must resolve to a number
     * @return the $atan2 trigonometry expression operator
     */
    public static final Bson atan2(Object expression1, Object expression2) {
        return new SimpleArrayOperator("$atan2", expression1, expression2);
    }

    /**
     * Creates an $asinh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $asinh trigonometry expression operator
     */
    public static final Bson asinh(Object expression) {
        return new SimpleOperator("$asinh", expression);
    }

    /**
     * Creates an $acosh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $acosh trigonometry expression operator
     */
    public static final Bson acosh(Object expression) {
        return new SimpleOperator("$acosh", expression);
    }

    /**
     * Creates an $atanh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $atanh trigonometry expression operator
     */
    public static final Bson atanh(Object expression) {
        return new SimpleOperator("$atanh", expression);
    }

    /**
     * Creates a $sinh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $sinh trigonometry expression operator
     */
    public static final Bson sinh(Object expression) {
        return new SimpleOperator("$sinh", expression);
    }

    /**
     * Creates a $cosh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $cosh trigonometry expression operator
     */
    public static final Bson cosh(Object expression) {
        return new SimpleOperator("$cosh", expression);
    }

    /**
     * Creates a $tanh trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $tanh trigonometry expression operator
     */
    public static final Bson tanh(Object expression) {
        return new SimpleOperator("$tanh", expression);
    }

    /**
     * Creates a $degreesToRadians trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $degreesToRadians trigonometry expression operator
     */
    public static final Bson degreesToRadians(Object expression) {
        return new SimpleOperator("$degreesToRadians", expression);
    }

    /**
     * Creates a $radiansToDegrees trigonometry expression operator.
     *
     * @param expression must resolve to a number
     * @return the $radiansToDegrees trigonometry expression operator
     */
    public static final Bson radiansToDegrees(Object expression) {
        return new SimpleOperator("$radiansToDegrees", expression);
    }

    /**
     * Creates a $convert type expression operator.
     *
     * @param input can be any valid expression
     * @param to    a specified type
     * @return the $convert type expression operator
     */
    public static final Bson convert(Object input, Object to) {
        return convert(input, to, null, null);
    }

    /**
     * Creates a $convert type expression operator.
     *
     * @param input   can be any valid expression
     * @param to      the specified type
     * @param onError optional, the value to return on encountering an error during conversion
     * @param onNull  optional, the value to return if the input is null or missing
     * @return the $convert type expression operator
     */
    public static final Bson convert(Object input, Object to, Object onError, Object onNull) {
        return new ConvertOperator(input, to, onError, onNull);
    }

    private record ConvertOperator(Object input, Object to, Object onError, Object onNull) implements Operator {

        @Override
        public String operator() {
            return "$convert";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeName("input");
            encodeValue(writer, input, codecRegistry);
            writer.writeName("to");
            encodeValue(writer, to, codecRegistry);
            if (onError != null) {
                writer.writeName("onError");
                encodeValue(writer, onError, codecRegistry);
            }
            if (onNull != null) {
                writer.writeName("onNull");
                encodeValue(writer, onNull, codecRegistry);
            }
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "ConvertOperator(input=" + ObjectUtil.toString(input) +
                    ", to=" + ObjectUtil.toString(to) +
                    ", onError=" + ObjectUtil.toString(onError) +
                    ", onNull=" + ObjectUtil.toString(onNull) + ")";
        }
    }

    /**
     * Creates a $isNumber type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $isNumber type expression operator
     */
    public static final Bson isNumber(Object expression) {
        return new SimpleOperator("$isNumber", expression);
    }

    /**
     * Creates a $toBool type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toBool type expression operator
     */
    public static final Bson toBool(Object expression) {
        return new SimpleOperator("$toBool", expression);
    }

    /**
     * Creates a $toDecimal type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toDecimal type expression operator
     */
    public static final Bson toDecimal(Object expression) {
        return new SimpleOperator("$toDecimal", expression);
    }

    /**
     * Creates a $toDouble type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toDouble type expression operator
     */
    public static final Bson toDouble(Object expression) {
        return new SimpleOperator("$toDouble", expression);
    }

    /**
     * Creates a $toInt type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toInt type expression operator
     */
    public static final Bson toInt(Object expression) {
        return new SimpleOperator("$toInt", expression);
    }

    /**
     * Creates a $toLong type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toLong type expression operator
     */
    public static final Bson toLong(Object expression) {
        return new SimpleOperator("$toLong", expression);
    }

    /**
     * Creates a $toObjectId type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $toObjectId type expression operator
     */
    public static final Bson toObjectId(Object expression) {
        return new SimpleOperator("$toObjectId", expression);
    }

    /**
     * Creates a $type type expression operator.
     *
     * @param expression can be any valid expression
     * @return the $type type expression operator
     */
    public static final Bson type(Object expression) {
        return new SimpleOperator("$type", expression);
    }

    /**
     * Creates an $avg expression operator.
     *
     * @param expression can be any valid expression
     * @return the $avg expression operator
     */
    public static final Bson avg(Object expression) {
        return new SimpleOperator("$avg", expression);
    }

    /**
     * Creates an $avg expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $avg expression operator
     */
    public static final Bson avg(Object... expressions) {
        return new SimpleArrayOperator("$avg", expressions);
    }

    /**
     * Creates a $max expression operator.
     *
     * @param expression can be any valid expression
     * @return the $max expression operator
     */
    public static final Bson max(Object expression) {
        return new SimpleOperator("$max", expression);
    }

    /**
     * Creates a $max expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $max expression operator
     */
    public static final Bson max(Object... expressions) {
        return new SimpleArrayOperator("$max", expressions);
    }

    /**
     * Creates a $min expression operator.
     *
     * @param expression can be any valid expression
     * @return the $min expression operator
     */
    public static final Bson min(Object expression) {
        return new SimpleOperator("$min", expression);
    }

    /**
     * Creates a $min expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $min expression operator
     */
    public static final Bson min(Object... expressions) {
        return new SimpleArrayOperator("$min", expressions);
    }

    /**
     * Creates a $push expression operator.
     *
     * @param expression can be any valid expression
     * @return the $push expression operator
     */
    public static final Bson push(Object expression) {
        return new SimpleOperator("$push", expression);
    }

    /**
     * Creates a $stdDevPop expression operator.
     *
     * @param expression can be any valid expression
     * @return the $stdDevPop expression operator
     */
    public static final Bson stdDevPop(Object expression) {
        return new SimpleOperator("$stdDevPop", expression);
    }

    /**
     * Creates a $stdDevPop expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $stdDevPop expression operator
     */
    public static final Bson stdDevPop(Object... expressions) {
        return new SimpleArrayOperator("$stdDevPop", expressions);
    }

    /**
     * Creates a $stdDevSamp expression operator.
     *
     * @param expression can be any valid expression
     * @return the $stdDevSamp expression operator
     */
    public static final Bson stdDevSamp(Object expression) {
        return new SimpleOperator("$stdDevSamp", expression);
    }

    /**
     * Creates a $stdDevSamp expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $stdDevSamp expression operator
     */
    public static final Bson stdDevSamp(Object... expressions) {
        return new SimpleArrayOperator("$stdDevSamp", expressions);
    }

    /**
     * Creates a $sum expression operator.
     *
     * @param expression can be any valid expression
     * @return the $sum expression operator
     */
    public static final Bson sum(Object expression) {
        return new SimpleOperator("$sum", expression);
    }

    /**
     * Creates a $sum expression operator.
     *
     * @param expressions can be any valid expressions
     * @return the $sum expression operator
     */
    public static final Bson sum(Object... expressions) {
        return new SimpleArrayOperator("$sum", expressions);
    }

    /**
     * Creates a $let variable expression operator.
     *
     * @param vars assignment block for the variables accessible in the {@code in} expression
     * @param in   the expression to evaluate
     * @return the $let variable expression operator
     */
    public static final Bson let(Object vars, Object in) {
        return new SimpleDocumentOperator2("$let", "vars", vars, "in", in);
    }

    /**
     * Creates a $let variable expression operator.
     *
     * @param in   the expression to evaluate
     * @param vars assignment block for the variables accessible in the {@code in} expression
     * @return the $let variable expression operator
     */
    @SafeVarargs
    public static final Bson let(Object in, Map.Entry<String, ?>... vars) {
        return new LetOperator(Arrays.asList(vars), in);
    }

    private record LetOperator(List<Map.Entry<String, ?>> vars, Object in) implements Operator {

        @Override
        public String operator() {
            return "$let";
        }

        @Override
        public <TDocument> void writeOperatorValue(BsonDocumentWriter writer, Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            writer.writeStartDocument();
            writer.writeStartDocument("vars");
            for (var variable : vars) {
                writer.writeName(variable.getKey());
                encodeValue(writer, variable.getValue(), codecRegistry);
            }
            writer.writeEndDocument();
            writer.writeName("in");
            encodeValue(writer, in, codecRegistry);
            writer.writeEndDocument();
        }

        @Override
        public String toString() {
            return "LetOperator(vars=" + vars +
                    ", in=" + ObjectUtil.toString(in) + ")";
        }
    }

    /**
     * Creates a new {@link SwitchBuilderV2} instance.
     *
     * @return the {@code SwitchBuilderV2} instance
     * @since 3.3
     */
    public static final SwitchBuilderV2 switchV2() {
        return new SwitchBuilderV2();
    }

    /**
     * Version 2 Builder for $switch conditional expression operator.
     *
     * @author MJ Fang
     * @since 3.3
     */
    public static final class SwitchBuilderV2 {

        private final List<Bson> branches = new ArrayList<>();

        private SwitchBuilderV2() {
        }

        /**
         * Append a branch.
         *
         * @param branch the branch
         * @return this builder
         */
        public SwitchBuilderV2 branch(Bson branch) {
            branches.add(branch);
            return this;
        }

        /**
         * Append a branch.
         *
         * @param caseExpression the case expression
         * @param thenExpression the then expression
         * @return this builder
         */
        public SwitchBuilderV2 branch(Object caseExpression, Object thenExpression) {
            return branch(Expressions.branch(caseExpression, thenExpression));
        }

        /**
         * Returns the count of the branches.
         *
         * @return the count of the branches
         */
        public int branchesCount() {
            return branches.size();
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

    }

    private Operators() {
    }

}
