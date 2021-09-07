package de.polocloud.modules.hubcommand.config;

import de.polocloud.api.config.IConfig;

public class HubCommandConfig implements IConfig {

    private String alreadyConnectedAtFallback;
    private String successfullyConnected;
    private String noFallbackServerFound;

    public HubCommandConfig() {
        this.alreadyConnectedAtFallback = "already on a fallback";
        this.successfullyConnected = "successfully connected to a fallback";
        this.noFallbackServerFound = "No fallback server found...";
    }

    public String getAlreadyConnectedAtFallback() {
        return alreadyConnectedAtFallback;
    }

    public String getSuccessfullyConnected() {
        return successfullyConnected;
    }

    public String getNoFallbackServerFound() {
        return noFallbackServerFound;
    }
}
