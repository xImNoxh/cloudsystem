package de.polocloud.api.logger.helper;

import de.polocloud.api.logger.PoloLogger;

public enum LogLevel {

    INFO,
    DEBUG,
    WARNING,
    ERROR,

    OFF,
    ALL;

    /**
     * Formats a logged line for a given logger and this type
     *
     * @param logger the logger
     * @param line the line
     * @return formatted string
     */
    public String format(PoloLogger logger, String line) {
        return "[" + logger.getName().toUpperCase() + "/" + this.name() + "] -> " + line;
    }
}
