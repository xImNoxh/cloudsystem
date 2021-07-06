package de.polocloud.logger.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter {

    public void write(String message) {
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

    public LogFileService getLogService(){
        return LogFileService.getLogFileService();
    }

    public BufferedWriter newBufferedWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file, true));
    }

}
