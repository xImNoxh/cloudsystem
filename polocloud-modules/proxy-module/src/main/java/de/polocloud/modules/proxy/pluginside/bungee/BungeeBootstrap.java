package de.polocloud.modules.proxy.pluginside.bungee;

import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.cloudside.ModuleBootstrap;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeBootstrap extends Plugin {

    /**
     * The module instance
     */
    private ProxyModule proxyModule;


    @Override
    public void onEnable() {
        this.proxyModule = new ProxyModule(new ModuleBootstrap());
        this.proxyModule.enable();
    }

    @Override
    public void onDisable() {
        this.proxyModule.shutdown();
    }

}
