package de.polocloud.modules.proxy.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.modules.proxy.config.motd.ProxyMotdSettings;
import de.polocloud.modules.proxy.config.notify.NotifyConfig;
import de.polocloud.modules.proxy.config.tablist.TablistConfig;

public class ProxyConfig implements IConfig {

    private ProxyMotdSettings proxyMotdSettings;
    private NotifyConfig notifyConfig;
    private TablistConfig tablistConfig;

    public ProxyConfig() {
        this.proxyMotdSettings = new ProxyMotdSettings();
        this.notifyConfig = new NotifyConfig(
            true,
            "cloud.notify",
            "§7The service §e%service% §7is now §6§lstarted§8...",
            "§7The service §e%service% §7is now §a§lonline§8.",
            "§7The service §e%service% §7will §c§lstopped§8.");

        this.tablistConfig = new TablistConfig("Test123","Polo is toll");
    }

    public TablistConfig getTablistConfig() {
        return tablistConfig;
    }

    public ProxyMotdSettings getProxyMotdSettings() {
        return proxyMotdSettings;
    }

    public NotifyConfig getNotifyConfig() {
        return notifyConfig;
    }
}
