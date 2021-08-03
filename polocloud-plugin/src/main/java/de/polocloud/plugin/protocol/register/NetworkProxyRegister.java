package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.*;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import de.polocloud.plugin.protocol.connections.NetworkLoginCache;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.UUID;

public class NetworkProxyRegister extends NetworkRegister {

    private Plugin plugin;
    private NetworkLoginCache networkLoginCache;

    public NetworkProxyRegister(NetworkClient networkClient, NetworkLoginCache networkLoginCache, Plugin plugin) {
        super(networkClient);

        this.plugin = plugin;
        this.networkLoginCache = networkLoginCache;

        registerMasterRequestServerListUpdatePacket();
        registerMasterPlayerRequestResponsePacket();
        registerGameServerUnregisterPacket();
        registerMasterPlayerKickPacket();
        registerMasterSendMessagePacket();
        registerMasterSendPlayerToPacket();
        registerPermissionCheckPacket(networkClient);
        registerTablistUpdatePacket();
    }

    private void registerPermissionCheckPacket(NetworkClient networkClient) {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                PermissionCheckResponsePacket packet = (PermissionCheckResponsePacket) obj;
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
                if (player != null) packet.setResponse(player.hasPermission(packet.getPermission()));
                networkClient.sendPacket(packet);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return PermissionCheckResponsePacket.class;
            }
        });
    }

    public void registerTablistUpdatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                ProxyTablistUpdatePacket packet = (ProxyTablistUpdatePacket) obj;
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
                if (player != null) {
                    player.setTabHeader(new TextComponent(packet.getHeader()), new TextComponent(packet.getFooter()));
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return ProxyTablistUpdatePacket.class;
            }
        });
    }

    private void registerMasterSendMessagePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterPlayerSendMessagePacket packet = (MasterPlayerSendMessagePacket) obj;
                UUID uuid = packet.getUuid();
                String message = packet.getMessage();
                if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(message);
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterPlayerSendMessagePacket.class;
            }
        });
    }

    private void registerMasterSendPlayerToPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterPlayerSendToServerPacket packet = (MasterPlayerSendToServerPacket) obj;

                UUID uuid = packet.getUuid();
                String targetServer = packet.getTargetServer();

                if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(targetServer);
                    if (serverInfo != null) {
                        ProxyServer.getInstance().getPlayer(uuid).connect(serverInfo);
                    }
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterPlayerSendToServerPacket.class;
            }
        });
    }

    private void registerMasterPlayerKickPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterPlayerKickPacket packet = (MasterPlayerKickPacket) obj;

                UUID uuid = packet.getUuid();
                String message = packet.getMessage();

                if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ProxyServer.getInstance().getPlayer(uuid).disconnect(message);
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterPlayerKickPacket.class;
            }
        });
    }

    private void registerGameServerUnregisterPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                GameServerUnregisterPacket packet = (GameServerUnregisterPacket) obj;
                ProxyServer.getInstance().getServers().remove(packet.getName());
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return GameServerUnregisterPacket.class;
            }
        });
    }

    public void registerMasterRequestServerListUpdatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterRequestServerListUpdatePacket packet = (MasterRequestServerListUpdatePacket) obj;
                
                ProxyServer.getInstance().getServers().put(packet.getName(), ProxyServer.getInstance().constructServerInfo(
                    packet.getName(), InetSocketAddress.createUnresolved(packet.getHost(), packet.getPort()),
                    "PoloCloud", false
                ));
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterRequestServerListUpdatePacket.class;
            }
        });
    }

    public void registerMasterPlayerRequestResponsePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler<Packet>() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterPlayerRequestJoinResponsePacket packet = (MasterPlayerRequestJoinResponsePacket) obj;
                LoginEvent loginEvent = networkLoginCache.getLoginEvents().remove(packet.getUuid());
                if (packet.getSnowflake() == -1) {
                    loginEvent.setCancelled(true);
                    loginEvent.setCancelReason("§cEs wurde kein fallback Server gefunden!");
                } else {
                    networkLoginCache.getLoginServers().put(loginEvent.getConnection().getUniqueId(), packet.getServiceName());
                }
                loginEvent.completeIntent(plugin);
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterPlayerRequestJoinResponsePacket.class;
            }
        });
    }

}
