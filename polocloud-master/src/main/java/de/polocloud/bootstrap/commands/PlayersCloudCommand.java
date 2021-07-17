package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.CompletableFuture;

@CloudCommand.Info(
    name = "players",
    description = "manage players",
    aliases = ""
)
public class PlayersCloudCommand extends CloudCommand {

    private ICloudPlayerManager playerManager;
    private IGameServerManager gameServerManager;

    public PlayersCloudCommand(ICloudPlayerManager playerManager, IGameServerManager gameServerManager) {
        this.playerManager = playerManager;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(String[] args) {

        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("message") || args[1].equalsIgnoreCase("send")) {
                String targetName = args[2];
                String message = "";
                for (int i = 3; i < args.length; i++) {
                    message += args[i] + " ";
                }
                message = message.substring(0, message.length() - 1);
                if (playerManager.isPlayerOnline(targetName)) {

                    if (args[1].equalsIgnoreCase("kick")) {
                        playerManager.getOnlinePlayer(targetName).kick(message);
                        Logger.log(LoggerType.INFO, "Der Spieler " + targetName + " wurde gekickt!");
                    } else if (args[1].equalsIgnoreCase("message")) {
                        playerManager.getOnlinePlayer(targetName).sendMessage(message);
                        Logger.log(LoggerType.INFO, "sent!");
                    } else if (args[1].equalsIgnoreCase("send")) {

                        gameServerManager.getGameServerByName(message).thenAccept(server -> {
                            playerManager.getOnlinePlayer(targetName).sendTo(server);
                            Logger.log(LoggerType.INFO, "sent!");
                        });

                    }


                } else {
                    Logger.log(LoggerType.INFO, "Der Spieler " + targetName + " wurde nicht gefunden!");
                }


            }
        }

    }
}
