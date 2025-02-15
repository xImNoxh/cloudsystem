package de.polocloud.server.threaded.exception;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionWriter {

    public void saveException(String date, String stackTrace) {
        new Thread(() -> {
            try {
                File file = new File("exceptions/" + "exception-" + date + ".txt");
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
