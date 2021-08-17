package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class GameServerPlayerUpdatePacket extends Packet {

    private UUID uuid;
    private String name;

    private String targetServer;

    public GameServerPlayerUpdatePacket() {

    }

    public GameServerPlayerUpdatePacket(UUID uuid, String name, String targetServer) {
        this.uuid = uuid;
        this.name = name;
        this.targetServer = targetServer;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(name);
        buf.writeString(targetServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());
        name = buf.readString();
        targetServer = buf.readString();
    }

    public String getName() {
        return name;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public UUID getUuid() {
        return uuid;
    }

}
