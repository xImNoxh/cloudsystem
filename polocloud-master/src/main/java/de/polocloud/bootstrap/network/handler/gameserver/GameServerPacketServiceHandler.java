package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.packet.RedirectPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerControlPlayerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUpdatePacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

public class GameServerPacketServiceHandler extends GameServerPacketController {

    @Inject
    public IGameServerManager gameServerManager;

    @Inject
    public ITemplateService templateService;

    public GameServerPacketServiceHandler() {

        new SimplePacketHandler<APIRequestGameServerCopyResponsePacket>(APIRequestGameServerCopyResponsePacket.class, packet ->
            Logger.log(packet.isFailed() ? LoggerType.ERROR : LoggerType.INFO, packet.isFailed() ?
                "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage()
                : "Successfully copied the gameserver " + packet.getGameservername() + " to its template!"));

        new SimplePacketHandler<GameServerControlPlayerPacket>(GameServerControlPlayerPacket.class, (packet) -> {
            //TODO fix only proxy join ctx.writeAndFlush(new MasterKickPlayerPacket(uuid, "§cPlease connect to the Proxy!"));
        });

        new SimplePacketHandler<APIRequestGameServerPacket>(APIRequestGameServerPacket.class, (ctx, packet) ->
            getGameServerByConnection(ctx, service -> confirmPacketTypeResponse(packet.getAction(), service, packet.getRequestId(), packet.getValue())));

        new SimplePacketHandler<RedirectPacket>(RedirectPacket.class, packet ->
            getRedirectPacketConnection(packet.getSnowflake(), packet.getPacket()));

        new SimplePacketHandler<GameServerRegisterPacket>(GameServerRegisterPacket.class, (ctx, packet) -> {
            Logger.log(LoggerType.INFO, "register server" + packet.getPort());
            getGameServerBySnowflake(server -> {
                ((SimpleGameServer) server).setCtx(ctx);
                ((SimpleGameServer) server).setPort(packet.getPort());

                ctx.writeAndFlush(new GameServerUpdatePacket(server));
                sendCloudCommandAcceptList(server);

                sendMotdUpdatePacket(server);
                server.setStatus(GameServerStatus.RUNNING);
                server.setVisible(ServiceVisibility.VISIBLE);

                updateProxyServerList(server);
                callServerStartedEvent(server);
                sendServerStartLog(server);
            }, packet.getSnowflake());

        });

    }
}
