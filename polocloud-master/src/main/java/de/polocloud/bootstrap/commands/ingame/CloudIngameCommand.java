package de.polocloud.bootstrap.commands.ingame;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.types.ConsoleColors;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CloudCommand.Info(name = "cloud", description = "Manage the cloud system ingame", aliases = "c", commandType = CommandType.INGAME)
public class CloudIngameCommand extends CloudCommand {

    private final String prefix = "§bPoloCloud §7" + "» ";

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private ITemplateService templateService;

    @Inject
    private Snowflake snowflake;

    @Inject
    private ICloudPlayerManager cloudPlayerManager;

    public CloudIngameCommand() {
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        ICloudPlayer player = (ICloudPlayer) sender;
        try {
            //DONE /cloud gameserver start <template> 4
            //DONE /cloud gameserver stop <gameserver> 4
            //DONE /cloud gameserver info <gameserver> 4

            //DONE /cloud gameserver copy <gameserver> worlds/entire 5
            //DONE /cloud gameserver start <template> <amount> 5

            //DONE /cloud gameserver execute <gameserver <command> > 5
            //DONE /cloud player <player> kick <message> > 5
            //DONE /cloud player <player> message <message> > 5
            //DONE /cloud player <player> send <server> > 5
            if (player.hasPermissions("cloud.use").get()) {
                if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("gameserver")) {
                        if (args[2].equalsIgnoreCase("start")) {
                            String templateName = args[3];

                            ITemplate template = templateService.getTemplateByName(templateName).get();

                            if (template == null) {
                                player.sendMessage(prefix + "The template » §b" + templateName + " §7doesn't exists!");
                                return;
                            }
                            int size = gameServerManager.getGameServersByTemplate(template).get().size();
                            if ((size + 1) >= template.getMaxServerCount()) {
                                player.sendMessage(prefix + "Cannot start the server, the maximal server online count of » §b" + template.getMaxServerCount()
                                    + " §7was reached! (With new server » §b" + (size + 1) + "§/)");
                                return;
                            }

                            Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                            if (!optionalWrapperClient.isPresent()) {
                                player.sendMessage(prefix + "No available Wrapper connected!");
                                return;
                            }

                            WrapperClient wrapperClient = optionalWrapperClient.get();

                            player.sendMessage(prefix + "Requesting start...");
                            SimpleGameServer newGameServer = new SimpleGameServer(wrapperClient, template.getName() + "-" + searchForAvailableID(template),
                                GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers(), false);
                            gameServerManager.registerGameServer(newGameServer);
                            wrapperClient.startServer(newGameServer);

                            player.sendMessage(prefix + "§aSuccessfully requested start.");
                        } else if (args[2].equalsIgnoreCase("stop") || args[2].equalsIgnoreCase("shutdown")) {
                            String gameserverName = args[3];
                            IGameServer gameServer = gameServerManager.getGameServerByName(gameserverName).get();

                            if (gameServer == null) {
                                player.sendMessage(prefix + "The gameserver » §b" + gameserverName + " §7isn't online!");
                                return;
                            }
                            if (gameServer.getStatus() == GameServerStatus.RUNNING) {
                                gameServer.stop();
                            } else {
                                gameServer.terminate();
                                gameServerManager.unregisterGameServer(gameServer);
                                player.sendMessage(prefix + "Server is not Running... §cTerminating Process");
                            }
                            player.sendMessage(prefix + "You " + "§asuccessfully §7" + "stopped the gameserver » §b" + gameserverName + "§7!");
                        } else if (args[2].equalsIgnoreCase("info")) {
                            String name = args[3];
                            IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                            if (gameServer == null) {
                                player.sendMessage(prefix + "The gameserver » §b" + name + " §7isn't online!");
                                return;
                            }

                            player.sendMessage(prefix + "----[Information]----");
                            player.sendMessage("");
                            player.sendMessage(prefix + "Gameserver » §b" + gameServer.getName());
                            player.sendMessage("");
                            player.sendMessage(prefix + "Total memory » §b" + gameServer.getTotalMemory() + "§7mb");
                            player.sendMessage("");
                            player.sendMessage(prefix + "Id » §b#" + gameServer.getSnowflake());
                            player.sendMessage("");
                            player.sendMessage(prefix + "Status » §b" + gameServer.getStatus());
                            player.sendMessage("");
                            player.sendMessage(prefix + "Started time » §b" + gameServer.getStartTime());
                            player.sendMessage("");
                            player.sendMessage(prefix + "Ping » §b" + gameServer.getPing() + "ms");
                            player.sendMessage("");
                            player.sendMessage(prefix + "----[/Information]----");
                        } else {
                            player.sendMessage(prefix + "----[Cloud-Gameserver]----");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver stop/shutdown <server> " + ConsoleColors.GRAY + "to shutdown a gameserver");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver start <template> " + ConsoleColors.GRAY + "to start a new gameserver");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver start <server> <amount> " + ConsoleColors.GRAY + "to start multiple new gameservers at once");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver copy <server> worlds/entire " + ConsoleColors.GRAY + "to copy the temproy file of a server into its template");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver info <server> " + ConsoleColors.GRAY + "to get information of a gameserver");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver execute <server> <command> " + ConsoleColors.GRAY + "to execuet a command on a gameserver");
                            player.sendMessage(prefix + "----[/Cloud-Gameserver]----");
                        }
                    } else {
                        sendHelp(player);
                    }
                } else if (args.length >= 5) {
                    if (args.length == 5) {
                        if (args[1].equalsIgnoreCase("gameserver")) {
                            if (args[2].equalsIgnoreCase("copy")) {
                                String gameServerName = args[3];
                                String type = args[4];
                                if (!(type.equalsIgnoreCase("worlds") || type.equalsIgnoreCase("entire"))) {
                                    player.sendMessage(prefix + "Use following command for copying a gameserver: §b" +
                                        "/cloud gameserver copy <gameserver> entire/worlds");
                                    player.sendMessage(prefix + "§7Explanation: \n" +
                                        prefix + "§bentire: §7" + "copies the entire GameServer to the template\n" +
                                        prefix + "§bworlds: §7" + "only copies the worlds of the GameServer to the template");
                                    return;
                                }
                                IGameServer gameServer = gameServerManager.getGameServerByName(gameServerName).get();
                                if (gameServer == null) {
                                    player.sendMessage(prefix + "The gameserver » §b" + gameServerName + " §7isn't online!");
                                    return;
                                }
                                if (type.equalsIgnoreCase("worlds")) {
                                    if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                                        player.sendMessage(prefix + "A Proxy Server doesn't have worlds, so its not compatibly with the 'worlds' mode, please use the 'entire' type!");
                                        return;
                                    }
                                    player.sendMessage(prefix + "Copying §b" + gameServer.getName() + "§7...");
                                    List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                                    for (WrapperClient wrapper : wrappers) {
                                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                                    }
                                } else if (type.equalsIgnoreCase("entire")) {
                                    player.sendMessage(prefix + "Copying §b" + gameServer.getName() + "§7...");
                                    List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                                    for (WrapperClient wrapper : wrappers) {
                                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                                    }
                                }
                            } else if (args[2].equalsIgnoreCase("start")) {
                                String templateName = args[3];
                                String amountString = args[4];

                                ITemplate template = templateService.getTemplateByName(templateName).get();

                                if (template == null) {
                                    player.sendMessage(prefix + "The template » §b" + templateName + " §7doesn't exists!");
                                    return;
                                }
                                int amount;
                                try {
                                    amount = Integer.parseInt(amountString);
                                } catch (NumberFormatException exception) {
                                    player.sendMessage(prefix + "Please provide a real number (int)");
                                    return;
                                }

                                if (amount < 0) {
                                    player.sendMessage(prefix + "You cannot start " + amountString + " servers!");
                                    return;
                                }
                                int size = gameServerManager.getGameServersByTemplate(template).get().size();
                                if ((size + amount) >= template.getMaxServerCount()) {
                                    player.sendMessage(prefix + "Cannot start the servers, the maximal servers online count of » §b" + template.getMaxServerCount()
                                        + " §7was reached! (With new servers » §b" + (size + amount) + "§/)");
                                    return;
                                }

                                Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                                if (!optionalWrapperClient.isPresent()) {
                                    player.sendMessage(prefix + "No available Wrapper connected!");
                                    return;
                                }

                                WrapperClient wrapperClient = optionalWrapperClient.get();

                                for (int i = 0; i < amount; i++) {
                                    player.sendMessage(prefix + "Requesting start...");
                                    SimpleGameServer newGameServer = new SimpleGameServer(wrapperClient, template.getName() + "-" + searchForAvailableID(template),
                                        GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers(), false);
                                    gameServerManager.registerGameServer(newGameServer);
                                    wrapperClient.startServer(newGameServer);
                                }
                                player.sendMessage(prefix + "§aSuccessfully requested start for » §b" + amount + " §7servers!");
                            } else {
                                player.sendMessage(prefix + "----[Cloud-Gameserver]----");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver stop/shutdown <server> " + ConsoleColors.GRAY + "to shutdown a gameserver");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver start <template> " + ConsoleColors.GRAY + "to start a new gameserver");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver start <server> <amount> " + ConsoleColors.GRAY + "to start multiple new gameservers at once");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver copy <server> worlds/entire " + ConsoleColors.GRAY + "to copy the temproy file of a server into its template");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver info <server> " + ConsoleColors.GRAY + "to get information of a gameserver");
                                player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud gameserver execute <server> <command> " + ConsoleColors.GRAY + "to execuet a command on a gameserver");
                                player.sendMessage(prefix + "----[/Cloud-Gameserver]----");
                            }
                        } else {
                            sendHelp(player);
                        }
                    }
                    if (args[1].equalsIgnoreCase("gameserver")) {
                        String gameserverName = args[3];
                        IGameServer gameServer = gameServerManager.getGameServerByName(gameserverName).get();

                        if (gameServer == null) {
                            player.sendMessage(prefix + "The gameserver » §b" + gameserverName + " §7isn't online!");
                            return;
                        }

                        String content = "";
                        for (int i = 4; i < args.length; i++) {
                            content += args[i] + " ";
                        }
                        content = content.substring(0, content.length() - 1);
                        player.sendMessage(prefix + "Processing...");
                        gameServer.sendPacket(new GameServerExecuteCommandPacket(content));
                        player.sendMessage(prefix + "§aSuccessfully executed command » §b" + content + " §7on server » §b" + gameServer.getName() + "§7!");
                    } else if (args[1].equalsIgnoreCase("player")) {
                        String targetPlayerName = args[2];
                        ICloudPlayer targetPlayer = cloudPlayerManager.getOnlinePlayer(targetPlayerName).get();
                        if (targetPlayer == null) {
                            player.sendMessage(prefix + "The player » §b" + targetPlayerName + " §7isn't online!");
                            return;
                        }
                        if (!(args[3].equalsIgnoreCase("kick") || args[3].equalsIgnoreCase("send") || args[3].equalsIgnoreCase("message"))) {
                            player.sendMessage(prefix + "----[Cloud-Player]----");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud player <player> message <message...> " + ConsoleColors.GRAY + "to send a message to a player");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud player <player> kick <message...> " + ConsoleColors.GRAY + "to kick a player with a message");
                            player.sendMessage(prefix + "Use " + ConsoleColors.LIGHT_BLUE + "/cloud player <player> send <server> " + ConsoleColors.GRAY + "to send a player to a gameserver");
                            player.sendMessage(prefix + "----[/Cloud-Player]----");
                            return;
                        }
                        String content = "";
                        for (int i = 4; i < args.length; i++) {
                            content += args[i] + " ";
                        }
                        content = content.substring(0, content.length() - 1);

                        if (args[3].equalsIgnoreCase("kick")) {
                            player.sendMessage(prefix + "Kicking...");
                            targetPlayer.kick(content);
                            player.sendMessage(prefix + "§aSuccessfully §7kicked player » §b" + targetPlayer.getName() + " §7for » §b" + content + "§7!");
                        } else if (args[3].equalsIgnoreCase("message")) {
                            player.sendMessage(prefix + "Messaging...");
                            targetPlayer.sendMessage("§7" + player.getName() + " -> you » " + content);
                            player.sendMessage(prefix + "§aSuccessfully §7messaged player » §b" + targetPlayer.getName() + "! (message » §b" + content + "§7)");
                        } else if (args[3].equalsIgnoreCase("send")) {
                            IGameServer gameServer = gameServerManager.getGameServerByName(content).get();
                            if (gameServer == null) {
                                player.sendMessage(prefix + "The gameserver » §b" + content + " §7isn't online!");
                                return;
                            }

                            player.sendMessage("Sending...");
                            targetPlayer.sendMessage(prefix + "Sending to » §b" + gameServer.getName() + " §7by » §b" + player.getName());
                            targetPlayer.sendTo(gameServer);
                            player.sendMessage(prefix + "§aSuccessfully §7sent player » §b" + targetPlayer.getName() + " §7to server » §b" + gameServer.getName() + "§7!");
                        }
                    } else {
                        sendHelp(player);
                    }
                } else {
                    sendHelp(player);
                }
            } else {
                player.sendMessage(prefix + "§cYou don't have the required permission for that!");
            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            player.sendMessage(prefix + "§cAn unexpected error occurred while executing the command: §7" + String.join(",", args));
        }
    }

    private void sendHelp(ICloudPlayer player) {
        player.sendMessage(prefix + "----[Cloud]----");
        player.sendMessage(prefix + "Use §b/cloud gameserver stop/shutdown <server> §7to shutdown a gameserver");
        player.sendMessage(prefix + "Use §b/cloud gameserver start <template> §7to start a new gameserver");
        player.sendMessage(prefix + "Use §b/cloud gameserver start <server> <amount> §7to start multiple new gameservers at once");
        player.sendMessage(prefix + "Use §b/cloud gameserver copy <server> worlds/entire §7to copy the temporary files of a server into its template");
        player.sendMessage(prefix + "Use §b/cloud gameserver info <server> §7to get information of a gameserver");
        player.sendMessage(prefix + "Use §b/cloud gameserver execute <server> <command> §7to execute a command on a gameserver");
        player.sendMessage(prefix + "Use §b/cloud player <player> message <message...> §7to send a message to a player");
        player.sendMessage(prefix + "Use §b/cloud player <player> kick <message...> §7to kick a player with a message");
        player.sendMessage(prefix + "Use §b/cloud player <player> send <server> §7to send a player to a gameserver");
        player.sendMessage(prefix + "----[/Cloud]----");
    }

    private int searchForAvailableID(ITemplate template) throws ExecutionException, InterruptedException {
        return gameServerManager.getGameServersByTemplate(template).get().size() + 1;
    }

}
