package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class MasterUpdatePlayerInfoPacket extends Packet {

    private int onlinePlayers;
    private int maxPlayers;

    public MasterUpdatePlayerInfoPacket() {

    }

    public MasterUpdatePlayerInfoPacket(int onlinePlayers, int maxPlayers) {
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(onlinePlayers);
        buf.writeInt(maxPlayers);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        onlinePlayers = buf.readInt();
        maxPlayers = buf.readInt();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }
}
