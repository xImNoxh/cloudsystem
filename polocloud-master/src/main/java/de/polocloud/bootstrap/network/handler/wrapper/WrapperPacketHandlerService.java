package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRegisterStaticServerPacket;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

public class WrapperPacketHandlerService extends WrapperHandlerServiceController {

    @Inject
    private IGameServerManager gameServerManager;

    public WrapperPacketHandlerService() {

        new SimplePacketHandler<WrapperRegisterStaticServerPacket>(WrapperRegisterStaticServerPacket.class, (ctx, packet) -> {
            Logger.log(LoggerType.INFO, "register static server with id " + packet.getSnowflake());
            getTemplateByName(packet, template ->
                gameServerManager.registerGameServer(createNewService(ctx, template, packet)));
        });

        new SimplePacketHandler<WrapperLoginPacket>(WrapperLoginPacket.class, (ctx, packet) -> getLoginResponse(packet, (response, client) -> {
            client.sendPacket(getMasterLoginResponsePacket(response));
            if (!response) {
                ctx.close();
                return;
            }
            wrapperManager.registerWrapper(client);
            sendWrapperSuccessfully(packet);
            wrapperManager.syncCache();
        }, ctx));
    }
}
