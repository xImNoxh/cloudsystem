package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class WrapperRequestShutdownPacket extends Packet {

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

    }
}
