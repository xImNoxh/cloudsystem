package de.polocloud.bootstrap.network.handler.gameserver;

import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

public class GameServerHandler extends GameServerPacketController {

    public GameServerHandler() {

        new SimplePacketHandler<APIRequestGameServerCopyResponsePacket>(APIRequestGameServerCopyResponsePacket.class, packet ->
            Logger.log(packet.isFailed() ? LoggerType.ERROR : LoggerType.INFO, packet.isFailed() ?
            "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage()
            : "Successfully copied the gameserver " + packet.getGameservername() + " to its template!"));

    }
}
