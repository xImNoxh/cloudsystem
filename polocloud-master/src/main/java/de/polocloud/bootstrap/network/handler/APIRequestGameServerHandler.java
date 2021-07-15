package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.master.MasterKickPlayerPacket;
import de.polocloud.api.template.TemplateType;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class APIRequestGameServerHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        APIRequestGameServerPacket packet = (APIRequestGameServerPacket) obj;

        UUID requestId = packet.getRequestId();
        String value = packet.getValue();
        APIRequestGameServerPacket.Action action = packet.getAction();
        try {
            final IGameServer requestServer = gameServerManager.getGameServerByConnection(ctx).get();


            if (action == APIRequestGameServerPacket.Action.NAME) {

                gameServerManager.getGameServerByName(value).thenAccept(gameServer -> {
                    requestServer.sendPacket(new APIResponseGameServerPacket(requestId, Collections.singletonList(gameServer), APIResponseGameServerPacket.Type.SINGLE));
                });
            } else if (action == APIRequestGameServerPacket.Action.ALL) {
                gameServerManager.getGameServers().thenAccept(gameServerList -> {
                    requestServer.sendPacket(new APIResponseGameServerPacket(requestId, gameServerList, APIResponseGameServerPacket.Type.LIST));

                });
            } else if (action == APIRequestGameServerPacket.Action.LIST_BY_TYPE) {
                gameServerManager.getGameServersByType(TemplateType.valueOf(value)).thenAccept(gameServerList -> {
                    requestServer.sendPacket(new APIResponseGameServerPacket(requestId, gameServerList, APIResponseGameServerPacket.Type.LIST));

                });
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return APIRequestGameServerPacket.class;
    }
}
