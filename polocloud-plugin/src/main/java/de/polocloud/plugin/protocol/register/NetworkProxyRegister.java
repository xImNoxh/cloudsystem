package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.proxy.ProxyMotdUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestResponsePacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import de.polocloud.plugin.protocol.connections.NetworkLoginCache;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;

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
        registerCloudMotdUpdatePacket();
    }

    private void registerGameServerUnregisterPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                GameServerUnregisterPacket packet = (GameServerUnregisterPacket) obj;
                ProxyServer.getInstance().getServers().remove(packet.getName());
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerUnregisterPacket.class;
            }
        });
    }

    public void registerMasterRequestServerListUpdatePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                MasterRequestServerListUpdatePacket packet = (MasterRequestServerListUpdatePacket) obj;
                ProxyServer.getInstance().getServers().put(packet.getName(), ProxyServer.getInstance().constructServerInfo(
                    packet.getName(), InetSocketAddress.createUnresolved(packet.getHost(), packet.getPort()),
                    "PoloCloud", false
                ));
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterRequestServerListUpdatePacket.class;
            }
        });
    }

    public void registerMasterPlayerRequestResponsePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                MasterPlayerRequestResponsePacket packet = (MasterPlayerRequestResponsePacket) obj;
                LoginEvent loginEvent = networkLoginCache.getLoginEvents().remove(packet.getUuid());
                if(packet.getSnowflake() == -1){
                    loginEvent.setCancelled(true);
                    loginEvent.setCancelReason("§cEs wurde kein fallback Server gefunden!");
                }else{
                    networkLoginCache.getLoginServers().put(loginEvent.getConnection().getUniqueId(), packet.getServiceName());
                }
                loginEvent.completeIntent(plugin);
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterPlayerRequestResponsePacket.class;
            }
        });
    }

    public void registerCloudMotdUpdatePacket(){
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                 ProxyMotdUpdatePacket packet = (ProxyMotdUpdatePacket) obj;

            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return ProxyMotdUpdatePacket.class;
            }
        });
    }

}
