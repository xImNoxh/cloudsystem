package de.polocloud.api.network.protocol.packet.gameserver;

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
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, uuid.toString());
        writeString(byteBuf, command);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        uuid = UUID.fromString(readString(byteBuf));
        command = readString(byteBuf);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }
}
