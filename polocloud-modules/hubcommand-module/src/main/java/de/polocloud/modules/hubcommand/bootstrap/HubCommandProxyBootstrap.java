package de.polocloud.modules.hubcommand.bootstrap;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.modules.hubcommand.channel.HubCommandMessageChannel;
import de.polocloud.modules.hubcommand.commands.HubCommand;
import de.polocloud.modules.hubcommand.config.HubCommandConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class HubCommandProxyBootstrap extends Plugin {

    private static HubCommandProxyBootstrap instance;

    private HubCommandConfig hubCommandConfig;

    @Override
    public void onEnable() {
        instance = this;
        HubCommandMessageChannel hubCommandMessageChannel = new HubCommandMessageChannel();
        hubCommandMessageChannel.getMessageChannel().registerListener((hubCommandConfigWrappedObject, startTime) ->
            this.hubCommandConfig = hubCommandConfigWrappedObject.unwrap(HubCommandConfig.class));

        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new HubCommand());
    }

    public static HubCommandProxyBootstrap getInstance() {
        return instance;
    }

    public HubCommandConfig getHubCommandConfig() {
        return hubCommandConfig;
    }

    @Override
    public void onDisable() {

    }
}
