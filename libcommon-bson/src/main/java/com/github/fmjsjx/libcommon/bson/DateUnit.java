package com.github.fmjsjx.libcommon.bson;

import org.bson.BsonString;

/**
 * The unit for a {@code date}.
 *
 * @author MJ Fang
 * @since 3.1
 */
public enum DateUnit {

    /**
     * year
     */
    YEAR,
    /**
     * quarter
     */
    QUARTER,
    /**
     * week
     */
    WEEK,
    /**
     * month
     */
    MONTH,
    /**
     * day
     */
    DAY,
    /**
     * hour
     */
    HOUR,
    /**
     * minute
     */
    MINUTE,
    /**
     * second
     */
    SECOND,
    /**
     * millisecond
     */
    MILLISECOND,
    ;

    /**
     * Parse {@link DateUnit} from string value.
     *
     * @param value the value
     * @return the {@code DateUnit}
     */
    public static final DateUnit from(String value) {
        return valueOf(value.toUpperCase());
    }

    private final String value;
    private BsonString bsonString;

    private DateUnit() {
        value = name().toLowerCase();
    }

    /**
     * Returns the string value.
     *
     * @return the string value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the {@link BsonString} value.
     *
     * @return the {@code BsonString} value
     */
    public BsonString toBsonString() {
        var bsonString = this.bsonString;
        if (bsonString == null) {
            this.bsonString = bsonString = new BsonString(value);
        }
        return bsonString;
    }

}
