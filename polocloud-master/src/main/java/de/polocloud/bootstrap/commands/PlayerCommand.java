package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlayerCommand implements CommandListener, TabCompletable {

    private ICloudPlayerManager cloudPlayerManager;
    private IGameServerManager gameServerManager;

    public PlayerCommand(ICloudPlayerManager cloudPlayerManager, IGameServerManager gameServerManager) {
        this.cloudPlayerManager = cloudPlayerManager;
        this.gameServerManager = gameServerManager;
    }


    @Command(name = "player", description = "Manage a CloudPlayer", aliases = "players")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(min = 2, message = {"----[Player]----", "Use §3player <player> message <message...> §7to send a message to a player", "Use §3player <player> kick <message...> §7to kick a player with a message", "Use §3player <player> send <server> §7to send a player to a gameserver", "Use §3player <player> info §7to display info about a player", "----[Player]----"}) String... params) {
        if (params.length == 3 && params[1].equalsIgnoreCase("send")) {
            String player = params[0];
            String server = params[2];
            ICloudPlayer cloudPlayer = cloudPlayerManager.getCached(player);
            if (cloudPlayer == null) {
                PoloLogger.print(LogLevel.WARNING, "The player » " + ConsoleColors.LIGHT_BLUE + player + ConsoleColors.GRAY + " isn't online!");
                return;
            }
            IGameServer gameServer = gameServerManager.getCached(server);
            if (gameServer == null) {
                PoloLogger.print(LogLevel.WARNING, "The gameserver » " + ConsoleColors.LIGHT_BLUE + server + ConsoleColors.GRAY + " isn't online!");
                return;
            }
            if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                PoloLogger.print(LogLevel.INFO, "You cannot send a player to a proxy server!");
                return;
            }
            PoloLogger.print(LogLevel.INFO, "sending...");
            cloudPlayer.sendTo(gameServer);
            PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "sent player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY +
                " to server » " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "!");
        } else if (params.length == 2 && params[1].equalsIgnoreCase("info")) {
            String player = params[0];
            ICloudPlayer cloudPlayer = cloudPlayerManager.getCached(player);
            if (cloudPlayer == null) {
                PoloLogger.print(LogLevel.WARNING, "The player » " + ConsoleColors.LIGHT_BLUE + player + ConsoleColors.GRAY + " isn't online!");
                return;
            }

            PoloLogger.print(LogLevel.INFO, "----[Player]----");
            PoloLogger.print(LogLevel.INFO, "§7Name §7: §b" + cloudPlayer.getName());
            PoloLogger.print(LogLevel.INFO, "§7UUID §7: §b" + cloudPlayer.getUUID());
            PoloLogger.print(LogLevel.INFO, "§7Proxy §7: §b" + (cloudPlayer.getProxyServer() == null ? "Logging in..." : cloudPlayer.getProxyServer().getName()));
            PoloLogger.print(LogLevel.INFO, "§7Server §7: §b" + (cloudPlayer.getMinecraftServer() == null ? "Logging in..." : cloudPlayer.getMinecraftServer().getName()));
            IPlayerSettings settings = cloudPlayer.getSettings();
            if (settings != null) {
                PoloLogger.print(LogLevel.INFO, "§7Settings §7:");
                PoloLogger.print(LogLevel.INFO, "  §8> §7Locale §7: §b" + settings.getLocale().getDisplayName());
                PoloLogger.print(LogLevel.INFO, "  §8> §7ChatMode §7: §b" + settings.getChatMode());
                PoloLogger.print(LogLevel.INFO, "  §8> §7Main-Hand §7: §b" + settings.getMainHand());
                PoloLogger.print(LogLevel.INFO, "  §8> §7RenderDistance §7: §b" + settings.getRenderDistance());
            }
            IPlayerConnection connection = cloudPlayer.getConnection();
            if (connection != null) {
                PoloLogger.print(LogLevel.INFO, "§7Connection §7:");
                PoloLogger.print(LogLevel.INFO, "  §8> §7Ping §7: §b" + cloudPlayer.getPing() + "ms");
                PoloLogger.print(LogLevel.INFO, "  §8> §7Version §7: §b" + connection.getVersion().getName() + " (Id: " + connection.getVersion().getProtocolId() + ")");
                PoloLogger.print(LogLevel.INFO, "  §8> §7Address §7: §b" + connection.getAddress().getAddress().getHostAddress() + ":" +connection.getPort());
                PoloLogger.print(LogLevel.INFO, "  §8> §7OnlineMode §7: §b" + connection.isOnlineMode());
                PoloLogger.print(LogLevel.INFO, "  §8> §7Legacy §b" + connection.isLegacy());
            }
            PoloLogger.print(LogLevel.INFO, "----[/Player]----");

        } else if (params.length >= 3) {
            if (params[1].equalsIgnoreCase("kick") || params[1].equalsIgnoreCase("message")) {
                String type = params[1];
                String player = params[0];
                ICloudPlayer cloudPlayer = cloudPlayerManager.getCached(player);
                if (cloudPlayer == null) {
                    PoloLogger.print(LogLevel.WARNING, "The player » " + ConsoleColors.LIGHT_BLUE + player + ConsoleColors.GRAY + " isn't online!");
                    return;
                }

                StringBuilder message = new StringBuilder();
                for (int i = 2; i < params.length; i++) {
                    message.append(params[i]).append(" ");
                }
                message = new StringBuilder(message.substring(0, message.length() - 1));

                if (type.equalsIgnoreCase("kick")) {
                    PoloLogger.print(LogLevel.INFO, "kicking...");
                    cloudPlayer.kick(message.toString());
                    PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "kicked player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY + "! (Message » " + ConsoleColors.LIGHT_BLUE + message + ConsoleColors.GRAY + ")");
                } else if (type.equalsIgnoreCase("message")) {
                    PoloLogger.print(LogLevel.INFO, "messaging...");
                    cloudPlayer.sendMessage(message.toString());
                    PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "messaged player » " + ConsoleColors.LIGHT_BLUE + cloudPlayer.getName() + ConsoleColors.GRAY + "! (Message » " + ConsoleColors.LIGHT_BLUE + message + ConsoleColors.GRAY + ")");
                }
            } else {
                sendHelp();
            }
        } else {
            sendHelp();
        }
    }


    private void sendHelp() {
        PoloLogger.print(LogLevel.INFO, "----[Player]----");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "player <player> message <message...> " + ConsoleColors.GRAY + "to send a message to a player");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "player <player> kick <message...> " + ConsoleColors.GRAY + "to kick a player with a message");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "player <player> send <server> " + ConsoleColors.GRAY + "to send a player to a gameserver");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "player <player> info " + ConsoleColors.GRAY + "to get info about a player");
        PoloLogger.print(LogLevel.INFO, "----[/Player]----");
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
            for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached()) {
                serverNames.add(gameServer.getName());
            }
            return serverNames;
        }

        if (args.length == 1) {
            return Arrays.asList("message", "kick", "send");
        }

        if (args.length == 0) {
            List<String> names = new LinkedList<>();
            for (ICloudPlayer iCloudPlayer :PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached()) {
                names.add(iCloudPlayer.getName());
            }
            return names;
        }
        return new LinkedList<>();
    }
}
