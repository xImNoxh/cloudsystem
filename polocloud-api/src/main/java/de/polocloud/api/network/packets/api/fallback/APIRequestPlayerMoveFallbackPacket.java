package de.polocloud.api.network.packets.api.fallback;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
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
