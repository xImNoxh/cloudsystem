package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
import de.polocloud.api.template.ITemplateService;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class APIRequestTemplateHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;
    @Inject
    private ITemplateService templateService;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        APIRequestTemplatePacket packet = (APIRequestTemplatePacket) obj;

        UUID requestId = packet.getRequestId();
        String value = packet.getValue();
        APIRequestTemplatePacket.Action action = packet.getAction();
        try {
            final IGameServer requestServer = gameServerManager.getGameServerByConnection(ctx).get();
            if (action == APIRequestTemplatePacket.Action.NAME) {
                requestServer.sendPacket(new APIResponseTemplatePacket(requestId, Collections.singletonList(templateService.getTemplateByName(value).get()), APIResponseTemplatePacket.Type.SINGLE));
            } else if (action == APIRequestTemplatePacket.Action.ALL) {
                templateService.getLoadedTemplates().thenAccept(gameServerList -> requestServer.sendPacket(new APIResponseTemplatePacket(requestId, gameServerList, APIResponseTemplatePacket.Type.LIST)));
            } else if (action == APIRequestTemplatePacket.Action.LIST_BY_TYPE) {

            }else if(action == APIRequestTemplatePacket.Action.SNOWFLAKE){

            }else if(action == APIRequestTemplatePacket.Action.LIST_BY_TEMPLATE){

            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return APIRequestTemplatePacket.class;
    }
}
