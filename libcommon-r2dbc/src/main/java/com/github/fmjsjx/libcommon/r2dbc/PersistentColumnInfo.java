package com.github.fmjsjx.libcommon.r2dbc;

import java.util.function.Function;

final class PersistentColumnInfo<T> {

    private final PersistentEntityInfo<T> entityInfo;
    private final String columnName;
    private final Function<T, Object> valueGetter;
    private final boolean id;
    private final boolean readOnly;
    private final boolean insertOnly;

    private PersistentColumnInfo(PersistentEntityInfo<T> entityInfo, String columnName, Function<T, Object> valueGetter,
                                 boolean id, boolean readOnly, boolean insertOnly) {
        this.entityInfo = entityInfo;
        this.columnName = columnName;
        this.valueGetter = valueGetter;
        this.id = id;
        this.readOnly = readOnly;
        this.insertOnly = insertOnly;
    }

    PersistentEntityInfo<T> getEntityInfo() {
        return entityInfo;
    }

    Class<T> getEntityClass() {
        return getEntityInfo().getEntityClass();
    }

    String getColumnName() {
        return columnName;
    }

    Function<T, Object> getValueGetter() {
        return valueGetter;
    }

    Object getValue(T entity) {
        return getValueGetter().apply(entity);
    }

    boolean isId() {
        return id;
    }

    boolean isReadOnly() {
        return readOnly;
    }

    boolean isInsertOnly() {
        return insertOnly;
    }

    Builder toBuilder() {
        return new Builder(entityInfo, columnName, valueGetter, id, readOnly, insertOnly);
    }

    static final Builder builder() {
        return new Builder();
    }

    static final Builder builder(PersistentColumnInfo<?> info) {
        return info.toBuilder();
    }

    static final class Builder {

        private PersistentEntityInfo<?> entityInfo;
        private String columnName;
        private Function<?, Object> valueGetter;
        private boolean id;
        private boolean readOnly;
        private boolean insertOnly;

        private Builder() {
        }

        private Builder(PersistentEntityInfo<?> entityInfo, String columnName, Function<?, Object> valueGetter,
                        boolean id, boolean readOnly, boolean insertOnly) {
            this.entityInfo = entityInfo;
            this.columnName = columnName;
            this.valueGetter = valueGetter;
            this.id = id;
            this.readOnly = readOnly;
            this.insertOnly = insertOnly;
        }

        Builder entityInfo(PersistentEntityInfo<?> entityInfo) {
            this.entityInfo = entityInfo;
            return this;
        }

        Builder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        Builder valueGetter(Function<?, Object> valueGetter) {
            this.valueGetter = valueGetter;
            return this;
        }

        Builder id(boolean id) {
            this.id = id;
            return this;
        }

        Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        Builder insertOnly(boolean insertOnly) {
            this.insertOnly = insertOnly;
            return this;
        }

        @SuppressWarnings("unchecked")
        <T> PersistentColumnInfo<T> build() {
            if (entityInfo == null) {
                throw new IllegalArgumentException("entityInfo must not be null");
            }
            if (columnName == null) {
                throw new IllegalArgumentException("columnName must not be null");
            }
            if (valueGetter == null) {
                throw new IllegalArgumentException("valueGetter must not be null");
            }
            var entityInfo = (PersistentEntityInfo<T>) this.entityInfo;
            var columnName = this.columnName;
            var valueGetter = (Function<T, Object>) this.valueGetter;
            var id = this.id;
            var readOnly = this.readOnly;
            var insertOnly = this.insertOnly;
            return new PersistentColumnInfo<>(entityInfo, columnName, valueGetter, id, readOnly, insertOnly);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "entityInfo=" + entityInfo +
                    ", columnName='" + columnName + '\'' +
                    ", valueGetter=" + valueGetter +
                    ", id=" + id +
                    ", readOnly=" + readOnly +
                    ", insertOnly=" + insertOnly +
                    '}';
        }
    }

}
