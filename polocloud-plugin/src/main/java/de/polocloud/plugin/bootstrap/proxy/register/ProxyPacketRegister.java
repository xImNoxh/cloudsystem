package de.polocloud.plugin.bootstrap.proxy.register;

import de.polocloud.api.network.protocol.packet.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestJoinResponsePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.property.GameServerProperty;
import de.polocloud.plugin.protocol.register.SimplePacketRegister;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ProxyPacketRegister {

    public ProxyPacketRegister(Plugin plugin) {

        NetworkClient networkClient = CloudPlugin.getCloudPluginInstance().getNetworkClient();
        GameServerProperty property = CloudPlugin.getCloudPluginInstance().getGameServerProperty();

        new SimplePacketRegister<MasterPlayerRequestJoinResponsePacket>(MasterPlayerRequestJoinResponsePacket.class, packet -> {
            LoginEvent loginEvent = property.getGameServerLoginEvents().remove(packet.getUuid());
            if (packet.getSnowflake() == -1) {
                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(new TextComponent("§cEs wurde kein fallback Server gefunden!"));
            } else
                property.getGameServerLoginServers().put(loginEvent.getConnection().getUniqueId(), packet.getServiceName());
            loginEvent.completeIntent(plugin);
        });

        new SimplePacketRegister<MasterRequestServerListUpdatePacket>(MasterRequestServerListUpdatePacket.class, packet -> {
            ProxyServer.getInstance().getServers().put(packet.getName(), ProxyServer.getInstance().constructServerInfo(
                packet.getName(), InetSocketAddress.createUnresolved(packet.getHost(), packet.getPort()),
                "PoloCloud", false
            ));
        });

        new SimplePacketRegister<MasterPlayerSendToServerPacket>(MasterPlayerSendToServerPacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(packet.getTargetServer());
                if (serverInfo != null) ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
            }
        });

        new SimplePacketRegister<MasterPlayerSendMessagePacket>(MasterPlayerSendMessagePacket.class, packet -> {
            UUID uuid = packet.getUuid();
            if (ProxyServer.getInstance().getPlayer(uuid) != null)
                ProxyServer.getInstance().getPlayer(uuid).sendMessage(new TextComponent(packet.getMessage()));
        });

        new SimplePacketRegister<GameServerUnregisterPacket>(GameServerUnregisterPacket.class, packet -> {
            ProxyServer.getInstance().getServers().remove(packet.getName());
        });

        new SimplePacketRegister<ProxyTablistUpdatePacket>(ProxyTablistUpdatePacket.class, packet -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            if (player != null)
                player.setTabHeader(new TextComponent(packet.getHeader()), new TextComponent(packet.getFooter()));
        });

        new SimplePacketRegister<PermissionCheckResponsePacket>(PermissionCheckResponsePacket.class, packet -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
            if (player != null) packet.setResponse(player.hasPermission(packet.getPermission()));
            networkClient.sendPacket(packet);
        });

    }
}
