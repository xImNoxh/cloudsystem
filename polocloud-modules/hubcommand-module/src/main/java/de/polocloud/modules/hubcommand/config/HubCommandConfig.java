package de.polocloud.modules.hubcommand.config;

import de.polocloud.api.config.IConfig;

public class HubCommandConfig implements IConfig {

    private final String alreadyConnectedAtFallback;
    private final String successfullyConnected;
    private final String noFallbackServerFound;

    public HubCommandConfig() {
        this.alreadyConnectedAtFallback = "§7You are already on a §bfallback§7!";
        this.successfullyConnected = "§7You were §asuccessfully §7connected to a §bfallback§7!";
        this.noFallbackServerFound = "§cNo §bfallback §7was found for you...";
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
