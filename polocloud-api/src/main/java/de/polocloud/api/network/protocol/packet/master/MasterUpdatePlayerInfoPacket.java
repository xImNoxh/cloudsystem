package de.polocloud.api.network.protocol.packet.master;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class MasterUpdatePlayerInfoPacket extends Packet {

    private int onlinePlayers;
    private int maxPlayers;

    public MasterUpdatePlayerInfoPacket(){

    }

    public MasterUpdatePlayerInfoPacket(int onlinePlayers, int maxPlayers){
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        byteBuf.writeInt(onlinePlayers);
        byteBuf.writeInt(maxPlayers);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        onlinePlayers = byteBuf.readInt();
        maxPlayers = byteBuf.readInt();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }
}
