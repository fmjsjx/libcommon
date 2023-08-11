package com.github.fmjsjx.libcommon.json;

import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <a href="https://github.com/FasterXML/jackson/">{@code Jackson2}</a> support for jsoniter.
 *
 * @author MJ Fang
 * @since 3.6
 */
public final class Jackson2Support {

    private static final Throwable UNAVAILABILITY_CAUSE;

    static {
        Throwable cause = null;

        try {
            Class.forName("com.github.fmjsjx.libcommon.json.Jackson2Library");
        } catch (ClassNotFoundException e) {
            cause = e;
        }

        UNAVAILABILITY_CAUSE = cause;
    }

    /**
     * Returns {@code true} if and only if the {@code libcommon-json-jackson2} is available.
     *
     * @return {@code true} if and only if the {@code libcommon-json-jackson2} is available.
     */
    public static boolean isAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    /**
     * Ensure that {@code libcommon-json-jackson2} is available.
     *
     * @throws UnsupportedOperationException if unavailable
     */
    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw new UnsupportedOperationException("Jackson2Library is unavailable", UNAVAILABILITY_CAUSE);
        }
    }

    /**
     * Returns the cause of unavailability of {@code libcommon-json-jackson2}.
     *
     * @return the cause if unavailable. {@code null} if available.
     */
    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    /**
     * Enable all <a href="https://github.com/FasterXML/jackson/">{@code Jackson2}</a> supports.
     */
    public static void enableAll() {
        ensureAvailability();
        Jackson2JsonNodeSupport.enable();
    }

    private Jackson2Support() {
    }

    /**
     * <a href="https://github.com/FasterXML/jackson/">{@code Jackson2}</a> {@code JsonNode} support.
     */
    public static final class Jackson2JsonNodeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code Jackson2JsonNodeSupport} is enabled, {@code false} otherwise.
         *
         * @return {@code true} if {@code Jackson2JsonNodeSupport} is enabled, {@code false} otherwise
         */
        public static boolean enabled() {
            return enabled.get();
        }

        private static final Encoder.ReflectionEncoder jsonNodeEncoder = new Encoder.ReflectionEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                if (obj == null) {
                    stream.writeNull();
                } else {
                    stream.write(Jackson2Library.getInstance().dumpsToBytes(obj));
                }
            }

            @Override
            public Any wrap(Object obj) {
                return JsoniterLibrary.getInstance().loads(Jackson2Library.getInstance().dumpsToBytes(obj));
            }

        };

        private static final Decoder jsonNodeDecoder = iter -> Jackson2Library.getInstance().loads(iter.readAny().toString());
        private static final Decoder objectNodeDecoder = iter -> {
            var any = iter.readAny();
            if (any.valueType() == ValueType.NULL) {
                return null;
            } else if (any.valueType() == ValueType.OBJECT) {
                return Jackson2Library.getInstance().loads(any.toString());
            }
            throw new JsonException("type mismatch, expected OBJECT but was " + any.valueType());
        };
        private static final Decoder arrayNodeDecoder = iter -> {
            var any = iter.readAny();
            if (any.valueType() == ValueType.NULL) {
                return null;
            } else if (any.valueType() == ValueType.ARRAY) {
                return Jackson2Library.getInstance().loads(any.toString());
            }
            throw new JsonException("type mismatch, expected ARRAY but was " + any.valueType());
        };

        /**
         * Enable the {@code Jackson2JsonNodeSupport}.
         */
        public static void enable() {
            if (enabled.compareAndSet(false, true)) {
                // register encoder for all nodes
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.JsonNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.ArrayNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.BigIntegerNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.BinaryNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.BooleanNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.DecimalNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.DoubleNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.FloatNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.IntNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.LongNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.MissingNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.NullNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.ObjectNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.POJONode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.ShortNode.class, jsonNodeEncoder);
                JsoniterSpi.registerTypeEncoder(com.fasterxml.jackson.databind.node.TextNode.class, jsonNodeEncoder);
                // register decoder for only JsonNode, ObjectNode & ArrayNode
                JsoniterSpi.registerTypeDecoder(com.fasterxml.jackson.databind.JsonNode.class, jsonNodeDecoder);
                JsoniterSpi.registerTypeDecoder(com.fasterxml.jackson.databind.node.ObjectNode.class, objectNodeDecoder);
                JsoniterSpi.registerTypeDecoder(com.fasterxml.jackson.databind.node.ArrayNode.class, arrayNodeDecoder);
            }
        }

        private Jackson2JsonNodeSupport() {
        }

    }

}
