package de.polocloud.bootstrap.config.properties;

import de.polocloud.api.config.IConfig;

public class Properties implements IConfig {

    private String wrapperKey = "--Polo--";
    private String[] fallback = new String[]{"Lobby"};
    private boolean logPlayerConnections = true;
    private int maxSimultaneouslyStartingTemplates = 2;
    private int port = 8869;

    public int getPort() {
        return port;
    }

    public boolean isLogPlayerConnections() {
        return logPlayerConnections;
    }

    public int getMaxSimultaneouslyStartingTemplates() {
        return maxSimultaneouslyStartingTemplates;
    }

    public String getWrapperKey() {
        return wrapperKey;
    }

    public String[] getFallback() {
        return fallback;
    }
}
