package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

final class RedisClusterConnectionAdapter<K, V> extends AbstractRedisConnectionAdapter<K, V, StatefulRedisClusterConnection<K, V>> {

    RedisClusterConnectionAdapter(StatefulRedisClusterConnection<K, V> connection) {
        super(connection);
    }

    @Override
    public RedisAdvancedClusterCommands<K, V> sync() {
        return connection.sync();
    }

    @Override
    public RedisAdvancedClusterAsyncCommands<K, V> async() {
        return connection.async();
    }

    @Override
    public RedisAdvancedClusterReactiveCommands<K, V> reactive() {
        return connection.reactive();
    }

}
