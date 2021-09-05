package de.polocloud.bootstrap.network.handler.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.RedirectPacket;
import de.polocloud.api.network.packets.gameserver.GameServerRegisterPacket;
import de.polocloud.api.network.packets.gameserver.GameServerSuccessfullyStartedPacket;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.packets.master.MasterStopServerPacket;
import de.polocloud.api.network.packets.other.PingPacket;
import de.polocloud.api.network.packets.wrapper.WrapperServerStoppedPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.bootstrap.network.SimplePacketHandler;

public class GameServerPacketServiceHandler extends GameServerPacketController {

    @Inject
    public IGameServerManager gameServerManager;

    public GameServerPacketServiceHandler() {

        new SimplePacketHandler<>(WrapperServerStoppedPacket.class, packet -> {
            String name = packet.getName();
            IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(name);
            if (gameServer != null) {
                PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(gameServer);
                PoloLogger.print(LogLevel.INFO, "Wrapper §3" + gameServer.getWrapper().getName() + " §7stopped " + (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ? "proxy" : "server") + " §c" + gameServer.getName() + "§7#§4" + gameServer.getSnowflake() + "§7!");
            }
        });

        new SimplePacketHandler<>(PingPacket.class, packet -> {
            long start = packet.getStart();

            packet.respond(jsonData -> {
                jsonData.append("time", (System.currentTimeMillis() - start));
                jsonData.append("respond", System.currentTimeMillis());
                jsonData.append("start", start);
            });
        });

        PacketMessenger.registerHandler(request -> {
            if (request.getKey().equalsIgnoreCase("server-handshake")) {
                String name = request.getData().getString("name");
                IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(name);
                gameServer.setRegistered(true);
                gameServer.update();
                request.respond("handshake", true);
            }
        });

        new SimplePacketHandler<>(MasterStartServerPacket.class, packet -> {

            IGameServer gameServer = packet.getGameServer();
            IWrapper wrapper = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(packet.getWrapper().getName());

            wrapper.startServer(gameServer);

        });

        new SimplePacketHandler<>(MasterStopServerPacket.class, packet -> {

            IGameServer gameServer = packet.getGameServer();
            IWrapper wrapper = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(packet.getWrapper().getName());
            if (gameServer != null) {
                PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(gameServer);
                PoloLogger.print(LogLevel.INFO, "Wrapper §3" + gameServer.getWrapper().getName() + " §7stopped " + (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ? "proxy" : "server") + " §c" + gameServer.getName() + "§7#§4" + gameServer.getSnowflake() + "§7!");
            }
            wrapper.stopServer(gameServer);
        });

        new SimplePacketHandler<>(GameServerSuccessfullyStartedPacket.class, packet -> {
            IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(packet.getServerName());

            gameServer.setStatus(GameServerStatus.RUNNING);
            gameServer.update();

            sendServerStartLog(gameServer);
            updateProxyServerList(gameServer);
        });

        new SimplePacketHandler<RedirectPacket>(RedirectPacket.class, (ctx, packet) -> getRedirectPacketConnection(packet.getSnowflake(), packet.getPacket()));

        new SimplePacketHandler<GameServerUpdatePacket>(GameServerUpdatePacket.class, (ctx, packet) -> {
            PoloCloudAPI.getInstance().getGameServerManager().updateObject(packet.getGameServer());
            packet.passOn();
        });

        new SimplePacketHandler<GameServerRegisterPacket>(GameServerRegisterPacket.class, (ctx, packet) -> getGameServerBySnowflake(server -> {

            if (server == null) {
                return;
            }

            ((SimpleGameServer) server).setChannelHandlerContext(ctx);
            server.setPort(packet.getPort());
            server.setRegistered(true);
            server.setVisible(true);
            server.update();

        }, packet.getSnowflake()));

    }
}
