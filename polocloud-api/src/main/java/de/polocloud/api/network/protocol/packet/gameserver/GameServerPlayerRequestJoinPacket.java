package de.polocloud.api.network.protocol.packet.gameserver;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));
    }

    public UUID getUuid() {
        return uuid;
    }

}
