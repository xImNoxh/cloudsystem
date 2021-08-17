package de.polocloud.plugin.bootstrap.proxy.events;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

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
    public void handle(ServerPing serverPing) {
        serverPing.getPlayers().setMax(CloudPlugin.getCloudPluginInstance().thisService().getMaxPlayers());
        serverPing.setDescriptionComponent(new TextComponent(CloudPlugin.getCloudPluginInstance().thisService().getMotd()));
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (cloudPlugin.thisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(property.getGameServerMaintenanceMessage()));
            return;
        }

        if (ProxyServer.getInstance().getPlayers().size() - 1 >= cloudPlugin.thisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.getPlayer().disconnect(property.getGameServerMaxPlayersMessage());
            return;
        }
    }

    @EventHandler
    public void handle(LoginEvent event) {
        UUID requestId = UUID.randomUUID();
        property.getGameServerLoginEvents().put(requestId, event);
        event.registerIntent(this.plugin);
        networkClient.sendPacket(new GameServerPlayerRequestJoinPacket(requestId));
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (property.getGameServerLoginServers().containsKey(event.getPlayer().getUniqueId())) {
            String targetServer = property.getGameServerLoginServers().remove(event.getPlayer().getUniqueId());
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
