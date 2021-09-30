package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @AllArgsConstructor @NoArgsConstructor @Getter
public class WrapperRequestLinesPacket extends Packet {

    private String serverName;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        serverName = buf.readString();
    }
}
