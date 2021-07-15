package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerMaxPlayersUpdatePacket extends IPacket {


    private String message;
    private int maxPlayers;

    public GameServerMaxPlayersUpdatePacket(String message, int maxPlayers) {
        this.message = message;
        this.maxPlayers = maxPlayers;
    }

    public GameServerMaxPlayersUpdatePacket() { }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeInt(maxPlayers);
        writeString(byteBuf, message);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        maxPlayers = byteBuf.readInt();
        message = readString(byteBuf);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getMessage() {
        return message;
    }
}


