package de.polocloud.api.logger.def;

import de.polocloud.api.logger.PoloLog;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.PoloLoggerFactory;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimplePoloLogger implements PoloLogger {

    /**
     * The name of the logger
     */
    private final String name;

    /**
     * The id of the logger
     */
    private final long snowflake;

    /**
     * All cached logs
     */
    private final List<PoloLog> poloLogs;

    /**
     * The current log
     */
    private final PoloLog currentLog;

    /**
     * The prefix
     */
    private String prefix;

    /**
     * The level
     */
    private LogLevel level;

    /**
     * If prefix should be appended to message
     */
    private boolean appendPrefix;

    /**
     * The directory
     */
    private final File logDirectory;

    public SimplePoloLogger(String name, PoloLoggerFactory loggerFactory) {
        this.name = name;

        this.snowflake = Snowflake.getInstance().nextId();

        this.prefix = "";
        this.level = LogLevel.ALL;
        this.appendPrefix = true;

        this.logDirectory = new File(loggerFactory.getLogDirectory(), name + "/");
        this.logDirectory.mkdirs();

        this.poloLogs = this.loadLogs();
        this.currentLog = this.createNewLog();

    }

    private PoloLog createNewLog() {
        String name = "[#" + (this.poloLogs.size() + 1) + "] - " + PoloHelper.getSimpleDate();
        return new SimplePoloLog(name, this);
    }

    private List<PoloLog> loadLogs() {
        List<PoloLog> poloLogs = new ArrayList<>();

        //Some os may return null if dir is empty
        if (this.logDirectory.listFiles() != null) {
            for (File file : Objects.requireNonNull(this.logDirectory.listFiles())) {
                //All logger-based directories

                String name = file.getName().split(".log")[0];
                PoloLog poloLog = new SimplePoloLog(name, this);
                poloLogs.add(poloLog);
            }
        }
        return poloLogs;
    }


    @Override
    public PoloLog getLog(String name) {
        return this.poloLogs.stream().filter(poloLog -> poloLog.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public PoloLog createLog(String name) {
        PoloLog poloLog = new SimplePoloLog(name, this);
        this.poloLogs.add(poloLog);
        return poloLog;
    }

    @Override
    public void removeLog(String name) {
        this.poloLogs.removeIf(poloLog -> poloLog.getName().equalsIgnoreCase(name));
    }

    @Override
    public PoloLog getCurrentLog() {
        return this.currentLog;
    }

    @Override
    public File getDirectory() {
        return this.logDirectory;
    }

    @Override
    public List<PoloLog> getAllLogs() {
        return poloLogs;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    @Override
    public PoloLogger noPrefix() {
        this.appendPrefix = false;
        return this;
    }

    @Override
    public PoloLogger noDisplay() {
        this.currentLog.noDisplay();
        return this;
    }

    @Override
    public PoloLogger saveNextLog() {
        this.currentLog.saveNextLog();
        return this;
    }

    @Override
    public void deleteDefaultLog() {
        for (PoloLog poloLog : this.poloLogs) {
            if (poloLog.getName().startsWith("[#1]")) {
                poloLog.clear();
                try {
                    poloLog.delete();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void log(LogLevel level, String message) {

        if (this.appendPrefix) {
            message = prefix + message;
        } else {
            this.appendPrefix = true;
        }

        this.currentLog.log(level, message);
    }

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }
}
