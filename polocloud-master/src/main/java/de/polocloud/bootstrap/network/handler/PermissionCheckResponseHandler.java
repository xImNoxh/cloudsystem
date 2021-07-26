package de.polocloud.bootstrap.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.response.ResponseHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CompletableFuture;

public class PermissionCheckResponseHandler extends IPacketHandler {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        PermissionCheckResponsePacket packet = (PermissionCheckResponsePacket) obj;
        CompletableFuture<Boolean> completableFuture = ResponseHandler.getCompletableFuture(packet.getRequest(), true);
        completableFuture.complete(packet.isResponse());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return PermissionCheckResponsePacket.class;
    }
}
