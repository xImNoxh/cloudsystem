package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.IOException;

@AutoRegistry
public class RequestPassOnPacket extends Packet {

    private String key;

    public RequestPassOnPacket(String key) {
        this.key = key;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(key);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        key = buf.readString();
    }

    public String getKey() {
        return key;
    }
}
