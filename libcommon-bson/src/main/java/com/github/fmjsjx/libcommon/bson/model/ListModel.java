package com.github.fmjsjx.libcommon.bson.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The abstract list implementation of BSON model.
 *
 * @param <E>      the type of elements in this list
 * @param <Parent> the type of the parent
 * @param <Self>   the type of the implementation class
 * 
 * @since 2.2
 */
@JsonSerialize(using = ListModelSerializer.class)
public abstract class ListModel<E, Parent extends BsonModel, Self extends ListModel<E, Parent, ?>>
        extends AbstractContainerModel<Parent> {

    protected List<E> list;

    /**
     * Constructs a new {@link ListModel} with the specified list.
     * 
     * @param parent the parent model
     * @param name   the field name of this map in document
     * @param list   a {@link List}
     */
    protected ListModel(Parent parent, String name, List<E> list) {
        super(parent, name);
        this.list = list;
    }

    /**
     * Constructs a new {@link ListModel}.
     * 
     * @param parent the parent model
     * @param name   the field name of this map in document
     */
    protected ListModel(Parent parent, String name) {
        super(parent, name);
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("unsupport toDocument() on list model");
    }

    @Override
    public void load(Document src) {
        throw new UnsupportedOperationException("unsupport load(Document) on list model");
    }

    @Override
    public void load(BsonDocument src) {
        throw new UnsupportedOperationException("unsupport load(BsonDocument) on list model");
    }

    /**
     * Load data from the source {@link BsonArray}.
     * 
     * @param src the source {@code BsonArray}
     */
    public abstract void load(BsonArray src);

    /**
     * Load data from the source list.
     * 
     * @param src the source list
     */
    public abstract void load(List<Object> src);

    /**
     * Returns the values of this model.
     * 
     * @return an {@code Optional<List<E>>}
     */
    public Optional<List<E>> values() {
        var list = this.list;
        if (list == null) {
            return Optional.empty();
        }
        return Optional.of(Collections.unmodifiableList(list));
    }

    /**
     * If the values are present, returns the values, otherwise returns the result
     * produced by the supplying function.
     * 
     * @param supplier the supplying function that produces the values to be
     *                 returned
     * @return the values, if present, otherwise the result produced by the
     *         supplying function
     */
    public List<E> orElseGet(Supplier<? extends List<E>> supplier) {
        var list = this.list;
        if (list == null) {
            list = supplier.get();
        }
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * If the values is present, returns the values, otherwise returns
     * {@code others}.
     * 
     * @param others the values to be returned, if no value is present. May be
     *               {@code null}.
     * @return the values, if present, otherwise {@code others}
     */
    public List<E> orElse(List<E> others) {
        var list = this.list;
        if (list == null) {
            list = others;
        }
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public int size() {
        var list = this.list;
        if (list == null) {
            return -1;
        }
        return list.size();
    }

    @Override
    public boolean empty() {
        var list = this.list;
        if (list == null) {
            return false;
        }
        return list.isEmpty();
    }

    /**
     * Returns if the values is {@code null} or not.
     * 
     * @return {@code true} if the values is {@code null}, {@code false} otherwise
     */
    public boolean nil() {
        return list == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + list + ")";
    }
}
