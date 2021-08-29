package de.polocloud.api.network.packets.master;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());

        buf.writeString(message);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());

        message = buf.readString();

    }


    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

}
