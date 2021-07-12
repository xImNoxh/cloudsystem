package de.polocloud.plugin.protocol.register;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.NetworkRegister;
import io.netty.channel.ChannelHandlerContext;

public class NetworkPluginRegister extends NetworkRegister {

    private BootstrapFunction bootstrapFunction;

    public NetworkPluginRegister(NetworkClient networkClient, BootstrapFunction bootstrapFunction) {
        super(networkClient);

        this.bootstrapFunction = bootstrapFunction;

        registerGameServerExecutePacket();
        registerGameServerShutdownPacket();
    }

    public void registerGameServerExecutePacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                GameServerExecuteCommandPacket gameServerExecutePacket = (GameServerExecuteCommandPacket) obj;
                bootstrapFunction.executeCommand(gameServerExecutePacket.getCommand());
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerExecuteCommandPacket.class;
            }
        });
    }

    public void registerGameServerShutdownPacket() {
        getNetworkClient().registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                bootstrapFunction.shutdown();
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerShutdownPacket.class;
            }
        });
    }


}
