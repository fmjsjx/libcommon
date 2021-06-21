package com.github.fmjsjx.libcommon.bson.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MapModelSerializer extends JsonSerializer<MapModel<?, ?, ?, ?>> {

    @Override
    public void serialize(MapModel<?, ?, ?, ?> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeObject(value.map);
    }

}
