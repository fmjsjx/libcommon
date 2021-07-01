package com.github.fmjsjx.libcommon.bson.model;

import com.github.fmjsjx.libcommon.bson.DotNotation;

/**
 * The abstract implementation of container model.
 *
 * @param <Parent> the type of the parent
 * 
 * @since 2.2
 */
public abstract class AbstractContainerModel<Parent extends BsonModel> extends AbstractBsonModel {

    protected final Parent parent;
    protected final String name;
    protected final DotNotation xpath;

    /**
     * Constructs a new {@link AbstractContainerModel} with the specified parent and
     * name.
     * 
     * @param parent the parent model
     * @param name   the field name of this container in document
     */
    protected AbstractContainerModel(Parent parent, String name) {
        this.parent = parent;
        this.name = name;
        this.xpath = parent.xpath().resolve(name);
    }

    /**
     * Returns the field name of this map in document.
     * 
     * @return the field name of this map in document
     */
    public String name() {
        return name;
    }

    @Override
    public Parent parent() {
        return parent;
    }

    @Override
    public DotNotation xpath() {
        return xpath;
    }

    /**
     * Returns the number of elements in this container.
     * 
     * @return the number of elements in this container
     */
    public abstract int size();

    /**
     * Return if this container is empty or not.
     * 
     * @return {@code true} if this container is empty, {@code false} otherwise
     */
    public boolean empty() {
        return size() == 0;
    }

}
