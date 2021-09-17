package de.polocloud.bootstrap.pubsub;

import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.api.PublishPacket;
import io.netty.channel.ChannelHandlerContext;

public class PublishPacketHandler implements IPacketHandler<Packet> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        PublishPacket packet = (PublishPacket) obj;
        MasterPubSubManager.getInstance().publish(packet);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return PublishPacket.class;
    }
}
