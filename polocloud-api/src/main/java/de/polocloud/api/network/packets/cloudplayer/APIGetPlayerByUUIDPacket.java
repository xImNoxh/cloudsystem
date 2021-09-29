package de.polocloud.api.network.packets.cloudplayer;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

@Getter @AllArgsConstructor @AutoRegistry
public class APIGetPlayerByUUIDPacket extends Packet {

    private UUID uuid;

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeUUID(uuid);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = buf.readUUID();
    }
}
