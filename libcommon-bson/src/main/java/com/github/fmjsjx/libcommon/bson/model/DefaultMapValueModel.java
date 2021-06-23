package com.github.fmjsjx.libcommon.bson.model;

import org.bson.BsonDocument;

import com.github.fmjsjx.libcommon.bson.DotNotation;

/***
 * The abstract class for values of {@link DefaultMapModel}.
 *
 * @param <K>    the type of key
 * @param <Self> the type of implementation
 * 
 * @since 2.0
 * 
 * @see DefaultMapModel
 */
public abstract class DefaultMapValueModel<K, Self extends DefaultMapValueModel<K, Self>> extends ObjectModel<Self> {

    protected K key;
    protected DefaultMapModel<K, Self, ?> parent;

    public K key() {
        return key;
    }

    @Override
    public abstract BsonDocument toBson();

    @SuppressWarnings("unchecked")
    protected Self key(K key) {
        this.key = key;
        return (Self) this;
    }

    @Override
    public DefaultMapModel<K, Self, ?> parent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    protected Self parent(DefaultMapModel<K, Self, ?> parent) {
        this.parent = parent;
        return (Self) this;
    }

    protected Self unbind() {
        return key(null).parent(null);
    }

    public boolean bound() {
        return key != null && parent != null;
    }

    @SuppressWarnings("unchecked")
    protected Self emitUpdated() {
        var key = this.key;
        var parent = this.parent;
        if (key != null && parent != null) {
            parent.updatedKeys.add(key);
        }
        return (Self) this;
    }

    @Override
    public DotNotation xpath() {
        if (!bound()) {
            throw new IllegalStateException("this DefaultMapValueModel is not binding");
        }
        return parent().xpath().resolve(String.valueOf(key()));
    }

}
