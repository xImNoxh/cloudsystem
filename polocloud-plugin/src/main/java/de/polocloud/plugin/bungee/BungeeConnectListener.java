package de.polocloud.plugin.bungee;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.plugin.CloudBootstrap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BungeeConnectListener implements Listener {

    private Plugin plugin;
    private CloudBootstrap bootstrap;
    private PoloCloudPlugin poloCloudPlugin;

    public BungeeConnectListener(Plugin plugin, CloudBootstrap bootstrap, PoloCloudPlugin poloCloudPlugin) {
        this.plugin = plugin;
        this.bootstrap = bootstrap;
        this.poloCloudPlugin = poloCloudPlugin;
    }

    @EventHandler
    public void handlePing(ProxyPingEvent event){
       // event.getResponse().setDescription(poloCloudPlugin.getMotd());
        //event.getResponse().getPlayers().setMax(poloCloudPlugin.getMaxPlayers());
       // event.getResponse().getPlayers().setOnline(poloCloudPlugin.getOnlinePlayers());
    }


    @EventHandler
    public void handle(PostLoginEvent event){
       // if(poloCloudPlugin.isMaintenance() && (!event.getPlayer().hasPermission("*") && !event.getPlayer().hasPermission("cloud.maintenance"))){
         //   event.getPlayer().disconnect(poloCloudPlugin.getMaintenanceMessage());
       // }
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

    @EventHandler
    public void handle(ServerConnectedEvent event) {
        //send player update to master
        bootstrap.sendPacket(new GameServerPlayerUpdatePacket(event.getPlayer().getUniqueId(), event.getPlayer().getName(), Long.parseLong(event.getServer().getInfo().getName())));
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        bootstrap.sendPacket(new GameServerPlayerDisconnectPacket(event.getPlayer().getUniqueId()));
    }

}
