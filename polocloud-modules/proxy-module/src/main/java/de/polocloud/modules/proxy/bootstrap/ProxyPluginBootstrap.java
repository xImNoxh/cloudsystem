package de.polocloud.modules.proxy.bootstrap;

import de.polocloud.modules.proxy.motd.MotdProxyService;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginBootstrap extends Plugin {

    private static ProxyPluginBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new MotdProxyService();
    }

    @Override
    public void onDisable() {

    }

    public static ProxyPluginBootstrap getInstance() {
        return instance;
    }
}
