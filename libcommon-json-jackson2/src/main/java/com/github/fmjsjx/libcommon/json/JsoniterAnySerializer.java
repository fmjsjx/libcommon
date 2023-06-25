package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

/**
 * The {@link JsonSerializer} to serialize {@link Any} values.
 *
 * @author MJ Fang
 * @since 3.4.3
 */
class JsoniterAnySerializer extends StdSerializer<Any> {

    private static final class InstanceHolder {
        private static final JsoniterAnySerializer INSTANCE = new JsoniterAnySerializer();
    }

    /**
     * Returns the singleton {@link JsoniterAnySerializer} instance.
     *
     * @return the singleton {@link JsoniterAnySerializer} instance
     */
    static final JsoniterAnySerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Constructs new {@link JsoniterAnySerializer} instance.
     */
    protected JsoniterAnySerializer() {
        super(Any.class);
    }

    @Override
    public void serialize(Any value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeRawValue(JsonStream.serialize(value));
    }

}
