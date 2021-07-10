package de.polocloud.bootstrap.config;

import de.polocloud.api.config.IConfig;

public class MasterConfig implements IConfig {

    private String loginKey = "--Polo--";
    private String fallbackServer = "Lobby";

    public String getFallbackServer() {
        return fallbackServer;
    }

    public String getLoginKey() {
        return loginKey;
    }
}
