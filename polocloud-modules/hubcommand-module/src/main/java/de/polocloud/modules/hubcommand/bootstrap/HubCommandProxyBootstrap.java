package de.polocloud.modules.hubcommand.bootstrap;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.hubcommand.commands.HubCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class HubCommandProxyBootstrap extends Plugin {

    @Override
    public void onEnable() {
        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new HubCommand());
    }

    @Override
    public void onDisable() {

    }
}
