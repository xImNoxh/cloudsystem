package de.polocloud.plugin.listener;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.connections.NetworkLoginCache;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class CollectiveProxyEvents implements Listener {

    private Plugin plugin;
    private NetworkClient networkClient;
    private NetworkLoginCache networkLoginCache;

    public CollectiveProxyEvents(Plugin plugin, NetworkClient networkClient, NetworkLoginCache networkLoginCache) {
        this.plugin = plugin;
        this.networkClient = networkClient;
        this.networkLoginCache = networkLoginCache;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void handle(LoginEvent event) {
        UUID requestId = UUID.randomUUID();
        networkLoginCache.getLoginEvents().put(requestId, event);
        event.registerIntent(this.plugin);
        networkClient.sendPacket(new GameServerPlayerRequestJoinPacket(requestId));
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (networkLoginCache.getLoginServers().containsKey(event.getPlayer().getUniqueId())) {
            String targetServer = networkLoginCache.getLoginServers().remove(event.getPlayer().getUniqueId());
            event.setTarget(ProxyServer.getInstance().getServerInfo(targetServer));
        }
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
