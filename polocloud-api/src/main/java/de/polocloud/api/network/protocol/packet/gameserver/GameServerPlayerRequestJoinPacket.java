package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

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
