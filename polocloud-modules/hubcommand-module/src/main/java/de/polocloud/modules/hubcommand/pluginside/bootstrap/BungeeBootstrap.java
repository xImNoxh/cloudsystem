package de.polocloud.modules.hubcommand.pluginside.bootstrap;

import de.polocloud.modules.hubcommand.HubModule;
import de.polocloud.modules.hubcommand.cloudside.ModuleBootstrap;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeBootstrap extends Plugin {

    private HubModule hubCommandModule;

    @Override
    public void onEnable() {
        this.hubCommandModule = new HubModule(new ModuleBootstrap());
    }

    @Override
    public void onDisable() {

    }

}
