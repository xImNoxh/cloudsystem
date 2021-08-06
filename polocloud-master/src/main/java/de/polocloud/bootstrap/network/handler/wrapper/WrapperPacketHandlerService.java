package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

public class WrapperPacketHandlerService extends WrapperHandlerServiceController {

    @Inject
    private ITemplateService templateService;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private MasterConfig config;

    public WrapperPacketHandlerService() {

        new SimplePacketHandler<WrapperRegisterStaticServerPacket>(WrapperRegisterStaticServerPacket.class, (ctx, packet) -> {
            Logger.log(LoggerType.INFO, "register static server with id " + packet.getSnowflake());
            getTemplateByName(templateService, packet, template ->
                gameServerManager.registerGameServer(createNewService(wrapperClientManager, ctx, template, packet)));
        });

        new SimplePacketHandler<WrapperLoginPacket>(WrapperLoginPacket.class, (ctx, packet) -> {
            getLoginResponse(config, packet, (response, client) -> {
                client.sendPacket(getMasterLoginResponsePacket(response));
                if (!response) {
                    ctx.close();
                    return;
                }
                wrapperClientManager.registerWrapperClient(client);
                sendWrapperSuccessfully(packet);
            }, ctx);
        });
    }
}
