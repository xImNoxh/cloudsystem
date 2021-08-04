package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerKickPacket;
import de.polocloud.plugin.bootstrap.SpigotBootstrap;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;

public class NetworkSpigotRegister extends NetworkRegister {

    public NetworkSpigotRegister(NetworkClient networkClient, SpigotBootstrap spigotBootstrap) {
        super(networkClient);

        register((channelHandlerContext, packet) -> {
            MasterPlayerKickPacket object = (MasterPlayerKickPacket) packet;
            if (Bukkit.getPlayer(object.getUuid()) != null) {
                Bukkit.getPlayer(object.getUuid()).kickPlayer(object.getMessage());
            }
        }, MasterPlayerKickPacket.class).register((channelHandlerContext, packet) -> {
            CommandListAcceptorPacket object = (CommandListAcceptorPacket) packet;
            spigotBootstrap.getCommandReader().setAllowedCommands(object.getCommandList());
            spigotBootstrap.getCommandReader().getAllowedCommands().addAll(object.getAliases());
        }, CommandListAcceptorPacket.class);
    }

}

