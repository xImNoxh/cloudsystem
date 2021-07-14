package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.master.MasterKickPlayerPacket;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.UUID;

public class APIRequestGameServerHandler extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        APIRequestGameServerPacket packet = (APIRequestGameServerPacket) obj;

        UUID requestId = packet.getRequestId();
        String value = packet.getValue();
        APIRequestGameServerPacket.Action action = packet.getAction();

        if (action == APIRequestGameServerPacket.Action.NAME) {
            gameServerManager.getGameServerByName(value).thenAccept(gameServer -> {
                gameServerManager.getGameServerByConnection(ctx).thenAccept(requestServer -> {
                    requestServer.sendPacket(new MasterKickPlayerPacket(UUID.randomUUID(), "Test"));
                    requestServer.sendPacket(new APIResponseGameServerPacket(requestId, Collections.singletonList(gameServer), APIResponseGameServerPacket.Type.SINGLE));
                });

            });
        }



    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return APIRequestGameServerPacket.class;
    }
}
