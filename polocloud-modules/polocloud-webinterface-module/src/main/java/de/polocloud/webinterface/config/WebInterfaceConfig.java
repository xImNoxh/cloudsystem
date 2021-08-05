package de.polocloud.webinterface.config;

import de.polocloud.api.config.IConfig;

public class WebInterfaceConfig implements IConfig {

    private int port = 6988;

    public int getPort() {
        return port;
    }
}
