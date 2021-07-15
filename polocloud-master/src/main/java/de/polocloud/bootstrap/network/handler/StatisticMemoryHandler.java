package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.statistics.StatisticPacket;
import io.netty.channel.ChannelHandlerContext;

public class StatisticMemoryHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        /*
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

         */
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return StatisticPacket.class;
    }

}
