package com.github.fmjsjx.libcommon.bson.model;

import org.bson.BsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.jsoniter.any.Any;

/**
 * The interface defines methods for types of values in {@link SimpleMapModel}.
 * 
 * @param <V> the type of value
 * 
 * @see SimpleValueTypes
 * 
 * @since 2.2
 */
public interface SimpleValueType<V> {

    /**
     * Returns the class of the value type.
     * 
     * @return the class of the value type
     */
    Class<V> type();

    /**
     * Parse the value from {@link BsonValue} to java type.
     * 
     * @param value the value as {@code BsonType}
     * @return the value in java type
     */
    V parse(BsonValue value);

    /**
     * Parse the value from {@link Any} to java type.
     * 
     * @param value the value as {@code Any}
     * @return the value in java type
     * @since 2.4
     */
    V parse(Any value);

    /**
     * Parse the value from {@link JsonNode} to java type.
     * 
     * @param value the value as {@code JsonNode}
     * @return the value in java type
     * @since 2.4
     */
    V parse(JsonNode value);

    /**
     * Converts the value from java type to {@link BsonValue}.
     * 
     * @param value the value in java type
     * @return a {@code BsonValue}
     */
    BsonValue toBson(V value);

    /**
     * Casts an object to this value type.
     * 
     * @param obj the object to be cast
     * @return the object after casting, or null if obj is null
     */
    @SuppressWarnings("unchecked")
    default V cast(Object obj) {
        return (V) obj;
    }

    /**
     * Converts value from model type to storage type.
     * 
     * @param value the value
     * @return the converted value
     * 
     * @since 2.3
     */
    default Object toStorage(V value) {
        return value;
    }

    /**
     * Converts value from model type to data type.
     * 
     * @param value the value
     * @return the converted value
     * @since 2.4
     */
    default Object toData(V value) {
        return value;
    }

}
