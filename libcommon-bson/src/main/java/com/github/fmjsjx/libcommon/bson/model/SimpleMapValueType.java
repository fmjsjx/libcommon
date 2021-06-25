package com.github.fmjsjx.libcommon.bson.model;

import org.bson.BsonValue;

/**
 * The interface defines methods for types of values in {@link SimpleMapModel}.
 * 
 * @param <V> the type of value
 * 
 * @see SimpleMapModel
 * @see SimpleMapValueTypes
 * 
 * @since 2.1
 */
public interface SimpleMapValueType<V> {

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
     * Converts the value from java type to {@link BsonValue}.
     * 
     * @param value the value in java type
     * @return a {@code BsonValue}
     */
    BsonValue toBson(V value);

}
