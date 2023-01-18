package com.github.fmjsjx.libcommon.json;

import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * A mixed implementation of {@link JsonLibrary}.
 *
 * @param <JSON> the type of dynamic JSON object
 * @see JsonEncoder
 * @see JsonDecoder
 */
public class MixedJsonLibrary<JSON> implements JsonLibrary<JSON> {

    /**
     * The encoder.
     */
    protected final JsonEncoder encoder;
    /**
     * The decoder.
     */
    protected final JsonDecoder<JSON> decoder;

    /**
     * Creates a new {@link MixedJsonLibrary} with the specified encoder and
     * decoder.
     * 
     * @param encoder the encoder
     * @param decoder the decoder
     */
    public MixedJsonLibrary(JsonEncoder encoder, JsonDecoder<JSON> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public byte[] dumpsToBytes(Object obj) throws JsonException {
        return encoder.dumpsToBytes(obj);
    }

    @Override
    public String dumpsToString(Object obj) throws JsonException {
        return encoder.dumpsToString(obj);
    }

    @Override
    public void dumps(Object obj, OutputStream out) throws JsonException {
        encoder.dumps(obj, out);
    }

    @Override
    public <T extends JSON> T loads(byte[] src) throws JsonException {
        return decoder.loads(src);
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) throws JsonException {
        return decoder.loads(src, type);
    }

    @Override
    public <T> T loads(byte[] src, Type type) throws JsonException {
        return decoder.loads(src, type);
    }

}
