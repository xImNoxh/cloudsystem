package de.polocloud.bootstrap.config.properties;

public class Properties {

    private String wrapperKey = "--Polo--";
    private String[] fallback = new String[]{"Lobby"};
    private int maxSimultaneouslyStartingTemplates = 2;
    private int port = 8869;

    public int getPort() {
        return port;
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
