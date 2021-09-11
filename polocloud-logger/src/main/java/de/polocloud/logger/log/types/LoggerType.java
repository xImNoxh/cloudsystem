package de.polocloud.logger.log.types;

/**
 * Class for declearing the different Types of a log
 */
public enum LoggerType {

    INFO(ConsoleColors.CYAN, "Info"), WARNING(ConsoleColors.ORANGE, "Warning"),
    PREFIX(ConsoleColors.CYAN, "Info"), DEBUG(ConsoleColors.GREEN, "Debug"),
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
