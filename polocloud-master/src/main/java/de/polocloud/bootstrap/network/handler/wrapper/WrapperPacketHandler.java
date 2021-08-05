package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.gameserver.ServiceVisibility;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WrapperPacketHandler extends WrapperHandlerController {

    @Inject
    private ITemplateService templateService;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private MasterConfig config;

    public WrapperPacketHandler() {

        new SimplePacketHandler<WrapperRegisterStaticServerPacket>(WrapperRegisterStaticServerPacket.class, (ctx, packet) -> {
            Logger.log(LoggerType.INFO, "register static server with id " + packet.getSnowflake());
            IGameServer gameServer = null;
            try {
                ITemplate template = templateService.getTemplateByName(packet.getTemplateName()).get();
                gameServer = new SimpleGameServer(wrapperClientManager.getWrapperClientByConnection(ctx),
                    packet.getServerName(), GameServerStatus.PENDING, null, packet.getSnowflake(), template,
                    System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers(), ServiceVisibility.INVISIBLE);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            gameServerManager.registerGameServer(gameServer);
        });

        new SimplePacketHandler<WrapperLoginPacket>(WrapperLoginPacket.class, (ctx, packet) -> {
            boolean response = config.getProperties().getWrapperKey().equals(packet.getKey());

            Logger.log(LoggerType.INFO, "The Wrapper " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + packet.getName() + ConsoleColors.GRAY.getAnsiCode() + " is successfully connected to the master.");

            MasterLoginResponsePacket responsePacket = new MasterLoginResponsePacket(response, response ?
                "Master authentication " + ConsoleColors.GREEN.getAnsiCode() + "successfully " + ConsoleColors.GRAY.getAnsiCode() + "completed."
                : "Master authentication " + ConsoleColors.RED.getAnsiCode() + "denied" + ConsoleColors.GRAY.getAnsiCode() + ".");
            WrapperClient wrapperClient = new WrapperClient(packet.getName(), ctx);
            wrapperClient.sendPacket(responsePacket);

            if (!response) {
                ctx.close();
            } else {
                wrapperClientManager.registerWrapperClient(wrapperClient);

                List<String> proxyList = new ArrayList<>();
                proxyList.add("localhost");
                proxyList.add("127.0.0.1");

                for (WrapperClient _wrapperClient : wrapperClientManager.getWrapperClients()) {
                    String data = _wrapperClient.getConnection().channel().remoteAddress().toString().substring(1).split(":")[0];
                    proxyList.add(data);

                }
            }
        });
    }
}
