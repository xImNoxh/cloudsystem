package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.master.MasterKickPlayerPacket;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.server.APIGameServerManager;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;

import java.util.UUID;

public class NetworkSpigotRegister extends NetworkRegister {

    private NetworkClient networkClient;

    public NetworkSpigotRegister(NetworkClient networkClient) {
        super(networkClient);
        this.networkClient = networkClient;

        registerMasterKickPlayerPacket();
    }


    public void registerMasterKickPlayerPacket() {
        networkClient.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                MasterKickPlayerPacket packet = (MasterKickPlayerPacket) obj;
                if (Bukkit.getPlayer(packet.getUuid()) != null) {
                    Bukkit.getPlayer(packet.getUuid()).kickPlayer(packet.getMessage());
                }
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return MasterKickPlayerPacket.class;
            }
        });
    }


}
