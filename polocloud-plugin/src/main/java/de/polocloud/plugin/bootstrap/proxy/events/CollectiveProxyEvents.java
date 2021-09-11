package de.polocloud.plugin.bootstrap.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.player.CloudPlayerLackMaintenanceEvent;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.player.def.SimplePlayerConnection;
import de.polocloud.api.scheduler.Scheduler;
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

public class CollectiveProxyEvents implements Listener {

    private final NetworkClient networkClient;

    public CollectiveProxyEvents(Plugin plugin) {

        this.networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }


    @EventHandler (priority = EventPriority.LOW)
    public void handle(ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();
        serverPing.getPlayers().setMax(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers());
        serverPing.setDescription(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMotd());

        event.setResponse(serverPing);
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
            connection.getUniqueId(),
            connection.getName(),
            connection.getAddress().getAddress().getHostAddress(),
            connection.getAddress().getPort(),
            MinecraftProtocol.valueOf(connection.getVersion()),
            connection.isOnlineMode(),
            connection.isLegacy()
        );

        SimpleCloudPlayer cloudPlayer = new SimpleCloudPlayer(connection.getName(), connection.getUniqueId(), playerConnection);

        cloudPlayer.setProxyServer(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName());
        cloudPlayer.update();

        PoloCloudAPI.getInstance().getCloudPlayerManager().register(cloudPlayer);
        PoloCloudAPI.getInstance().sendPacket(new CloudPlayerRegisterPacket(cloudPlayer));

        event.setCancelled(false);
    }

    @EventHandler
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
            event.setCancelled(false);
            event.setTarget(serverInfo);

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
