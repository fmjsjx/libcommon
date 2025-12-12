package com.github.fmjsjx.libcommon.r2dbc;

/**
 * The Order record.
 *
 * @param column the column name
 * @param sort   the sort
 * @author MJ Fang
 * @since 3.12
 */
public record Order(String column, Sort sort) {

    /**
     * Convert to order string.
     * <p>
     * e.g. {@code "id ASC"}, {@code "create_time DESC"}, etc.
     *
     * @return order string
     */
    public String toOrderString() {
        if (sort == null) {
            return column;
        }
        return column + " " + sort.name();
    }

}
