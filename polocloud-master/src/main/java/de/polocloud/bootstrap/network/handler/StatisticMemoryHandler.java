package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.statistics.StatisticPacket;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class StatisticMemoryHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        StatisticPacket packet = (StatisticPacket) obj;
        try {
            IGameServer gameServer = gameServerManager.getGameServerByConnection(ctx).get();

            long ping = System.currentTimeMillis() - packet.getTimestamp();
            SimpleGameServer simpleGameServer = (SimpleGameServer) gameServer;
            simpleGameServer.setPing(ping);
            simpleGameServer.setTotalMemory(packet.getCurrentMemory());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return StatisticPacket.class;
    }

}
