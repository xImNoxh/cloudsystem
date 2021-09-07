package de.polocloud.modules.hubcommand.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
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
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();

        if(PoloCloudAPI.getInstance().getFallbackManager().isOnFallback(cloudPlayer)) {
            cloudPlayer.sendMessage(prefix + "§7Du bist bereits auf einem fallback");
            return;
        }
        if(PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks().isEmpty()){
            cloudPlayer.sendMessage(prefix + "§7Es konnte kein fallback gefunden werden...");
            return;
        }
        cloudPlayer.sendToFallback();
        cloudPlayer.sendMessage("§7Du wirst auf einem fallback service verschoben§8.");
    }

}
