package de.polocloud.api.network.packets;

import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@AutoRegistry//(id = 0x45)
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

        int packetId = PacketFactory.getPacketId(packet.getClass());
        buf.writeInt(packetId);
        packet.write(buf);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        snowflake = buf.readLong();
        int id = buf.readInt();
        packet = PacketFactory.createPacket(id);
        packet.read(buf);
    }

    public Packet getPacket() {
        return packet;
    }

    public long getSnowflake() {
        return snowflake;
    }

}
