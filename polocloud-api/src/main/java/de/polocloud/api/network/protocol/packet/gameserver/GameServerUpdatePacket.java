package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerUpdatePacket extends Packet {

    private IGameServer gameServer;

    public GameServerUpdatePacket() {}

    public GameServerUpdatePacket(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeGameServer(gameServer);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        gameServer = buf.readGameServer();
    }

    public IGameServer getGameServer() {
        return gameServer;
    }
}
