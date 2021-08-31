package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class BuffedPacket extends Packet {

    private Packet packet;

    public BuffedPacket(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(PacketFactory.getPacketId(packet.getClass()));
        packet.write(buf);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        int id = buf.readInt();
        this.packet = PacketFactory.createPacket(id);
        this.packet.read(buf);
    }

    public Packet getPacket() {
        return packet;
    }
}
