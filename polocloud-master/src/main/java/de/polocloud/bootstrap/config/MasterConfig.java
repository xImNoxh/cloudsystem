package de.polocloud.bootstrap.config;

import de.polocloud.api.config.IConfig;

public class MasterConfig implements IConfig {

    private String loginKey = "--Polo--";
    private String fallbackServer = "Lobby";
    private String maintenanceMessage = "§7This §b§lservice §7is in maintenance§8.";

    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }

    public String getFallbackServer() {
        return fallbackServer;
    }

    public String getLoginKey() {
        return loginKey;
    }
}
