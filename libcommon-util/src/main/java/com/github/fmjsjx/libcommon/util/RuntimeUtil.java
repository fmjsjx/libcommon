package com.github.fmjsjx.libcommon.util;

/**
 * Utility for Runtime.
 */
public class RuntimeUtil {

    private static final class AvailableProcessorsHolder {
        private static final int availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Returns the number of processors available to the Java virtual machine.
     * 
     * @return the number of processors available to the Java virtual machine
     */
    public static final int availableProcessors() {
        return AvailableProcessorsHolder.availableProcessors;
    }

    private RuntimeUtil() {
    }

}
