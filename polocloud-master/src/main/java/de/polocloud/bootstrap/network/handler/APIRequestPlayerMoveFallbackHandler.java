package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

public class APIRequestPlayerMoveFallbackHandler extends IPacketHandler<Packet> {

    @Inject
    private ICloudPlayerManager cloudPlayerManager;

    public APIRequestPlayerMoveFallbackHandler() {
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        APIRequestPlayerMoveFallbackPacket packet = (APIRequestPlayerMoveFallbackPacket) obj;

        cloudPlayerManager.getOnlinePlayer(packet.getPlayername()).thenAccept(player -> {
            if (player != null) {
                player.sendToFallback();
            }
        });
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return APIRequestPlayerMoveFallbackPacket.class;
    }
}
