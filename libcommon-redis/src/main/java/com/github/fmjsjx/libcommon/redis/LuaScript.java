package com.github.fmjsjx.libcommon.redis;

import java.util.Objects;
import java.util.Optional;

import com.github.fmjsjx.libcommon.util.DigestUtil;

public class LuaScript {

    private final Optional<String> name;
    private final String script;
    private final String sha1;

    public LuaScript(String name, String script) {
        this.name = Optional.ofNullable(name);
        this.script = Objects.requireNonNull(script, "script must not be null");
        this.sha1 = DigestUtil.sha1AsHex(script);
    }

    public LuaScript(String script) {
        this(null, script);
    }

    public Optional<String> name() {
        return name;
    }

    public String script() {
        return script;
    }

    public String sha1() {
        return sha1;
    }

    @Override
    public String toString() {
        if (name.isPresent()) {
            return "LuaScript(name=" + name.get() + ", script=" + script + ", sha1=" + sha1 + ")";
        }
        return "LuaScript(script=" + script + ", sha1=" + sha1 + ")";
    }

}
