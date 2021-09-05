package de.polocloud.server.config;

import de.polocloud.api.config.IConfig;

public class PoloCloudServerStatisticsConfig implements IConfig {

    private int requestedStatusAmount = 0;
    private int requestedAPIDownloads = 0;
    private int requestedBoostrapDownloads = 0;

    public int getRequestedStatusAmount() {
        return requestedStatusAmount;
    }

    public void setRequestedStatusAmount(int requestedStatusAmount) {
        this.requestedStatusAmount = requestedStatusAmount;
    }

    public int getRequestedAPIDownloads() {
        return requestedAPIDownloads;
    }

    public void setRequestedAPIDownloads(int requestedAPIDownloads) {
        this.requestedAPIDownloads = requestedAPIDownloads;
    }

    public int getRequestedBoostrapDownloads() {
        return requestedBoostrapDownloads;
    }

    public void setRequestedBoostrapDownloads(int requestedBoostrapDownloads) {
        this.requestedBoostrapDownloads = requestedBoostrapDownloads;
    }
}
