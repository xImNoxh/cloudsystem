package de.polocloud.bootstrap.network.handler.wrapper;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.bootstrap.network.SimplePacketHandler;

public class WrapperPacketHandlerService extends WrapperHandlerServiceController {

    @Inject
    private IGameServerManager gameServerManager;

    public WrapperPacketHandlerService() {

        new SimplePacketHandler<WrapperLoginPacket>(WrapperLoginPacket.class, (ctx, packet) -> getLoginResponse(packet, (response, client) -> {
            client.sendPacket(getMasterLoginResponsePacket(response));
            if (!response) {
                ctx.close();
                return;
            }

            wrapperManager.registerWrapper(client);

            sendWrapperSuccessfully(client, packet);
        }, ctx));
    }
}
