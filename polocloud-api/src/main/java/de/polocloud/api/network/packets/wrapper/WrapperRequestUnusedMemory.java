package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor @Getter @AutoRegistry @NoArgsConstructor
public class WrapperRequestUnusedMemory extends Packet {

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
