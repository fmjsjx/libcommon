package com.github.fmjsjx.libcommon.bson.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonNull;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import com.github.fmjsjx.libcommon.util.ObjectUtil;
import com.mongodb.client.model.Updates;

/**
 * The simple implementation of map model.
 *
 * @param <E> the type of the elements in this list
 * @param <P> the type of the parent
 * 
 * @since 2.2
 */
public final class SimpleListModel<E, P extends BsonModel> extends ListModel<E, P, SimpleListModel<E, P>> {

    private final SimpleValueType<E> valueType;

    private boolean updated;

    /**
     * Constructs a new {@link SimpleListModel} instance with the specified
     * components.
     * 
     * @param parent    the parent model
     * @param name      the field name of this list in document
     * @param valueType the value type
     */
    public SimpleListModel(P parent, String name, SimpleValueType<E> valueType) {
        super(parent, name);
        this.valueType = valueType;
    }

    @Override
    public BsonValue toBson() {
        var list = this.list;
        if (list == null) {
            return BsonNull.VALUE;
        }
        var bson = new BsonArray();
        var valueType = this.valueType;
        for (var e : list) {
            bson.add(valueType.toBson(e));
        }
        return bson;
    }

    @Override
    public void load(BsonArray src) {
        var list = new ArrayList<E>(src.size());
        var valueType = this.valueType;
        for (var bsonValue : src) {
            list.add(valueType.parse(bsonValue));
        }
        super.list = list;
    }

    @Override
    public void load(List<Object> src) {
        var list = new ArrayList<E>(src.size());
        var valueType = this.valueType;
        for (var value : src) {
            list.add(valueType.cast(value));
        }
        super.list = list;
    }

    @Override
    public int appendUpdates(List<Bson> updates) {
        if (updated) {
            if (nil()) {
                updates.add(Updates.unset(xpath().value()));
            } else {
                updates.add(Updates.set(xpath().value(), toBson()));
            }
            return 1;
        }
        return 0;
    }

    /**
     * Sets the values to the {@code list}.
     * 
     * @param list the list
     * @return this model
     */
    public SimpleListModel<E, P> values(List<E> list) {
        if (ObjectUtil.isNotEquals(super.list, list)) {
            super.list = list;
            updated = true;
        }
        return this;
    }

    public SimpleListModel<E, P> clear() {
        if (super.list != null) {
            super.list = null;
            updated = true;
        }
        return this;
    }

    @Override
    public boolean updated() {
        return updated;
    }

    @Override
    public Object toUpdate() {
        if (updated) {
            return orElse(null);
        }
        return null;
    }

    @Override
    protected void resetChildren() {
        // skip
    }

    @Override
    protected void resetStates() {
        updated = false;
    }

    @Override
    public Map<Object, Object> toDelete() {
        return Map.of();
    }

    @Override
    public int deletedSize() {
        return updated && nil() ? 1 : 0;
    }

}
