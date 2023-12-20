package com.github.fmjsjx.libcommon.redis;

import java.util.List;

import com.github.fmjsjx.libcommon.util.DigestUtil;

import io.lettuce.core.ScriptOutputType;

/**
 * A {@code LUA} script to be executed using the {@code Redis scripting support}
 * (based on lettuce).
 *
 * @param <R> the result type of the script
 */
public interface LuaScript<R> {

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param script the script text, must not be {@code null}
     * @return a new {@link LuaScript} instance with output type {@code VALUE} and
     *         return type {@code String}
     */
    static LuaScript<String> forValue(String script) {
        return of(script, ScriptOutputType.VALUE);
    }

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param script the script text, must not be {@code null}
     * @return a new {@link LuaScript} instance with output type {@code STATUS} and
     *         return type {@code String}
     */
    static LuaScript<String> forStatus(String script) {
        return of(script, ScriptOutputType.STATUS);
    }

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param script the script text, must not be {@code null}
     * @return a new {@link LuaScript} instance with output type {@code INTEGER} and
     *         return type {@code Long}
     */
    static LuaScript<Long> forNumber(String script) {
        return of(script, ScriptOutputType.INTEGER);
    }

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param script the script text, must not be {@code null}
     * @return a new {@link LuaScript} instance with output type {@code BOOLEAN} and
     *         return type {@code Boolean}
     */
    static LuaScript<Boolean> forBoolean(String script) {
        return of(script, ScriptOutputType.BOOLEAN);
    }

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param script the script text, must not be {@code null}
     * @param <E>    the element type of the result list
     * @return a new {@link LuaScript} instance with output type {@code MULTI} and
     *         return type {@code List}
     */
    static <E> LuaScript<List<E>> forList(String script) {
        return of(script, ScriptOutputType.MULTI);
    }

    /**
     * Creates a new {@link LuaScript} from {@link String}.
     * 
     * @param <R>        the result type of the script
     * @param script     the script text, must not be {@code null}
     * @param outputType the output type
     * @return a new {@link LuaScript} instance with output type {@code INTEGER} and
     *         return type {@code Long}
     */
    @SuppressWarnings("unchecked")
    static <R> LuaScript<R> of(String script, ScriptOutputType outputType) {
        var sha1 = DigestUtil.sha1AsHex(script);
        return (LuaScript<R>) switch (outputType) {
            case BOOLEAN -> new DefaultLuaScript<>(script, sha1, outputType, Boolean.class);
            case INTEGER -> new DefaultLuaScript<>(script, sha1, outputType, Long.class);
            case MULTI -> new DefaultLuaScript<>(script, sha1, outputType, List.class);
            case STATUS, VALUE -> new DefaultLuaScript<>(script, sha1, outputType, String.class);
            default -> throw new UnsupportedOperationException("Unsupported script output type " + outputType + " occurs");
        };
    }

    /**
     * Returns the script text.
     * 
     * @return the script text
     */
    String script();

    /**
     * Returns the {@code SHA-1} of the script, used for executing Redis evalsha
     * command.
     * 
     * @return the {@code SHA-1} of the script
     */
    String sha1();

    /**
     * Returns the output type of the script.
     * 
     * @return the output type of the script
     */
    ScriptOutputType outputType();

    /**
     * Returns the result type of the script.
     * 
     * @return the result type of the script
     */
    Class<R> resultType();

}
