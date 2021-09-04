package de.polocloud.api.network.protocol.codec;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.buffer.SimplePacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) throws Exception {
        IPacketBuffer buf = new SimplePacketBuffer(byteBuf);

        int id = PacketFactory.getPacketId(packet.getClass());

        if (id == -1) {
            throw new NullPointerException("Packet with " + packet.getClass().getSimpleName() + " was not registered");
        }
        buf.writeInt(id); //The packet id
        buf.writeLong(packet.getSnowflake()); //The packet snowflake

        packet.write(buf);

    }
}
