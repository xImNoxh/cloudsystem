package de.polocloud.api.network.packets.master;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x36)
public class MasterRequestServerStartPacket extends Packet {

    private String serverName;
    private int port;

    public MasterRequestServerStartPacket() {

    }

    public MasterRequestServerStartPacket(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
        buf.writeInt(port);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        serverName = buf.readString();
        port = buf.readInt();
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

}
