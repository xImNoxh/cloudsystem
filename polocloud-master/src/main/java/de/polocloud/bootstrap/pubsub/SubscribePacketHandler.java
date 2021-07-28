package de.polocloud.bootstrap.pubsub;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.SubscribePacket;
import io.netty.channel.ChannelHandlerContext;

public class SubscribePacketHandler extends IPacketHandler<Packet> {

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        SubscribePacket packet = (SubscribePacket) obj;
        pubSubManager.subscribe(ctx, packet.getChannel());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return SubscribePacket.class;
    }
}
