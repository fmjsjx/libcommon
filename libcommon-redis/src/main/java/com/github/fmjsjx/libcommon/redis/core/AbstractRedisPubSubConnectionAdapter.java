package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

abstract class AbstractRedisPubSubConnectionAdapter<K, V, C extends StatefulRedisPubSubConnection<K, V>>
        extends AbstractRedisConnectionAdapter<K, V, C> implements RedisPubSubConnectionAdapter<K, V> {

    protected AbstractRedisPubSubConnectionAdapter(C connection) {
        super(connection);
    }

    @Override
    public RedisPubSubCommands<K, V> sync() {
        return connection.sync();
    }

    @Override
    public RedisPubSubAsyncCommands<K, V> async() {
        return connection.async();
    }

    @Override
    public RedisPubSubReactiveCommands<K, V> reactive() {
        return connection.reactive();
    }

}
