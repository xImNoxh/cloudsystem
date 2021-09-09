package de.polocloud.plugin.bootstrap.proxy.commands;

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
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.master.MasterShutdownPacket;
import de.polocloud.api.network.packets.other.PingPacket;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.util.system.ressources.IResourceConverter;
import de.polocloud.api.util.system.ressources.IResourceProvider;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class CloudCommand implements CommandListener {

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
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();

        if (player.hasPermission("cloud.use")) {

            if (params.length == 1) {
                if (params[0].equalsIgnoreCase("info")) {
                    APIVersion version = PoloCloudAPI.getInstance().getVersion();
                    player.sendMessage(prefix + "§7PoloCloud §b" + version.version());
                    player.sendMessage(prefix + "Thanks to §3" + Arrays.toString(version.developers()).replace("[", "").replace("]", ""));
                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7Global Servers §8: §3" + gameServerManager.getAllCached().size());
                    player.sendMessage(prefix + "§7Spigot Servers §8: §3" + gameServerManager.getAllCached(TemplateType.MINECRAFT).size());
                    player.sendMessage(prefix + "§7Proxy Servers §8: §3" + gameServerManager.getAllCached(TemplateType.PROXY).size());
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

                    player.sendMessage(prefix + "§7Online Players §8: §3" + cloudPlayerManager.getAllCached().size());
                    player.sendMessage(prefix + "§7Most Players §8: §3" + (highest == null ? "N/A" : highest.getName() + " §8(§3" + highest.getOnlinePlayers() + "§8)"));
                    player.sendMessage(prefix + "§7Fewest Players §8: §3" + (lowest == null ? "N/A" : (lowest.getName().equalsIgnoreCase(highest == null ? "" : highest.getName()) ? "Same players" : lowest.getName() + " §8(§3" + lowest.getOnlinePlayers() + "§8)")));

                    player.sendMessage("§8");
                    player.sendMessage(prefix + "§7OS §8: §3" + resourceProvider.getSystem().getName());
                    player.sendMessage(prefix + "§7Used memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemUsedMemory()));
                    player.sendMessage(prefix + "§7Free memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemFreeMemory()));
                    player.sendMessage(prefix + "§7Physical Memory §8: §3" + resourceConverter.convertLongToSize(resourceProvider.getSystemPhysicalMemory()));
                    player.sendMessage(prefix + "§7System CPU §8: §3" + resourceConverter.roundDouble(resourceProvider.getSystemCpuLoad()) + "%");
                    player.sendMessage(prefix + "§7Process CPU §8: §3" + resourceConverter.roundDouble(resourceProvider.getProcessCpuLoad()) + "%");
                    player.sendMessage(prefix + "§7Virutal Cores §8: §3" + resourceProvider.getSystemProcessors());
                    player.sendMessage("§8");

                    IResponse response = PacketMessenger.newInstance().blocking().timeOutAfter(TimeUnit.SECONDS, 3L).orElse(new Response(new JsonData("time", -1L), ResponseState.TIMED_OUT)).send(new PingPacket(System.currentTimeMillis()));

                    if (response.get("start").isNull()) {
                        player.sendMessage(prefix + "§cCouldn't get §ePing-Information§c!");
                    } else {

                        long start = response.get("start").getAsLong();
                        long respond = response.get("respond").getAsLong();
                        long time = response.get("time").getAsLong();
                        long now = System.currentTimeMillis();

                        player.sendMessage(prefix + "§7Ping-Requested-At §8: §3" + PoloHelper.SIMPLE_DATE_FORMAT.format(start));
                        player.sendMessage(prefix + "§7Ping-Responded-At §8: §3" + PoloHelper.SIMPLE_DATE_FORMAT.format(respond));
                        player.sendMessage(prefix + "§7Ping-Time-Now §8: §3" + PoloHelper.SIMPLE_DATE_FORMAT.format(now));
                        player.sendMessage(prefix + "§7#1 Ping (Server -> Cloud) §8: §3" + time + "ms");
                        player.sendMessage(prefix + "§7#2 Ping (Server <-> Cloud) §8: §3" + (now - start) + "ms");
                    }
                    player.sendMessage("§8");

                } else if (params[0].equalsIgnoreCase("debug") && player.getName().equalsIgnoreCase("Lystx")) {

                    player.sendMessage(prefix + "§7Debug was §aexecuted§8!");

                } else if (params[0].equalsIgnoreCase("end") || params[0].equalsIgnoreCase("exit")) {

                    for (ICloudPlayer cloudPlayer : cloudPlayerManager) {
                        cloudPlayer.kick(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNetworkShutdown());
                    }
                    player.sendMessage(prefix + "§7Requesting §cShut down§8...");
                    PoloCloudAPI.getInstance().sendPacket(new MasterShutdownPacket());

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
                } else if (params[0].equalsIgnoreCase("list") ) {
                    String type = params[1];
                    if (type.equalsIgnoreCase("wrapper")) {

                        if (wrapperManager.getWrappers().isEmpty()) {
                            player.sendMessage(prefix + "§cHmm that's really weird! There are no §econnected Wrappers §cat the moment! How are you connected to this Server when no Wrapper has started it?");
                            return;
                        }

                        player.sendMessage(prefix + "----[Wrappers]----");
                        for (IWrapper wrapper : wrapperManager.getWrappers()) {
                            player.sendMessage(prefix + "§b" + wrapper.getName() + "§8#§b" + wrapper.getSnowflake() + " §8[§3" + wrapper.getServers().size() + " Servers§8]");
                        }
                        player.sendMessage(prefix + "----[/Wrappers]----");

                    } else if (type.equalsIgnoreCase("server")) {
                        if (gameServerManager.getAllCached().isEmpty()) {
                            player.sendMessage(prefix + "§cHmm that's really weird! There are no §eonline Servers §cat the moment! How are you even online on a non-existent server?");
                            return;
                        }

                        player.sendMessage(prefix + "----[Servers]----");
                        for (IGameServer gameServer : gameServerManager.getAllCached()) {
                            player.sendMessage(prefix + "§b" + gameServer.getName() + " §8[§3" + gameServer.getCloudPlayers().size() + " Players§8]");
                        }
                        player.sendMessage(prefix + "----[/Servers]----");

                    } else if (type.equalsIgnoreCase("player")) {
                        if (cloudPlayerManager.getAllCached().isEmpty()) {
                            player.sendMessage(prefix + "§cHmm that's really weird! There are no §eonline Servers §cat the moment! How are you even online on a non-existent server?");
                            return;
                        }

                        player.sendMessage(prefix + "----[Players]----");
                        for (ICloudPlayer cloudPlayer : cloudPlayerManager.getAllCached()) {
                            player.sendMessage(prefix + "§b" + cloudPlayer.getName() + " §8[§3" + cloudPlayer.getProxyServer().getName() + "§8@§b" + cloudPlayer.getMinecraftServer().getName() + "§8]");
                        }
                        player.sendMessage(prefix + "----[/Players]----");

                    } else if (type.equalsIgnoreCase("template")) {
                        if (templateManager.getTemplates().isEmpty()) {
                            player.sendMessage(prefix + "§cHmm that's really weird! There are no §eloaded Templates §cat the moment! How are you even online on a non-existent Template?");
                            return;
                        }

                        player.sendMessage(prefix + "----[Templates]----");
                        for (ITemplate template : templateManager.getTemplates()) {
                            player.sendMessage(prefix + "§b" + template.getName() + " §8(§b" + template.getTemplateType() + " §8@ §b" + template.getVersion().getTitle() + "§8)" + " §8[§3" + template.getServers().size() + " Servers§8]");
                        }
                        player.sendMessage(prefix + "----[/Servers]----");
                    } else {
                        sendHelp(player);
                    }
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
                    player.sendMessage(prefix + "Registered » §b" + gameServer.isRegistered());
                    player.sendMessage(prefix + "Status » §b" + gameServer.getStatus());
                    player.sendMessage(prefix + "Visible » §b" + gameServer.getServiceVisibility());
                    player.sendMessage(prefix + "Started time » §b" + gameServer.getStartTime());
                    if (gameServer.getProperties().isEmpty()) {
                        player.sendMessage(prefix + "§7Properties » §cNone");
                    } else {
                        player.sendMessage(prefix + "§7Properties » §b" + gameServer.getProperties().size());
                        for (IProperty property : gameServer.getProperties()) {
                            player.sendMessage(prefix + "  §8> §b" + property.getName() + " §8: §3" + property.getJsonValue());
                        }
                    }
                    player.sendMessage(prefix + "----[/Information]----");

                } else if (params[0].equalsIgnoreCase("stop") || params[0].equalsIgnoreCase("shutdown")) {

                    String server = params[1];
                    IGameServer cached = gameServerManager.getCached(server);

                    if (cached == null) {
                        player.sendMessage(prefix + "§cThere is no online GameServer with name '§e" + server + "§c'!");
                        return;
                    }

                    cached.terminate();

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
                            player.sendMessage(prefix + "Copying §b" + cached.getName() + "'s §7worlds into template §a" + cached.getTemplate().getName() + "§8...");
                            templateManager.copyServer(cached, ITemplateManager.Type.WORLD);
                        } else if (type.equalsIgnoreCase("entire")) {
                            player.sendMessage(prefix + "Copying §b" + cached.getName() + " §7entirely into template §a" + cached.getTemplate().getName() + "§8...");
                            templateManager.copyServer(cached, ITemplateManager.Type.ENTIRE);
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
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();

        player.sendMessage(prefix + "----[Cloud]----");
        player.sendMessage(prefix + "§8» §b/cloud stop/shutdown <server> §7Shuts down a gameserver");
        player.sendMessage(prefix + "§8» §b/cloud end/exit §7Shuts down the cloud");
        player.sendMessage(prefix + "§8» §b/cloud reload/rl §7Reloads the cloud");
        player.sendMessage(prefix + "§8» §b/cloud start <template> §7Starts a new gameserver");
        player.sendMessage(prefix + "§8» §b/cloud start <server> <amount> §7Starts multiple new gameservers at once");
        player.sendMessage(prefix + "§8» §b/cloud info (<server>) §7Gets information of a gameserver or of the whole cloud");
        player.sendMessage(prefix + "§8» §b/cloud list <server/wrapper/template/player> §7Lists online values");
        player.sendMessage(prefix + "§8» §b/cloud copy <server> worlds/entire §7Copies the temporary files of a server into its template");
        player.sendMessage(prefix + "§8» §b/cloud execute <server> <command> §7Executes a command on a gameserver");
        player.sendMessage(prefix + "----[/Cloud]----");
    }

}
