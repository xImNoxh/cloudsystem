package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerCommand implements CommandListener, TabCompletable {

    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;

    public PlayerCommand(ICloudPlayerManager cloudPlayerManager, IGameServerManager gameServerManager) {
        this.cloudPlayerManager = cloudPlayerManager;
        this.gameServerManager = gameServerManager;
    }


    @Command(name = "player", description = "Manage a CloudPlayer", aliases = "players")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(min = 3, message = {"----[Player]----", "Use §3player <player> message <message...> §7to send a message to a player", "Use §3player <player> kick <message...> §7to kick a player with a message", "Use §3player <player> send <server> §7to send a player to a gameserver", "----[Player]----"}) String... params) {
        //player <player> kick/message/send <Lobby-1 | Du hast dumm>
        try {
            if (params.length == 3 && params[1].equalsIgnoreCase("send")) {
                String player = params[0];
                String server = params[2];
                ICloudPlayer cloudPlayer = cloudPlayerManager.getOnlinePlayer(player).get();
                if (cloudPlayer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The player » " + ConsoleColors.LIGHT_BLUE + player + ConsoleColors.GRAY + " isn't online!");
                    return;
                }
                IGameServer gameServer = gameServerManager.getGameServerByName(server).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + server + ConsoleColors.GRAY + " isn't online!");
                    return;
                }
                if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "You cannot send a player to a proxy server!");
                    return;
                }
                Logger.log(LoggerType.INFO, Logger.PREFIX + "sending...");
                cloudPlayer.sendTo(gameServer);
                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "sent player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY +
                    " to server » " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "!");
            } else if (params.length >= 3) {
                if (params[1].equalsIgnoreCase("kick") || params[1].equalsIgnoreCase("message")) {
                    String type = params[1];
                    String player = params[0];
                    ICloudPlayer cloudPlayer = cloudPlayerManager.getOnlinePlayer(player).get();
                    if (cloudPlayer == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The player » " + ConsoleColors.LIGHT_BLUE + player + ConsoleColors.GRAY + " isn't online!");
                        return;
                    }


                    StringBuilder message = new StringBuilder();
                    for (int i = 2; i < params.length; i++) {
                        message.append(params[i]).append(" ");
                    }
                    message = new StringBuilder(message.substring(0, message.length() - 1));

                    if (type.equalsIgnoreCase("kick")) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "kicking...");
                        cloudPlayer.kick(message.toString());
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "kicked player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY + "! (Message » " + ConsoleColors.LIGHT_BLUE + message + ConsoleColors.GRAY + ")");
                    } else if (type.equalsIgnoreCase("message")) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "messaging...");
                        cloudPlayer.sendMessage(message.toString());
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "messaged player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY + "! (Message » " + ConsoleColors.LIGHT_BLUE + message + ConsoleColors.GRAY + ")");
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
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "player <player> message <message...> " + ConsoleColors.GRAY + "to send a message to a player");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "player <player> kick <message...> " + ConsoleColors.GRAY + "to kick a player with a message");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "player <player> send <server> " + ConsoleColors.GRAY + "to send a player to a gameserver");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Player]----");
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {

        if (args.length >= 2 && args[1].equalsIgnoreCase("message")) {
            return new LinkedList<>();
        }

        if (args.length >= 2 && args[1].equalsIgnoreCase("kick")) {
            return new LinkedList<>();
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("send")) {
            List<String> serverNames = new LinkedList<>();
            for (IGameServer gameServer : PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getGameServerManager().getGameServers().get())) {
                serverNames.add(gameServer.getName());
            }
            return serverNames;
        }

        if (args.length == 1) {
            return Arrays.asList("message", "kick", "send");
        }

        if (args.length == 0) {
            List<String> names = new LinkedList<>();
            for (ICloudPlayer iCloudPlayer : PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get())) {
                names.add(iCloudPlayer.getName());
            }
            return names;
        }
        return new LinkedList<>();
    }
}
