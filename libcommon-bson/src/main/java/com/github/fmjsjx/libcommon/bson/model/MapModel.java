package com.github.fmjsjx.libcommon.bson.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmjsjx.libcommon.bson.DotNotation;
import com.mongodb.client.model.Updates;

/**
 * The abstract map implementation of BSON model.
 * 
 * @param <K>      the type of keys maintained by this map
 * @param <V>      the type of mapped values
 * @param <Parent> the type of the parent
 * @param <Self>   the type of the implementation class
 * @since 2.0
 */
@JsonSerialize(using = MapModelSerializer.class)
public abstract class MapModel<K, V, Parent extends BsonModel, Self extends MapModel<K, V, Parent, ?>>
        extends AbstractBsonModel {

    protected final Map<K, V> map;

    protected final Set<K> updatedKeys = new LinkedHashSet<>();
    protected final Set<K> removedKeys = new LinkedHashSet<>();

    protected final Parent parent;
    protected final String name;
    protected final Function<String, K> keyParser;
    protected final DotNotation xpath;

    /**
     * Constructs a new {@link MapModel} using {@link LinkedHashMap}.
     * 
     * @param parent    the parent model
     * @param name      the field name of this map in document
     * @param keyParser the parser parses keys
     */
    protected MapModel(Parent parent, String name, Function<String, K> keyParser) {
        this(parent, name, keyParser, LinkedHashMap::new);
    }

    /**
     * Constructs a new {@link MapModel} by the specified factory.
     * 
     * @param parent     the parent model
     * @param name       the field name of this map in document
     * @param keyParser  the parser parses keys
     * @param mapFactory the factory to create {@link Map}s
     */
    protected MapModel(Parent parent, String name, Function<String, K> keyParser, Supplier<Map<K, V>> mapFactory) {
        this(parent, name, keyParser, mapFactory.get());
    }

    /**
     * Constructs a new {@link MapModel} with the specified map given.
     * 
     * @param parent    the parent model
     * @param name      the field name of this map in document
     * @param keyParser the parser parses keys
     * @param map       a {@link Map}
     */
    protected MapModel(Parent parent, String name, Function<String, K> keyParser, Map<K, V> map) {
        this.parent = parent;
        this.name = name;
        this.keyParser = keyParser;
        this.map = map;
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
    @SuppressWarnings("unchecked")
    public Parent parent() {
        return parent;
    }

    /**
     * Parse the specified key with the {@code keyParser}.
     * 
     * @param key the key to be parsed
     * @return the parsed key
     */
    protected K parseKey(String key) {
        return keyParser.apply(key);
    }

    @Override
    public boolean updated() {
        return updatedKeys.size() > 0 || deletedSize() > 0;
    }

    /**
     * Returns the size of removed entries.
     * 
     * @return the size of removed entries
     */
    @Override
    public int deletedSize() {
        return removedKeys.size();
    }

    /**
     * Returns the value to which the specified key is mapped.
     * 
     * @param key the key
     * @return an {@code Optional<V>}
     */
    public Optional<V> get(K key) {
        return Optional.ofNullable(map.get(key));
    }

    /**
     * Associates the specified value with the specified key in this map.
     * 
     * @param key   the key
     * @param value the value
     * @return the previous value associated with the key
     */
    public abstract Optional<V> put(K key, V value);

    /**
     * Removes the mapping for a key from this map if it is present.
     * 
     * @param key the key
     * @return the previous value associated with the key
     */
    public abstract Optional<V> remove(K key);

    /**
     * Removes the entry for the specified key only if it is currently mapped to the
     * specified value.
     * 
     * @param key   the key
     * @param value the value
     * @return {@code true} if the value was removed
     */
    public abstract boolean remove(K key, V value);

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return number of key-value mappings in this map;
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns if this map is empty or not.
     * 
     * @return {@code true} if this map is empty, {@code false} otherwise
     */
    public boolean empty() {
        return map.isEmpty();
    }

    /**
     * Returns a sequential {@code Stream} with the the keys contained in this map.
     * 
     * @return a sequential {@code Stream} with the the keys contained in this map
     */
    public Stream<K> keys() {
        return map.keySet().stream();
    }

    /**
     * Returns a sequential {@code Stream} with the the values contained in this
     * map.
     * 
     * @return a sequential {@code Stream} with the the values contained in this map
     */
    public Stream<V> values() {
        return map.values().stream();
    }

    /**
     * Performs the given action for each entry in this map until all entries have
     * been processed or the action throws an exception.
     * 
     * @param action the action to be performed for each entry
     */
    public void forEach(BiConsumer<K, V> action) {
        map.forEach(action);
    }

    /**
     * Returns if this map contains a mapping for the specified key or not.
     * 
     * @param key the key
     * @return {@code true} if this map contains a mapping for the specified key,
     *         {@code false} otherwise
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    protected void resetStates() {
        updatedKeys.clear();
        removedKeys.clear();
    }

    @Override
    public DotNotation xpath() {
        return xpath;
    }

    @Override
    public int appendUpdates(List<Bson> updates) {
        var updatedKeys = this.updatedKeys;
        var removedKeys = this.removedKeys;
        for (var key : updatedKeys) {
            var value = map.get(key);
            appendUpdates(updates, key, value);
        }
        for (var key : removedKeys) {
            updates.add(Updates.unset(xpath().resolve(key.toString()).value()));
        }
        return updatedKeys.size() + removedKeys.size();
    }

    /**
     * Appends the updates of specified value into the given list.
     * 
     * @param updates updates the list of updates
     * @param key     the key
     * @param value   the value
     */
    protected abstract void appendUpdates(List<Bson> updates, K key, V value);

}
