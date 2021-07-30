package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.ExecutionException;

@CloudCommand.Info(
    name = "players",
    description = "manage players",
    aliases = "", commandType = CommandType.CONSOLE
)
public class PlayersCloudCommand extends CloudCommand {

    private ICloudPlayerManager playerManager;
    private IGameServerManager gameServerManager;

    public PlayersCloudCommand(ICloudPlayerManager playerManager, IGameServerManager gameServerManager) {
        this.playerManager = playerManager;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {

        if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("message") || args[1].equalsIgnoreCase("send")) {
                String targetName = args[2];
                String message = "";
                for (int i = 3; i < args.length; i++) {
                    message += args[i] + " ";
                }
                message = message.substring(0, message.length() - 1);
                try {
                    if (playerManager.isPlayerOnline(targetName).get()) {

                        if (args[1].equalsIgnoreCase("kick")) {
                            playerManager.getOnlinePlayer(targetName).get().kick(message);
                            Logger.log(LoggerType.INFO, "The player " + targetName + " was kicked!");
                        } else if (args[1].equalsIgnoreCase("message")) {
                            playerManager.getOnlinePlayer(targetName).get().sendMessage(message);
                            Logger.log(LoggerType.INFO, "sent!");
                        } else if (args[1].equalsIgnoreCase("send")) {

                            gameServerManager.getGameServerByName(message).thenAccept(server -> {
                                try {
                                    playerManager.getOnlinePlayer(targetName).get().sendTo(server);
                                    Logger.log(LoggerType.INFO, "sent!");
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            });

                        }


                    } else {
                        Logger.log(LoggerType.INFO, "The player " + targetName + " is not founded!");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }


            }
        } else {
            Logger.log(LoggerType.INFO, Logger.PREFIX + "players <kick/message/send> <player> <message/serverName>");
        }

    }
}
