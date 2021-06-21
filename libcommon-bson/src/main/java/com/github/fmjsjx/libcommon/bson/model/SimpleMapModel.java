package com.github.fmjsjx.libcommon.bson.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Updates;

/**
 * The simple implementation of map model.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @param <P> the type of the parent
 * 
 * @since 1.3
 */
public final class SimpleMapModel<K, V, P extends BsonModel> extends MapModel<K, V, P, SimpleMapModel<K, V, P>> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMapModel.class);

    /**
     * Constructs a new {@link SimpleMapModel} instance with integer keys and the
     * specified components.
     * 
     * @param <T>            the type of mapped values
     * @param <U>            the type of the parent
     * @param parent         the parent model
     * @param name           the field name of this map in document
     * @param valueConverter the converter convert values to BSON values
     * @return a new {@code SimpleMapModel<Integer, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T, U extends BsonModel> SimpleMapModel<Integer, T, U> integerKeys(U parent, String name,
            Function<T, ? extends BsonValue> valueConverter) {
        return new SimpleMapModel<>(parent, name, Integer::parseInt, valueConverter);
    }

    /**
     * Constructs a new {@link SimpleMapModel} instance with long keys and the
     * specified components.
     * 
     * @param <T>            the type of mapped values
     * @param <U>            the type of the parent
     * @param parent         the parent model
     * @param name           the field name of this map in document
     * @param valueConverter the converter convert values to BSON values
     * @return a new {@code SimpleMapModel<Long, T>} instance with integer keys and
     *         the specified components
     */
    public static final <T, U extends BsonModel> SimpleMapModel<Long, T, U> longKeys(U parent, String name,
            Function<T, ? extends BsonValue> valueConverter) {
        return new SimpleMapModel<>(parent, name, Long::parseLong, valueConverter);
    }

    /**
     * Constructs a new {@link SimpleMapModel} instance with string keys and the
     * specified components.
     * 
     * @param <T>            the type of mapped values
     * @param <U>            the type of the parent
     * @param parent         the parent model
     * @param name           the field name of this map in document
     * @param valueConverter the converter convert values to BSON values
     * @return a new {@code SimpleMapModel<String, T>} instance with integer keys
     *         and the specified components
     */
    public static final <T, U extends BsonModel> SimpleMapModel<String, T, U> stringKeys(U parent, String name,
            Function<T, ? extends BsonValue> valueConverter) {
        return new SimpleMapModel<>(parent, name, Function.identity(), valueConverter);
    }

    private final Function<V, ? extends BsonValue> valueConverter;

    /**
     * Constructs a new {@link SimpleMapModel} instance with the specified
     * components.
     * 
     * @param parent         the parent model
     * @param name           the field name of this map in document
     * @param keyParser      the parser to parse keys
     * @param valueConverter the converter convert values to BSON values
     */
    public SimpleMapModel(P parent, String name, Function<String, K> keyParser,
            Function<V, ? extends BsonValue> valueConverter) {
        super(parent, name, keyParser);
        this.valueConverter = valueConverter;
    }

    @Override
    public BsonDocument toBson() {
        var bson = new BsonDocument();
        var valueConverter = this.valueConverter;
        map.forEach((k, v) -> bson.append(k.toString(), valueConverter.apply(v)));
        return bson;
    }

    @Override
    public Document toDocument() {
        var doc = new Document();
        map.forEach((k, v) -> doc.append(k.toString(), v));
        return doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(Document src) {
        src.forEach((k, v) -> {
            try {
                map.put(parseKey(k), (V) v);
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Loading data failed on {}: {}", xpath().resolve(k), v);
                }
                // skip unsupported type values
            }
        });
    }

    @Override
    protected void appendUpdates(List<Bson> updates, K key, V value) {
        updates.add(Updates.set(xpath().resolve(key.toString()).value(), value));
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
        }
        removedKeys.remove(key);
        updatedKeys.add(key);
        return Optional.ofNullable(old);
    }

    @Override
    public Optional<V> remove(K key) {
        var value = map.remove(key);
        if (value != null) {
            updatedKeys.remove(key);
            removedKeys.add(key);
            return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public boolean remove(K key, V value) {
        if (map.remove(key, value)) {
            updatedKeys.remove(key);
            removedKeys.add(key);
            return true;
        }
        return false;
    }

    @Override
    protected void resetChildren() {
        // skip
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
            update.put(key, value);
        }
        return update;
    }

    @Override
    public Object toDelete() {
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
