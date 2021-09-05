package de.polocloud.logger.log.file;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.util.gson.PoloHelper;

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

    public static LogFileService getLogFileService() {
        return logFileService;
    }

    public void createLogDirectory() {
        FileConstants.LOGGER_FOLDER.mkdirs();
    }

    public void createCurrentLatestLog() {
        try {
            getLatestLog().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeLatestLog() {
        File file = getLatestLog();
        if (file.exists()) file.delete();
    }

    public File getLatestLog() {
        return new File(FileConstants.LOGGER_FOLDER, FileConstants.LOGGER_LATEST_NAME);
    }

    public File getCurrentDayLog() {
        new File(FileConstants.LOGGER_FOLDER, PoloHelper.getSimpleDate() + "/").mkdirs();
        File file = new File(PoloHelper.getSimpleDate() + "/services.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public LogFileWriter getLogFileWriter() {
        return logFileWriter;
    }
}
