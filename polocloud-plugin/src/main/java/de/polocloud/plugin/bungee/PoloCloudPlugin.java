package de.polocloud.plugin.bungee;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestResponsePacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.plugin.CloudBootstrap;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.executes.call.BungeeCommandCall;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PoloCloudPlugin extends Plugin {

    public static Map<UUID, LoginEvent> loginEvents = new HashMap<>();
    public static Map<UUID, String> loginServers = new HashMap<>();

    @Override
    public void onEnable() {

        CloudBootstrap bootstrap = new CloudBootstrap();

        bootstrap.connect(-1);

        bootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                MasterRequestServerListUpdatePacket packet = (MasterRequestServerListUpdatePacket) obj;
                ProxyServer.getInstance().getServers().put(packet.getSnowflake() + "", ProxyServer.getInstance().constructServerInfo(
                    packet.getSnowflake() + "",
                    InetSocketAddress.createUnresolved(packet.getHost(), packet.getPort()),
                    "PoloCloud",
                    false
                ));


            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterRequestServerListUpdatePacket.class;
            }
        });

        bootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                ProxyServer.getInstance().stop();
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerShutdownPacket.class;
            }
        });

        bootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

                MasterPlayerRequestResponsePacket packet = (MasterPlayerRequestResponsePacket) obj;

                LoginEvent loginEvent = loginEvents.remove(packet.getUuid());
                loginServers.put(loginEvent.getConnection().getUniqueId(), packet.getSnowflake() + "");

                loginEvent.completeIntent(PoloCloudPlugin.this);

            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterPlayerRequestResponsePacket.class;
            }
        });

        getProxy().getPluginManager().registerListener(this, new BungeeConnectListener(this, bootstrap, this));
        new CloudPlugin(bootstrap, new BungeeCommandCall());
    }

}
