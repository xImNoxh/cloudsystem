package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class MasterPlayerSendToServerPacket extends Packet {

    private UUID uuid;
    private String targetServer;

    public MasterPlayerSendToServerPacket() {

    }

    public MasterPlayerSendToServerPacket(UUID uuid, String targetServer) {
        this.uuid = uuid;
        this.targetServer = targetServer;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, this.targetServer);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.uuid = UUID.fromString(readString(byteBuf));
        this.targetServer = readString(byteBuf);
    }

    public String getTargetServer() {
        return targetServer;
    }

    public UUID getUuid() {
        return uuid;
    }
    
}
