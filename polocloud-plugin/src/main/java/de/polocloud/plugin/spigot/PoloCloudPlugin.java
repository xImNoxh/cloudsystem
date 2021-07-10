package de.polocloud.plugin.spigot;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestResponsePacket;
import de.polocloud.plugin.CloudBootstrap;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PoloCloudPlugin extends JavaPlugin {


    @Override
    public void onEnable() {

        CloudBootstrap bootstrap = new CloudBootstrap();
        bootstrap.connect(Bukkit.getPort());

        bootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                Bukkit.shutdown();
            }
            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerShutdownPacket.class;
            }
        });
    }
}
