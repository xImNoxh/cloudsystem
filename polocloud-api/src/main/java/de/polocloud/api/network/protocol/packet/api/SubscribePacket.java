package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class SubscribePacket extends Packet {

    private String channel;

    public SubscribePacket() {
        
    }

    public SubscribePacket(String channel) {
        this.channel = channel;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(channel);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        channel = buf.readString();
    }

    public String getChannel() {
        return channel;
    }
}
