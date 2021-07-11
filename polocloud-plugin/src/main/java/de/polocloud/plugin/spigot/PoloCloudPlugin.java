package de.polocloud.plugin.spigot;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.master.MasterKickPlayerPacket;
import de.polocloud.plugin.CloudBootstrap;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.executes.call.SpigotCommandCall;
import de.polocloud.plugin.spigot.listener.CloudSpigotEvents;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PoloCloudPlugin extends JavaPlugin {

    public static List<String> allowedProxies = new ArrayList<>();

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

        bootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                MasterKickPlayerPacket packet = (MasterKickPlayerPacket) obj;
                Bukkit.getPlayer(packet.getUuid()).kickPlayer(packet.getMessage());
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterKickPlayerPacket.class;
            }
        });

        new CloudPlugin(bootstrap, new SpigotCommandCall());

        Bukkit.getPluginManager().registerEvents(new CloudSpigotEvents(bootstrap), this);

    }
}
