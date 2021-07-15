package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerMaxPlayersUpdatePacket extends IPacket {

    private int maxPlayers;

    public GameServerMaxPlayersUpdatePacket(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }


    public GameServerMaxPlayersUpdatePacket() { }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeInt(maxPlayers);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        maxPlayers = byteBuf.readInt();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}


