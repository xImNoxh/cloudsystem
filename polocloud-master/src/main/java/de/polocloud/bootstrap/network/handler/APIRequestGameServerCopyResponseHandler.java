package de.polocloud.bootstrap.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class APIRequestGameServerCopyResponseHandler extends IPacketHandler<Packet> {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        APIRequestGameServerCopyResponsePacket packet = (APIRequestGameServerCopyResponsePacket) obj;
        if (packet.isFailed()) {
            Logger.log(LoggerType.ERROR, "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage());
        } else {
            Logger.log(LoggerType.INFO, "Successfully copied the gameserver " + packet.getGameservername() + " to its template!");
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return APIRequestGameServerCopyResponsePacket.class;
    }
}
