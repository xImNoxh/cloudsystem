package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(this.message);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.uuid = UUID.fromString(buf.readString());
        this.message = buf.readString();
    }

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

}
