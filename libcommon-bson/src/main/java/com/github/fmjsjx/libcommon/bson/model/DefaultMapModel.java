package com.github.fmjsjx.libcommon.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * The default implementation of map model.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @param <P> the type of the parent
 * 
 * @since 2.0
 */
public final class DefaultMapModel<K, V extends DefaultMapValueModel<K, V>, P extends BsonModel>
        extends MapModel<K, V, P, DefaultMapModel<K, V, P>> {

    /**
     * Constructs a new {@link DefaultMapModel} instance with integer keys and the
     * specified components.
     * 
     * @param <T>         the type of mapped values
     * @param parent      the parent model
     * @param name        the field name of this map in document
     * @param valueLoader the loader to load values
     * @return a new {@code DefaultMapModel<Integer, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T extends DefaultMapValueModel<Integer, T>, U extends BsonModel> DefaultMapModel<Integer, T, U> integerKeys(
            U parent, String name, Function<Document, T> valueLoader) {
        return new DefaultMapModel<>(parent, name, Integer::parseInt, valueLoader);
    }

    /**
     * Constructs a new {@link DefaultMapModel} instance with long keys and the
     * specified components.
     * 
     * @param <T>         the type of mapped values
     * @param parent      the parent model
     * @param name        the field name of this map in document
     * @param valueLoader the loader to load values
     * @return a new {@code DefaultMapModel<Long, T>} instance with integer keys and
     *         the specified components
     */
    public static final <T extends DefaultMapValueModel<Long, T>, U extends BsonModel> DefaultMapModel<Long, T, U> longKeys(
            U parent, String name, Function<Document, T> valueLoader) {
        return new DefaultMapModel<>(parent, name, Long::parseLong, valueLoader);
    }

    /**
     * Constructs a new {@link DefaultMapModel} instance with string keys and the
     * specified components.
     * 
     * @param <T>         the type of mapped values
     * @param parent      the parent model
     * @param name        the field name of this map in document
     * @param valueLoader the loader to load values
     * @return a new {@code DefaultMapModel<String, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T extends DefaultMapValueModel<String, T>, U extends BsonModel> DefaultMapModel<String, T, U> stringKeys(
            U parent, String name, Function<Document, T> valueLoader) {
        return new DefaultMapModel<>(parent, name, Function.identity(), valueLoader);
    }

    private final Function<Document, V> valueLoader;

    /**
     * Constructs a new {@link DefaultMapModel} instance with the specified
     * components.
     * 
     * @param parent      the parent model
     * @param name        the field name of this map in document
     * @param keyParser   the parser to parse keys
     * @param valueLoader the loader to load values
     */
    public DefaultMapModel(P parent, String name, Function<String, K> keyParser, Function<Document, V> valueLoader) {
        super(parent, name, keyParser);
        this.valueLoader = valueLoader;
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        map.forEach((k, v) -> bson.append(k.toString(), v.toBson()));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        map.forEach((k, v) -> doc.append(k.toString(), v.toDocument()));
        return doc;
    }

    @Override
    public void load(Document src) {
        src.forEach((k, v) -> {
            if (v instanceof Document) {
                var key = parseKey(k);
                var value = valueLoader.apply((Document) v).key(key).parent(this);
                map.put(key, value);
            }
            // skip other type values
        });
    }

    @Override
    protected void appendUpdates(List<Bson> updates, K key, V value) {
        value.appendUpdates(updates);
    }

    @Override
    protected void resetChildren() {
        for (var key : updatedKeys) {
            var value = map.get(key);
            value.reset();
        }
    }

    @Override
    public Optional<V> put(K key, V value) {
        if (value == null) {
            return remove(key);
        }
        var old = map.put(key, value);
        if (old != null) {
            if (old == value) {
                return Optional.of(value);
            }
            old.unbind();
        }
        value.key(key).parent(this);
        removedKeys.remove(key);
        updatedKeys.add(key);
        return Optional.ofNullable(old);
    }

    @Override
    public Optional<V> remove(K key) {
        var value = map.remove(key);
        if (value != null) {
            value.unbind();
            updatedKeys.remove(key);
            removedKeys.add(key);
            return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(K key, V value) {
        if (map.remove(key, value)) {
            value.unbind();
            updatedKeys.remove(key);
            removedKeys.add(key);
            return true;
        }
        return false;
    }

    @Override
    public Object toUpdate() {
        var updatedKeys = this.updatedKeys;
        if (updatedKeys.isEmpty()) {
            return Map.of();
        }
        var update = new LinkedHashMap<Object, Object>();
        for (var key : updatedKeys) {
            var value = map.get(key);
            update.put(key, value.toUpdate());
        }
        return update;
    }

    @Override
    public Map<Object, Object> toDelete() {
        var removedKeys = this.removedKeys;
        if (removedKeys.isEmpty()) {
            return Map.of();
        }
        var delete = new LinkedHashMap<Object, Object>();
        for (var key : removedKeys) {
            delete.put(key, 1);
        }
        return delete;
    }

}
