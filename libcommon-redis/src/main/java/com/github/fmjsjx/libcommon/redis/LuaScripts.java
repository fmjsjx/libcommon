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

    private LuaScripts() {
    }

}
