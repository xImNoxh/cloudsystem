package de.polocloud.plugin.bootstrap.proxy.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.SimpleCloudPlayer;
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
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class CollectiveProxyEvents implements Listener {

    private CloudPlugin cloudPlugin;
    private Plugin plugin;

    private GameServerProperty property;
    private NetworkClient networkClient;

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
        SimpleCloudPlayer cloudPlayer = (SimpleCloudPlayer) PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName());
        if (BaseComponent.toPlainText(event.getKickReasonComponent()).equalsIgnoreCase("Server closed")) {
            event.setCancelled(true);

            IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);
            if (fallback == null || ProxyServer.getInstance().getServerInfo(fallback.getName()) == null) {
                TextComponent textComponent = new TextComponent("§8[§bCloudPlugin§8] §cCouldn't find a suitable fallback to connect you to!");
                event.getPlayer().disconnect(textComponent);
                return;
            }
            event.setCancelled(false);
            event.setCancelServer(ProxyServer.getInstance().getServerInfo(fallback.getName()));

        }
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
        if (proxiedPlayer.getServer() == null) {
            IGameServer fallback = PoloCloudAPI.getInstance().getFallbackManager().getFallback(cloudPlayer);
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(fallback.getName());

            if (serverInfo == null) {
                TextComponent textComponent = new TextComponent("§8[§bCloudPlugin§8] §cCouldn't find a suitable fallback to connect you to!");
                proxiedPlayer.disconnect(textComponent);
                event.setCancelled(true);
                return;
            }
            event.setCancelled(false);
            event.setTarget(serverInfo);

            Scheduler.runtimeScheduler().schedule(() -> {
                cloudPlayer.setMinecraftServer(serverInfo.getName());
                cloudPlayer.update();
            }, () -> PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(proxiedPlayer.getName()) != null);

        } else {
            ServerInfo target = event.getTarget();
            cloudPlayer.setMinecraftServer(target.getName());
            cloudPlayer.update();
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
            System.out.println("Tried unregistering nulled CloudPlayer");
            return;
        }
        PoloCloudAPI.getInstance().getCloudPlayerManager().unregisterPlayer(cloudPlayer);
        networkClient.sendPacket(new GameServerPlayerDisconnectPacket(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
        networkClient.sendPacket(new CloudPlayerUnregisterPacket(PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName())));
    }


}
