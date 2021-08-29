package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry//(id = 0x22)
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
