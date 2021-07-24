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

    private NetworkClient networkClient;
    private SpigotBootstrap spigotBootstrap;

    public NetworkSpigotRegister(NetworkClient networkClient, SpigotBootstrap spigotBootstrap) {
        super(networkClient);
        this.networkClient = networkClient;
        this.spigotBootstrap = spigotBootstrap;

        registerMasterKickPlayerPacket();
    }


    public void registerMasterKickPlayerPacket() {
        networkClient.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                MasterPlayerKickPacket packet = (MasterPlayerKickPacket) obj;
                if (Bukkit.getPlayer(packet.getUuid()) != null) {
                    Bukkit.getPlayer(packet.getUuid()).kickPlayer(packet.getMessage());
                }
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return MasterPlayerKickPacket.class;
            }
        });

        networkClient.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
                CommandListAcceptorPacket packet = (CommandListAcceptorPacket) obj;
                spigotBootstrap.getCommandReader().setAllowedCommands(packet.getCommandList());
            }

            @Override
            public Class<? extends Packet> getPacketClass() {
                return CommandListAcceptorPacket.class;
            }
        });
    }


}
