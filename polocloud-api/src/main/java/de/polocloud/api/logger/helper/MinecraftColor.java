package de.polocloud.api.logger.helper;

import org.fusesource.jansi.Ansi;

public enum MinecraftColor {

    RESET(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.DEFAULT).boldOff().toString(), 'f'),
    WHITE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString(), 'f'),
    BLACK(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString(), '0'),
    RED(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString(), 'c'),
    YELLOW(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString(), 'e'),
    BLUE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString(), '9'),
    GREEN(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString(), 'a'),
    PURPLE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString(), '5'),
    ORANGE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString(), '6'),
    GRAY(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString(), '7'),
    DARK_RED(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString(), '4'),
    DARK_GRAY( Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString(), '8'),
    DARK_BLUE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString(), '1'),
    DARK_GREEN(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString(), '2'),
    LIGHT_BLUE(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString(), '3'),
    CYAN(Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString(), 'b');

    private final String code;
    private final char c;

    MinecraftColor(String code, char c) {
        this.code = code;
        this.c = c;
    }

    public char getChar() {
        return c;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * Formats a String with Minecraft-Color-Codes and replaces them with
     * the correct {@link MinecraftColor}
     *
     * @param c the char before the index
     * @param input the input string to format
     * @return formatted string
     */
    public static String translateColorCodes(char c, String input) {
        for (MinecraftColor value : values()) {
            input = input.replace(String.valueOf(c) + value.getChar(), value.toString());
        }
        return input + MinecraftColor.RESET;
    }

    /**
     * Removes all the Minecraft-Color-Codes out of a given {@link String}
     *
     * @param s the input to be replaced
     * @return the formatted string without any color codes
     */
    public static String replaceColorCodes(String s) {
        for (MinecraftColor value : MinecraftColor.values()) {
            s = s.replace("§" + value.getChar(), "");
        }
        for (MinecraftColor value : MinecraftColor.values()) {
            s = s.replace(value.toString(), "");
        }
        return s;
    }
}
