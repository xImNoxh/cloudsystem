package de.polocloud.logger.log;

import de.polocloud.logger.log.file.LogFileService;

public class LogService {

    private static LogService logService;
    private final LogFileService logFileService;

    public LogService() {
        logService = this;
        logFileService = new LogFileService();

    }

    public static LogService getLogService() {
        return logService;
    }

    public LogFileService getLogFileService() {
        return logFileService;
    }

}
