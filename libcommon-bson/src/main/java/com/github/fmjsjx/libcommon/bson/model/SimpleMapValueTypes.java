package com.github.fmjsjx.libcommon.bson.model;

import java.time.LocalDateTime;

import org.bson.BsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.jsoniter.any.Any;

/**
 * Constants of {@link SimpleMapValueType}.
 * 
 * @see SimpleMapValueType
 * 
 * @since 2.1
 * @deprecated please use {@link SimpleValueTypes} instead
 */
@Deprecated(since = "2.2")
public final class SimpleMapValueTypes {

    /**
     * Type Integer.
     */
    public static final SimpleMapValueType<Integer> INTEGER = new SimpleMapValueNumberType<>(SimpleValueTypes.INTEGER);

    /**
     * Type Long.
     */
    public static final SimpleMapValueType<Long> LONG = new SimpleMapValueNumberType<>(SimpleValueTypes.LONG);

    /**
     * Type Double.
     */
    public static final SimpleMapValueType<Double> DOUBLE = new SimpleMapValueNumberType<>(SimpleValueTypes.DOUBLE);

    /**
     * Type String.
     */
    public static final SimpleMapValueType<String> STRING = new SimpleMapValueSimpleType<>(SimpleValueTypes.STRING);

    /**
     * Type Boolean.
     */
    public static final SimpleMapValueType<Boolean> BOOLEAN = new SimpleMapValueSimpleType<>(SimpleValueTypes.BOOLEAN);
    /**
     * Type LocalDateTime.
     */
    public static final SimpleMapValueType<LocalDateTime> LOCAL_DATE_TIME = new SimpleMapValueSimpleType<>(
            SimpleValueTypes.DATETIME);

    private static final class SimpleMapValueNumberType<V extends Number> implements SimpleMapValueType<V> {

        private final SimpleValueType<V> wrapper;

        private SimpleMapValueNumberType(SimpleValueType<V> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public Class<V> type() {
            return wrapper.type();
        }

        @Override
        public V parse(BsonValue value) {
            return wrapper.parse(value);
        }

        @Override
        public V parse(Any value) {
            return wrapper.parse(value);
        }

        @Override
        public V parse(JsonNode value) {
            return wrapper.parse(value);
        }

        @Override
        public BsonValue toBson(V value) {
            return wrapper.toBson(value);
        }

    }

    private static final class SimpleMapValueSimpleType<V> implements SimpleMapValueType<V> {

        private final SimpleValueType<V> wrapper;

        private SimpleMapValueSimpleType(SimpleValueType<V> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public Class<V> type() {
            return wrapper.type();
        }

        @Override
        public V parse(BsonValue value) {
            return wrapper.parse(value);
        }

        @Override
        public V parse(Any value) {
            return wrapper.parse(value);
        }

        @Override
        public V parse(JsonNode value) {
            return wrapper.parse(value);
        }

        @Override
        public BsonValue toBson(V value) {
            return wrapper.toBson(value);
        }

    }

    private SimpleMapValueTypes() {
    }

}
