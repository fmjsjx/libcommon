package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class TypeReferenceImpl<T> extends TypeReference<T> {

    private final ParameterizedType type;

    TypeReferenceImpl(ParameterizedType type) {
        super();
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TypeReferenceImpl<" + type.getTypeName() + ">";
    }

}
