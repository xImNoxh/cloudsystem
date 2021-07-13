package de.polocloud.wrapper.config;

import de.polocloud.api.config.IConfig;

public class WrapperConfig implements IConfig {

    private String loginKey = "--Polo--";
    private String masterAddress = "127.0.0.1:8869";
    private String wrapperName = "Wrapper-1";

    private String apiVersion = "-1";

    public String getApiVersion() {
        return apiVersion;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public String getLoginKey() {
        return loginKey;
    }
}
