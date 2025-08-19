package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

final class RedisDirectPubSubConnectionAdapter<K, V>
        extends AbstractRedisPubSubConnectionAdapter<K, V, StatefulRedisPubSubConnection<K, V>> {

    RedisDirectPubSubConnectionAdapter(StatefulRedisPubSubConnection<K, V> connection) {
        super(connection);
    }

}
