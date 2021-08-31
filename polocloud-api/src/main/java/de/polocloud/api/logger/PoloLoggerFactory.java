package de.polocloud.api.logger;

import de.polocloud.api.logger.exception.LoggerAlreadyExistsException;

import java.io.File;
import java.util.List;

public interface PoloLoggerFactory {

    /**
     * Creates a {@link PoloLogger} with a given name to identify it later
     * and all the sub {@link PoloLog}s
     *
     * @param name the name of the logger
     * @return logger instance
     * @throws LoggerAlreadyExistsException if there is already a logger with this name
     */
    PoloLogger createLogger(String name) throws LoggerAlreadyExistsException;

    /**
     * Removes a {@link PoloLogger} with a given name
     *
     * @param name the name of the logger to remove
     */
    void removeLogger(String name);

    /**
     * Sets the prefix that every {@link PoloLogger} will receive
     *
     * @param prefix the prefix to set
     * @param except the logger names that won't receive this prefix
     */
    void setGlobalPrefix(String prefix, String... except);

    /**
     * Searches for a given {@link PoloLogger} with a certain name
     *
     * @param name the name
     * @return logger or null if not found
     */
    PoloLogger getLogger(String name);

    /**
     * Tries to call {@link PoloLoggerFactory#getLogger(String)}
     * and if its null it will create a new {@link PoloLogger} with the given name
     *
     * @param name the name of the logger
     * @return logger instance
     */
    PoloLogger getLoggerOrCreate(String name);

    /**
     * Gets all loaded {@link PoloLogger}s
     *
     * @return list of loggers
     */
    List<PoloLogger> getLoggers();

    /**
     * The directory of all loggers
     */
    File getLogDirectory();

    /**
     * Shuts down the logger factory
     * And runs the runnable if every logger is successfully
     * shut down and all logs were saved
     *
     * @param finishRunnable the runnable
     */
    void shutdown(Runnable finishRunnable);
}
