package de.polocloud.server.config;

import de.polocloud.api.config.IConfig;

public class PoloCloudServerConfig implements IConfig {

    private String apiVersion = "0.1";
    private String boostrapVersion = "0.1";
    private String boostrapFileName = "bootstrap.jar";
    private String apiFileName = "PoloCloud-API.jar";

    public String getApiVersion() {
        return apiVersion;
    }

    public String getBoostrapVersion() {
        return boostrapVersion;
    }

    public String getBoostrapFileName() {
        return boostrapFileName;
    }

    public String getApiFileName() {
        return apiFileName;
    }

}
