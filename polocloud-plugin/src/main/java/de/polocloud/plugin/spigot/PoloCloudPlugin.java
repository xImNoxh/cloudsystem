package de.polocloud.plugin.spigot;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestResponsePacket;
import de.polocloud.api.network.protocol.packet.master.MasterUpdateProxyListPacket;
import de.polocloud.plugin.CloudBootstrap;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.executes.call.SpigotCommandCall;
import de.polocloud.plugin.spigot.listener.CloudSpigotEvents;
import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.event.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PoloCloudPlugin extends JavaPlugin {

    public static List<String> allowedProxies = new ArrayList<>();

    public final static Object proxyListLOCK = new Object();

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
                MasterUpdateProxyListPacket packet = (MasterUpdateProxyListPacket) obj;
                synchronized (proxyListLOCK){
                    allowedProxies = packet.getProxyList();
                }
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterUpdateProxyListPacket.class;
            }
        });

        new CloudPlugin(bootstrap, new SpigotCommandCall());

        Bukkit.getPluginManager().registerEvents(new CloudSpigotEvents(), this);

    }
}
