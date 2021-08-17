package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.util.PoloUtils;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PublishPacket extends Packet {

    private String channel;
    private String data;

    public PublishPacket() {

    }

    public PublishPacket(String channel, String data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(channel);
        buf.writeString(data);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        channel = buf.readString();
        data = buf.readString();
    }

    public <T> T convertTo(Class<? extends T> clazz) {
        return PoloUtils.GSON_INSTANCE.fromJson(this.data, clazz);
    }

    public String getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }
}
