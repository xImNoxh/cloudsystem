package de.polocloud.api.network.packets.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class MasterShutdownPacket extends Packet {

    @Override
    public void write(IPacketBuffer buf) throws IOException {

    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

    }
}
