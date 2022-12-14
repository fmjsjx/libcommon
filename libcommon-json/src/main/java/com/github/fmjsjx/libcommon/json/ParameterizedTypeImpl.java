package com.github.fmjsjx.libcommon.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

final class ParameterizedTypeImpl implements ParameterizedType {

    private final Type[] typeArguments;
    private final Class<?> rawType;
    private String typeName;

    ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments) {
        this.rawType = Objects.requireNonNull(rawType, "rawType");
        this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments") ;
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("the length of the typeArguments must be greater than zero");
        }
        for (var arg: typeArguments) {
            if (arg == null) {
                throw new IllegalArgumentException("the value of the typeArguments must not be null");
            }
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public String getTypeName() {
        var typeName = this.typeName;
        if (typeName == null) {
            synchronized (this) {
                typeName = this.typeName;
                if (typeName == null) {
                    var b = new StringBuilder().append(rawType.getName()).append('<');
                    var args = typeArguments;
                    b.append(args[0].getTypeName());
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            b.append(", ").append(args[i].getTypeName());
                        }
                    }
                    this.typeName = typeName = b.append('>').toString();
                }
            }
        }
        return typeName;
    }
}
