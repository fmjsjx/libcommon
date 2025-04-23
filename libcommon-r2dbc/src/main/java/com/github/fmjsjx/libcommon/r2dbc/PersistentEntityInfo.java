package com.github.fmjsjx.libcommon.r2dbc;

import static com.github.fmjsjx.libcommon.r2dbc.SqlBuilder.getTableName;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

final class PersistentEntityInfo<T> {

    private final Class<T> entityClass;
    private final List<PersistentColumnInfo<T>> columns;
    private final int insertColumns;
    private final int insertColumnsWithoutId;
    private final ConcurrentMap<String, String> cachedSelectColumns = new ConcurrentHashMap<>();

    PersistentEntityInfo(Class<T> entityClass, List<PersistentColumnInfo.Builder> columnBuilders) {
        this.entityClass = entityClass;
        this.columns = columnBuilders.stream().map(column -> column.entityInfo(this).<T>build()).toList();
        this.insertColumns = columns.stream()
                .filter(it -> !it.isReadOnly())
                .mapToInt(it -> 1).sum();
        this.insertColumnsWithoutId = columns.stream()
                .filter(column -> !column.isReadOnly() && !column.isId())
                .mapToInt(it -> 1).sum();
    }

    Class<T> getEntityClass() {
        return entityClass;
    }

    List<PersistentColumnInfo<T>> getColumns() {
        return columns;
    }

    int getInsertColumns() {
        return insertColumns;
    }

    int getInsertColumnsWithoutId() {
        return insertColumnsWithoutId;
    }

    String getSelectColumns(String tableAlias, String columnsAliasPrefix) {
        var key1 = tableAlias == null ? "" : tableAlias;
        var key2 = columnsAliasPrefix == null ? "" : columnsAliasPrefix;
        var key = key1 + ":" + key2;
        return cachedSelectColumns.computeIfAbsent(key, this::generateSelectColumns);
    }

    private String generateSelectColumns(String key) {
        var sepIndex = key.lastIndexOf(":");
        var key0 = key.substring(0, sepIndex);
        var key1 = key.substring(sepIndex + 1);
        var table = key0.isBlank() ? getTableName(entityClass) : key0;
        var columnsAliasPrefix = key1.isBlank() ? null : key1;
        return columns.stream().filter(it -> !it.isInsertOnly()).map(it -> {
            var columnBuilder = new StringBuilder().append(table).append('.').append(it.getColumnName());
            if (columnsAliasPrefix != null) {
                columnBuilder.append(" ").append(columnsAliasPrefix).append(it.getColumnName());
            }
            return columnBuilder.toString();
        }).collect(Collectors.joining(", "));
    }

}
