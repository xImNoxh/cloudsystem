package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class PingPacket extends Packet {

    private long start;

    public PingPacket(long start) {
        this.start = start;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeLong(start);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        start = buf.readLong();
    }

    public long getStart() {
        return start;
    }
}
