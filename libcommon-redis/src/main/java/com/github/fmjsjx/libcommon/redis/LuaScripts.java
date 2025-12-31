package com.github.fmjsjx.libcommon.redis;

/**
 * Provides some LUA scripts.
 */
public class LuaScripts {

    /**
     * Script for action: delete if value equals.
     */
    public static final LuaScript<Boolean> DEL_IF_VALUE_EQUALS = LuaScript.forBoolean(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");

    /**
     * Script for action: Get the value of key and delete the key.
     */
    public static final LuaScript<String> GET_DEL = LuaScript.forValue(
            "local value = redis.call('get', KEYS[1]) if value ~= nil then redis.call('del', KEYS[1]) end return value");

    private LuaScripts() {
    }

}
