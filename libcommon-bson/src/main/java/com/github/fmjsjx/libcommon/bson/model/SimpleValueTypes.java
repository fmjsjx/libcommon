package com.github.fmjsjx.libcommon.bson.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.bson.BsonBoolean;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.github.fmjsjx.libcommon.bson.BsonUtil;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import com.mongodb.Function;

/**
 * Constants of {@link SimpleValueType}.
 * 
 * @see SimpleValueType
 * 
 * @since 2.2
 */
public final class SimpleValueTypes {

    /**
     * Type Integer.
     */
    public static final SimpleValueType<Integer> INTEGER = new SimpleValueNumberType<>(Integer.class,
            BsonNumber::intValue, BsonInt32::new);

    /**
     * Type Long.
     */
    public static final SimpleValueType<Long> LONG = new SimpleValueNumberType<>(Long.class, BsonNumber::longValue,
            BsonInt64::new);

    /**
     * Type Double.
     */
    public static final SimpleValueType<Double> DOUBLE = new SimpleValueNumberType<>(Double.class,
            BsonNumber::doubleValue, BsonDouble::new);

    /**
     * Type String.
     */
    public static final SimpleValueType<String> STRING = new SimpleValueSimpleType<>(String.class,
            v -> v.asString().getValue(), BsonString::new);

    /**
     * Type Boolean.
     */
    public static final SimpleValueType<Boolean> BOOLEAN = new SimpleValueSimpleType<>(Boolean.class,
            v -> v.asBoolean().getValue(), BsonBoolean::valueOf);

    /**
     * Type LocalDateTime.
     * 
     * @since 2.3
     */
    public static final SimpleValueType<LocalDateTime> DATETIME = new SimpleValueType<>() {

        @Override
        public Class<LocalDateTime> type() {
            return LocalDateTime.class;
        }

        @Override
        public BsonValue toBson(LocalDateTime value) {
            if (value == null) {
                return BsonNull.VALUE;
            }
            return BsonUtil.toBsonDateTime(value);
        }

        @Override
        public LocalDateTime parse(BsonValue value) {
            if (value == null || value.isNull()) {
                return null;
            }
            return BsonUtil.toLocalDateTime(value);
        }

        @Override
        public LocalDateTime cast(Object obj) {
            if (obj instanceof Date) {
                return DateTimeUtil.local((Date) obj);
            }
            return SimpleValueType.super.cast(obj);
        }

        @Override
        public Object toStorage(LocalDateTime value) {
            if (value == null) {
                return null;
            }
            return DateTimeUtil.toLegacyDate(value);
        }

    };

    /**
     * Type LocalDate.
     * 
     * @since 2.3
     */
    public static final SimpleValueType<LocalDate> DATE = new SimpleValueType<>() {

        @Override
        public Class<LocalDate> type() {
            return LocalDate.class;
        }

        @Override
        public LocalDate parse(BsonValue value) {
            if (value == null || value.isNull()) {
                return null;
            }
            if (value instanceof BsonNumber) {
                return DateTimeUtil.toDate(((BsonNumber) value).intValue());
            }
            throw new ClassCastException(
                    String.format("The value is not a BsonNumber (%s)", value.getClass().getName()));
        }

        @Override
        public BsonValue toBson(LocalDate value) {
            if (value == null) {
                return BsonNull.VALUE;
            }
            return new BsonInt32(DateTimeUtil.toNumber(value));
        }

        @Override
        public LocalDate cast(Object obj) {
            if (obj instanceof Number) {
                return DateTimeUtil.toDate(((Number) obj).intValue());
            }
            return SimpleValueType.super.cast(obj);
        }

        @Override
        public Object toStorage(LocalDate value) {
            if (value == null) {
                return null;
            }
            return DateTimeUtil.toNumber(value);
        }

    };

    static final class SimpleValueNumberType<V> implements SimpleValueType<V> {

        private final Class<V> type;
        private final Function<BsonNumber, V> parser;
        private final Function<V, BsonValue> converter;

        private SimpleValueNumberType(Class<V> type, Function<BsonNumber, V> parser, Function<V, BsonValue> converter) {
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

    static final class SimpleValueSimpleType<V> implements SimpleValueType<V> {

        private final Class<V> type;
        private final Function<BsonValue, V> parser;
        private final Function<V, BsonValue> converter;

        private SimpleValueSimpleType(Class<V> type, Function<BsonValue, V> parser, Function<V, BsonValue> converter) {
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

    private SimpleValueTypes() {
    }

}
