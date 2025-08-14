package com.github.fmjsjx.libcommon.redis;

import java.util.Objects;

import io.lettuce.core.ScriptOutputType;

/**
 * The default implementation of {@link LuaScript}.
 *
 * @param <R> the result type of the script
 */
public class DefaultLuaScript<R> implements LuaScript<R> {

    private final String script;
    private final String sha1;
    private final ScriptOutputType outputType;
    private final Class<R> resultType;

    /**
     * Constructs a new {@link DefaultLuaScript} with the specified script,
     * {@code SHA-1}, output type and result type.
     * 
     * @param script     the script text
     * @param sha1       the SHA-1 of the script
     * @param outputType the output type of the script
     * @param resultType the result type of the script
     */
    public DefaultLuaScript(String script, String sha1, ScriptOutputType outputType, Class<R> resultType) {
        this.script = Objects.requireNonNull(script, "script must not be null");
        this.sha1 = Objects.requireNonNull(sha1, "sha1 must not be null");
        this.outputType = Objects.requireNonNull(outputType, "outputType must not be null");
        this.resultType = resultType;
    }

    @Override
    public String script() {
        return script;
    }

    @Override
    public String sha1() {
        return sha1;
    }

    @Override
    public ScriptOutputType outputType() {
        return outputType;
    }

    @Override
    public Class<R> resultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return "LuaScript(script=" + script + ", sha1=" + sha1 + ", outputType=" + ", resultType=" + ")";
    }

}
