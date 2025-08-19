package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.api.AsyncCloseable;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.reactive.RedisClusterReactiveCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

import java.util.concurrent.CompletableFuture;

/**
 * An adapter for redis connection.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @author MJ Fang
 * @since 3.16
 */
public interface RedisConnectionAdapter<K, V> extends AutoCloseable, AsyncCloseable {

    /**
     * Creates and returns a new {@link RedisConnectionAdapter} instance.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param <C>        Connection type.
     * @return a new {@link RedisConnectionAdapter} instance
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static <K, V, C extends StatefulConnection<K, V>> RedisConnectionAdapter<K, V> of(C connection) {
        if (connection instanceof StatefulRedisClusterConnection c) {
            return ofCluster(c);
        }
        if (connection instanceof StatefulRedisConnection c) {
            return ofDirect(c);
        }
        throw new UnsupportedOperationException("unsupported connection type " + connection.getClass());
    }

    /**
     * Creates and returns a new {@link RedisConnectionAdapter} instance
     * with the specified direct {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisConnectionAdapter} instance
     */
    static <K, V> RedisConnectionAdapter<K, V> ofDirect(StatefulRedisConnection<K, V> connection) {
        return new RedisDirectConnectionAdapter<>(connection);
    }

    /**
     * Creates and returns a new {@link RedisConnectionAdapter} instance
     * with the specified cluster {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisConnectionAdapter} instance
     */
    static <K, V> RedisConnectionAdapter<K, V> ofCluster(StatefulRedisClusterConnection<K, V> connection) {
        return new RedisClusterConnectionAdapter<>(connection);
    }

    /**
     * Returns the delegated connection.
     *
     * @return the delegated connection
     */
    StatefulConnection<K, V> getConnection();

    /**
     * Returns the delegated, type unsafe connection.
     *
     * @param <C> the type of the connection
     * @return the delegated, type unsafe connection
     */
    @SuppressWarnings("unchecked")
    default <C extends StatefulConnection<K, V>> C getUnsafeConnection() {
        return (C) getConnection();
    }

    /**
     * Returns the {@link RedisClusterCommands} API for the delegated
     * connection.
     *
     * @return the synchronous API for the underlying connection.
     */
    RedisClusterCommands<K, V> sync();

    /**
     * Returns the {@link RedisClusterAsyncCommands} API for the delegated
     * connection.
     *
     * @return the asynchronous API for the underlying connection.
     */
    RedisClusterAsyncCommands<K, V> async();

    /**
     * Returns the {@link RedisClusterReactiveCommands} API for the
     * delegated connection.
     *
     * @return the reactive API for the underlying connection.
     */
    RedisClusterReactiveCommands<K, V> reactive();

    /**
     * Close the delegated connection.
     */
    @Override
    void close();

    /**
     * Request to close the delegated connection and return the
     * {@link CompletableFuture} that is notified about its progress. The
     * connection will become not usable anymore as soon as this method
     * was called.
     *
     * @return a {@link CompletableFuture} that is notified once the
     * operation completes, either because the operation was successful
     * or because of an error.
     */
    @Override
    CompletableFuture<Void> closeAsync();

}
