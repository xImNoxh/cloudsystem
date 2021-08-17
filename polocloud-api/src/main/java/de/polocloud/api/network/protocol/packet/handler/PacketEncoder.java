package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.network.protocol.buffer.SimplePacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) throws Exception {
        int id = PacketRegistry.getPacketId(packet.getClass());
        if (id == -1) {
            throw new NullPointerException("Packet with " + packet.getClass().getSimpleName() + " was not registered");
        }
        byteBuf.writeInt(id);
        packet.write(new SimplePacketBuffer(byteBuf));

    }
}
