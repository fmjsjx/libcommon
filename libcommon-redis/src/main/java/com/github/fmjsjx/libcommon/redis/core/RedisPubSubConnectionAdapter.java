package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

/**
 * An adapter for redis pub/sub connection.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @author MJ Fang
 * @since 3.16
 */
public interface RedisPubSubConnectionAdapter<K, V> extends RedisConnectionAdapter<K, V> {

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param <C>        Connection type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @since 4.3
     */
    @SuppressWarnings("unchecked")
    static <K, V, C extends StatefulRedisPubSubConnection<K, V>> RedisPubSubConnectionAdapter<K, V> autoPubSub(C connection) {
        return connection instanceof StatefulRedisClusterPubSubConnection<?, ?>
                ? ofClusterPubSub((StatefulRedisClusterPubSubConnection<K, V>) connection)
                : ofDirectPubSub(connection);
    }

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance with the specified direct {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @since 4.3
     */
    static <K, V> RedisPubSubConnectionAdapter<K, V> ofDirectPubSub(StatefulRedisPubSubConnection<K, V> connection) {
        return new RedisDirectPubSubConnectionAdapter<>(connection);
    }

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance with the specified cluster {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @since 4.3
     */
    static <K, V> RedisPubSubConnectionAdapter<K, V> ofClusterPubSub(StatefulRedisClusterPubSubConnection<K, V> connection) {
        return new RedisClusterPubSubConnectionAdapter<>(connection);
    }

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param <C>        Connection type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @deprecated since 4.3, please use {@link #autoPubSub(StatefulRedisPubSubConnection)} instead
     */
    @Deprecated(since = "4.3")
    static <K, V, C extends StatefulRedisPubSubConnection<K, V>> RedisPubSubConnectionAdapter<K, V> of(C connection) {
        return autoPubSub(connection);
    }

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance with the specified direct {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @deprecated since 4.3, please use {@link #ofDirectPubSub(StatefulRedisPubSubConnection)} instead
     */
    @Deprecated(since = "4.3")
    static <K, V> RedisPubSubConnectionAdapter<K, V> ofDirect(StatefulRedisPubSubConnection<K, V> connection) {
        return ofDirectPubSub(connection);
    }

    /**
     * Creates and returns a new {@link RedisPubSubConnectionAdapter}
     * instance with the specified cluster {@code connection} given.
     *
     * @param connection the delegated connection
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @return a new {@link RedisPubSubConnectionAdapter} instance
     * @deprecated since 4.3, please use {@link #ofClusterPubSub(StatefulRedisClusterPubSubConnection)} instead
     */
    @Deprecated(since = "4.3")
    static <K, V> RedisPubSubConnectionAdapter<K, V> ofCluster(StatefulRedisClusterPubSubConnection<K, V> connection) {
        return ofClusterPubSub(connection);
    }

    @Override
    StatefulRedisPubSubConnection<K, V> getConnection();

    /**
     * Returns the {@link RedisPubSubCommands} API for the delegated
     * connection.
     *
     * @return the synchronous API for the underlying connection.
     */
    @Override
    RedisPubSubCommands<K, V> sync();

    /**
     * Returns the {@link RedisPubSubAsyncCommands} API for the delegated
     * connection.
     *
     * @return the asynchronous API for the underlying connection.
     */
    @Override
    RedisPubSubAsyncCommands<K, V> async();

    /**
     * Returns the {@link RedisPubSubReactiveCommands} API for the
     * delegated connection.
     *
     * @return the reactive API for the underlying connection.
     */
    @Override
    RedisPubSubReactiveCommands<K, V> reactive();

}
