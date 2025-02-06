package com.github.fmjsjx.libcommon.r2dbc;

import java.util.Objects;

/**
 * The style enum of parameters in R2DBC.
 *
 * @author MJ Fang
 * @since 3.11
 */
public enum ParameterStyle {

    /**
     * None
     */
    NONE,
    /**
     * MySQL
     */
    MYSQL,
    /**
     * MariaDB
     */
    MARIADB,
    /**
     * PostgreSQL
     */
    POSTGRESQL("$", 1),
    /**
     * Microsoft SQL Server
     */
    MSSQL("@", 0),
    /**
     * H2
     */
    H2("$", 1),
    /**
     * Oracle
     */
    ORACLE,
    ;

    private final boolean prefixed;
    private final String prefix;
    private final int baseIndex;

    ParameterStyle() {
        this(false, null, -1);
    }

    ParameterStyle(String prefix, int baseIndex) {
        this(true, prefix, baseIndex);
    }

    ParameterStyle(boolean prefixed, String prefix, int baseIndex) {
        this.prefixed = prefixed;
        this.prefix = prefixed ? Objects.requireNonNull(prefix, "prefix must not be null") : null;
        this.baseIndex = baseIndex;
    }

    /**
     * Returns if is prefixed style.
     *
     * @return {@code true} if is prefixed style, {@code false} otherwise
     */
    public boolean isPrefixed() {
        return prefixed;
    }

    /**
     * Returns the prefix string.
     *
     * @return the prefix string
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the base index of the indexed parameters.
     *
     * @return the base index of the indexed parameters
     */
    public int getBaseIndex() {
        return baseIndex;
    }

    @Override
    public String toString() {
        return name() + "(prefixed=" + prefixed + ", prefix=" + prefix + ", baseIndex=" + baseIndex + ")";
    }

}
