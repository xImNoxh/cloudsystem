package de.polocloud.logger.log.types;

public enum LoggerType {
    INFO(ConsoleColors.CYAN.getAnsiCode(), "Info"), WARNING(ConsoleColors.RED.getAnsiCode(), "Warning"),
    ERROR(ConsoleColors.RED.getAnsiCode(), "Error"), MEMORY(ConsoleColors.YELLOW.getAnsiCode(), "Memory");

    private final String consoleColors;
    private final String label;
    LoggerType(String consoleColors, String label) {
        this.consoleColors = consoleColors;
        this.label = label;
    }

    public String getConsoleColors() {
        return consoleColors;
    }

    public String getLabel() {
        return label;
    }
}
