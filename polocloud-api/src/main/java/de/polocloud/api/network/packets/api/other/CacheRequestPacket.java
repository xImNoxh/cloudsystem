package de.polocloud.api.network.packets.api.other;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x07)
public class CacheRequestPacket extends Packet {
    @Override
    public void write(IPacketBuffer buf) throws IOException {

    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

    }
}
