package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.TemplateType;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.concurrent.ExecutionException;

@CloudCommand.Info(name = "player", description = "Manage a CloudPlayer", aliases = "players", commandType = CommandType.CONSOLE)
public class PlayerCommand extends CloudCommand {

    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;

    public PlayerCommand(ICloudPlayerManager cloudPlayerManager, IGameServerManager gameServerManager) {
        this.cloudPlayerManager = cloudPlayerManager;
        this.gameServerManager = gameServerManager;
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        //player <player> kick/message/send <Lobby-1 | Du hast dumm>
        try {
            if (args.length == 4 && args[2].equalsIgnoreCase("send")) {
                String player = args[1];
                String server = args[3];
                ICloudPlayer cloudPlayer = cloudPlayerManager.getOnlinePlayer(player).get();
                if (cloudPlayer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The player » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + player + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                    return;
                }
                IGameServer gameServer = gameServerManager.getGameServerByName(server).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + server + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                    return;
                }
                if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "You cannot send a player to a proxy server!");
                    return;
                }
                Logger.log(LoggerType.INFO, Logger.PREFIX + "sending...");
                cloudPlayer.sendTo(gameServer);
                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "sent player » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + cloudPlayer.getName() + ConsoleColors.GRAY.getAnsiCode() +
                    " to server » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() + ConsoleColors.GRAY.getAnsiCode() + "!");
            } else if (args.length >= 4) {
                if (args[2].equalsIgnoreCase("kick") || args[2].equalsIgnoreCase("message")) {
                    String type = args[2];
                    String player = args[1];
                    ICloudPlayer cloudPlayer = cloudPlayerManager.getOnlinePlayer(player).get();
                    if (cloudPlayer == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The player » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + player + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                        return;
                    }


                    String message = "";
                    for (int i = 3; i < args.length; i++) {
                        message += args[i] + " ";
                    }
                    message = message.substring(0, message.length() - 1);


                    if (type.equalsIgnoreCase("kick")) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "kicking...");
                        cloudPlayer.kick(message);
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "kicked player » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + cloudPlayer.getName() + ConsoleColors.GRAY.getAnsiCode() + "! (Message » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + message + ConsoleColors.GRAY.getAnsiCode() + ")");
                    } else if (type.equalsIgnoreCase("message")) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "messaging...");
                        cloudPlayer.sendMessage(message);
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully " + ConsoleColors.GRAY.getAnsiCode() + "messaged player » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + cloudPlayer.getName() + ConsoleColors.GRAY.getAnsiCode() + "! (Message » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + message + ConsoleColors.GRAY.getAnsiCode() + ")");
                    }
                } else {
                    sendHelp();
                }
            } else {
                sendHelp();
            }
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }


    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Player]----");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "player <player> message <message...> " + ConsoleColors.GRAY.getAnsiCode() + "to send a message to a player");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "player <player> kick <message...> " + ConsoleColors.GRAY.getAnsiCode() + "to kick a player with a message");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "player <player> send <server> " + ConsoleColors.GRAY.getAnsiCode() + "to send a player to a gameserver");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Player]----");
    }
}
