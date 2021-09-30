package de.polocloud.api.network.packets.cloudplayer;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter @AllArgsConstructor @AutoRegistry
public class APIGetPlayerByNamePacket extends Packet {

    private String name;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(name);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        name = buf.readString();
    }
}
