package de.polocloud.modules.proxy.bootstrap;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.command.ProxyCommand;
import de.polocloud.modules.proxy.motd.MotdProxyService;
import de.polocloud.modules.proxy.whitelist.WhitelistProxyService;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginBootstrap extends Plugin {

    private static ProxyPluginBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new MotdProxyService();
        new WhitelistProxyService();

        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new ProxyCommand());

    }

    @Override
    public void onDisable() {

    }

    public static ProxyPluginBootstrap getInstance() {
        return instance;
    }
}
