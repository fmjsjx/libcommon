package com.github.fmjsjx.libcommon.util.ansi;

/**
 * {@code ANSI} color string.
 */
public class AnsiColorString {

    private final AnsiColor color;
    private final String value;

    /**
     * Create a new {@link AnsiColorString} instance with the specified
     * {@code color} and string.
     * 
     * @param color the {@code ANSI} color
     * @param value the string value
     */
    public AnsiColorString(AnsiColor color, String value) {
        this.color = color;
        this.value = value;
    }

    /**
     * Create a new {@link AnsiColorString} instance with the default {@code color}
     * and the specified string.
     * 
     * @param value the string value
     */
    public AnsiColorString(String value) {
        this(AnsiColor.DEFAULT, value);
    }

    /**
     * Return the {@code ANSI} color.
     * 
     * @return an {@code AnsiColor}
     */
    public AnsiColor color() {
        return color;
    }

    /**
     * Returns the string value.
     * 
     * @return the string value
     */
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return appendTo(new StringBuilder()).toString();
    }

    /**
     * Appends this {@code ANSI} color string to the end of the specified string
     * builder.
     * 
     * @param builder the string builder
     * @return the input string builder instance
     */
    public StringBuilder appendTo(StringBuilder builder) {
        builder.append("\u001B[").append(color.getCode()).append("m").append(value);
        if (color != AnsiColor.DEFAULT) {
            builder.append("\u001B[").append(AnsiColor.DEFAULT.getCode()).append("m");
        }
        return builder;
    }

}
