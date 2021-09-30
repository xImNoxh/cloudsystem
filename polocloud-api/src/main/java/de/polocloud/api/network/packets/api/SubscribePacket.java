package de.polocloud.api.network.packets.api;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x13)
public class SubscribePacket extends Packet {

    private String channel;

    public SubscribePacket() {

    }

    public SubscribePacket(String channel) {
        this.channel = channel;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(channel == null ? "polo::api::main" : channel);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        channel = buf.readString();
    }

    public String getChannel() {
        return channel;
    }
}
