package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;

final class RedisDirectConnectionAdapter<K, V> extends AbstractRedisConnectionAdapter<K, V, StatefulRedisConnection<K, V>> {

    RedisDirectConnectionAdapter(StatefulRedisConnection<K, V> connection) {
        super(connection);
    }

    @Override
    public RedisCommands<K, V> sync() {
        return connection.sync();
    }

    @Override
    public RedisAsyncCommands<K, V> async() {
        return connection.async();
    }

    @Override
    public RedisReactiveCommands<K, V> reactive() {
        return connection.reactive();
    }

}
