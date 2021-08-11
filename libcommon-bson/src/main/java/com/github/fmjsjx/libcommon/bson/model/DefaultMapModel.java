package com.github.fmjsjx.libcommon.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;

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
     * @param <T>          the type of mapped values
     * @param <U>          the type of the parent model
     * @param parent       the parent model
     * @param name         the field name of this map in document
     * @param valueFactory the factory creates value instances
     * @return a new {@code DefaultMapModel<Integer, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T extends DefaultMapValueModel<Integer, T>, U extends BsonModel> DefaultMapModel<Integer, T, U> integerKeys(
            U parent, String name, Supplier<T> valueFactory) {
        return new DefaultMapModel<>(parent, name, Integer::parseInt, valueFactory);
    }

    /**
     * Constructs a new {@link DefaultMapModel} instance with long keys and the
     * specified components.
     * 
     * @param <T>          the type of mapped values
     * @param <U>          the type of the parent model
     * @param parent       the parent model
     * @param name         the field name of this map in document
     * @param valueFactory the factory creates value instances
     * @return a new {@code DefaultMapModel<Long, T>} instance with integer keys and
     *         the specified components
     */
    public static final <T extends DefaultMapValueModel<Long, T>, U extends BsonModel> DefaultMapModel<Long, T, U> longKeys(
            U parent, String name, Supplier<T> valueFactory) {
        return new DefaultMapModel<>(parent, name, Long::parseLong, valueFactory);
    }

    /**
     * Constructs a new {@link DefaultMapModel} instance with string keys and the
     * specified components.
     * 
     * @param <T>          the type of mapped values
     * @param <U>          the type of the parent model
     * @param parent       the parent model
     * @param name         the field name of this map in document
     * @param valueFactory the factory creates value instances
     * @return a new {@code DefaultMapModel<String, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T extends DefaultMapValueModel<String, T>, U extends BsonModel> DefaultMapModel<String, T, U> stringKeys(
            U parent, String name, Supplier<T> valueFactory) {
        return new DefaultMapModel<>(parent, name, Function.identity(), valueFactory);
    }

    private final Supplier<V> valueFactory;

    /**
     * Constructs a new {@link DefaultMapModel} instance with the specified
     * components.
     * 
     * @param parent       the parent model
     * @param name         the field name of this map in document
     * @param keyParser    the parser to parse keys
     * @param valueFactory the factory creates value instances
     */
    public DefaultMapModel(P parent, String name, Function<String, K> keyParser, Supplier<V> valueFactory) {
        super(parent, name, keyParser);
        this.valueFactory = valueFactory;
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
    public void load(BsonDocument src) {
        clear0();
        src.forEach((k, v) -> {
            if (v.isDocument()) {
                var key = parseKey(k);
                var value = valueFactory.get().parent(this).key(key);
                value.load((BsonDocument) v);
                map.put(key, value);
            }
        });
    }

    @Override
    public void load(Document src) {
        clear0();
        src.forEach((k, v) -> {
            if (v instanceof Document) {
                var key = parseKey(k);
                var value = valueFactory.get().parent(this).key(key);
                value.load((Document) v);
                map.put(key, value);
            }
            // skip other type values
        });
    }

    @Override
    public void load(Any src) {
        clear0();
        if (src.valueType() == ValueType.OBJECT) {
            src.asMap().forEach((k, v) -> {
                if (v.valueType() == ValueType.OBJECT) {
                    var key = parseKey(k);
                    var value = valueFactory.get().parent(this).key(key);
                    value.load(v);
                    map.put(key, value);
                }
                // skip other type values
            });
        }
    }

    @Override
    public void load(JsonNode src) {
        clear0();
        if (src.isObject()) {
            for (var iter = src.fields(); iter.hasNext();) {
                var entry = iter.next();
                var k = entry.getKey();
                var v = entry.getValue();
                if (v.isObject()) {
                    var key = parseKey(k);
                    var value = valueFactory.get().parent(this).key(key);
                    value.load(v);
                    map.put(key, value);
                }
                // skip other type values
            }
        }
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
    public DefaultMapModel<K, V, P> clear() {
        updatedKeys.clear();
        removedKeys.addAll(map.keySet());
        clear0();
        return this;
    }

    private void clear0() {
        map.values().forEach(DefaultMapValueModel::unbind);
        map.clear();
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

    @Override
    public Map<K, ?> toData() {
        var map = super.map;
        if (map.isEmpty()) {
            return new LinkedHashMap<>();
        }
        var data = new LinkedHashMap<K, Object>(Math.max(8, map.size() << 1));
        map.forEach((k, v) -> data.put(k, v.toData()));
        return data;
    }

}
