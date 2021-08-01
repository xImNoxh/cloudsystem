package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class MasterPlayerKickPacket extends Packet {

    private UUID uuid;
    private String message;

    public MasterPlayerKickPacket() {

    }

    public MasterPlayerKickPacket(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());

        writeString(byteBuf, message);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));

        message = readString(byteBuf);

    }


    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }
    
}
