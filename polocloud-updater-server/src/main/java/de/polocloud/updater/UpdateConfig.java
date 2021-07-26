package de.polocloud.updater;

import de.polocloud.api.config.IConfig;

public class UpdateConfig implements IConfig {

    private String bootstrapVersion = "0.1";
    private String apiVersion = "0.1";

    private int bootstrapDownloadCount;
    private int apiDownloadCount;


    public String getBootstrapVersion() {
        return bootstrapVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void addBootstrapDownloadCount() {
        bootstrapDownloadCount = bootstrapDownloadCount + 1;
    }

    public void addApiDownloadCount() {
        apiDownloadCount = apiDownloadCount + 1;
    }

}
