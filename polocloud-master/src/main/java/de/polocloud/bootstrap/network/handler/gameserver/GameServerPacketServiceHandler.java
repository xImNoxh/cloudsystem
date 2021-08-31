package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.server.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.RedirectPacket;
import de.polocloud.api.network.packets.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.api.network.packets.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.packets.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.packets.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.other.IRequestHandler;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.bootstrap.network.SimplePacketHandler;

public class GameServerPacketServiceHandler extends GameServerPacketController {

    @Inject
    public IGameServerManager gameServerManager;

    public GameServerPacketServiceHandler() {

        PoloCloudAPI.getInstance().getConnection().getRequestManager().registerRequestHandler((IRequestHandler<String>) request -> {
            if (request.getKey().equalsIgnoreCase("polo::api::service::request::register")) {
                PoloCloudAPI.getInstance().updateCache();
                PoloComponent<String> response = request.createResponse(String.class);
                response.value("Handshake received!");
                response.respond();

            }
        });

        PoloCloudAPI.getInstance().getEventManager().registerListener(new IListener() {

            @EventHandler
            public void handleChange(CloudGameServerStatusChangeEvent event) {
                IGameServer gameServer = event.getGameServer();
                GameServerStatus status = event.getStatus();
                PoloLogger.print(LogLevel.INFO, gameServer.getName() + " changed Status to " + status);
            }

        });


        new SimplePacketHandler<>(MasterStartServerPacket.class, packet -> {

            IGameServer gameServer = packet.getGameServer();
            IWrapper wrapper = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(packet.getWrapper().getName());

            wrapper.startServer(gameServer);
        });

        new SimplePacketHandler<>(APIRequestGameServerCopyResponsePacket.class, packet ->
            PoloLogger.print(packet.isFailed() ? LogLevel.ERROR : LogLevel.INFO, packet.isFailed() ?
                "Failed to copy the gameserver " + packet.getGameservername() + " to the template! Error: " + packet.getErrorMessage()
                : "Successfully copied the gameserver " + packet.getGameservername() + " to its template!"));


        new SimplePacketHandler<>(GameServerSuccessfullyStartedPacket.class, packet -> {
            IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(packet.getServerName());

            gameServer.setStatus(GameServerStatus.RUNNING);
            gameServer.update();

            sendServerStartLog(gameServer);
            updateProxyServerList(gameServer);
        });

        new SimplePacketHandler<APIRequestGameServerPacket>(APIRequestGameServerPacket.class, (ctx, packet) -> getGameServerByConnection(ctx, service -> confirmPacketTypeResponse(packet.getAction(), service, packet.getRequestId(), packet.getValue())));

        new SimplePacketHandler<RedirectPacket>(RedirectPacket.class, (ctx, packet) -> getRedirectPacketConnection(packet.getSnowflake(), packet.getPacket()));

        new SimplePacketHandler<GameServerUpdatePacket>(GameServerUpdatePacket.class, (ctx, packet) -> PoloCloudAPI.getInstance().getGameServerManager().updateObject(packet.getGameServer()));

        new SimplePacketHandler<GameServerRegisterPacket>(GameServerRegisterPacket.class, (ctx, packet) -> getGameServerBySnowflake(server -> {

            ((SimpleGameServer) server).setChannelHandlerContext(ctx);
            server.setPort(packet.getPort());
            server.setRegistered(true);
            server.setStatus(GameServerStatus.RUNNING);
            server.setVisible(true);
            server.updateInternally();

        }, packet.getSnowflake()));

    }
}
