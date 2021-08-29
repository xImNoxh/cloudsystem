package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry//(id = 0x23)
public class GameServerPlayerRequestJoinPacket extends Packet {

    private UUID uuid;

    public GameServerPlayerRequestJoinPacket() {

    }

    public GameServerPlayerRequestJoinPacket(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());
    }

    public UUID getUuid() {
        return uuid;
    }

}
