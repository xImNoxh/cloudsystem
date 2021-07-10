package de.polocloud.plugin.bungee;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.plugin.CloudBootstrap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BungeeConnectListener implements Listener {

    private Plugin plugin;
    private CloudBootstrap bootstrap;

    public BungeeConnectListener(Plugin plugin, CloudBootstrap bootstrap) {
        this.plugin = plugin;
        this.bootstrap = bootstrap;
    }


    @EventHandler
    public void handle(LoginEvent event) {

        UUID requestId = UUID.randomUUID();
        PoloCloudPlugin.loginEvents.put(requestId, event);
        event.registerIntent(this.plugin);
        bootstrap.sendPacket(new GameServerPlayerRequestJoinPacket(requestId));

    }

    @EventHandler
    public void handle(ServerConnectEvent event) {

        if (PoloCloudPlugin.loginServers.containsKey(event.getPlayer().getUniqueId())) {
            String targetServer = PoloCloudPlugin.loginServers.remove(event.getPlayer().getUniqueId());

            event.setTarget(ProxyServer.getInstance().getServerInfo(targetServer));
        }

    }

}
