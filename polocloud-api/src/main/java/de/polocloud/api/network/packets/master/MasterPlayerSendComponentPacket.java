package de.polocloud.api.network.packets.master;

import de.polocloud.api.chat.CloudComponent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.common.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry
@Getter @AllArgsConstructor @NoArgsConstructor
public class MasterPlayerSendComponentPacket extends Packet {

    private UUID uuid;
    private CloudComponent component;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeProtocol(component);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.uuid = UUID.fromString(buf.readString());
        this.component = buf.readProtocol();
    }

}
