package com.github.fmjsjx.libcommon.bson.model;

/**
 * The abstract implementation of {@link BsonModel}.
 * 
 * @since 1.3
 */
abstract class AbstractBsonModel implements BsonModel {

    @Override
    public void reset() {
        resetChildren();
        resetStates();
    }

    /**
     * Reset children of this model.
     */
    protected abstract void resetChildren();

    /**
     * Reset states of this model.
     */
    protected abstract void resetStates();

}
