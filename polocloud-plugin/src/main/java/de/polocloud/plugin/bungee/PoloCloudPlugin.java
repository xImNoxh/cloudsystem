package de.polocloud.plugin.bungee;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerListUpdatePacket;
import de.polocloud.plugin.CloudBootstrap;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class PoloCloudPlugin extends Plugin {

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

        getProxy().getPluginManager().registerListener(this, new TestListener());


    }
}
