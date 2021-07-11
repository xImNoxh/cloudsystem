package de.polocloud.bootstrap.config;

import de.polocloud.api.config.IConfig;

public class MasterConfig implements IConfig {

    private String loginKey = "--Polo--";
    private String fallbackServer = "Lobby";
    private String cloudMotdFirstLine = "PoloCloud";
    private String cloudMotdSecondLine = "v1.01";

    public String getFallbackServer() {
        return fallbackServer;
    }

    public String getCloudMotdFirstLine() {
        return cloudMotdFirstLine;
    }

    public String getCloudMotdSecondLine() {
        return cloudMotdSecondLine;
    }

    public String getLoginKey() {
        return loginKey;
    }
}
