package com.github.fmjsjx.libcommon.util.ansi;

/**
 * Enumerations for {@code ANSI} colors.
 */
public enum AnsiColor {

    /**
     * BLACK {@code "30"}
     */
    BLACK("30"),

    /**
     * RED {@code "31"}
     */
    RED("31"),

    /**
     * GREEN {@code "32"}
     */
    GREEN("32"),

    /**
     * YELLOW {@code "33"}
     */
    YELLOW("33"),

    /**
     * BLUE {@code "34"}
     */
    BLUE("34"),

    /**
     * MAGENTA {@code "35"}
     */
    MAGENTA("35"),

    /**
     * CYAN {@code "36"}
     */
    CYAN("36"),

    /**
     * WHITE {@code "37"}
     */
    WHITE("37"),

    /**
     * DEFAULT {@code "39"}
     */
    DEFAULT("39"),

    /**
     * BRIGHT_BLACK {@code "90"}
     */
    BRIGHT_BLACK("90"),

    /**
     * BRIGHT_RED {@code "91"}
     */
    BRIGHT_RED("91"),

    /**
     * BRIGHT_GREEN {@code "92"}
     */
    BRIGHT_GREEN("92"),

    /**
     * BRIGHT_YELLOW {@code "93"}
     */
    BRIGHT_YELLOW("93"),

    /**
     * BRIGHT_BLUE {@code "94"}
     */
    BRIGHT_BLUE("94"),

    /**
     * BRIGHT_MAGENTA {@code "95"}
     */
    BRIGHT_MAGENTA("95"),

    /**
     * BRIGHT_CYAN {@code "96"}
     */
    BRIGHT_CYAN("96"),

    /**
     * BRIGHT_WHITE {@code "97"}
     */
    BRIGHT_WHITE("97");

    private final String code;

    private AnsiColor(String code) {
        this.code = code;
    }

    /**
     * Returns the code number of the color.
     * 
     * @return the code number of the color
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name() + "(" + code + ")";
    }

}
