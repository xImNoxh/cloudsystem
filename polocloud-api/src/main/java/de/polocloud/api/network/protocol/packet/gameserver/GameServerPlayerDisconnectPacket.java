package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(name);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());
        name = buf.readString();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

}
