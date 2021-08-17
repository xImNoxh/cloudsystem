package de.polocloud.api.network.protocol.packet.api.fallback;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(playername);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        playername = buf.readString();
    }

    public String getPlayername() {
        return playername;
    }
}
