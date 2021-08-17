package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class RedirectPacket extends Packet {

    private long snowflake;
    private Packet packet;

    public RedirectPacket() {

    }

    public RedirectPacket(long snowflake, Packet packet) {
        this.snowflake = snowflake;
        this.packet = packet;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeLong(snowflake);

        int packetId = PacketRegistry.getPacketId(packet.getClass());
        buf.writeInt(packetId);
        packet.write(buf);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        snowflake = buf.readLong();
        int id = buf.readInt();
        try {
            packet = PacketRegistry.createInstance(id);
            packet.read(buf);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Packet getPacket() {
        return packet;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
