package de.polocloud.bootstrap.pubsub;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.api.SubscribePacket;
import io.netty.channel.ChannelHandlerContext;

public class SubscribePacketHandler implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        SubscribePacket packet = (SubscribePacket) obj;
        MasterPubSubManager.getInstance().subscribe(ctx, packet.getChannel());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return SubscribePacket.class;
    }
}
