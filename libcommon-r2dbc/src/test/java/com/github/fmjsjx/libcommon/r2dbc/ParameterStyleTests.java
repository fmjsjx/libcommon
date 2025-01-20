package com.github.fmjsjx.libcommon.r2dbc;

import static com.github.fmjsjx.libcommon.r2dbc.ParameterStyle.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ParameterStyleTests {

    @Test
    public void testIsPrefixed() {
        assertFalse(NONE.isPrefixed());
        assertFalse(MYSQL.isPrefixed());
        assertFalse(MARIADB.isPrefixed());
        assertTrue(POSTGRESQL.isPrefixed());
        assertTrue(MSSQL.isPrefixed());
        assertTrue(H2.isPrefixed());
        assertFalse(ORACLE.isPrefixed());
    }

    @Test
    public void testGetPrefix() {
        assertNull(NONE.getPrefix());
        assertNull(MYSQL.getPrefix());
        assertNull(MARIADB.getPrefix());
        assertEquals("$", POSTGRESQL.getPrefix());
        assertEquals("@", MSSQL.getPrefix());
        assertEquals("$", H2.getPrefix());
        assertNull(ORACLE.getPrefix());
    }

    @Test
    public void testGetBaseIndex() {
        assertEquals(-1, NONE.getBaseIndex());
        assertEquals(-1, MYSQL.getBaseIndex());
        assertEquals(-1, MARIADB.getBaseIndex());
        assertEquals(1, POSTGRESQL.getBaseIndex());
        assertEquals(0, MSSQL.getBaseIndex());
        assertEquals(1, H2.getBaseIndex());
        assertEquals(-1, ORACLE.getBaseIndex());
    }

    @Test
    public void testToString() {
        assertEquals("NONE(prefixed=false, prefix=null, baseIndex=-1)", NONE.toString());
        assertEquals("MYSQL(prefixed=false, prefix=null, baseIndex=-1)", MYSQL.toString());
        assertEquals("MARIADB(prefixed=false, prefix=null, baseIndex=-1)", MARIADB.toString());
        assertEquals("POSTGRESQL(prefixed=true, prefix=$, baseIndex=1)", POSTGRESQL.toString());
        assertEquals("MSSQL(prefixed=true, prefix=@, baseIndex=0)", MSSQL.toString());
        assertEquals("H2(prefixed=true, prefix=$, baseIndex=1)", H2.toString());
        assertEquals("ORACLE(prefixed=false, prefix=null, baseIndex=-1)", ORACLE.toString());
    }

}
