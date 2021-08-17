package de.polocloud.api.network.protocol.packet.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(name);
        buf.writeString(key);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        name = buf.readString();
        key = buf.readString();
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

}
