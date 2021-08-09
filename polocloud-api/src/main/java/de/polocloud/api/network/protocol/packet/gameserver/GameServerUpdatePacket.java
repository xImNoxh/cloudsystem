package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.gameserver.IGameServer;
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
    public void write(ByteBuf byteBuf) throws IOException {
        writeGameServer(byteBuf, gameServer);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        gameServer = readGameServer(byteBuf);
    }

    public IGameServer getGameServer() {
        return gameServer;
    }
}
