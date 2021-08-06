package de.polocloud.bootstrap.network.handler.template;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.network.SimplePacketHandler;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TemplatePacketHandler extends TemplatePacketServiceController {

    @Inject
    private IGameServerManager gameServerManager;
    @Inject
    private ITemplateService templateService;

    public TemplatePacketHandler() {
        new SimplePacketHandler<APIRequestTemplatePacket>(APIRequestTemplatePacket.class, (ctx, packet) -> {
            UUID requestId = packet.getRequestId();
            APIRequestTemplatePacket.Action action = packet.getAction();
            try {
                final IGameServer requestServer = gameServerManager.getGameServerByConnection(ctx).get();
                if (action == APIRequestTemplatePacket.Action.NAME) {
                    requestServer.sendPacket(new APIResponseTemplatePacket(requestId,
                        Collections.singletonList(templateService.getTemplateByName(packet.getValue()).get()), APIResponseTemplatePacket.Type.SINGLE));
                } else if (action == APIRequestTemplatePacket.Action.ALL) {
                    templateService.getLoadedTemplates().thenAccept(gameServerList ->
                        requestServer.sendPacket(new APIResponseTemplatePacket(requestId, gameServerList, APIResponseTemplatePacket.Type.LIST)));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }
}
