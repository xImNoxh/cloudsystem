package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerShutdownPacket extends Packet {

    private String serverName;

    public GameServerShutdownPacket() {

    }

    public GameServerShutdownPacket(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, serverName);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        this.serverName = readString(byteBuf);
    }

    public String getServerName() {
        return serverName;
    }
}
