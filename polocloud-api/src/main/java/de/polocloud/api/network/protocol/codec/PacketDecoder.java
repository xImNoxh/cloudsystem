package de.polocloud.api.network.protocol.codec;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.buffer.SimplePacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chx, ByteBuf byteBuf, List<Object> output) throws Exception {
        IPacketBuffer buf = new SimplePacketBuffer(byteBuf);

        int id = buf.readInt();
        long snowflake = buf.readLong();

        if (PacketFactory.getPacketClass(id) == null) {
            throw new NullPointerException("Packet with ID " + id + " was not registered");
        }

        Packet packet = PacketFactory.createPacket(id);

        if (packet == null) {
            System.out.println("[Netty@PacketDecoder] Couldn't create Packet for ID " + id + "!");
            return;
        }

        packet.setSnowflake(snowflake);
        packet.setConnection(PoloCloudAPI.getInstance() == null ? null : PoloCloudAPI.getInstance().getConnection());

        try {
            packet.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
            PoloLogger.print(LogLevel.ERROR, "An exception was caught while decoding a packet (" + PacketFactory.getPacketClass(id).getName() + ")");
            PoloCloudAPI.getInstance().reportException(e);
        }
        output.add(packet);
    }

}
