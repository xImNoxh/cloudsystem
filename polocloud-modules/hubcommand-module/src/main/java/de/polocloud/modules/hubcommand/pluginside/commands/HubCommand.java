package de.polocloud.modules.hubcommand.pluginside.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;

public class HubCommand implements CommandListener {

    @Command(
        name = "hub",
        aliases = {"l", "lobby", "leave"},
        description = "A player hub command"
    )
    @CommandExecutors(ExecutorType.PLAYER)
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {

        ICloudPlayer cloudPlayer = (ICloudPlayer) executor;

        if (PoloCloudAPI.getInstance().getFallbackManager().isOnFallback(cloudPlayer)) {
            cloudPlayer.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getAlreadyOnFallback());
            return;
        }

        IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);

        if (fallback == null){
            cloudPlayer.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNoFallbackServer());
            return;
        }

        cloudPlayer.sendTo(fallback);
        if (PoloCloudAPI.getInstance().getMasterConfig().getMessages().getSuccessfullyConnected().trim().isEmpty()) {
            return;
        }
        cloudPlayer.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getSuccessfullyConnected().replace("%server%", fallback.getName()));
    }

}
