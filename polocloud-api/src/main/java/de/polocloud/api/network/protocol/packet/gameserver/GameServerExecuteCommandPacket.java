package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class GameServerExecuteCommandPacket extends Packet {

    private String command;

    public GameServerExecuteCommandPacket() {
    }

    public GameServerExecuteCommandPacket(String command) {
        this.command = command;
    }


    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, command);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        command = readString(byteBuf);
    }

    public String getCommand() {
        return command;
    }

}
