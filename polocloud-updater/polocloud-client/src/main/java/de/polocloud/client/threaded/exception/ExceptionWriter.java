package de.polocloud.client.threaded.exception;

import de.polocloud.api.config.FileConstants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionWriter {

    public static void saveException(String date, String stackTrace) {
        new Thread(() -> {
            try {
                File file = new File(FileConstants.GLOBAL_FOLDER, "data/reports/" + "exception-" + date + ".txt");
                if (!file.exists() || stackTrace != null) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    PrintWriter out = new PrintWriter(file);
                    out.write("\nDate: " + date + "\n" + stackTrace);
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save exception!");
            }
        }).start();
    }
}
