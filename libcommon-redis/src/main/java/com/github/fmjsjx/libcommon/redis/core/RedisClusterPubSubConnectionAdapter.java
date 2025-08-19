package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;

final class RedisClusterPubSubConnectionAdapter<K, V>
        extends AbstractRedisPubSubConnectionAdapter<K, V, StatefulRedisClusterPubSubConnection<K, V>> {

    RedisClusterPubSubConnectionAdapter(StatefulRedisClusterPubSubConnection<K, V> connection) {
        super(connection);
    }

}
