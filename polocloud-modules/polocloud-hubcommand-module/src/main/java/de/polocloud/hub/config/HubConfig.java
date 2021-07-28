package de.polocloud.hub.config;

import de.polocloud.api.config.IConfig;

public class HubConfig implements IConfig {

    private Boolean use = true;
    private String[] aliases = new String[]{"l", "lobby", "leave"};
    private String noFallback = "§cEs konnte kein Fallback gefunden werden...";
    private String alreadyConnected = "§cDu bist bereits auf einer Lobby verbunden!";

    public Boolean getUse() {
        return use;
    }

    public String getNoFallback() {
        return noFallback;
    }

    public String getAlreadyConnected() {
        return alreadyConnected;
    }

    public String[] getAliases() {
        return aliases;
    }
}
