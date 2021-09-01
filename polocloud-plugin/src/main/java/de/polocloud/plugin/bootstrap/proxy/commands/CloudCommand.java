package de.polocloud.plugin.bootstrap.proxy.commands;

import com.google.inject.Inject;
import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapperManager;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CloudCommand implements CommandListener {

    private final String prefix = "§bPoloCloud §7" + "» ";

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateManager templateService;

    @Inject
    private Snowflake snowflake;

    @Inject
    private ICloudPlayerManager cloudPlayerManager;

    public CloudCommand() {
    }

    @Command(name = "cloud", description = "Manage the cloud system ingame", aliases = "c")
    @CommandExecutors(ExecutorType.PLAYER)
    public void execute(CommandExecutor sender, String[] fullArgs, String... params) {
        ICloudPlayer player = (ICloudPlayer) sender;

        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
        ITemplateManager templateManager = PoloCloudAPI.getInstance().getTemplateManager();
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
        ICloudPlayerManager cloudPlayerManager = PoloCloudAPI.getInstance().getCloudPlayerManager();

        if (player.hasPermission("cloud.use")) {


            if (params.length == 1) {
                if (params[0].equalsIgnoreCase("ver") || params[0].equalsIgnoreCase("version")) {
                    APIVersion version = PoloCloudAPI.getInstance().getVersion();
                    player.sendMessage(prefix + "§7PoloCloud §b" + version.version());
                    player.sendMessage(prefix + "Thanks to §3" + Arrays.toString(version.developers()).replace("[", "").replace("]", ""));
                } else if (params[0].equalsIgnoreCase("debug")) {

                    IProperty property = player.getProperty("lobbySystem");

                    player.sendMessage(property.toString());
                    for (IProperty sub : property.getProperties()) {
                       player.sendMessage(sub.toString());
                       if (!sub.isSingleProperty()) {
                           for (IProperty subProperty : sub.getProperties()) {
                               player.sendMessage(subProperty.toString());
                           }
                       }
                    }

                    player.sendMessage(prefix + "§7Debug executed!");
                } else {
                    sendHelp(player);
                }
            } else if (params.length == 2) {
                if (params[0].equalsIgnoreCase("start")) {

                    String group = params[1];
                    ITemplate template = templateManager.getTemplate(group);

                    if (template == null) {
                        player.sendMessage(prefix + "§cThere is no Template with name '§e" + group + "§c'!");
                        return;
                    }

                    gameServerManager.startServer(template, 1);
                    player.sendMessage(prefix + "§7Trying to start §bone §7GameServer of template §3" + template.getName() + "§8...");
                } else if (params[0].equalsIgnoreCase("info") ) {
                    String name = params[1];
                    IGameServer gameServer = gameServerManager.getCached(name);
                    if (gameServer == null) {
                        player.sendMessage(prefix + "§cThere is no online GameServer with name '§e" + name + "§c'!");
                        return;
                    }

                    player.sendMessage(prefix + "----[Information]----");
                    player.sendMessage(prefix + "Gameserver » §b" + gameServer.getName());
                    player.sendMessage(prefix + "Total memory » §b" + gameServer.getTotalMemory() + "§7mb");
                    player.sendMessage(prefix + "Id » §b#" + gameServer.getSnowflake());
                    player.sendMessage(prefix + "Status » §b" + gameServer.getStatus());
                    player.sendMessage(prefix + "Started time » §b" + gameServer.getStartTime());
                    player.sendMessage(prefix + "Ping » §b" + gameServer.getPing() + "ms");
                    player.sendMessage(prefix + "----[/Information]----");

                } else if (params[0].equalsIgnoreCase("stop") || params[0].equalsIgnoreCase("shutdown")) {

                    String server = params[1];
                    IGameServer cached = gameServerManager.getCached(server);

                    if (cached == null) {
                        player.sendMessage(prefix + "§cThere is no online GameServer with name '§e" + server + "§c'!");
                        return;
                    }

                    gameServerManager.stopServer(cached);
                    player.sendMessage(prefix + "§7Trying to stop §c" + cached.getName() + "§8...");

                } else {
                    sendHelp(player);
                }
            } else if (params.length == 3) {
                if (params[0].equalsIgnoreCase("start")) {
                    String group = params[1];
                    ITemplate template = templateManager.getTemplate(group);

                    if (template == null) {
                        player.sendMessage(prefix + "§cThere is no Template with name '§e" + group + "§c'!");
                        return;
                    }

                    try {
                        int amount = Integer.parseInt(params[2]);
                        gameServerManager.startServer(template, amount);
                        player.sendMessage(prefix + "§7Trying to start §b" + amount + " §7GameServer(s) of template §3" + template.getName() + "§8...");
                    } catch (NumberFormatException e) {
                        player.sendMessage(prefix + "§cPlease provide a valid §enumber§c!");
                    }
                } else {
                    sendHelp(player);
                }
            } else {
                sendHelp(player);
            }

            /*
            if (params.length >= 5) {
                if (params.length == 5) {
                    if (params[1].equalsIgnoreCase("gameserver")) {
                        if (params[2].equalsIgnoreCase("copy")) {
                            String gameServerName = params[3];
                            String type = params[4];
                            if (!(type.equalsIgnoreCase("worlds") || type.equalsIgnoreCase("entire"))) {
                                player.sendMessage(prefix + "Use following command for copying a gameserver: §b" +
                                    "/cloud gameserver copy <gameserver> entire/worlds");
                                player.sendMessage(prefix + "§7Explanation: \n" +
                                    prefix + "§bentire: §7" + "copies the entire GameServer to the template\n" +
                                    prefix + "§bworlds: §7" + "only copies the worlds of the GameServer to the template");
                                return;
                            }
                            IGameServer gameServer = gameServerManager.getCached(gameServerName);
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
                                List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                                for (IWrapper wrapper : wrappers) {
                                    wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                                }
                            } else if (type.equalsIgnoreCase("entire")) {
                                player.sendMessage(prefix + "Copying §b" + gameServer.getName() + "§7...");
                                List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                                for (IWrapper wrapper : wrappers) {
                                    wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                                }
                            }
                        } else if (params[2].equalsIgnoreCase("start")) {
                            String templateName = params[3];
                            String amountString = params[4];

                            ITemplate template = templateService.getTemplate(templateName);

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
                            int size = gameServerManager.getGameServersByTemplate(template).size();
                            if ((size + amount) >= template.getMaxServerCount()) {
                                player.sendMessage(prefix + "Cannot start the servers, the maximal servers online count of » §b" + template.getMaxServerCount()
                                    + " §7was reached! (With new servers » §b" + (size + amount) + "§/)");
                                return;
                            }

                            Optional<IWrapper> optionalWrapperClient = wrapperManager.getWrappers().stream().findAny();

                            if (!optionalWrapperClient.isPresent()) {
                                player.sendMessage(prefix + "No available Wrapper connected!");
                                return;
                            }

                            IWrapper wrapperClient = optionalWrapperClient.get();

                            for (int i = 0; i < amount; i++) {
                                player.sendMessage(prefix + "Requesting start...");


                                IGameServer iGameServer = IGameServer.create();
                                iGameServer.setName(template.getName() + "-" + searchForAvailableID(template));
                                iGameServer.applyTemplate(template); //Memory, motd, maxplayers
                                iGameServer.newSnowflake();
                                iGameServer.setStartedTime(System.currentTimeMillis());
                                iGameServer.setPort(-1);
                                iGameServer.setVisible(false);
                                iGameServer.setStatus(GameServerStatus.PENDING);

                                wrapperClient.startServer(iGameServer);
                            }
                            player.sendMessage(prefix + "§aSuccessfully requested start for » §b" + amount + " §7servers!");
                        } else {
                            player.sendMessage(prefix + "----[Cloud-Gameserver]----");
                            player.sendMessage(prefix + "Use §3/cloud gameserver stop/shutdown <server> §7to shutdown a gameserver");
                            player.sendMessage(prefix + "Use §3/cloud gameserver start <template> §7to start a new gameserver");
                            player.sendMessage(prefix + "Use §3/cloud gameserver start <server> <amount> §7to start multiple new gameservers at once");
                            player.sendMessage(prefix + "Use §3/cloud gameserver copy <server> worlds/entire §7to copy the temproy file of a server into its template");
                            player.sendMessage(prefix + "Use §3/cloud gameserver info <server> §7to get information of a gameserver");
                            player.sendMessage(prefix + "Use §3/cloud gameserver execute <server> <command> §7to execuet a command on a gameserver");
                            player.sendMessage(prefix + "----[/Cloud-Gameserver]----");
                        }
                    } else {
                        sendHelp(player);
                    }
                }
                if (params[1].equalsIgnoreCase("gameserver")) {
                    String gameserverName = params[3];
                    IGameServer gameServer = gameServerManager.getCached(gameserverName);

                    if (gameServer == null) {
                        player.sendMessage(prefix + "The gameserver » §b" + gameserverName + " §7isn't online!");
                        return;
                    }

                    StringBuilder content = new StringBuilder();
                    for (int i = 4; i < params.length; i++) {
                        content.append(params[i]).append(" ");
                    }
                    content = new StringBuilder(content.substring(0, content.length() - 1));
                    player.sendMessage(prefix + "Processing...");
                    gameServer.sendPacket(new GameServerExecuteCommandPacket(content.toString()));
                    player.sendMessage(prefix + "§aSuccessfully executed command » §b" + content + " §7on server » §b" + gameServer.getName() + "§7!");
                } else if (params[1].equalsIgnoreCase("player")) {
                    String targetPlayerName = params[2];
                    ICloudPlayer targetPlayer = cloudPlayerManager.getCached(targetPlayerName);
                    if (targetPlayer == null) {
                        player.sendMessage(prefix + "The player » §b" + targetPlayerName + " §7isn't online!");
                        return;
                    }
                    if (!(params[3].equalsIgnoreCase("kick") || params[3].equalsIgnoreCase("send") || params[3].equalsIgnoreCase("message"))) {
                        player.sendMessage(prefix + "----[Cloud-Player]----");
                        player.sendMessage(prefix + "Use §3/cloud player <player> message <message...> §7to send a message to a player");
                        player.sendMessage(prefix + "Use §3/cloud player <player> kick <message...> §7to kick a player with a message");
                        player.sendMessage(prefix + "Use §3/cloud player <player> send <server> §7to send a player to a gameserver");
                        player.sendMessage(prefix + "----[/Cloud-Player]----");
                        return;
                    }
                    StringBuilder content = new StringBuilder();
                    for (int i = 4; i < params.length; i++) {
                        content.append(params[i]).append(" ");
                    }
                    content = new StringBuilder(content.substring(0, content.length() - 1));

                    if (params[3].equalsIgnoreCase("kick")) {
                        player.sendMessage(prefix + "Kicking...");
                        targetPlayer.kick(content.toString());
                        player.sendMessage(prefix + "§aSuccessfully §7kicked player » §b" + targetPlayer.getName() + " §7for » §b" + content + "§7!");
                    } else if (params[3].equalsIgnoreCase("message")) {
                        player.sendMessage(prefix + "Messaging...");
                        targetPlayer.sendMessage("§7" + player.getName() + " -> you » " + content);
                        player.sendMessage(prefix + "§aSuccessfully §7messaged player » §b" + targetPlayer.getName() + "! (message » §b" + content + "§7)");
                    } else if (params[3].equalsIgnoreCase("send")) {
                        IGameServer gameServer = gameServerManager.getCached(content.toString());
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
            }*/
        } else {
            player.sendMessage(prefix + "§cYou don't have the required permission for that!");
        }
    }

    private void sendHelp(ICloudPlayer player) {
        player.sendMessage(prefix + "----[Cloud]----");
        player.sendMessage(prefix + "Use §b/cloud stop/shutdown <server> §7to shutdown a gameserver");
        player.sendMessage(prefix + "Use §b/cloud start <template> §7to start a new gameserver");
        player.sendMessage(prefix + "Use §b/cloud start <server> <amount> §7to start multiple new gameservers at once");
        player.sendMessage(prefix + "Use §b/cloud info <server> §7to get information of a gameserver");
        player.sendMessage(prefix + "Use §b/cloud copy <server> worlds/entire §7to copy the temporary files of a server into its template");
        player.sendMessage(prefix + "Use §b/cloud execute <server> <command> §7to execute a command on a gameserver");
        //player.sendMessage(prefix + "Use §b/cloud player <player> message <message...> §7to send a message to a player");
        //player.sendMessage(prefix + "Use §b/cloud player <player> kick <message...> §7to kick a player with a message");
        //player.sendMessage(prefix + "Use §b/cloud player <player> send <server> §7to send a player to a gameserver");
        player.sendMessage(prefix + "----[/Cloud]----");
    }

    private int searchForAvailableID(ITemplate template) throws ExecutionException, InterruptedException {
        return gameServerManager.getCached(template).size() + 1;
    }

}
