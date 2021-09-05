package de.polocloud.modules.proxy.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.modules.proxy.config.motd.ProxyMotdSettings;

public class ProxyConfig implements IConfig {

    private ProxyMotdSettings proxyMotdSettings;

    public ProxyConfig() {
        this.proxyMotdSettings = new ProxyMotdSettings();
    }

    public ProxyMotdSettings getProxyMotdSettings() {
        return proxyMotdSettings;
    }
}
