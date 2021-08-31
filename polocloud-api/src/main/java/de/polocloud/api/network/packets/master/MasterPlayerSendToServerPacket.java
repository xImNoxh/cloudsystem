package de.polocloud.api.network.packets.master;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry//(id = 0x34)
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(this.targetServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.uuid = UUID.fromString(buf.readString());
        this.targetServer = buf.readString();
    }

    public String getTargetServer() {
        return targetServer;
    }

    public UUID getUuid() {
        return uuid;
    }

}
