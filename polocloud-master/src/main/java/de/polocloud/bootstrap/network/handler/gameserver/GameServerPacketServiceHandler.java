package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.RedirectPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.gameserver.*;
import de.polocloud.api.network.protocol.packet.master.MasterStartServerPacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.wrapper.IWrapper;
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

        new SimplePacketHandler<>(MasterStartServerPacket.class, packet -> {

            IGameServer gameServer = packet.getGameServer();
            IWrapper wrapper = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(packet.getWrapper().getName());

            wrapper.startServer(gameServer);
        });

        new SimplePacketHandler<>(APIRequestGameServerCopyResponsePacket.class, packet ->
            Logger.log(packet.isFailed() ? LoggerType.ERROR : LoggerType.INFO, packet.isFailed() ?
                "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage()
                : "Successfully copied the gameserver " + packet.getGameservername() + " to its template!"));

        new SimplePacketHandler<>(GameServerControlPlayerPacket.class, (packet) -> {
            //TODO fix only proxy join ctx.writeAndFlush(new MasterKickPlayerPacket(uuid, "§cPlease connect to the Proxy!"));
        });


        new SimplePacketHandler<>(GameServerSuccessfullyStartedPacket.class, packet -> PoloCloudAPI.getInstance().getGameServerManager().getGameServerByName(packet.getServerName()).thenAccept(it -> {
            it.setStatus(GameServerStatus.RUNNING);
            sendServerStartLog(it);
            updateProxyServerList(it);
        }));

        new SimplePacketHandler<APIRequestGameServerPacket>(APIRequestGameServerPacket.class, (ctx, packet) ->
            getGameServerByConnection(ctx, service -> confirmPacketTypeResponse(packet.getAction(), service, packet.getRequestId(), packet.getValue())));

        new SimplePacketHandler<RedirectPacket>(RedirectPacket.class, (ctx, packet) ->
            getRedirectPacketConnection(packet.getSnowflake(), packet.getPacket()));

        new SimplePacketHandler<GameServerUpdatePacket>(GameServerUpdatePacket.class, (ctx, packet) ->
            PoloCloudAPI.getInstance().getGameServerManager().getGameServerByName(packet.getGameServer().getName()).thenAccept(key -> {
                key.setStatus(packet.getGameServer().getStatus());
                key.setMotd(packet.getGameServer().getMotd());
                key.setMaxPlayers(packet.getGameServer().getMaxPlayers());
                key.setVisible(packet.getGameServer().getServiceVisibility());
            }));

        new SimplePacketHandler<GameServerRegisterPacket>(GameServerRegisterPacket.class, (ctx, packet) -> getGameServerBySnowflake(server -> {
            ((SimpleGameServer) server).setCtx(ctx);
            server.setPort(packet.getPort());

            server.sendPacket(new GameServerUpdatePacket(server));
            sendCloudCommandAcceptList(server);

            server.setVisible(true);

            callServerStartedEvent(server);
        }, packet.getSnowflake()));

    }
}
