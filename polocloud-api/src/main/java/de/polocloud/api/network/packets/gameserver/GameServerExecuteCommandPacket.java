package de.polocloud.api.network.packets.gameserver;

import de.polocloud.api.common.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;

@AutoRegistry
public class GameServerExecuteCommandPacket extends Packet {

    private String command;
    private String server;

    public GameServerExecuteCommandPacket(String command, String server) {
        this.command = command;
        this.server = server;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(command);
        buf.writeString(server);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        command = buf.readString();
        server = buf.readString();
    }

    public String getServer() {
        return server;
    }

    public String getCommand() {
        return command;
    }

}
