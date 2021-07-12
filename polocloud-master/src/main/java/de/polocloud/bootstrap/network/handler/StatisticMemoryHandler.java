package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.statistics.StatisticMemoryPacket;
import io.netty.channel.ChannelHandlerContext;

public class StatisticMemoryHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        StatisticMemoryPacket packet = (StatisticMemoryPacket) obj;
        IGameServer gameServer = gameServerManager.getGameServerByConnection(ctx);
        gameServer.setTotalMemory(packet.getCurrentMemory());
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return StatisticMemoryPacket.class;
    }

}
