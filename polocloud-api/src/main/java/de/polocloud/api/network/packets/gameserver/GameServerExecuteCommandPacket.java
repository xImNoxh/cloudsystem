package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
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
