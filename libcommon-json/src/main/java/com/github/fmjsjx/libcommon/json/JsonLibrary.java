package com.github.fmjsjx.libcommon.json;

/**
 * A JSON encode/decode library. Combines {@link JsonEncoder} and
 * {@link JsonDecoder}.
 * 
 * @param <JSON> the type of dynamic JSON object
 */
public interface JsonLibrary<JSON> extends JsonEncoder, JsonDecoder<JSON> {

    /**
     * Returns a mixed {@link JsonLibrary} with the specified encoder and decoder.
     * 
     * @param <JSON>  the type of dynamic JSON object
     * @param encoder the encoder
     * @param decoder the decoder
     * @return a mixed {@code JsonLibrary} with the specified encoder and decoder
     */
    static <JSON> JsonLibrary<JSON> mixed(JsonEncoder encoder, JsonDecoder<JSON> decoder) {
        return new MixedJsonLibrary<>(encoder, decoder);
    }

}
