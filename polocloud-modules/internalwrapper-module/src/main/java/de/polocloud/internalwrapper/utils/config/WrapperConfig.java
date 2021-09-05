package de.polocloud.internalwrapper.utils.config;

import de.polocloud.api.config.IConfig;

public class WrapperConfig implements IConfig {

    private final String wrapperName;
    private final boolean logServerScreens;

    public WrapperConfig() {
        this.wrapperName = "Wrapper-1";
        this.logServerScreens = false;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public boolean isLogServerScreens() {
        return logServerScreens;
    }
}
