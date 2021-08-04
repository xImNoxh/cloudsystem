package de.polocloud.plugin.listener;

import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class CollectiveProxyEvents implements Listener {

    private Plugin plugin;
    private NetworkClient networkClient;

    public CollectiveProxyEvents(Plugin plugin, NetworkClient networkClient) {
        this.plugin = plugin;
        this.networkClient = networkClient;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void handle(ServerKickEvent event) {
        if (BaseComponent.toPlainText(event.getKickReasonComponent()).equalsIgnoreCase("Server closed")) {
            event.setCancelled(true);
            networkClient.sendPacket(new APIRequestPlayerMoveFallbackPacket(event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void handle(ProxyPingEvent event) {
        event.getResponse().getPlayers().setMax(CloudPlugin.getInstance().getProperty().getGameServerMaxPlayers());
        event.getResponse().setDescription(CloudPlugin.getInstance().getProperty().getGameServerMotd());
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (CloudPlugin.getInstance().getProperty().isGameServerInMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(CloudPlugin.getInstance().getProperty().getGameServerMaintenanceMessage()));
            return;
        }

        if (ProxyServer.getInstance().getPlayers().size() - 1 >= CloudPlugin.getInstance().getProperty().getGameServerMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.getPlayer().disconnect(CloudPlugin.getInstance().getProperty().getGameServerMaxPlayersMessage());
            return;
        }
    }

    @EventHandler
    public void handle(LoginEvent event) {

        UUID requestId = UUID.randomUUID();
        CloudPlugin.getInstance().getProperty().getGameServerLoginEvents().put(requestId, event);
        event.registerIntent(this.plugin);
        networkClient.sendPacket(new GameServerPlayerRequestJoinPacket(requestId));
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (CloudPlugin.getInstance().getProperty().getGameServerLoginServers().containsKey(event.getPlayer().getUniqueId())) {
            String targetServer = CloudPlugin.getInstance().getProperty().getGameServerLoginServers().remove(event.getPlayer().getUniqueId());
            event.setTarget(ProxyServer.getInstance().getServerInfo(targetServer));
        }
    }

    @EventHandler
    public void handlePluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals("MC|BSign") || event.getTag().equals("MC|BEdit")) event.setCancelled(true);
    }

    @EventHandler
    public void handle(ServerConnectedEvent event) {
        networkClient.sendPacket(new GameServerPlayerUpdatePacket(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getServer().getInfo().getName()));
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        networkClient.sendPacket(new GameServerPlayerDisconnectPacket(event.getPlayer().getUniqueId(), event.getPlayer().getName()));
    }

}
