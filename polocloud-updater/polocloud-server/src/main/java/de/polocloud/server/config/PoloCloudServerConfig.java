package de.polocloud.server.config;

import de.polocloud.api.config.IConfig;

public class PoloCloudServerConfig implements IConfig {

    private double apiVersion = 0.1;
    private double boostrapVersion = 0.1;
    private String boostrapFileName = "boostrap.jar";
    private String apiFileName = "PoloCloud-Plugin.jar";

    public double getApiVersion() {
        return apiVersion;
    }

    public double getBoostrapVersion() {
        return boostrapVersion;
    }

    public String getBoostrapFileName() {
        return boostrapFileName;
    }

    public String getApiFileName() {
        return apiFileName;
    }

}
