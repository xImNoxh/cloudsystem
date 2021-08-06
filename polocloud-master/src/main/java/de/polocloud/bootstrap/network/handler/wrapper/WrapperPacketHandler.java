package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

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
            getTemplateByName(templateService, packet, template ->
                gameServerManager.registerGameServer(createNewService(wrapperClientManager, ctx, template, packet)));
        });

        new SimplePacketHandler<WrapperLoginPacket>(WrapperLoginPacket.class, (ctx, packet) -> {
            getLoginResponse(config, packet, response -> {
                Logger.log(LoggerType.INFO, "The Wrapper " + ConsoleColors.LIGHT_BLUE + packet.getName() + ConsoleColors.GRAY + " is successfully connected to the master.");
                WrapperClient wrapperClient = new WrapperClient(packet.getName(), ctx);
                wrapperClient.sendPacket(getMasterLoginResponsePacket(response));
                if (!response) {
                    ctx.close();
                } else wrapperClientManager.registerWrapperClient(wrapperClient);
            });
        });
    }
}
