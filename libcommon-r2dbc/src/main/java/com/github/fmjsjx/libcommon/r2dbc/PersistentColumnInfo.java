package com.github.fmjsjx.libcommon.r2dbc;

import java.util.function.Function;

final class PersistentColumnInfo<T> {

    private final PersistentEntityInfo<T> entityInfo;
    private final String columnName;
    private final Function<T, Object> valueGetter;
    private final boolean insertOnly;

    private PersistentColumnInfo(PersistentEntityInfo<T> entityInfo, String columnName, Function<T, Object> valueGetter, boolean insertOnly) {
        this.entityInfo = entityInfo;
        this.columnName = columnName;
        this.valueGetter = valueGetter;
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

    boolean isInsertOnly() {
        return insertOnly;
    }

    Builder toBuilder() {
        return new Builder(entityInfo, columnName, valueGetter, insertOnly);
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
        private boolean insertOnly;

        private Builder() {
        }

        private Builder(PersistentEntityInfo<?> entityInfo, String columnName, Function<?, Object> valueGetter,
                        boolean insertOnly) {
            this.entityInfo = entityInfo;
            this.columnName = columnName;
            this.valueGetter = valueGetter;
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
            var insertOnly = this.insertOnly;
            return new PersistentColumnInfo<>(entityInfo, columnName, valueGetter, insertOnly);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "entityInfo=" + entityInfo +
                    ", columnName='" + columnName + '\'' +
                    ", valueGetter=" + valueGetter +
                    ", insertOnly=" + insertOnly +
                    '}';
        }
    }

}
