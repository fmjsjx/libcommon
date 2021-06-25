package com.github.fmjsjx.libcommon.bson.model;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.bson.DotNotation;

/**
 * The top interface for BSON model.
 * 
 * @since 2.0
 */
public interface BsonModel {

    /**
     * Convert this model to {@link BsonValue} type.
     * 
     * @return a {@code BsonValue}
     */
    BsonValue toBson();

    /**
     * Convert this model to {@link Document} type.
     * 
     * @return a {@code Document}
     */
    Document toDocument();

    /**
     * Load data from the source {@link BsonDocument}
     * 
     * @param src the source {@code BsonDocument}
     * @since 2.1
     */
    void load(BsonDocument src);

    /**
     * Load data from the source {@link Document}.
     * 
     * @param src the source {@code Document}
     */
    void load(Document src);

    /**
     * Appends the updates of this model into the given list.
     * 
     * @param updates the list of updates
     * @return the number of the updates added
     */
    int appendUpdates(List<Bson> updates);

    /**
     * Reset states of this model.
     */
    void reset();

    /**
     * Returns {@code true} if this model has been updated in context, {@code false}
     * otherwise.
     * 
     * @return {@code true} if this model has been updated in context, {@code false}
     *         otherwise
     */
    boolean updated();

    /**
     * Returns the parent model.
     * 
     * @return the parent model
     */
    BsonModel parent();

    /**
     * Returns the {@code dot notation} of this model.
     * 
     * @return the {@code dot notation} of this model
     */
    DotNotation xpath();

    /**
     * Creates and returns a new update object for this model.
     * 
     * @return a new update object for this model
     */
    Object toUpdate();

    /**
     * Creates and returns a new delete object for this model.
     * 
     * @return a new delete object for this model
     */
    Object toDelete();

}
