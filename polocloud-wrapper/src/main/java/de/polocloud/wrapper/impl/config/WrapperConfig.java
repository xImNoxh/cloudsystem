package de.polocloud.wrapper.impl.config;

import de.polocloud.api.config.IConfig;

import java.util.ArrayList;
import java.util.List;

public class WrapperConfig implements IConfig {

    private String loginKey;
    private String masterAddress;
    private String wrapperName;
    private boolean logServerOutput;

    private String apiVersion;

    public WrapperConfig() {
        this.apiVersion = "-1";
        this.wrapperName = "Wrapper-1";
        this.masterAddress = "127.0.0.1:8869";
        this.loginKey = "--Polo--";
        this.logServerOutput = true;
    }

    public boolean isLogServerOutput() {
        return logServerOutput;
    }

    public void setLogServerOutput(boolean logServerOutput) {
        this.logServerOutput = logServerOutput;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String fetchedVersion) {
        this.apiVersion = fetchedVersion;
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
