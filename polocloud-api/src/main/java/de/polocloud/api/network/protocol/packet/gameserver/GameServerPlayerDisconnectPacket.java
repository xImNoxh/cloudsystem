package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class GameServerPlayerDisconnectPacket extends Packet {

    private UUID uuid;
    private String name;

    public GameServerPlayerDisconnectPacket() {
        
    }

    public GameServerPlayerDisconnectPacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, name);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));
        name = readString(byteBuf);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

}
