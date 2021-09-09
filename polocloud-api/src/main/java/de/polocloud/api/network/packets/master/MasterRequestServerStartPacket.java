package de.polocloud.api.network.packets.master;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry//(id = 0x36)
public class MasterRequestServerStartPacket extends Packet {

    private String serverName;
    private int port;

    private IGameServer gameServer;

    public MasterRequestServerStartPacket() {

    }

    public MasterRequestServerStartPacket(IGameServer gameServer) {
        this.serverName = gameServer.getName();
        this.port = gameServer.getPort();
        this.gameServer = gameServer;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(serverName);
        buf.writeInt(port);
        buf.writeGameServer(gameServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        serverName = buf.readString();
        port = buf.readInt();
        gameServer = buf.readGameServer();
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

}
