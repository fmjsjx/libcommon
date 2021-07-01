package com.github.fmjsjx.libcommon.bson.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * The {@link JsonSerializer} for {@link ListModel}s.
 * 
 * @since 2.2
 */
public class ListModelSerializer extends JsonSerializer<ListModel<?, ?, ?>> {

    @Override
    public void serialize(ListModel<?, ?, ?> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        var pojo = value.list;
        if (pojo == null) {
            gen.writeNull();
        } else {
            gen.writeObject(pojo);
        }
    }
    
    @Override
    public boolean isEmpty(SerializerProvider provider, ListModel<?, ?, ?> value) {
        return value == null || value.nil();
    }

}
