package de.polocloud.api.network.protocol.packet.gameserver;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, name);
        writeString(byteBuf, targetServer);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));
        name = readString(byteBuf);
        targetServer = readString(byteBuf);
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
