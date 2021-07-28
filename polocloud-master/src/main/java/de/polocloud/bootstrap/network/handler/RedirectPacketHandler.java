package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.RedirectPacket;
import de.polocloud.logger.log.Logger;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class RedirectPacketHandler extends IPacketHandler<Packet> {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        RedirectPacket packet = (RedirectPacket) obj;

        long snowflake = packet.getSnowflake();
        Packet packetData = packet.getPacket();


        try {
            IGameServer iGameServer = gameServerManager.getGameSererBySnowflake(snowflake).get();
            iGameServer.sendPacket(packetData);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return RedirectPacket.class;
    }
}
