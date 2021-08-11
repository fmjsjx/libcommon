package com.github.fmjsjx.libcommon.bson.model;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.jsoniter.any.Any;

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
     * Creates and returns a new data object for this model.
     * <p>
     * Type of the returned object may be Map or List.
     * 
     * @return a new data object for this model
     * @since 2.4
     */
    Object toData();

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
     * Load data from the source data {@link Any}.
     * 
     * @param src the source data {@code Any}
     * @since 2.4
     */
    void load(Any src);

    /**
     * Load data from the source data {@link JsonNode}.
     * 
     * @param src the source data {@code JsonNode}
     * @since 2.4
     */
    void load(JsonNode src);

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

    /**
     * Returns {@code true} if any value of this model has been deleted in context,
     * {@code false} otherwise.
     * 
     * @return {@code true} if any value of this model has been deleted in context,
     *         {@code false} otherwise
     * @since 2.4
     */
    boolean deleted();

}
