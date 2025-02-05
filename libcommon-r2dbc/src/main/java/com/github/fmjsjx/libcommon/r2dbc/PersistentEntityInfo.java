package com.github.fmjsjx.libcommon.r2dbc;

import java.util.List;

final class PersistentEntityInfo<T> {

    private final Class<T> entityClass;
    private final List<PersistentColumnInfo<T>> columns;

    PersistentEntityInfo(Class<T> entityClass, List<PersistentColumnInfo.Builder> columnBuilders) {
        this.entityClass = entityClass;
        this.columns = columnBuilders.stream().map(column -> column.entityInfo(this).<T>build()).toList();
    }

    Class<T> getEntityClass() {
        return entityClass;
    }

    List<PersistentColumnInfo<T>> getColumns() {
        return columns;
    }

}
