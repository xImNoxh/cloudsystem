package de.polocloud.plugin.bootstrap.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class CollectiveProxyEvents implements Listener {

    private CloudPlugin cloudPlugin;
    private Plugin plugin;

    private final GameServerProperty property;
    private final NetworkClient networkClient;

    public CollectiveProxyEvents(Plugin plugin) {
        this.plugin = plugin;
        this.cloudPlugin = CloudPlugin.getCloudPluginInstance();

        this.property = CloudPlugin.getCloudPluginInstance().getGameServerProperty();
        this.networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
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
            event.getPlayer().disconnect("§8[§bCloudPlugin§8] §cSome internal Cache-Error occured!");
            return;
        }
        cloudPlayer.sendToFallback();
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(property.getGameServerMaintenanceMessage()));
            return;
        }

        if (ProxyServer.getInstance().getPlayers().size() - 1 >= PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(property.getGameServerMaxPlayersMessage()));
        }
    }

    @EventHandler
    public void handle(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        SimpleCloudPlayer cloudPlayer = new SimpleCloudPlayer(connection.getName(), connection.getUniqueId());

        cloudPlayer.setProxyServer(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName());
        cloudPlayer.update();

        PoloCloudAPI.getInstance().getCloudPlayerManager().registerPlayer(cloudPlayer);
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
                proxiedPlayer.disconnect(TextComponent.fromLegacyText("§8[§bCloudPlugin§8] §cCouldn't find a suitable fallback to connect you to!"));
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
            }, () -> PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(proxiedPlayer.getName()) != null);

        } else {
            //Player is switching servers

            //Setting new info for the player
            ServerInfo target = event.getTarget();
            cloudPlayer.setMinecraftServer(target.getName());
            cloudPlayer.update();
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
            PoloCloudAPI.getInstance().messageCloud(thisService.getName() + " tried to unregister '" + event.getPlayer().getName() + ":" + event.getPlayer().getUniqueId() + "' but it's ICloudPlayer was null!");
            return;
        }
        PoloCloudAPI.getInstance().getCloudPlayerManager().unregisterPlayer(cloudPlayer);
        networkClient.sendPacket(new CloudPlayerUnregisterPacket(cloudPlayer));
    }


}
