package de.polocloud.wrapper.config.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class ServiceProperties {

    private File file;
    private int port;
    private String[] properties;

    public ServiceProperties(File file, String child, int port) {
        this.file = new File(file.getPath(), child);

        this.port = port;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public void writeFile() {
        try {
            FileWriter fileWriter = new FileWriter(file);

            for (String line : properties) {
                fileWriter.write(line + "\n");
            }
            fileWriter.close();

        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }


    public int getPort() {
        return port;
    }
}
