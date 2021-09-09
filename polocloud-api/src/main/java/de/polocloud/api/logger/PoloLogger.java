package de.polocloud.api.logger;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.pool.PoloObject;

import java.io.File;
import java.util.List;

public interface PoloLogger extends PoloObject<PoloLogger> {

    /**
     * Shorter method to access the current {@link PoloLogger}
     * and instantly log some message
     *
     * @param level the level
     * @param message the message
     */
    static void print(LogLevel level, String message) {
        if (PoloCloudAPI.getInstance() == null) {
            System.out.println(message);
            return;
        }
        getInstance().log(level, message);
    }

    static void newLine() {
        print(LogLevel.INFO, "");
    }

    /**
     * Gets the default Logger instance
     */
    static PoloLogger getInstance() {
        String defaultLogger = "cloud" + PoloCloudAPI.getInstance().getType().getName();
        PoloLoggerFactory loggerFactory = PoloCloudAPI.getInstance().getLoggerFactory();
        return loggerFactory.getLoggerOrCreate(defaultLogger);
    }

    /**
     * Deletes the default {@link PoloLog} with id #1
     */
    void deleteDefaultLog();

    /**
     * The level of this logger
     */
    LogLevel getLevel();

    /**
     * The current {@link PoloLog} that's receiving
     * logs and is active to be saved later on
     *
     * @return log instance
     */
    PoloLog getCurrentLog();

    /**
     * The directory where all logs are stored
     */
    File getDirectory();

    /**
     * Gets a list of all {@link PoloLog}s from this logger
     * that were ever created manually or automatically
     */
    List<PoloLog> getAllLogs();

    /**
     * Gets a {@link PoloLog} by its name out of all cached logs
     *
     * @param name the name of the log
     * @return log or null if not found
     */
    PoloLog getLog(String name);

    /**
     * Creates a new {@link PoloLog} instance with a given name
     *
     * @param name the name
     * @return log instance
     */
    PoloLog createLog(String name);

    /**
     * Removes an existing {@link PoloLog}
     *
     * @param name the name
     */
    void removeLog(String name);

    /**
     * The prefix to be put before logs if
     * {@link PoloLogger#noPrefix()} has not been executed before
     *
     * @return prefix as String
     */
    String getPrefix();

    /**
     * Sets the prefix of this logger
     *
     * @param prefix the prefix
     */
    void setPrefix(String prefix);

    /**
     * Sets the {@link LogLevel} of this logger
     *
     * @param level the level
     */
    void setLevel(LogLevel level);

    /**
     * Disables to use prefix in the logger
     *
     * @return current logger instance
     */
    PoloLogger noPrefix();

    /**
     * Will directly save the next logging
     * message into the file and will not cache it until it's shut down
     *
     * @return current logger
     */
    PoloLogger saveNextLog();

    /**
     * This will directly log the next logging
     * and will not display it to the logging instance
     *
     * @return current logger
     */
    PoloLogger noDisplay();

    /**
     * Logs a message with a given type
     *
     * @param level the level to log
     * @param message the message to log
     */
    void log(LogLevel level, String message);

}
