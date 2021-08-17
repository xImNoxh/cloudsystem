package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.packet.Packet;
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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, channel);
        writeString(byteBuf, data);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        channel = readString(byteBuf);
        data = readString(byteBuf);
    }

    public <T> T convertTo(Class<? extends T> clazz) {
        return gson.fromJson(this.data, clazz);
    }

    public String getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }
}
