package com.github.fmjsjx.libcommon.bson.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonValue;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import com.mongodb.Function;

/**
 * Constants of {@link SimpleMapValueType}.
 * 
 * @see SimpleMapValueType
 * 
 * @since 2.1
 */
public final class SimpleMapValueTypes {

    /**
     * Type Integer.
     */
    public static final SimpleMapValueType<Integer> INTEGER = new SimpleMapValueNumberType<>(Integer.class,
            BsonNumber::intValue, BsonInt32::new);

    /**
     * Type Long.
     */
    public static final SimpleMapValueType<Long> LONG = new SimpleMapValueNumberType<>(Long.class,
            BsonNumber::longValue, BsonInt64::new);

    /**
     * Type Double.
     */
    public static final SimpleMapValueType<Double> DOUBLE = new SimpleMapValueNumberType<>(Double.class,
            BsonNumber::doubleValue, BsonDouble::new);

    /**
     * Type String.
     */
    public static final SimpleMapValueType<String> STRING = new SimpleMapValueSimpleType<>(String.class,
            v -> v.asString().getValue(), BsonString::new);

    /**
     * Type Boolean.
     */
    public static final SimpleMapValueType<Boolean> BOOLEAN = new SimpleMapValueSimpleType<>(Boolean.class,
            v -> v.asBoolean().getValue(), BsonBoolean::valueOf);
    /**
     * Type LocalDateTime.
     */
    public static final SimpleMapValueType<LocalDateTime> LOCAL_DATE_TIME = new SimpleMapValueSimpleType<>(
            LocalDateTime.class, v -> {
                if (v.isDateTime()) {
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli(((BsonDateTime) v).getValue()),
                            ZoneId.systemDefault());
                } else if (v.isTimestamp()) {
                    return DateTimeUtil.local(((BsonTimestamp) v).getTime());
                }
                throw new ClassCastException(
                        String.format("The value is not a BsonDateTime or BsonTimestamp (%s)", v.getClass().getName()));
            }, BsonUtil::toBsonDateTime);

    private static final class SimpleMapValueNumberType<V extends Number> implements SimpleMapValueType<V> {

        private final Class<V> type;
        private final Function<BsonNumber, V> parser;
        private final Function<V, BsonValue> converter;

        private SimpleMapValueNumberType(Class<V> type, Function<BsonNumber, V> parser,
                Function<V, BsonValue> converter) {
            this.type = type;
            this.parser = parser;
            this.converter = converter;
        }

        @Override
        public Class<V> type() {
            return type;
        }

        @Override
        public V parse(BsonValue value) {
            if (value == null || value.isNull()) {
                return null;
            }
            if (value instanceof BsonNumber) {
                return parser.apply((BsonNumber) value);
            }
            throw new ClassCastException(
                    String.format("The value is not a BsonNumber (%s)", value.getClass().getName()));
        }

        @Override
        public BsonValue toBson(V value) {
            if (value == null) {
                return BsonNull.VALUE;
            }
            return converter.apply(value);
        }

    }

    private static final class SimpleMapValueSimpleType<V> implements SimpleMapValueType<V> {

        private final Class<V> type;
        private final Function<BsonValue, V> parser;
        private final Function<V, BsonValue> converter;

        private SimpleMapValueSimpleType(Class<V> type, Function<BsonValue, V> parser,
                Function<V, BsonValue> converter) {
            this.type = type;
            this.parser = parser;
            this.converter = converter;
        }

        @Override
        public Class<V> type() {
            return type;
        }

        @Override
        public V parse(BsonValue value) {
            if (value == null || value.isNull()) {
                return null;
            }
            return parser.apply(value);
        }

        @Override
        public BsonValue toBson(V value) {
            if (value == null) {
                return BsonNull.VALUE;
            }
            return converter.apply(value);
        }

    }

    private SimpleMapValueTypes() {
    }

}
