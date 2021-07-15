package de.polocloud.bootstrap.pubsub;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import io.netty.channel.ChannelHandlerContext;

public class PublishPacketHandler extends IPacketHandler {

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        PublishPacket packet = (PublishPacket) obj;
        pubSubManager.publish(packet);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return PublishPacket.class;
    }
}
