package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerShutdownPacket extends IPacket {

    public GameServerShutdownPacket() { }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {

    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {

    }
}
