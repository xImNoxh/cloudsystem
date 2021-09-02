package de.polocloud.plugin.bootstrap.proxy.commands;

import com.google.inject.Inject;
import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.messaging.IMessageManager;
import de.polocloud.api.network.packets.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.other.PingPacket;
import de.polocloud.api.network.protocol.packet.base.response.Response;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.request.PacketMessenger;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.util.WrappedObject;
import de.polocloud.api.util.system.ressources.IResourceConverter;
import de.polocloud.api.util.system.ressources.IResourceProvider;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        IResourceConverter resourceConverter = PoloCloudAPI.getInstance().getSystemManager().getResourceConverter();
        IResourceProvider resourceProvider = PoloCloudAPI.getInstance().getSystemManager().getResourceProvider();

        if (player.hasPermission("cloud.use")) {

            if (params.length == 1) {
                if (params[0].equalsIgnoreCase("info")) {
                    APIVersion version = PoloCloudAPI.getInstance().getVersion();
                    player.sendMessage(prefix + "§7PoloCloud §b" + version.version());
                    player.sendMessage(prefix + "Thanks to §3" + Arrays.toString(version.developers()).replace("[", "").replace("]", ""));
                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7Global Servers §8: §3" + gameServerManager.getAllCached().size());
                    player.sendMessage(prefix + "§7Spigot Servers §8: §3" + gameServerManager.getCached(TemplateType.MINECRAFT).size());
                    player.sendMessage(prefix + "§7Proxy Servers §8: §3" + gameServerManager.getCached(TemplateType.PROXY).size());
                    player.sendMessage(prefix + "§7Current Server §8: §3" + gameServerManager.getThisService().getName());
                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7Available Wrappers §8: §3" + wrapperManager.getWrappers().size());
                    player.sendMessage(prefix + "§7Current Wrapper §8: §3" + gameServerManager.getThisService().getWrapper().getName());
                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7Global Templates §8: §3" + templateManager.getTemplates().size());
                    player.sendMessage(prefix + "§7Spigot Templates §8: §3" + (int) templateManager.getTemplates().stream().filter(template -> template.getTemplateType() == TemplateType.MINECRAFT).count());
                    player.sendMessage(prefix + "§7Proxy Templates §8: §3" + (int) templateManager.getTemplates().stream().filter(template -> template.getTemplateType() == TemplateType.PROXY).count());
                    player.sendMessage("§8");
                    IGameServer highest = gameServerManager.getAllCached().stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);
                    IGameServer lowest = gameServerManager.getAllCached().stream().min(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);

                    player.sendMessage(prefix + "§7Online Players §8: §3" +  cloudPlayerManager.getAllCached().size());
                    player.sendMessage(prefix + "§7Most Players §8: §3" +  (highest == null ? "N/A" : highest.getName() + " §8(§3" + highest.getOnlinePlayers() + "§8)"));
                    player.sendMessage(prefix + "§7Fewest Players §8: §3" +  (lowest == null ? "N/A" : (lowest.getName().equalsIgnoreCase(highest == null ? "" : highest.getName()) ? "Same players" : lowest.getName() + " §8(§3" + lowest.getOnlinePlayers() + "§8)")));

                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7OS §8: §3" + resourceProvider.getSystem().getName());
                    player.sendMessage(prefix + "§7Used memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemUsedMemory()));
                    player.sendMessage(prefix + "§7Free memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemFreeMemory()));
                    player.sendMessage(prefix + "§7Physical Memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemPhysicalMemory()));
                    player.sendMessage(prefix + "§7System CPU §8: §3" + resourceConverter.roundDouble(resourceProvider.getSystemCpuLoad()) + "%");
                    player.sendMessage(prefix + "§7Process CPU §8: §3" + resourceConverter.roundDouble(resourceProvider.getProcessCpuLoad()) + "%");
                    player.sendMessage(prefix + "§7Virutal Cores §8: §3" + resourceProvider.getSystemProcessors());
                    player.sendMessage("§8");

                    Response response = PacketMessenger.newInstance().blocking().timeOutAfter(TimeUnit.SECONDS, 3L).orElse(new Response(new JsonData("time", -1L), ResponseState.TIMED_OUT)).send(new PingPacket(System.currentTimeMillis()));

                    player.sendMessage(prefix + "§7Cloud-Ping §b" + response.getDocument().getLong("time") + "ms");
                    player.sendMessage("§8");

                } else if (params[0].equalsIgnoreCase("rl")) {
                    PoloCloudAPI.getInstance().reload();
                    player.sendMessage(prefix + "§7The Cloud was §areloaded§8!");
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
                } else if (params[0].equalsIgnoreCase("copy")) {
                    String server = params[1];
                    IGameServer cached = gameServerManager.getCached(server);

                    if (cached == null) {
                        player.sendMessage(prefix + "§cThere is no online GameServer with name '§e" + server + "§c'!");
                        return;
                    }
                    String type = params[2];
                    if (type.equalsIgnoreCase("worlds") || type.equalsIgnoreCase("entire")) {

                        if (type.equalsIgnoreCase("worlds")) {
                            if (cached.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                                player.sendMessage(prefix + "§cProxyGameServers don't have worlds, so its not compatibly with the 'worlds' mode, please use the 'entire' type!");
                                return;
                            }
                            player.sendMessage(prefix + "Copying §b" + cached.getName() + "§7...");
                            List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(cached.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                            for (IWrapper wrapper : wrappers) {
                                wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, cached.getName(), String.valueOf(cached.getSnowflake()), cached.getTemplate().getName()));
                            }
                        } else if (type.equalsIgnoreCase("entire")) {
                            player.sendMessage(prefix + "Copying §b" + cached.getName() + "§7...");
                            List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(cached.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                            for (IWrapper wrapper : wrappers) {
                                wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, cached.getName(), String.valueOf(cached.getSnowflake()), cached.getTemplate().getName()));
                            }
                        }
                    } else {
                        player.sendMessage(prefix + "§8» following command for copying a gameserver: §b" +
                            "/cloud gameserver copy <gameserver> entire/worlds");
                        player.sendMessage(prefix + "§7Explanation: \n" +
                            prefix + "§bentire: §7" + "copies the entire GameServer to the template\n" +
                            prefix + "§bworlds: §7" + "only copies the worlds of the GameServer to the template");
                    }

                } else {
                    sendHelp(player);
                }
            } else {
                if (params.length >= 2 && (params[0].equalsIgnoreCase("execute") || params[0].equalsIgnoreCase("cmd"))) {

                    String server = params[1];
                    IGameServer cached = gameServerManager.getCached(server);

                    if (cached == null) {
                        player.sendMessage(prefix + "§cThere is no online GameServer with name '§e" + server + "§c'!");
                        return;
                    }

                    StringBuilder content = new StringBuilder();
                    for (int i = 2; i < params.length; i++) {
                        content.append(params[i]).append(" ");
                    }
                    content = new StringBuilder(content.substring(0, content.length() - 1));
                    player.sendMessage(prefix + "Processing...");
                    PoloCloudAPI.getInstance().sendPacket(new GameServerExecuteCommandPacket(content.toString(), cached.getName()));
                    player.sendMessage(prefix + "§aSuccessfully executed command » §b" + content + " §7on server » §b" + cached.getName() + "§7!");
                } else {
                    sendHelp(player);
                }
            }
        } else {
            player.sendMessage(prefix + "§cYou don't have the required permission for that!");
        }
    }

    private void sendHelp(ICloudPlayer player) {
        player.sendMessage(prefix + "----[Cloud]----");
        player.sendMessage(prefix + "§8» §b/cloud stop/shutdown <server> §7Shuts down a gameserver");
        player.sendMessage(prefix + "§8» §b/cloud reload/rl §7Reloads the cloud");
        player.sendMessage(prefix + "§8» §b/cloud start <template> §7Starts a new gameserver");
        player.sendMessage(prefix + "§8» §b/cloud start <server> <amount> §7Starts multiple new gameservers at once");
        player.sendMessage(prefix + "§8» §b/cloud info (<server>) §7Gets information of a gameserver or of the whole cloud");
        player.sendMessage(prefix + "§8» §b/cloud list <server/wrapper/template> §7Lists online values");
        player.sendMessage(prefix + "§8» §b/cloud copy <server> worlds/entire §7Copies the temporary files of a server into its template");
        player.sendMessage(prefix + "§8» §b/cloud execute <server> <command> §7Executes a command on a gameserver");
        player.sendMessage(prefix + "----[/Cloud]----");
    }

}
