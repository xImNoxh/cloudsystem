package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerExecuteCommandPacket extends Packet {

    private String command;

    public GameServerExecuteCommandPacket() {}

    public GameServerExecuteCommandPacket(String command) {
        this.command = command;
    }


    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(command);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        command = buf.readString();
    }

    public String getCommand() {
        return command;
    }

}
