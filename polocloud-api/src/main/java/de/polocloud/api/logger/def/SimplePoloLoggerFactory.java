package de.polocloud.api.logger.def;

import de.polocloud.api.logger.PoloLog;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.PoloLoggerFactory;
import de.polocloud.api.logger.exception.LoggerAlreadyExistsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplePoloLoggerFactory implements PoloLoggerFactory {

    /**
     * All cached loggers
     */
    private final List<PoloLogger> loggers;

    /**
     * The logging directory
     */
    private final File logDirectory;

    /**
     * The global prefix
     */
    private String globalPrefix;

    public SimplePoloLoggerFactory(File logDirectory) {
        this.logDirectory = logDirectory;
        this.loggers = new ArrayList<>();
        this.globalPrefix = "";

        this.logDirectory.mkdirs();
    }

    @Override
    public void setGlobalPrefix(String prefix, String... except) {
        this.globalPrefix = prefix;
        for (PoloLogger logger : this.loggers) {
            if (!Arrays.asList(except).contains(logger.getName())) {
                logger.setPrefix(prefix);
            }
        }
    }

    @Override
    public void shutdown(Runnable finishRunnable) {
        if (this.loggers.isEmpty()) {
            finishRunnable.run();
        }
        int size = this.loggers.size();
        for (PoloLogger logger : new ArrayList<>(this.loggers)) {

            List<PoloLog> allLogs = logger.getAllLogs();
            allLogs.add(logger.getCurrentLog()); //Adding current log

            for (PoloLog allLog : new ArrayList<>(allLogs)) {
                if (!allLog.isArchived()) {
                    allLog.save();
                    if ((size = (size - 1)) <= 0) {
                        finishRunnable.run();
                    }
                }
            }
        }
    }

    @Override
    public PoloLogger getLoggerOrCreate(String name) {
        PoloLogger logger = getLogger(name);
        return logger == null ? createLogger(name) : logger;
    }

    @Override
    public PoloLogger createLogger(String name) throws LoggerAlreadyExistsException {
        if (this.getLogger(name) != null) {
            throw new LoggerAlreadyExistsException("The logger with name '" + name + "' already exists! Maybe it was created by another Java-Process?");
        }
        PoloLogger logger = new SimplePoloLogger(name, this);
        logger.setPrefix(this.globalPrefix);
        this.loggers.add(logger);
        return logger;
    }

    @Override
    public void removeLogger(String name) {
        PoloLogger logger = this.getLogger(name);
        if (logger == null) {
            return;
        }
        for (PoloLog poloLog : logger.getAllLogs()) {
            poloLog.clear();
            try {
                poloLog.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        this.loggers.remove(logger);
    }

    @Override
    public PoloLogger getLogger(String name) {
        return this.loggers.stream().filter(poloLogger -> poloLogger.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public List<PoloLogger> getLoggers() {
        return loggers;
    }

    @Override
    public File getLogDirectory() {
        return logDirectory;
    }
}
