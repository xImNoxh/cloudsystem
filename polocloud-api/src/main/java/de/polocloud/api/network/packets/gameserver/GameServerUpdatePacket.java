package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
public class GameServerUpdatePacket extends Packet {

    private IGameServer gameServer;

    public GameServerUpdatePacket() {
    }

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
