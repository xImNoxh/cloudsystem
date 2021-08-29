package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x44)
public class WrapperRequestShutdownPacket extends Packet {

    public WrapperRequestShutdownPacket() {
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

    }
}
