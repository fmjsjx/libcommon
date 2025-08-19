package com.github.fmjsjx.libcommon.redis.core;

import io.lettuce.core.api.StatefulConnection;

import java.util.concurrent.CompletableFuture;

abstract class AbstractRedisConnectionAdapter<K, V, C extends StatefulConnection<K, V>> implements RedisConnectionAdapter<K, V> {

    protected final C connection;

    protected AbstractRedisConnectionAdapter(C connection) {
        this.connection = connection;
    }

    @Override
    public C getConnection() {
        return connection;
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return connection.closeAsync();
    }

}
