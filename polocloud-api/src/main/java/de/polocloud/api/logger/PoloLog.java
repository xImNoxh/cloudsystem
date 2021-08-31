package de.polocloud.api.logger;

import de.polocloud.api.logger.helper.LogHandler;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.pool.PoloObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public interface PoloLog extends PoloObject<PoloLog> {

    /**
     * The time the log was started
     *
     * @return time in millis as long
     */
    long getStartTime();

    /**
     * Will directly save the next logging
     * message into the file and will not cache it until it's shut down
     *
     * @return current log
     */
    PoloLog saveNextLog();

    /**
     * Adds a handler to the logger
     * to determine what to do with the logged message
     * if you want to simply print it or do something else with it
     *
     * @param printHandler the handler
     */
    void addPrintHandler(LogHandler printHandler);

    /**
     * This will directly log the next logging
     * and will not display it to the logging instance
     *
     * @return current log
     */
    PoloLog noDisplay();

    /**
     * Checks if this log is archived and was
     * re-cached and is not created within the last session
     */
    boolean isArchived();

    /**
     * The file of the log
     */
    File getFile();

    /**
     * All lines that were logged with their {@link LogLevel}
     * And the lines are instance of {@link String}
     *
     * @return map containing level and line
     */
    Map<String, LogLevel> getLoggedLines();

    /**
     * Logs something to this log
     *
     * @param level the level
     * @param line the line
     */
    void log(LogLevel level, String line);

    /**
     * Clears the log
     */
    void clear();

    /**
     * Saves this log to a file
     */
    void save();

    /**
     * Deletes this log and all its content from all caches
     *
     * @throws FileNotFoundException if the file was not created yet or simply not found
     */
    void delete() throws FileNotFoundException;
}
