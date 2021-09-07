package de.polocloud.modules.hubcommand.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.hubcommand.bootstrap.HubCommandProxyBootstrap;

public class HubCommand implements CommandListener {

    @Command(
        name = "hub",
        aliases = {"l", "lobby", "leave"},
        description = "A player hub command"
    )
    @CommandExecutors(ExecutorType.PLAYER)
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {

        if(HubCommandProxyBootstrap.getInstance().getHubCommandConfig() == null) return;

        ICloudPlayer cloudPlayer = (ICloudPlayer) executor;
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();

        if(PoloCloudAPI.getInstance().getFallbackManager().isOnFallback(cloudPlayer)) {
            cloudPlayer.sendMessage(prefix + HubCommandProxyBootstrap.getInstance().getHubCommandConfig().getAlreadyConnectedAtFallback());
            return;
        }

        IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);

        if(fallback == null){
            cloudPlayer.sendMessage(prefix + HubCommandProxyBootstrap.getInstance().getHubCommandConfig().getNoFallbackServerFound());
            return;
        }

        cloudPlayer.sendTo(fallback);
        cloudPlayer.sendMessage(HubCommandProxyBootstrap.getInstance().getHubCommandConfig().getSuccessfullyConnected());
    }

}
