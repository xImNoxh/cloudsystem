package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x26)
public class GameServerShutdownPacket extends Packet {

    private String serverName;

    public GameServerShutdownPacket() {

    }

    public GameServerShutdownPacket(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.serverName = buf.readString();
    }

    public String getServerName() {
        return serverName;
    }
}
