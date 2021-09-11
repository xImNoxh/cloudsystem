package de.polocloud.logger.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter {

    /**
     * Writes a message to the CurrentDay and to the Latest log
     * @param message to write in the logs
     */
    public void write(String message) {
        message = message.replaceAll("\u001B\\[[;\\d]*m", "");
        File[] files = {getLogService().getCurrentDayLog(), getLogService().getLatestLog()};
        for (File file : files) {
            try {
                BufferedWriter writer = newBufferedWriter(file);
                writer.write(message);
                writer.newLine();
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new instance of a {@link BufferedWriter} for a File
     * @param file for creating the {@link BufferedWriter}
     * @return the new instance of the {@link BufferedWriter}
     * @throws IOException if something goes wrong
     */
    public BufferedWriter newBufferedWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file, true));
    }

    public LogFileService getLogService() {
        return LogFileService.getLogFileService();
    }

}
