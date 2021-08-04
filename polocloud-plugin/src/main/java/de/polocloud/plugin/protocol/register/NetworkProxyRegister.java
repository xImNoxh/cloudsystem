package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.*;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.UUID;

public class NetworkProxyRegister extends NetworkRegister {

    public NetworkProxyRegister(CloudPlugin cloudPlugin, Plugin plugin) {
        super(cloudPlugin.getNetworkClient());

        NetworkClient networkClient = cloudPlugin.getNetworkClient();
        GameServerProperty property = cloudPlugin.getProperty();

        register((channelHandlerContext, packet) -> {
            PermissionCheckResponsePacket object = (PermissionCheckResponsePacket) packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(object.getPlayer());
            if (player != null) object.setResponse(player.hasPermission(object.getPermission()));
            networkClient.sendPacket(object);
        }, PermissionCheckResponsePacket.class)

            .register((channelHandlerContext, packet) -> {
                ProxyTablistUpdatePacket object = (ProxyTablistUpdatePacket) packet;
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(object.getUuid());
                if (player != null)
                    player.setTabHeader(new TextComponent(object.getHeader()), new TextComponent(object.getFooter()));
            }, ProxyTablistUpdatePacket.class)

            .register((channelHandlerContext, packet) -> {
                MasterPlayerSendMessagePacket object = (MasterPlayerSendMessagePacket) packet;
                UUID uuid = object.getUuid();
                if (ProxyServer.getInstance().getPlayer(uuid) != null)
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(new TextComponent(object.getMessage()));
            }, MasterPlayerSendMessagePacket.class)

            .register((channelHandlerContext, packet) -> {
                MasterPlayerSendToServerPacket object = (MasterPlayerSendToServerPacket) packet;
                UUID uuid = object.getUuid();
                if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(object.getTargetServer());
                    if (serverInfo != null) ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
                }
            }, MasterPlayerSendToServerPacket.class)

            .register((channelHandlerContext, packet) -> {
                MasterPlayerKickPacket object = (MasterPlayerKickPacket) packet;
                UUID uuid = object.getUuid();
                if (ProxyServer.getInstance().getPlayer(uuid) != null)
                    ProxyServer.getInstance().getPlayer(uuid).disconnect(new TextComponent(object.getMessage()));
            }, MasterPlayerKickPacket.class)

            .register((channelHandlerContext, packet) -> ProxyServer.getInstance().getServers().remove(((GameServerUnregisterPacket) packet).getName()), GameServerUnregisterPacket.class)

            .register((channelHandlerContext, packet) -> {
                MasterRequestServerListUpdatePacket object = (MasterRequestServerListUpdatePacket) packet;
                ProxyServer.getInstance().getServers().put(object.getName(), ProxyServer.getInstance().constructServerInfo(
                    object.getName(), InetSocketAddress.createUnresolved(object.getHost(), object.getPort()),
                    "PoloCloud", false
                ));
            }, MasterRequestServerListUpdatePacket.class)

            .register((channelHandlerContext, packet) -> {
                MasterPlayerRequestJoinResponsePacket object = (MasterPlayerRequestJoinResponsePacket) packet;
                LoginEvent loginEvent = CloudPlugin.getInstance().getProperty().getGameServerLoginEvents().remove(object.getUuid());
                if (object.getSnowflake() == -1) {
                    loginEvent.setCancelled(true);
                    loginEvent.setCancelReason(new TextComponent("§cEs wurde kein fallback Server gefunden!"));
                } else property.getGameServerLoginServers().put(loginEvent.getConnection().getUniqueId(), object.getServiceName());
                loginEvent.completeIntent(plugin);
            }, MasterPlayerRequestJoinResponsePacket.class);
    }
}
