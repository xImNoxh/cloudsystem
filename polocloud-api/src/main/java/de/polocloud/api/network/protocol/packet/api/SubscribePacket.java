package de.polocloud.api.network.protocol.packet.api;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, channel);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        channel = readString(byteBuf);
    }

    public String getChannel() {
        return channel;
    }
}
