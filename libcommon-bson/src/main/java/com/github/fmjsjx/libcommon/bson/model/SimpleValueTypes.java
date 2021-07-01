package com.github.fmjsjx.libcommon.bson.model;

import org.bson.BsonBoolean;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonValue;

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

    static final class SimpleValueNumberType<V extends Number> implements SimpleValueType<V> {

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
