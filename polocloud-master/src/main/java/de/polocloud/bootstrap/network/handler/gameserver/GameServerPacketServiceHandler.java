package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

public class GameServerPacketServiceHandler extends GameServerPacketController {

    @Inject
    public ICloudPlayerManager playerManager;

    public GameServerPacketServiceHandler() {

        new SimplePacketHandler<APIRequestGameServerCopyResponsePacket>(APIRequestGameServerCopyResponsePacket.class, packet ->
            Logger.log(packet.isFailed() ? LoggerType.ERROR : LoggerType.INFO, packet.isFailed() ?
                "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage()
                : "Successfully copied the gameserver " + packet.getGameservername() + " to its template!"));

        new SimplePacketHandler<GameServerControlPlayerPacket>(GameServerControlPlayerPacket.class, (packet) -> {
            //TODO fix only proxy join ctx.writeAndFlush(new MasterKickPlayerPacket(uuid, "§cPlease connect to the Proxy!"));
        });

    }
}
