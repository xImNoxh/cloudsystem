package de.polocloud.api.network.protocol.packet.api.fallback;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class APIRequestPlayerMoveFallbackPacket extends Packet {

    private String playername;

    public APIRequestPlayerMoveFallbackPacket() {

    }

    public APIRequestPlayerMoveFallbackPacket(String playername) {
        this.playername = playername;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, playername);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        playername = readString(byteBuf);
    }

    public String getPlayername() {
        return playername;
    }
}
