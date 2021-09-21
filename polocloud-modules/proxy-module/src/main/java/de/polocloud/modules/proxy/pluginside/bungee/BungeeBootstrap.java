package de.polocloud.modules.proxy.pluginside.bungee;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.cloudside.ModuleBootstrap;
import de.polocloud.modules.proxy.pluginside.global.command.ProxyCommand;
import de.polocloud.modules.proxy.pluginside.global.listener.WhitelistListener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeBootstrap extends Plugin {

    /**
     * The module instance
     */
    private final ProxyModule proxyModule;

    public BungeeBootstrap() {
        this.proxyModule = new ProxyModule(new ModuleBootstrap());
    }

    @Override
    public void onEnable() {
        this.proxyModule.enable();
    }

    @Override
    public void onDisable() {
        this.proxyModule.shutdown();
    }

}
