package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class WrapperLoginPacket extends Packet {

    private String name;
    private String key;

    public WrapperLoginPacket() {
    }

    public WrapperLoginPacket(String name, String key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, name);
        writeString(byteBuf, key);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        name = readString(byteBuf);
        key = readString(byteBuf);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

}
