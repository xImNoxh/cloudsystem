package de.polocloud.logger.log.file;

import de.polocloud.logger.log.Logger;

import java.io.File;
import java.io.IOException;

public class LogFileService {

    private static LogFileService logFileService;
    private LogFileWriter logFileWriter;

    public LogFileService() {
        logFileService = this;
        logFileWriter = new LogFileWriter();

        createLogDirectory();

        removeLatestLog();
        createCurrentLatestLog();
    }

    public void createLogDirectory() {
        new File("log").mkdirs();
    }

    public void createCurrentLatestLog() {
        try {
            new File("log/latest.log").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeLatestLog() {
        File file = new File("log/latest.log");
        if (file.exists()) file.delete();
    }

    public File getLatestLog() {
        return new File("log/latest.log");
    }

    public File getCurrentDayLog() {
        new File("log/" + Logger.getSimpleDate()).mkdirs();
        File file = new File("log/" + Logger.getSimpleDate() + "/services.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static LogFileService getLogFileService() {
        return logFileService;
    }

    public LogFileWriter getLogFileWriter() {
        return logFileWriter;
    }
}
