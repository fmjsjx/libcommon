package com.github.fmjsjx.libcommon.bson;

import org.bson.BsonString;

/**
 * Meta data keyword.
 *
 * @author MJ Fang
 * @since 3.1
 */
public enum MetaDataKeyword {

    /**
     * {@code "testScore"}
     */
    TEXT_SCORE("textScore"),
    /**
     * {@code "indexKey"}
     */
    INDEX_KEY("indexKey"),
    ;

    private final String value;
    private BsonString bsonString;

    private MetaDataKeyword(String value) {
        this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Returns the {@link BsonString}.
     *
     * @return the {@code BsonString}
     */
    public BsonString toBsonString() {
        var bson = bsonString;
        if (bson == null) {
            bsonString = bson = new BsonString(value);
        }
        return bson;
    }

}
