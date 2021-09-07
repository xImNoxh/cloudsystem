package de.polocloud.modules.hubcommand.config;

import de.polocloud.api.config.IConfig;

public class HubCommandConfig implements IConfig {

    private String alreadyConnectedAtFallback;
    private String successfullyConnected;
    private String noFallbackServerFound;

    public HubCommandConfig(String alreadyConnectedAtFallback, String successfullyConnected, String noFallbackServerFound) {
        this.alreadyConnectedAtFallback = alreadyConnectedAtFallback;
        this.successfullyConnected = successfullyConnected;
        this.noFallbackServerFound = noFallbackServerFound;
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
