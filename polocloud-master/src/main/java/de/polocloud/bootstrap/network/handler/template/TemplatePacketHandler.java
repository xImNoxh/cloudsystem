package de.polocloud.bootstrap.network.handler.template;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.packets.api.template.APIResponseTemplatePacket;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;

import java.util.Collections;
import java.util.UUID;

public class TemplatePacketHandler extends TemplatePacketServiceController {

    @Inject
    private IGameServerManager gameServerManager;
    @Inject
    private ITemplateManager templateService;

    public TemplatePacketHandler() {
        new SimplePacketHandler<APIRequestTemplatePacket>(APIRequestTemplatePacket.class, (ctx, packet) -> {
            UUID requestId = packet.getRequestId();
            APIRequestTemplatePacket.Action action = packet.getAction();
            final IGameServer requestServer = gameServerManager.getCached(ctx);
            if (action == APIRequestTemplatePacket.Action.NAME) {
                requestServer.sendPacket(new APIResponseTemplatePacket(requestId,
                    Collections.singletonList(templateService.getTemplate(packet.getValue())), APIResponseTemplatePacket.Type.SINGLE));
            } else if (action == APIRequestTemplatePacket.Action.ALL) {
                requestServer.sendPacket(new APIResponseTemplatePacket(requestId, templateService.getTemplates(), APIResponseTemplatePacket.Type.LIST));
            }
        });

    }
}
