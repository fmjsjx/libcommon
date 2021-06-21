package com.github.fmjsjx.libcommon.bson.model;

import java.util.BitSet;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Updates;

/**
 * The abstract object implementation of BSON Model.
 * 
 * @param <Self> the type of the implementation class
 * 
 * @since 1.3
 */
public abstract class ObjectModel<Self extends ObjectModel<Self>> extends AbstractBsonModel {

    protected static final int FULL = 0;
    protected final BitSet updatedFields = new BitSet();

    @Override
    public int appendUpdates(List<Bson> updates) {
        var base = updates.size();
        if (fullyUpdate()) {
            appendFullyUpdate(updates);
        } else {
            appendFieldUpdates(updates);
        }
        return updates.size() - base;
    }

    @Override
    public boolean updated() {
        return updatedFields.length() > 0;
    }

    @Override
    protected void resetStates() {
        updatedFields.clear();
    }

    /**
     * Append the fully update of this model into the given list.
     * 
     * @param updates the list of updates
     */
    protected void appendFullyUpdate(List<Bson> updates) {
        updates.add(Updates.set(xpath().value(), toBson()));
    }

    /**
     * Append the updates of updated fields on this model into the given list.
     * 
     * @param updates the list of updates
     */
    protected abstract void appendFieldUpdates(List<Bson> updates);

    /**
     * Returns if this model will be fully updated or not.
     * 
     * @return {@code true} if this model will be fully updated, {@code false}
     *         otherwise
     */
    public boolean fullyUpdate() {
        return updatedFields.get(FULL);
    }

    /**
     * Sets if this model will be fully updated or not.
     * 
     * @param fullyUpdate {@code true} if this model will be fully updated,
     *                    {@code false} otherwise
     * @return this model
     */
    @SuppressWarnings("unchecked")
    public Self fullyUpdate(boolean fullyUpdate) {
        updatedFields.set(FULL, fullyUpdate);
        return (Self) this;
    }

    @Override
    public Object toUpdate() {
        if (fullyUpdate()) {
            return this;
        }
        return toSubUpdate();
    }

    /**
     * Creates and returns a new sub update object for this model.
     * 
     * @return a new sub update object for this model
     */
    protected abstract Object toSubUpdate();

}
