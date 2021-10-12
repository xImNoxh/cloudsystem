package de.polocloud.api.network.protocol.packet.base.other;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AutoRegistry @NoArgsConstructor @AllArgsConstructor @Getter
public class ForwardingPacket extends Packet {

    private PoloType type;
    private String receiver;
    private Packet packet;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeEnum(type);
        buf.writeString(receiver);
        buf.writePacket(this.packet);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.type = buf.readEnum();
        this.receiver = buf.readString();
        this.packet = buf.readPacket();
    }

}
