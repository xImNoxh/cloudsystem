package de.polocloud.modules.proxy.bootstrap;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.proxy.command.ProxyCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginBootstrap extends Plugin {

    @Override
    public void onEnable() {
        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new ProxyCommand());
    }

    @Override
    public void onDisable() {

    }
}
