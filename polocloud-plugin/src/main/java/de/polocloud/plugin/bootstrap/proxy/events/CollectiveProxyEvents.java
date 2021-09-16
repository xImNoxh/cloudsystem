package de.polocloud.plugin.bootstrap.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.event.impl.other.ProxyConstructPlayerEvent;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.logger.def.Pair;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.player.def.SimplePlayerConnection;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.helper.GameServerVersion;
import de.polocloud.api.util.MinecraftProtocol;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.UUID;

public class CollectiveProxyEvents implements Listener {

    private final NetworkClient networkClient;

    public CollectiveProxyEvents(Plugin plugin) {

        this.networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(ProxyPingEvent event) {

        PendingConnection connection = event.getConnection();
        InetSocketAddress address = connection.getAddress();

        ServerPing serverPing = event.getResponse();

        MasterConfig masterConfig = PoloCloudAPI.getInstance().getMasterConfig();
        IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();

        int maxPlayers = thisService == null ? 0 : thisService.getMaxPlayers();
        int onlinePlayers;

        if (masterConfig != null && masterConfig.getProperties().isSyncProxyOnlinePlayers()) {
            onlinePlayers = PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size();
        } else {
            onlinePlayers = thisService == null ? 0 : thisService.getOnlinePlayers();
        }

        String[] maintenancePlayerInfo = thisService == null ? new String[0] : ((SimpleGameServer)thisService).getPlayerInfo();
        ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[maintenancePlayerInfo.length];

        for (int i = 0; i < maintenancePlayerInfo.length; i++) {
            sample[i] = new ServerPing.PlayerInfo(replace(maintenancePlayerInfo[i], thisService, address), UUID.randomUUID());
        }

        serverPing.setPlayers(new ServerPing.Players(maxPlayers, onlinePlayers, sample)); //Setting players
        if (thisService != null && thisService.getMotd() != null) {
            serverPing.setDescriptionComponent(new TextComponent(replace(thisService.getMotd(), thisService, address))); //Setting motd
        }

        if (thisService != null && ((SimpleGameServer) thisService).getVersionString() != null && !((SimpleGameServer) thisService).getVersionString().trim().isEmpty()) {
            serverPing.setVersion(new ServerPing.Protocol(replace(((SimpleGameServer) thisService).getVersionString(), thisService, address), -1)); //Setting version
        }

        event.setResponse(serverPing);
    }


    private String replace(String input, IGameServer gameServer, InetSocketAddress address) {

        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().stream().filter(cp -> {
            System.out.println(cp.getConnection().getHost() + ":" + cp.getConnection().getPort() + " - " + address.getAddress().getHostAddress() + ":" + address.getPort());
            return cp.getConnection().getHost().equalsIgnoreCase(address.getAddress().getHostAddress()) && cp.getConnection().getPort() == address.getPort();
        }).findFirst().orElse(null);

        if (cloudPlayer == null) {
            System.out.println("NULL");
            input = input.replace("%PROXY%", gameServer.getName());
        } else {
            input = input.replace("%PROXY%", cloudPlayer.getProxyServer().getName());
        }

        input = input.replace("%NUMBER%", String.valueOf(gameServer.getId()));
        input = input.replace("%MAX_PLAYERS%", String.valueOf(gameServer.getMaxPlayers()));
        input = input.replace("%ONLINE_PLAYERS%", String.valueOf(gameServer.getOnlinePlayers()));

        return input;
    }

    @EventHandler
    public void handle(ServerKickEvent event) {
        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName());
        if (cloudPlayer == null) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§cSome internal Cache-Error occurred!"));
            return;
        }
        cloudPlayer.sendToFallback();
    }



    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerLackMaintenanceEvent(PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getUniqueId())), cloudPlayerLackMaintenanceEvent -> {
                if(!cloudPlayerLackMaintenanceEvent.isCancelled()){
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getGroupMaintenanceMessage()));
                }
            });
            return;
        }

        if (ProxyServer.getInstance().getPlayers().size() - 1 >= PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getServiceIsFull()));
        }
    }

    @EventHandler
    public void handle(LoginEvent event) {
        PendingConnection connection = event.getConnection();

        IPlayerConnection playerConnection = new SimplePlayerConnection(
            connection.getAddress(),
            connection.getUniqueId(),
            connection.getName(),
            connection.getAddress().getAddress().getHostAddress(),
            connection.getAddress().getPort(),
            MinecraftProtocol.valueOf(connection.getVersion()),
            connection.isOnlineMode(),
            connection.isLegacy()
        );
        ProxyConstructPlayerEvent playerEvent = PoloCloudAPI.getInstance().getEventManager().fireEvent(new ProxyConstructPlayerEvent(playerConnection));

        ICloudPlayer cloudPlayer = playerEvent.getResult();

        if (cloudPlayer == null) {
            cloudPlayer = new SimpleCloudPlayer(connection.getName(), connection.getUniqueId(), playerConnection);
            ((SimpleCloudPlayer)cloudPlayer).setProxyServer(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName());
        }
        cloudPlayer.update();

        PoloCloudAPI.getInstance().getCloudPlayerManager().register(cloudPlayer);
        PoloCloudAPI.getInstance().sendPacket(new CloudPlayerRegisterPacket(cloudPlayer));

        event.setCancelled(false);
    }


    /**
     * Checks if the version of a player matches the version of a server
     *
     * @param playerProtocolId the protocol id of the player
     * @param gameServer the server to request
     * @return pair containing boolean (if event is cancelled) and String (message)
     */
    private Pair<Boolean, String> checkVersion(int playerProtocolId, IGameServer gameServer) {
        GameServerVersion version = gameServer.getTemplate().getVersion();
        boolean cancelled = false;
        String msg = null;

        if (version.getProtocolId() != -1) {
            int serverProtocolId = version.getProtocolId();
            MinecraftProtocol serverProtocol = MinecraftProtocol.valueOf(serverProtocolId);
            MinecraftProtocol playerProtocol = MinecraftProtocol.valueOf(playerProtocolId);


            if (!Arrays.asList(serverProtocol.getVersionNames()).contains(playerProtocol.getName())) {
                msg = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getWrongMinecraftVersion();

                msg = msg.replace("%server%", gameServer.getName());
                msg = msg.replace("%required_version%", serverProtocol.getNewestVersion());
                msg = msg.replace("%your_version%", playerProtocol.getNewestVersion());

                cancelled = true;
            }
        }
        return new Pair<>(cancelled, msg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(ServerConnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        SimpleCloudPlayer cloudPlayer = (SimpleCloudPlayer) PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName());

        //Player is joining the network
        if (proxiedPlayer.getServer() == null) {

            //Searching a fallback for the player
            IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);

            //No fallback was found
            if (fallback == null || ProxyServer.getInstance().getServerInfo(fallback.getName()) == null) {

                proxiedPlayer.disconnect(TextComponent.fromLegacyText(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getNoFallbackServer()));
                event.setCancelled(true);
                return;
            }

            //Sending player to fallback
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(fallback.getName());

            Pair<Boolean, String> eventData = checkVersion(proxiedPlayer.getPendingConnection().getVersion(), fallback);

            if (eventData.getKey()) {
                event.setCancelled(true);
                proxiedPlayer.disconnect(TextComponent.fromLegacyText(eventData.getValue()));
            } else {
                event.setCancelled(false);
                event.setTarget(serverInfo);
            }

            //Setting the new Server from the player
            Scheduler.runtimeScheduler().schedule(() -> {
                cloudPlayer.setMinecraftServer(serverInfo.getName());
                cloudPlayer.update();
                PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, cloudPlayer.getMinecraftServer(), null), serverEvent -> {
                    if (serverEvent.isCancelled()) {
                        IGameServer target = serverEvent.getTarget();
                        cloudPlayer.sendTo(target);
                    }
                });
            }, () -> PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(proxiedPlayer.getName()) != null);

        } else {
            //Player is switching servers

            if (cloudPlayer == null || cloudPlayer.getMinecraftServer() == null) {
                return;
            }
            if (event.isCancelled() || event.getTarget().getName().equalsIgnoreCase(cloudPlayer.getMinecraftServer().getName())) {
                return;
            }
            //Setting new info for the player
            ServerInfo target = event.getTarget();

            IGameServer targetGameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(target.getName());
            if (targetGameServer != null) {
                Pair<Boolean, String> eventData = checkVersion(proxiedPlayer.getPendingConnection().getVersion(), targetGameServer);

                if (eventData.getKey()) {
                    event.setCancelled(true);
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(eventData.getValue()));
                }
            }
            IGameServer from = cloudPlayer.getMinecraftServer();
            cloudPlayer.setMinecraftServer(target.getName());
            cloudPlayer.update();
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, cloudPlayer.getMinecraftServer(), from), serverEvent -> {
                if (serverEvent.isCancelled()) {
                    IGameServer t = serverEvent.getTarget();
                    cloudPlayer.sendTo(t);
                }
            });
        }
    }


    @EventHandler
    public void handleCommand(ChatEvent event) {
        if (event.isCommand() || event.isProxyCommand()) {

            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
            ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(proxiedPlayer.getUniqueId());

            if (PoloCloudAPI.getInstance().getCommandManager().runCommand(event.getMessage().split("/")[1], cloudPlayer)) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("MC|BSign") || event.getTag().equals("MC|BEdit")) event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName());
        if (cloudPlayer == null) {
            IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
            PoloCloudAPI.getInstance().messageCloud("§c" + thisService.getName() + " tried to unregister '§e" + event.getPlayer().getName() + ":" + event.getPlayer().getUniqueId() + "§c' but it's ICloudPlayer was null!");
            return;
        }
        PoloCloudAPI.getInstance().getCloudPlayerManager().unregister(cloudPlayer);
        networkClient.sendPacket(new CloudPlayerUnregisterPacket(cloudPlayer));
    }


}
