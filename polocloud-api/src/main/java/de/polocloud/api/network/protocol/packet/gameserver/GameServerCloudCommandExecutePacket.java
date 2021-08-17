package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class GameServerCloudCommandExecutePacket extends Packet {

    private UUID uuid;
    private String command;

    public GameServerCloudCommandExecutePacket() {
        
    }

    public GameServerCloudCommandExecutePacket(UUID uuid, String command) {
        this.uuid = uuid;
        this.command = command;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(uuid.toString());
        buf.writeString(command);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        uuid = UUID.fromString(buf.readString());
        command = buf.readString();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }
}
