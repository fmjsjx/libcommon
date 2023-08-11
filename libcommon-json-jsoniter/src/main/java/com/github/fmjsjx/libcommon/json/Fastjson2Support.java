package com.github.fmjsjx.libcommon.json;

import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <a href="https://github.com/alibaba/fastjson2">{@code Fastjson2}</a> support for jsoniter.
 *
 * @author MJ Fang
 * @since 3.6
 */
public final class Fastjson2Support {

    private static final Throwable UNAVAILABILITY_CAUSE;

    static {
        Throwable cause = null;

        try {
            Class.forName("com.github.fmjsjx.libcommon.json.Fastjson2Library");
        } catch (ClassNotFoundException e) {
            cause = e;
        }

        UNAVAILABILITY_CAUSE = cause;
    }

    /**
     * Returns {@code true} if and only if the {@code libcommon-json-fastjson2} is available.
     *
     * @return {@code true} if and only if the {@code libcommon-json-fastjson2} is available.
     */
    public static boolean isAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    /**
     * Ensure that {@code libcommon-json-fastjson2} is available.
     *
     * @throws UnsupportedOperationException if unavailable
     */
    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw new UnsupportedOperationException("Fastjson2Library is unavailable", UNAVAILABILITY_CAUSE);
        }
    }

    /**
     * Returns the cause of unavailability of {@code libcommon-json-fastjson2}.
     *
     * @return the cause if unavailable. {@code null} if available.
     */
    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    /**
     * Enable all <a href="https://github.com/alibaba/fastjson2">{@code Fastjson2}</a> supports.
     */
    public static void enableAll() {
        ensureAvailability();
        Fastjson2JSONObjectSupport.enable();
        Fastjson2JSONArraySupport.enable();
    }

    private Fastjson2Support() {
    }

    private static final class Fastjson2Encoder implements Encoder.ReflectionEncoder {

        private static final Fastjson2Encoder INSTANCE = new Fastjson2Encoder();

        @Override
        public Any wrap(Object obj) {
            return JsoniterLibrary.getInstance().loads(Fastjson2Library.getInstance().dumpsToBytes(obj));
        }

        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            if (obj == null) {
                stream.writeNull();
            } else {
                stream.write(Fastjson2Library.getInstance().dumpsToBytes(obj));
            }
        }

        private Fastjson2Encoder() {
        }

    }

    /**
     * <a href="https://github.com/alibaba/fastjson2">{@code Fastjson2}</a> {@code JSONObject} support.
     */
    public static final class Fastjson2JSONObjectSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code Fastjson2JSONObjectSupport} is enabled, {@code false} otherwise.
         *
         * @return {@code true} if {@code Fastjson2JSONObjectSupport} is enabled, {@code false} otherwise
         */
        public static boolean enabled() {
            return enabled.get();
        }

        /**
         * Enable the {@code Fastjson2JSONObjectSupport}.
         */
        public static void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(com.alibaba.fastjson2.JSONObject.class, Fastjson2Encoder.INSTANCE);
                JsoniterSpi.registerTypeDecoder(com.alibaba.fastjson2.JSONObject.class, iter -> {
                    var any = iter.readAny();
                    if (any.valueType() == ValueType.NULL) {
                        return null;
                    } else if (any.valueType() == ValueType.OBJECT) {
                        return Fastjson2Library.getInstance().loads(any.toString());
                    }
                    throw new JsonException("type mismatch, expected OBJECT but was " + any.valueType());
                });
            }
        }

        private Fastjson2JSONObjectSupport() {
        }

    }

    /**
     * <a href="https://github.com/alibaba/fastjson2">{@code Fastjson2}</a> {@code JSONArray} support.
     */
    public static final class Fastjson2JSONArraySupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code Fastjson2JSONArraySupport} is enabled, {@code false} otherwise.
         *
         * @return {@code true} if {@code Fastjson2JSONArraySupport} is enabled, {@code false} otherwise
         */
        public static boolean enabled() {
            return enabled.get();
        }

        /**
         * Enable the {@code Fastjson2JSONArraySupport}.
         */
        public static void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(com.alibaba.fastjson2.JSONArray.class, Fastjson2Encoder.INSTANCE);
                JsoniterSpi.registerTypeDecoder(com.alibaba.fastjson2.JSONArray.class, iter -> {
                    var any = iter.readAny();
                    if (any.valueType() == ValueType.NULL) {
                        return null;
                    } else if (any.valueType() == ValueType.ARRAY) {
                        return Fastjson2Library.getInstance().loads(any.toString());
                    }
                    throw new JsonException("type mismatch, expected ARRAY but was " + any.valueType());
                });
            }
        }

        private Fastjson2JSONArraySupport() {
        }

    }

}
