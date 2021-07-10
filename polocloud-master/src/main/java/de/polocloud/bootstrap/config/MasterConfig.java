package de.polocloud.bootstrap.config;

import de.polocloud.api.config.IConfig;

import java.io.*;

public class MasterConfig implements IConfig {

    private transient final File file = new File("config.json");

    private String loginKey = "--Polo--";
    private String fallbackServer = "Lobby";

    @Override
    public File getFile() {
        return this.file;
    }

    public String getFallbackServer() {
        return fallbackServer;
    }

    public String getLoginKey() {
        return loginKey;
    }
}
