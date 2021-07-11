package de.polocloud.plugin;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecutePacket;
import de.polocloud.plugin.executes.CloudCommandExecute;
import io.netty.channel.ChannelHandlerContext;

public class CloudPlugin {

    private static CloudPlugin instance;
    private CloudCommandExecute cloudCommandExecute;

    public CloudPlugin(CloudBootstrap cloudBootstrap, CloudCommandExecute cloudCommandExecute) {
        instance = this;

        cloudBootstrap.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
                GameServerExecutePacket gameServerExecutePacket = (GameServerExecutePacket) obj;
                cloudCommandExecute.callExecute(gameServerExecutePacket.getCommand());
            }

            @Override
            public Class<? extends IPacket> getPacketClass() {
                return GameServerExecutePacket.class;
            }
        });



    }

    public static CloudPlugin getInstance() {
        return instance;
    }

}
