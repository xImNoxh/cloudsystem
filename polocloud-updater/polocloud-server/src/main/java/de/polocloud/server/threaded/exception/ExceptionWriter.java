package de.polocloud.server.threaded.exception;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionWriter {

    public void saveException(String ip, String date, String stackTrace) {
        new Thread(() -> {
            try {
                File file = new File("exceptions/" + "exception-" + date + "-" + ip.substring(0, 3) + ".txt");
                if (!file.exists() || stackTrace != null) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    PrintWriter out = new PrintWriter(file);
                    out.write("IP: " + ip + "\nDate: " + date + "\n" + stackTrace);
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
