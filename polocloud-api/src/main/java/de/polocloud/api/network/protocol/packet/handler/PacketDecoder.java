package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.network.protocol.buffer.SimplePacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext chx, ByteBuf byteBuf, List<Object> output) throws Exception {
        int id = byteBuf.readInt();
        Packet packet = PacketRegistry.createPacket(id);
        packet.read(new SimplePacketBuffer(byteBuf));
        output.add(packet);
    }
}
