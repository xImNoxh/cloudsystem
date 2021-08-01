package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class MasterPlayerSendMessagePacket extends Packet {

    private UUID uuid;
    private String message;

    public MasterPlayerSendMessagePacket() {

    }

    public MasterPlayerSendMessagePacket(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, this.message);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.uuid = UUID.fromString(readString(byteBuf));
        this.message = readString(byteBuf);
    }

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }
    
}
