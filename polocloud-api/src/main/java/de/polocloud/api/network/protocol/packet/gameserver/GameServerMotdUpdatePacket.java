package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerMotdUpdatePacket extends Packet {

    private String motd;

    public GameServerMotdUpdatePacket() {
    }

    public GameServerMotdUpdatePacket(String motd) {
        this.motd = motd;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, motd);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        motd = readString(byteBuf);
    }

    public String getMotd() {
        return motd;
    }
}
