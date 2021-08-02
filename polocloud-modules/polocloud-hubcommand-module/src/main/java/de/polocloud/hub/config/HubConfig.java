package de.polocloud.hub.config;

import de.polocloud.api.config.IConfig;

public class HubConfig implements IConfig {

    private Boolean use = true;
    private String[] aliases = new String[]{"l", "lobby", "leave"};
    private String noService = "§cEs wurde kein verfügbarer Fallback Server gefunden!";
    private String alreadyConnected = "§cDu bist bereits mit einem Fallback Server verbunden!";

    public Boolean getUse() {
        return use;
    }

    public String getAlreadyConnected() {
        return alreadyConnected;
    }

    public String getNoService() {
        return noService;
    }

    public String[] getAliases() {
        return aliases;
    }
}
