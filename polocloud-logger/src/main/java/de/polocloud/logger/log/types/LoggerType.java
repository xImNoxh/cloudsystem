package de.polocloud.logger.log.types;

public enum LoggerType {
    INFO(ConsoleColors.CYAN, "Info"), WARNING(ConsoleColors.ORANGE, "Warning"),
    ERROR(ConsoleColors.RED, "Error"), MEMORY(ConsoleColors.YELLOW, "Memory");

    private final ConsoleColors consoleColors;
    private final String label;

    LoggerType(ConsoleColors consoleColors, String label) {
        this.consoleColors = consoleColors;
        this.label = label;
    }

    public ConsoleColors getConsoleColors() {
        return consoleColors;
    }

    public String getLabel() {
        return label;
    }
}
