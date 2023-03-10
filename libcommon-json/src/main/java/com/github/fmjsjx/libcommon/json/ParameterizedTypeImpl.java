package com.github.fmjsjx.libcommon.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

final class ParameterizedTypeImpl implements ParameterizedType {

    private final Type[] typeArguments;
    private final Class<?> rawType;
    private String typeName;
    private int hash;
    private boolean hashIsZero;

    ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments) {
        this.rawType = Objects.requireNonNull(rawType, "rawType");
        this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments");
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("the length of the typeArguments must be greater than zero");
        }
        for (var arg : typeArguments) {
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

    @Override
    public int hashCode() {
        var h = hash;
        if (h == 0 && !hashIsZero) {
            h = rawType.hashCode();
            for (var type : typeArguments) {
                h ^= type.hashCode();
            }
            if (h == 0) {
                hashIsZero = true;
            } else {
                hash = h;
            }
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ParameterizedTypeImpl type) {
            return getTypeName().equals(type.getTypeName());
        } else if (obj instanceof ParameterizedType type) {
            return rawType.equals(type.getRawType()) && Arrays.equals(typeArguments, type.getActualTypeArguments());
        }
        return super.equals(obj);
    }

}
