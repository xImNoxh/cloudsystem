package de.polocloud.bootstrap.module;

import java.io.File;

public class ModuleData {

    private transient File file;

    private String name;

    private String version;

    private String author;

    private String main;

    public ModuleData() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getAuthor() {
        return author;
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
    }
}
