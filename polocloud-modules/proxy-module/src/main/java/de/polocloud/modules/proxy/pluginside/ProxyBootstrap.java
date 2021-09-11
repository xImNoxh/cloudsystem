package de.polocloud.modules.proxy.pluginside;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.cloudside.ModuleBootstrap;
import de.polocloud.modules.proxy.pluginside.command.ProxyCommand;
import de.polocloud.modules.proxy.pluginside.listener.WhitelistListener;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyBootstrap extends Plugin {

    /**
     * The module instance
     */
    private final ProxyModule proxyModule;

    public ProxyBootstrap() {
        this.proxyModule = new ProxyModule(new ModuleBootstrap());
    }

    @Override
    public void onEnable() {

        this.proxyModule.enable();

        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new ProxyCommand());
        PoloCloudAPI.getInstance().getEventManager().registerListener(new WhitelistListener());
    }

    @Override
    public void onDisable() {
        this.proxyModule.shutdown();
    }

}
